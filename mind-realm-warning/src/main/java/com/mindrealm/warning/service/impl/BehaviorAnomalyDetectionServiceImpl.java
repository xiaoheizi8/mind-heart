package com.mindrealm.warning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.common.util.ValidatorUtil;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.warning.service.BehaviorAnomalyDetectionService;
import com.mindrealm.warning.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BehaviorAnomalyDetectionServiceImpl implements BehaviorAnomalyDetectionService {

    private static final Logger log = LoggerFactory.getLogger(BehaviorAnomalyDetectionServiceImpl.class);

    private static final String BEHAVIOR_KEY_PREFIX = "behavior:";
    private static final String ACTIVITY_KEY_PREFIX = "activity:";
    private static final int DEFAULT_SILENT_DAYS = 7;
    private static final double MOOD_DECLINE_THRESHOLD = 0.2;
    private static final double ACTIVITY_SPIKE_MULTIPLIER = 3.0;

    private final DiaryService diaryService;
    private final WarningService warningService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public BehaviorAnomalyDetectionServiceImpl(DiaryService diaryService,
                                               WarningService warningService,
                                               RedisTemplate<String, Object> redisTemplate) {
        this.diaryService = diaryService;
        this.warningService = warningService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, Object> analyzeUserBehavior(Long userId) {
        Map<String, Object> analysis = new HashMap<>();

        boolean silentPattern = detectSilentPattern(userId, DEFAULT_SILENT_DAYS);
        analysis.put("silentPattern", silentPattern);
        analysis.put("silentPatternDesc", silentPattern ? "检测到沉默模式：长期未登录" : "无沉默迹象");

        boolean moodDecline = detectMoodDeclinePattern(userId);
        analysis.put("moodDecline", moodDecline);
        analysis.put("moodDeclineDesc", moodDecline ? "检测到情绪持续下降趋势" : "情绪趋势正常");

        boolean activitySpike = detectActivitySpike(userId);
        analysis.put("activitySpike", activitySpike);
        analysis.put("activitySpikeDesc", activitySpike ? "检测到活动异常增加" : "活动模式正常");

        boolean shouldWarn = silentPattern || moodDecline;
        analysis.put("shouldTriggerWarning", shouldWarn);

        if (shouldWarn) {
            warningService.analyzeRisk(userId, buildBehaviorWarningContent(analysis));
        }

        return analysis;
    }

    @Override
    public boolean detectSilentPattern(Long userId, int daysThreshold) {
        try {
            List<Diary> recentDiaries = diaryService.getRecentByUser(userId, daysThreshold + 1);

            if (recentDiaries == null || recentDiaries.isEmpty()) {
                return true;
            }

            LocalDateTime lastActivity = recentDiaries.stream()
                    .map(Diary::getCreatedAt)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            if (lastActivity == null) {
                return true;
            }

            long silentDays = Duration.between(lastActivity, LocalDateTime.now()).toDays();
            return silentDays >= daysThreshold;

        } catch (Exception e) {
            log.error("检测沉默模式失败: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean detectMoodDeclinePattern(Long userId) {
        try {
            List<Diary> diaries = diaryService.getRecentByUser(userId, 14);

            if (diaries == null || diaries.size() < 3) {
                return false;
            }

            List<Diary> sortedDiaries = diaries.stream()
                    .filter(d -> d.getCreatedAt() != null)
                    .sorted(Comparator.comparing(Diary::getCreatedAt))
                    .collect(Collectors.toList());

            if (sortedDiaries.size() < 3) {
                return false;
            }

            int half = sortedDiaries.size() / 2;
            List<Diary> firstHalf = sortedDiaries.subList(0, half);
            List<Diary> secondHalf = sortedDiaries.subList(half, sortedDiaries.size());

            double firstHalfAvg = calculateAvgScore(firstHalf);
            double secondHalfAvg = calculateAvgScore(secondHalf);

            return (firstHalfAvg - secondHalfAvg) >= MOOD_DECLINE_THRESHOLD;

        } catch (Exception e) {
            log.error("检测情绪下降模式失败: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean detectActivitySpike(Long userId) {
        try {
            List<Diary> diaries = diaryService.getRecentByUser(userId, 30);

            if (diaries == null || diaries.size() < 10) {
                return false;
            }

            Map<String, Long> dailyCount = diaries.stream()
                    .filter(d -> d.getCreatedAt() != null)
                    .collect(Collectors.groupingBy(
                            d -> d.getCreatedAt().toLocalDate().toString(),
                            Collectors.counting()
                    ));

            if (dailyCount.isEmpty()) {
                return false;
            }

            double avgDaily = dailyCount.values().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0);

            if (avgDaily < 0.5) {
                return false;
            }

            long maxDaily = dailyCount.values().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0);

            return maxDaily > avgDaily * ACTIVITY_SPIKE_MULTIPLIER;

        } catch (Exception e) {
            log.error("检测活动异常失败: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getUserBehaviorProfile(Long userId) {
        Map<String, Object> profile = new HashMap<>();

        try {
            List<Diary> diaries = diaryService.getRecentByUser(userId, 30);
            profile.put("totalDiaries", diaries != null ? diaries.size() : 0);

            if (diaries != null && !diaries.isEmpty()) {
                long uniqueDays = diaries.stream()
                        .filter(d -> d.getCreatedAt() != null)
                        .map(d -> d.getCreatedAt().toLocalDate())
                        .distinct()
                        .count();
                profile.put("activeDays", uniqueDays);

                double avgScore = calculateAvgScore(diaries);
                profile.put("avgEmotionScore", avgScore);

                String dominantEmotion = diaries.stream()
                        .filter(d -> d.getEmotionCategory() != null)
                        .collect(Collectors.groupingBy(Diary::getEmotionCategory, Collectors.counting()))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("calm");
                profile.put("dominantEmotion", dominantEmotion);

                long recentDays = Duration.between(
                        diaries.stream()
                                .filter(d -> d.getCreatedAt() != null)
                                .map(Diary::getCreatedAt)
                                .min(LocalDateTime::compareTo)
                                .orElse(LocalDateTime.now()),
                        LocalDateTime.now()
                ).toDays();
                profile.put("lastActivityDaysAgo", recentDays);
            } else {
                profile.put("activeDays", 0);
                profile.put("avgEmotionScore", 0.5);
                profile.put("dominantEmotion", "unknown");
                profile.put("lastActivityDaysAgo", -1);
            }

            profile.put("silent", detectSilentPattern(userId, DEFAULT_SILENT_DAYS));
            profile.put("moodDecline", detectMoodDeclinePattern(userId));

        } catch (Exception e) {
            log.error("获取用户行为画像失败: userId={}, error={}", userId, e.getMessage());
        }

        return profile;
    }

    @Override
    public List<Map<String, Object>> getAnomalyWarnings(Long userId) {
        List<Map<String, Object>> warnings = new ArrayList<>();

        if (detectSilentPattern(userId, DEFAULT_SILENT_DAYS)) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("type", "silent");
            warning.put("level", 2);
            warning.put("message", "用户已超过" + DEFAULT_SILENT_DAYS + "天未发布日记");
            warning.put("recommendation", "建议通过短信或邮件关怀用户");
            warnings.add(warning);
        }

        if (detectMoodDeclinePattern(userId)) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("type", "mood_decline");
            warning.put("level", 2);
            warning.put("message", "检测到用户情绪呈下降趋势");
            warning.put("recommendation", "建议关注用户心理状态，适时提供支持");
            warnings.add(warning);
        }

        if (detectActivitySpike(userId)) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("type", "activity_spike");
            warning.put("level", 1);
            warning.put("message", "用户活动频率异常增加");
            warning.put("recommendation", "持续观察，可能是情绪波动的表现");
            warnings.add(warning);
        }

        return warnings;
    }

    @Override
    public void recordActivity(Long userId, String activityType) {
        String key = ACTIVITY_KEY_PREFIX + userId + ":" + activityType;
        try {
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, Duration.ofDays(30));
        } catch (Exception e) {
            log.error("记录用户活动失败: userId={}, type={}, error={}", userId, activityType, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getRecentTrend(Long userId, int days) {
        Map<String, Object> trend = new HashMap<>();

        try {
            List<Diary> diaries = diaryService.getRecentByUser(userId, days);

            if (diaries == null || diaries.isEmpty()) {
                trend.put("dataPoints", 0);
                trend.put("trend", "no_data");
                return trend;
            }

            List<Map<String, Object>> dataPoints = diaries.stream()
                    .filter(d -> d.getCreatedAt() != null)
                    .sorted(Comparator.comparing(Diary::getCreatedAt))
                    .map(d -> {
                        Map<String, Object> point = new HashMap<>();
                        point.put("date", d.getCreatedAt().toLocalDate().toString());
                        point.put("score", getScore(d));
                        point.put("emotion", d.getEmotionCategory());
                        return point;
                    })
                    .collect(Collectors.toList());

            trend.put("dataPoints", dataPoints.size());
            trend.put("data", dataPoints);
            trend.put("avgScore", calculateAvgScore(diaries));

            if (dataPoints.size() >= 2) {
                double[] scores = dataPoints.stream()
                        .mapToDouble(p -> ((Number) p.get("score")).doubleValue())
                        .toArray();

                int mid = scores.length / 2;
                double firstHalf = Arrays.stream(Arrays.copyOfRange(scores, 0, mid == 0 ? 1 : mid))
                        .average().orElse(0.5);
                double secondHalf = Arrays.stream(Arrays.copyOfRange(scores, mid, scores.length))
                        .average().orElse(0.5);

                if (secondHalf - firstHalf > 0.1) {
                    trend.put("trend", "improving");
                } else if (firstHalf - secondHalf > 0.1) {
                    trend.put("trend", "declining");
                } else {
                    trend.put("trend", "stable");
                }
            } else {
                trend.put("trend", "insufficient_data");
            }

        } catch (Exception e) {
            log.error("获取近期趋势失败: userId={}, error={}", userId, e.getMessage());
            trend.put("error", e.getMessage());
        }

        return trend;
    }

    @Override
    public boolean shouldTriggerWarning(Long userId) {
        Map<String, Object> profile = getUserBehaviorProfile(userId);

        boolean silent = Boolean.TRUE.equals(profile.get("silent"));
        boolean moodDecline = Boolean.TRUE.equals(profile.get("moodDecline"));

        return silent || moodDecline;
    }

    private double calculateAvgScore(List<Diary> diaries) {
        if (diaries == null || diaries.isEmpty()) {
            return 0.5;
        }

        double sum = diaries.stream()
                .filter(d -> d.getEmotionScore() != null)
                .mapToDouble(d -> d.getEmotionScore().doubleValue())
                .sum();

        long withScore = diaries.stream()
                .filter(d -> d.getEmotionScore() != null)
                .count();

        if (withScore > 0) {
            return sum / withScore;
        }

        Map<String, Long> emotionCount = diaries.stream()
                .filter(d -> d.getEmotionCategory() != null)
                .collect(Collectors.groupingBy(Diary::getEmotionCategory, Collectors.counting()));

        if (emotionCount.isEmpty()) {
            return 0.5;
        }

        double total = 0;
        for (Map.Entry<String, Long> entry : emotionCount.entrySet()) {
            total += getScoreFromCategory(entry.getKey()) * entry.getValue();
        }
        return total / diaries.size();
    }

    private double getScore(Diary diary) {
        if (diary.getEmotionScore() != null) {
            return diary.getEmotionScore().doubleValue();
        }
        return getScoreFromCategory(diary.getEmotionCategory());
    }

    private double getScoreFromCategory(String category) {
        if (category == null) return 0.5;
        return switch (category.toLowerCase()) {
            case "happy" -> 0.9;
            case "calm", "excited" -> 0.75;
            case "anxious" -> 0.4;
            case "sad" -> 0.3;
            case "angry", "fear" -> 0.2;
            default -> 0.5;
        };
    }

    private String buildBehaviorWarningContent(Map<String, Object> analysis) {
        StringBuilder content = new StringBuilder("[行为异常检测]");

        if (Boolean.TRUE.equals(analysis.get("silentPattern"))) {
            content.append("检测到沉默模式（长期未活跃）");
        }

        if (Boolean.TRUE.equals(analysis.get("moodDecline"))) {
            if (content.length() > 0) content.append("；");
            content.append("检测到情绪持续下降");
        }

        return content.toString();
    }
}

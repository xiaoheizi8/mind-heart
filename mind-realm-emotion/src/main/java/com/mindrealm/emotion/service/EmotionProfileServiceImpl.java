package com.mindrealm.emotion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: EmotionProfileServiceImpl
 * @description: 情感画像服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class EmotionProfileServiceImpl implements IEmotionProfileService {

    private static final Logger logger = LoggerFactory.getLogger(EmotionProfileServiceImpl.class);

    /**
     * 获取情感画像
     * 综合分析用户日记数据,生成情感画像报告
     * @param diaries 日记数据列表
     * @param days 分析天数
     * @return 情感画像数据(Map)
     */
    @Override
    public Map<String, Object> getProfile(List<IEmotionProfileService.DiaryData> diaries, int days) {
        if (diaries == null || diaries.isEmpty()) {
            return buildEmptyProfile(days);
        }

        Map<String, Object> profile = new HashMap<>();
        
        profile.put("totalDiaries", diaries.size());
        profile.put("analysisPeriod", days + "天");
        profile.put("lastUpdate", LocalDateTime.now());
        
        profile.put("emotionDistribution", calculateEmotionDistribution(diaries));
        profile.put("emotionTrend", calculateEmotionTrend(diaries));
        profile.put("avgEmotionScore", calculateAverageScore(diaries));
        profile.put("stability", calculateStability(diaries));
        profile.put("dominantEmotion", getDominantEmotion(diaries));
        profile.put("stressTriggers", identifyStressTriggers(diaries));
        profile.put("riskSignals", analyzeRiskSignals(diaries));
        profile.put("suggestions", generateSuggestions(diaries));

        return profile;
    }

    private Map<String, Object> calculateEmotionDistribution(List<DiaryData> diaries) {
        Map<String, Integer> distribution = new HashMap<>();
        Map<String, Double> percentages = new HashMap<>();
        
        for (DiaryData diary : diaries) {
            String category = diary.getEmotionCategory();
            if (category != null) {
                distribution.put(category, distribution.getOrDefault(category, 0) + 1);
            }
        }
        
        int total = diaries.size();
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            percentages.put(entry.getKey(), (entry.getValue() * 100.0) / total);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("counts", distribution);
        result.put("percentages", percentages);
        return result;
    }

    private List<Map<String, Object>> calculateEmotionTrend(List<DiaryData> diaries) {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        Map<String, List<DiaryData>> byDate = diaries.stream()
                .collect(Collectors.groupingBy(d -> d.getCreatedAt().toLocalDate().toString()));
        
        List<String> sortedDates = new ArrayList<>(byDate.keySet());
        Collections.sort(sortedDates);
        
        for (String date : sortedDates) {
            List<DiaryData> dayDiaries = byDate.get(date);
            double avgScore = dayDiaries.stream()
                    .filter(d -> d.getEmotionScore() != null)
                    .mapToDouble(d -> d.getEmotionScore().doubleValue())
                    .average()
                    .orElse(0.0);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date);
            dayData.put("avgScore", avgScore);
            dayData.put("count", dayDiaries.size());
            
            String dominant = dayDiaries.stream()
                    .filter(d -> d.getEmotionCategory() != null)
                    .collect(Collectors.groupingBy(DiaryData::getEmotionCategory, Collectors.counting()))
                    .entrySet().stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("calm");
            dayData.put("dominantEmotion", dominant);
            
            trend.add(dayData);
        }
        
        return trend;
    }

    private double calculateAverageScore(List<DiaryData> diaries) {
        return diaries.stream()
                .filter(d -> d.getEmotionScore() != null)
                .mapToDouble(d -> d.getEmotionScore().doubleValue())
                .average()
                .orElse(0.0);
    }

    private Map<String, Object> calculateStability(List<DiaryData> diaries) {
        List<Double> scores = diaries.stream()
                .filter(d -> d.getEmotionScore() != null)
                .map(d -> d.getEmotionScore().doubleValue())
                .collect(Collectors.toList());
        
        if (scores.size() < 2) {
            Map<String, Object> result = new HashMap<>();
            result.put("level", "insufficient_data");
            result.put("description", "数据不足,无法评估稳定性");
            return result;
        }
        
        double mean = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = scores.stream()
                .mapToDouble(s -> Math.pow(s - mean, 2))
                .average()
                .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        String level, description;
        if (stdDev < 0.2) {
            level = "stable";
            description = "情绪稳定";
        } else if (stdDev < 0.4) {
            level = "moderate";
            description = "情绪波动适中";
        } else {
            level = "unstable";
            description = "情绪波动较大";
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("level", level);
        result.put("description", description);
        result.put("stdDeviation", Math.round(stdDev * 100.0) / 100.0);
        return result;
    }

    private Map<String, Object> getDominantEmotion(List<DiaryData> diaries) {
        Map<String, Long> counts = diaries.stream()
                .filter(d -> d.getEmotionCategory() != null)
                .collect(Collectors.groupingBy(DiaryData::getEmotionCategory, Collectors.counting()));
        
        if (counts.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("category", "calm");
            result.put("percentage", 100);
            return result;
        }
        
        String dominant = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("calm");
        
        long maxCount = counts.get(dominant);
        double percentage = (maxCount * 100.0) / diaries.size();
        
        Map<String, Object> result = new HashMap<>();
        result.put("category", dominant);
        result.put("percentage", Math.round(percentage * 10.0) / 10.0);
        result.put("count", maxCount);
        return result;
    }

    private List<Map<String, Object>> identifyStressTriggers(List<DiaryData> diaries) {
        List<Map<String, Object>> triggers = new ArrayList<>();
        
        List<String> negativeKeywords = Arrays.asList("压力", "焦虑", "烦恼", "疲惫", "郁闷", "难受", "伤心");
        
        Map<String, Integer> keywordCount = new HashMap<>();
        for (DiaryData diary : diaries) {
            if (diary.getEmotionCategory() != null && 
                (diary.getEmotionCategory().equals("sad") || diary.getEmotionCategory().equals("anxious"))) {
                String content = diary.getContent();
                for (String keyword : negativeKeywords) {
                    if (content != null && content.contains(keyword)) {
                        keywordCount.put(keyword, keywordCount.getOrDefault(keyword, 0) + 1);
                    }
                }
            }
        }
        
        for (Map.Entry<String, Integer> entry : keywordCount.entrySet()) {
            Map<String, Object> trigger = new HashMap<>();
            trigger.put("type", entry.getKey());
            trigger.put("frequency", entry.getValue());
            triggers.add(trigger);
        }
        
        triggers.sort((a, b) -> Integer.compare((Integer) b.get("frequency"), (Integer) a.get("frequency")));
        
        return triggers.stream().limit(5).collect(Collectors.toList());
    }

    private List<Map<String, Object>> analyzeRiskSignals(List<DiaryData> diaries) {
        List<Map<String, Object>> signals = new ArrayList<>();
        
        List<String> riskKeywords = Arrays.asList("自杀", "自伤", "绝望", "活着没意思", "不想活");
        
        for (DiaryData diary : diaries) {
            String content = diary.getContent();
            if (content != null) {
                for (String keyword : riskKeywords) {
                    if (content.contains(keyword)) {
                        Map<String, Object> signal = new HashMap<>();
                        signal.put("keyword", keyword);
                        signal.put("date", diary.getCreatedAt());
                        signal.put("severity", "high");
                        signals.add(signal);
                    }
                }
            }
        }
        
        List<DiaryData> recentDiaries = diaries.stream()
                .filter(d -> d.getEmotionScore() != null && d.getEmotionScore().doubleValue() < -0.5)
                .collect(Collectors.toList());
        
        if (recentDiaries.size() >= 5) {
            Map<String, Object> signal = new HashMap<>();
            signal.put("type", "persistent_low_mood");
            signal.put("count", recentDiaries.size());
            signal.put("severity", "high");
            signals.add(signal);
        }
        
        return signals;
    }

    private List<String> generateSuggestions(List<DiaryData> diaries) {
        List<String> suggestions = new ArrayList<>();
        
        double avgScore = calculateAverageScore(diaries);
        
        if (avgScore < -0.5) {
            suggestions.add("近期情绪偏低,建议寻求专业心理咨询帮助");
        } else if (avgScore < 0) {
            suggestions.add("情绪有些低落,可以尝试运动或冥想来调节");
        }
        
        long sleepIssues = diaries.stream()
                .filter(d -> d.getContent() != null && d.getContent().contains("失眠"))
                .count();
        
        if (sleepIssues > 3) {
            suggestions.add("多次提到睡眠问题,建议关注睡眠质量");
        }
        
        long stressCount = diaries.stream()
                .filter(d -> d.getContent() != null && d.getContent().contains("压力"))
                .count();
        
        if (stressCount > 5) {
            suggestions.add("近期压力较大,建议适当放松,合理安排时间");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("情绪状态良好,继续保持!");
        }
        
        return suggestions;
    }

    private Map<String, Object> buildEmptyProfile(int days) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("totalDiaries", 0);
        profile.put("analysisPeriod", days + "天");
        profile.put("message", "暂无足够数据进行分析");
        return profile;
    }

    @Override
    public String getQuickStatus(List<IEmotionProfileService.DiaryData> diaries) {
        if (diaries == null || diaries.isEmpty()) {
            return "no_data";
        }
        
        double avgScore = diaries.stream()
                .filter(d -> d.getEmotionScore() != null)
                .mapToDouble(d -> d.getEmotionScore().doubleValue())
                .average()
                .orElse(0.0);
        
        if (avgScore > 0.3) {
            return "happy";
        } else if (avgScore < -0.3) {
            return "sad";
        } else {
            return "calm";
        }
    }
}

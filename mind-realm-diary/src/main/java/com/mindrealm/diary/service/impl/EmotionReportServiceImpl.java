package com.mindrealm.diary.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.entity.EmotionReport;
import com.mindrealm.diary.mapper.EmotionReportMapper;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.diary.service.EmotionReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: EmotionReportServiceImpl
 * @description: 情绪报告服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Service
public class EmotionReportServiceImpl implements EmotionReportService {

    private static final Logger logger = LoggerFactory.getLogger(EmotionReportServiceImpl.class);

    @Autowired
    private EmotionReportMapper reportMapper;

    @Autowired
    private DiaryService diaryService;

    @Override
    public EmotionReport generateAndSaveReport(Long userId, String reportType) {
        // 计算时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        int days;

        switch (reportType) {
            case "week":
                startDate = endDate.minusDays(6);
                days = 7;
                break;
            case "month":
                startDate = endDate.minusDays(29);
                days = 30;
                break;
            case "quarter":
                startDate = endDate.minusDays(89);
                days = 90;
                break;
            default:
                throw new IllegalArgumentException("不支持的报告类型: " + reportType);
        }

        // 检查是否已存在该时间段的报告
        EmotionReport existingReport = getLatestReportInPeriod(userId, startDate, endDate, reportType);
        if (existingReport != null) {
            logger.info("用户在{}-{}的{}报告已存在,直接返回", startDate, endDate, reportType);
            return existingReport;
        }

        // 获取日记数据
        List<Diary> diaries = diaryService.getRecentByUser(userId, days);

        // 构建报告数据
        EmotionReport report = buildReport(userId, reportType, startDate, endDate, diaries);

        // 保存到数据库
        reportMapper.insert(report);
        logger.info("用户{}的{}报告已生成并保存,ID={}", userId, reportType, report.getId());

        return report;
    }

    @Override
    public List<EmotionReport> getUserReports(Long userId) {
        LambdaQueryWrapper<EmotionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmotionReport::getUserId, userId)
                   .orderByDesc(EmotionReport::getCreatedAt);
        return reportMapper.selectList(queryWrapper);
    }

    @Override
    public List<EmotionReport> getUserReportsByType(Long userId, String reportType) {
        LambdaQueryWrapper<EmotionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmotionReport::getUserId, userId)
                   .eq(EmotionReport::getReportType, reportType)
                   .orderByDesc(EmotionReport::getCreatedAt);
        return reportMapper.selectList(queryWrapper);
    }

    @Override
    public EmotionReport getReportById(Long reportId) {
        return reportMapper.selectById(reportId);
    }

    @Override
    public EmotionReport getLatestReportInPeriod(Long userId, LocalDate startDate, LocalDate endDate, String reportType) {
        LambdaQueryWrapper<EmotionReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmotionReport::getUserId, userId)
                   .eq(EmotionReport::getReportType, reportType)
                   .ge(EmotionReport::getPeriodStart, startDate)
                   .le(EmotionReport::getPeriodEnd, endDate)
                   .orderByDesc(EmotionReport::getCreatedAt)
                   .last("LIMIT 1");
        return reportMapper.selectOne(queryWrapper);
    }

    /**
     * 构建报告对象
     */
    private EmotionReport buildReport(Long userId, String reportType, LocalDate startDate, 
                                      LocalDate endDate, List<Diary> diaries) {
        // 统计情绪数据
        Map<String, Integer> emotionStats = new HashMap<>();
        double totalScore = 0;
        int count = 0;

        for (Diary diary : diaries) {
            String category = diary.getEmotionCategory();
            if (category != null) {
                emotionStats.put(category, emotionStats.getOrDefault(category, 0) + 1);
            }
            if (diary.getEmotionScore() != null) {
                totalScore += diary.getEmotionScore().doubleValue();
                count++;
            }
        }

        double avgScore = count > 0 ? totalScore / count : 0;
        String dominantEmotion = emotionStats.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("unknown");

        // 情绪分布
        Map<String, Double> emotionDistribution = new HashMap<>();
        int totalDiaries = diaries.size();
        for (Map.Entry<String, Integer> entry : emotionStats.entrySet()) {
            emotionDistribution.put(entry.getKey(), 
                totalDiaries > 0 ? (entry.getValue() * 100.0 / totalDiaries) : 0);
        }

        // 趋势分析
        String trendAnalysis = analyzeTrend(diaries);

        // AI分析
        String aiAnalysis = generateAiAnalysis(avgScore, dominantEmotion, diaries.size(), emotionStats);

        // 构建报告
        return EmotionReport.builder()
            .userId(userId)
            .reportType(reportType)
            .periodStart(startDate)
            .periodEnd(endDate)
            .summary(String.format("%d天内记录了%d篇日记,主要情绪为'%s',平均情绪得分%.1f", 
                diaries.size() > 0 ? ChronoUnit.DAYS.between(startDate, endDate) + 1 : 0,
                totalDiaries, dominantEmotion, avgScore))
            .emotionStats(JSON.toJSONString(emotionStats))
            .emotionDistribution(JSON.toJSONString(emotionDistribution))
            .trendAnalysis(trendAnalysis)
            .aiAnalysis(aiAnalysis)
            .diaryCount(totalDiaries)
            .avgEmotionScore(BigDecimal.valueOf(avgScore))
            .mainEmotion(dominantEmotion)
            .build();
    }

    /**
     * 分析情绪趋势
     */
    private String analyzeTrend(List<Diary> diaries) {
        if (diaries.size() < 4) {
            return "数据不足,无法分析趋势。继续记录日记,积累更多数据吧!";
        }

        int mid = diaries.size() / 2;
        double firstHalf = diaries.subList(0, mid).stream()
            .filter(d -> d.getEmotionScore() != null)
            .mapToDouble(d -> d.getEmotionScore().doubleValue())
            .average().orElse(50);
        double secondHalf = diaries.subList(mid, diaries.size()).stream()
            .filter(d -> d.getEmotionScore() != null)
            .mapToDouble(d -> d.getEmotionScore().doubleValue())
            .average().orElse(50);

        double diff = secondHalf - firstHalf;
        if (diff > 10) {
            return "你的情绪正在逐渐好转,继续保持积极的心态!最近的情绪状态比之前有明显改善。";
        } else if (diff < -10) {
            return "最近情绪有所下降,建议多关注自己的感受,适当放松。记住,寻求帮助是勇敢的表现。";
        } else {
            return "情绪整体平稳,偶尔的波动是正常的。你已经学会了与情绪和谐相处。";
        }
    }

    /**
     * 生成AI分析报告
     */
    private String generateAiAnalysis(double avgScore, String dominantEmotion, int diaryCount, 
                                      Map<String, Integer> emotionStats) {
        StringBuilder analysis = new StringBuilder();

        // 总体评价
        if (avgScore >= 70) {
            analysis.append(String.format("这段时间你的情绪状态很不错呢!平均情绪分%.1f分,主要情绪是'%s'。", 
                avgScore, dominantEmotion));
            analysis.append("大部分时间都保持着积极乐观的心态,继续保持哦~\n\n");
        } else if (avgScore >= 50) {
            analysis.append(String.format("这段时间你的情绪整体平稳,平均情绪分%.1f分。", avgScore));
            analysis.append("偶尔有些波动是很正常的,学会与情绪共处本身就是一种成长。\n\n");
        } else {
            analysis.append(String.format("这段时间似乎有些艰难,平均情绪分%.1f分。", avgScore));
            analysis.append("但我看到了你愿意面对的勇气。每一段低谷都是成长的开始,我会一直陪伴着你。\n\n");
        }

        // 情绪分布说明
        if (!emotionStats.isEmpty()) {
            analysis.append("【情绪分布】\n");
            emotionStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> {
                    analysis.append(String.format("• %s: %d次\n", entry.getKey(), entry.getValue()));
                });
            analysis.append("\n");
        }

        // 建议
        analysis.append("【建议】\n");
        if (emotionStats.containsKey("anxious")) {
            analysis.append("• 焦虑时深呼吸,告诉自己:此刻的我,已经很棒了\n");
        }
        if (emotionStats.containsKey("sad")) {
            analysis.append("• 难过时允许自己脆弱,这是人之常情\n");
        }
        if (emotionStats.containsKey("angry")) {
            analysis.append("• 生气时可以暂时离开现场,给自己冷静的空间\n");
        }
        if (emotionStats.containsKey("happy") || emotionStats.containsKey("calm")) {
            analysis.append("• 记得保持这份平和,记录下让你感到平静的时刻\n");
        }
        if (analysis.toString().equals("【建议】\n")) {
            analysis.append("• 坚持记录日记,了解自己的情绪模式\n");
            analysis.append("• 每天给自己一些安静的时间很重要\n");
        }

        return analysis.toString();
    }
}

package com.mindrealm.admin.controller;

import com.mindrealm.common.result.Result;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.entity.EmotionReport;
import com.mindrealm.api.service.DiaryReportExportService;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.diary.service.EmotionReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: AdminReportController
 * @description: 管理端报告控制器,提供用户情绪报告导出功能
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@RestController
@RequestMapping("/admin/v1/reports")
public class AdminReportController {

    private static final Logger logger = LoggerFactory.getLogger(AdminReportController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private EmotionReportService reportService;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryReportExportService reportExportService;

    /**
     * 导出用户情绪报告PDF
     * @param userId 用户ID
     * @param reportType 报告类型: week/month/quarter
     * @param days 天数(可选,如果不指定则根据reportType自动计算)
     * @return PDF文件
     */
    @GetMapping("/export/{userId}")
    public ResponseEntity<byte[]> exportUserReport(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "week") String reportType,
            @RequestParam(required = false) Integer days) {
        
        // 计算天数
        int exportDays;
        if (days != null) {
            exportDays = days;
        } else {
            switch (reportType) {
                case "week":
                    exportDays = 7;
                    break;
                case "month":
                    exportDays = 30;
                    break;
                case "quarter":
                    exportDays = 90;
                    break;
                default:
                    exportDays = 7;
            }
        }

        // 获取用户日记
        List<Diary> diaries = diaryService.getRecentByUser(userId, exportDays);
        
        if (diaries.isEmpty()) {
            logger.warn("用户{}在指定时间段内没有日记记录", userId);
            return ResponseEntity.badRequest().build();
        }

        // 构建报告数据
        Map<String, Object> reportData = buildReportData(diaries, exportDays);
        String period = exportDays + "天情绪报告";
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            reportExportService.exportToPdf(diaries, reportData, period, outputStream);
            
            byte[] pdfBytes = outputStream.toByteArray();
            String filename = URLEncoder.encode("用户" + userId + "_" + period + ".pdf", 
                StandardCharsets.UTF_8).replace("+", "%20");
            
            logger.info("管理端导出用户{}的情绪报告成功,类型:{},天数:{},PDF大小:{}KB", 
                userId, reportType, exportDays, pdfBytes.length / 1024);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
                
        } catch (Exception e) {
            logger.error("导出用户{}的情绪报告失败: {}", userId, e.getMessage(), e);
            // 返回详细错误信息
            return ResponseEntity.internalServerError()
                .body(("导出失败: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 生成并获取用户的报告数据(不导出PDF,只返回JSON)
     * @param userId 用户ID
     * @param reportType 报告类型: week/month/quarter
     * @return 报告数据
     */
    @PostMapping("/generate/{userId}")
    public Result<EmotionReport> generateUserReport(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "week") String reportType) {
        
        try {
            EmotionReport report = reportService.generateAndSaveReport(userId, reportType);
            return Result.success(report);
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 查询用户的历史报告
     * @param userId 用户ID
     * @param reportType 可选,报告类型筛选
     * @return 报告列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<EmotionReport>> getUserReports(
            @PathVariable Long userId,
            @RequestParam(required = false) String reportType) {
        
        List<EmotionReport> reports;
        if (reportType != null && !reportType.isEmpty()) {
            reports = reportService.getUserReportsByType(userId, reportType);
        } else {
            reports = reportService.getUserReports(userId);
        }
        
        return Result.success(reports);
    }

    /**
     * 构建报告数据(复用DiaryController的逻辑)
     */
    private Map<String, Object> buildReportData(List<Diary> diaries, int days) {
        Map<String, Object> reportData = new HashMap<>();
        
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
            .orElse("--");
        
        reportData.put("avgScore", avgScore);
        reportData.put("dominantEmotion", dominantEmotion);
        reportData.put("emotionStats", emotionStats);
        
        // 趋势分析
        String trendType = "stable";
        String description = "情绪整体平稳";
        if (diaries.size() >= 4) {
            int mid = diaries.size() / 2;
            double firstHalf = diaries.subList(0, mid).stream()
                .filter(d -> d.getEmotionScore() != null)
                .mapToDouble(d -> d.getEmotionScore().doubleValue())
                .average().orElse(50);
            double secondHalf = diaries.subList(mid, diaries.size()).stream()
                .filter(d -> d.getEmotionScore() != null)
                .mapToDouble(d -> d.getEmotionScore().doubleValue())
                .average().orElse(50);
            
            if (secondHalf - firstHalf > 10) {
                trendType = "improving";
                description = "你的情绪正在逐渐好转,继续保持积极的心态!";
            } else if (firstHalf - secondHalf > 10) {
                trendType = "declining";
                description = "最近情绪有所下降,建议多关注自己的感受,适当放松。";
            }
        }
        
        Map<String, Object> trendAnalysis = new HashMap<>();
        trendAnalysis.put("trendType", trendType);
        trendAnalysis.put("description", description);
        reportData.put("trendAnalysis", trendAnalysis);
        
        // AI分析
        reportData.put("aiReport", generateAiReport(avgScore, dominantEmotion, days));
        reportData.put("suggestions", generateSuggestions(emotionStats));
        
        return reportData;
    }
    
    private String generateAiReport(double avgScore, String dominantEmotion, int days) {
        if (avgScore >= 70) {
            return String.format("这%d天你的情绪状态很不错呢！平均情绪分%.1f分,主要情绪是'%s'。大部分时间都保持着积极乐观的心态,继续保持哦~", days, avgScore, dominantEmotion);
        } else if (avgScore >= 50) {
            return String.format("这%d天你的情绪整体平稳,平均情绪分%.1f分。偶尔有些波动是很正常的,学会与情绪共处本身就是一种成长。主要情绪是'%s'。", days, avgScore, dominantEmotion);
        } else {
            return String.format("这%d天似乎有些艰难,平均情绪分%.1f分。但我看到了你愿意面对的勇气。每一段低谷都是成长的开始,我会一直陪伴着你。", days, avgScore);
        }
    }
    
    private List<String> generateSuggestions(Map<String, Integer> emotionStats) {
        List<String> suggestions = new java.util.ArrayList<>();
        
        if (emotionStats.containsKey("anxious")) {
            suggestions.add("焦虑时深呼吸,告诉自己:此刻的我,已经很棒了");
        }
        if (emotionStats.containsKey("sad")) {
            suggestions.add("难过时允许自己脆弱,这是人之常情");
        }
        if (emotionStats.containsKey("angry")) {
            suggestions.add("生气时可以暂时离开现场,给自己冷静的空间");
        }
        if (emotionStats.containsKey("happy") || emotionStats.containsKey("calm")) {
            suggestions.add("记得保持这份平和,记录下让你感到平静的时刻");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("坚持记录日记,了解自己的情绪模式");
            suggestions.add("每天给自己一些安静的时间很重要");
        }
        
        return suggestions.subList(0, Math.min(3, suggestions.size()));
    }
}

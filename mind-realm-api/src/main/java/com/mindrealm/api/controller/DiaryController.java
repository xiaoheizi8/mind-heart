package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.entity.EmotionReport;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.diary.service.EmotionReportService;
import com.mindrealm.warning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: DiaryController
 * @description: 日记控制器,处理日记的创建、查询、删除等请求
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestController
@RequestMapping("/api/v1/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;
    
    @Autowired
    private WarningService warningService;
    
    @Autowired
    private EmotionReportService reportService;

    /**
     * 创建日记
     * 创建时会自动进行情感分析,并触发风险预警检测
     * @param diary 日记内容(content, mediaUrls等)
     * @return 创建结果(包含日记ID)
     */
    @PostMapping("/create")
    public Result<Diary> create(@RequestBody Diary diary) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        if (!StringUtils.hasText(diary.getContent()) && !StringUtils.hasText(diary.getMediaUrls())) {
            return Result.badRequest("日记内容或媒体不能为空");
        }
        
        diary.setUserId(userId);

        Diary created = diaryService.create(diary);
        
        // 触发风险预警检测
        if (StringUtils.hasText(diary.getContent())) {
            warningService.analyzeRisk(userId, diary.getContent());
        }
        
        return Result.success(created);
    }

    /**
     * 更新日记
     * @param id 日记ID
     * @param diary 更新的内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Diary> update(@PathVariable Long id, @RequestBody Diary diary) {
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        Diary existing = diaryService.getById(id);
        if (existing == null) {
            return Result.notFound("日记不存在");
        }
        
        if (!existing.getUserId().equals(currentUserId)) {
            return Result.forbidden("无权限修改他人的日记");
        }
        
        // 更新内容
        if (StringUtils.hasText(diary.getContent())) {
            existing.setContent(diary.getContent());
        }
        if (diary.getMediaUrls() != null) {
            existing.setMediaUrls(diary.getMediaUrls());
        }
        
        // 重新分析情感
        if (StringUtils.hasText(diary.getContent())) {
            var emotion = diaryService.analyzeEmotion(diary.getContent());
            existing.setEmotionScore(new java.math.BigDecimal(emotion.getScore()));
            existing.setEmotionCategory(emotion.getCategory());
        }
        
        Diary updated = diaryService.update(existing);
        
        // 触发风险检测
        if (StringUtils.hasText(updated.getContent())) {
            warningService.analyzeRisk(currentUserId, updated.getContent());
        }
        
        return Result.success(updated);
    }

    /**
     * 获取日记详情
     * 只能查看自己的日记
     * @param id 日记ID
     * @return 日记详情
     */
    @GetMapping("/{id}")
    public Result<Diary> getById(@PathVariable Long id) {
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        Diary diary = diaryService.getById(id);
        if (diary == null) {
            return Result.notFound("日记不存在");
        }
        
        // 只能查看自己的日记
        if (!diary.getUserId().equals(currentUserId) && !RequestContext.isAdmin()) {
            return Result.forbidden("无权限查看他人日记");
        }
        
        return Result.success(diary);
    }

    /**
     * 获取日记列表
     * @param emotionCategory 情绪分类筛选
     * @param pageNum 页码(默认1)
     * @param pageSize 每页数量(默认10)
     * @return 分页日记列表
     */
    @GetMapping("/list")
    public Result<PageResult<Diary>> list(
            @RequestParam(required = false) String emotionCategory,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        Page<Diary> page = diaryService.getListByUser(userId, emotionCategory, pageNum, pageSize);
        
        PageResult<Diary> result = PageResult.of(
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize(), 
                page.getTotal()
        );
        
        return Result.success(result);
    }

    /**
     * 删除日记
     * 只有日记所有者可以删除
     * @param id 日记ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        if (diaryService.delete(id, currentUserId)) {
            return Result.ok("删除成功");
        }
        return Result.forbidden("无权限或日记不存在");
    }

    /**
     * 获取情绪报告
     * @param days 天数(默认7)
     * @return 情绪报告数据
     */
    @GetMapping("/report")
    public Result<Map<String, Object>> getEmotionReport(
            @RequestParam(defaultValue = "7") int days) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        List<Diary> diaries = diaryService.getRecentByUser(userId, days);
        
        // 统计情感分布
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
        
        // 计算平均得分
        double avgScore = count > 0 ? totalScore / count : 0;
        
        // 趋势数据
        List<Map<String, Object>> trendData = new java.util.ArrayList<>();
        for (Diary diary : diaries) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", diary.getCreatedAt());
            item.put("score", diary.getEmotionScore());
            item.put("category", diary.getEmotionCategory());
            trendData.add(item);
        }
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalDiaries", diaries.size());
        report.put("avgScore", avgScore);
        report.put("emotionStats", emotionStats);
        report.put("trendData", trendData);
        report.put("period", days + "天");
        
        return Result.success(report);
    }

    /**
     * 获取日记统计
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        List<Diary> recent = diaryService.getRecentByUser(userId, 30);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", recent.size());
        
        // 统计情感分布
        Map<String, Integer> categoryCount = new HashMap<>();
        for (Diary diary : recent) {
            String cat = diary.getEmotionCategory();
            if (cat != null) {
                categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            }
        }
        stats.put("categoryCount", categoryCount);
        
        return Result.success(stats);
    }

    /**
     * 生成情绪报告(周/月/季度)
     * @param reportType 报告类型: week/month/quarter
     * @return 报告数据
     */
    @PostMapping("/report/generate")
    public Result<EmotionReport> generateReport(@RequestParam(defaultValue = "week") String reportType) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        try {
            EmotionReport report = reportService.generateAndSaveReport(userId, reportType);
            return Result.success(report);
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 获取用户的历史报告列表
     * @param reportType 可选,报告类型筛选: week/month/quarter
     * @return 报告列表
     */
    @GetMapping("/report/list")
    public Result<List<EmotionReport>> getReportList(@RequestParam(required = false) String reportType) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        List<EmotionReport> reports;
        if (reportType != null && !reportType.isEmpty()) {
            reports = reportService.getUserReportsByType(userId, reportType);
        } else {
            reports = reportService.getUserReports(userId);
        }
        
        return Result.success(reports);
    }

    /**
     * 获取报告详情
     * @param reportId 报告ID
     * @return 报告详情
     */
    @GetMapping("/report/{reportId}")
    public Result<EmotionReport> getReportDetail(@PathVariable Long reportId) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        EmotionReport report = reportService.getReportById(reportId);
        if (report == null) {
            return Result.notFound("报告不存在");
        }
        
        // 验证是否属于当前用户
        if (!report.getUserId().equals(userId)) {
            return Result.forbidden("无权访问该报告");
        }
        
        return Result.success(report);
    }

    /**
     * 构建报告数据(辅助方法)
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
        
        // 简单趋势分析
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
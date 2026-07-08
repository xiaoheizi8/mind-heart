package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.api.service.ParentChildBindingService;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.ParentChildBinding;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.entity.EmotionReport;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.diary.service.EmotionReportService;
import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.service.WarningService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @className: ParentController
 * @description: 家长端控制器，提供查看绑定孩子数据的API
 *               所有接口均需验证家长-孩子绑定关系
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.8
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parent")
public class ParentController {

    private static final Logger log = LoggerFactory.getLogger(ParentController.class);

    private final ParentChildBindingService bindingService;
    private final WarningService warningService;
    private final DiaryService diaryService;
    private final EmotionReportService reportService;

    // ==================== 绑定管理 ====================

    /**
     * 获取已绑定的孩子列表
     */
    @GetMapping("/children")
    public Result<List<Map<String, Object>>> getChildren() {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();

        List<User> children = bindingService.getBoundChildren(parentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User child : children) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", child.getId());
            info.put("username", child.getUsername());
            info.put("nickname", child.getNickname());
            info.put("avatar", child.getAvatar());
            info.put("age", child.getAge());
            info.put("gender", child.getGender());
            // 获取最新情绪
            List<Diary> recent = diaryService.getRecentByUser(child.getId(), 1);
            if (!recent.isEmpty()) {
                info.put("latestEmotion", recent.get(0).getEmotionCategory());
                info.put("latestEmotionScore", recent.get(0).getEmotionScore());
            }
            // 检查是否有未处理的高风险预警
            Page<WarningRecord> warningPage = warningService.getUserWarnings(child.getId(), 1, 3);
            boolean hasHighRisk = warningPage.getRecords().stream()
                    .anyMatch(w -> w.getRiskLevel() != null && w.getRiskLevel() >= 3 && w.getHandled() == 0);
            info.put("hasHighRiskWarning", hasHighRisk);
            result.add(info);
        }
        return Result.success(result);
    }

    /**
     * 发送绑定请求
     */
    @PostMapping("/bind")
    public Result<Map<String, Object>> sendBindRequest(@RequestBody Map<String, String> body) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();

        String childIdentifier = body.get("childIdentifier");
        if (childIdentifier == null || childIdentifier.isBlank()) {
            return Result.badRequest("请输入孩子的用户名或手机号");
        }

        try {
            ParentChildBinding binding = bindingService.sendBindRequest(parentId, childIdentifier.trim());
            Map<String, Object> info = new HashMap<>();
            info.put("id", binding.getId());
            info.put("status", binding.getStatus());
            return Result.success(info);
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 获取发出的绑定请求列表
     */
    @GetMapping("/bind/requests")
    public Result<List<ParentChildBinding>> getBindRequests() {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();

        return Result.success(bindingService.getPendingRequests(parentId));
    }

    /**
     * 取消/解除绑定
     */
    @DeleteMapping("/bind/{bindingId}")
    public Result<Void> cancelBind(@PathVariable Long bindingId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();

        try {
            bindingService.cancelOrRemoveBinding(bindingId, parentId);
            return Result.ok("操作成功");
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    // ==================== 孩子数据查看 ====================

    /**
     * 获取孩子概览数据
     */
    @GetMapping("/child/{childId}/overview")
    public Result<Map<String, Object>> getChildOverview(@PathVariable Long childId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        Map<String, Object> overview = new HashMap<>();

        // 孩子基本信息
        User child = null;
        // ... 通过 children list 获取 ...
        // 最近日记
        List<Diary> recentDiaries = diaryService.getRecentByUser(childId, 7);
        overview.put("recentDiaries", recentDiaries.subList(0, Math.min(3, recentDiaries.size())));

        // 最近预警
        Page<WarningRecord> warningPage = warningService.getUserWarnings(childId, 1, 5);
        overview.put("recentWarnings", warningPage.getRecords());
        overview.put("warningCount", warningPage.getTotal());

        // 情绪趋势（最近7天）
        List<Map<String, Object>> emotionTrend = new ArrayList<>();
        for (Diary diary : recentDiaries) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", diary.getCreatedAt());
            point.put("score", diary.getEmotionScore());
            point.put("category", diary.getEmotionCategory());
            emotionTrend.add(point);
        }
        overview.put("emotionTrend", emotionTrend);

        // 计算平均情绪分
        double avgScore = recentDiaries.stream()
                .filter(d -> d.getEmotionScore() != null)
                .mapToDouble(d -> d.getEmotionScore().doubleValue())
                .average().orElse(0);
        overview.put("avgEmotionScore", Math.round(avgScore * 100.0) / 100.0);

        return Result.success(overview);
    }

    /**
     * 获取孩子预警列表
     */
    @GetMapping("/child/{childId}/warnings")
    public Result<PageResult<WarningRecord>> getChildWarnings(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        Page<WarningRecord> page = warningService.getUserWarnings(childId, pageNum, pageSize);
        return Result.success(PageResult.of(
                page.getRecords(), page.getCurrent(), page.getSize(), page.getTotal()));
    }

    /**
     * 获取孩子预警详情
     */
    @GetMapping("/child/{childId}/warnings/{warningId}")
    public Result<WarningRecord> getChildWarningDetail(
            @PathVariable Long childId, @PathVariable Long warningId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        // 通过 warning list 查找
        Page<WarningRecord> page = warningService.getUserWarnings(childId, 1, 100);
        return page.getRecords().stream()
                .filter(w -> w.getId().equals(warningId))
                .findFirst()
                .map(Result::success)
                .orElse(Result.notFound("预警记录不存在"));
    }

    /**
     * 获取孩子日记列表
     */
    @GetMapping("/child/{childId}/diaries")
    public Result<PageResult<Diary>> getChildDiaries(
            @PathVariable Long childId,
            @RequestParam(required = false) String emotionCategory,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        Page<Diary> page = diaryService.getListByUser(childId, emotionCategory, pageNum, pageSize);
        return Result.success(PageResult.of(
                page.getRecords(), page.getCurrent(), page.getSize(), page.getTotal()));
    }

    /**
     * 获取孩子日记详情
     */
    @GetMapping("/child/{childId}/diaries/{diaryId}")
    public Result<Diary> getChildDiaryDetail(
            @PathVariable Long childId, @PathVariable Long diaryId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        Diary diary = diaryService.getById(diaryId);
        if (diary == null || !diary.getUserId().equals(childId)) {
            return Result.notFound("日记不存在");
        }
        return Result.success(diary);
    }

    /**
     * 获取孩子情绪报告列表
     */
    @GetMapping("/child/{childId}/reports")
    public Result<List<EmotionReport>> getChildReports(@PathVariable Long childId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        List<EmotionReport> reports = reportService.getUserReports(childId);
        return Result.success(reports);
    }

    /**
     * 获取孩子情绪报告详情
     */
    @GetMapping("/child/{childId}/reports/{reportId}")
    public Result<EmotionReport> getChildReportDetail(
            @PathVariable Long childId, @PathVariable Long reportId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        EmotionReport report = reportService.getReportById(reportId);
        if (report == null || !report.getUserId().equals(childId)) {
            return Result.notFound("报告不存在");
        }
        return Result.success(report);
    }

    /**
     * 获取孩子实时情绪数据
     */
    @GetMapping("/child/{childId}/emotion-report")
    public Result<Map<String, Object>> getChildEmotionReport(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "7") Integer days) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        List<Diary> diaries = diaryService.getRecentByUser(childId, days);

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

        Map<String, Object> report = new HashMap<>();
        report.put("totalDiaries", diaries.size());
        report.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
        report.put("emotionStats", emotionStats);
        report.put("period", days + "天");

        return Result.success(report);
    }

    // ==================== 新增: 报告生成 & 预警处理 & 聚合数据 ====================

    /**
     * 生成孩子的情绪报告
     */
    @PostMapping("/child/{childId}/reports/generate")
    public Result<EmotionReport> generateReport(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "week") String type) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }
        EmotionReport report = reportService.generateAndSaveReport(childId, type);
        return Result.success(report);
    }

    /**
     * 标记预警为已处理
     */
    @PostMapping("/child/{childId}/warnings/{warningId}/handle")
    public Result<Void> handleWarning(
            @PathVariable Long childId, @PathVariable Long warningId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }
        warningService.handleWarning(warningId, parentId, "家长已处理");
        return Result.ok("处理成功");
    }

    /**
     * 孩子情绪趋势数据（日级别，用于折线图）
     */
    @GetMapping("/child/{childId}/emotion-trend")
    public Result<List<Map<String, Object>>> getEmotionTrend(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "7") Integer days) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        List<Diary> diaries = diaryService.getRecentByUser(childId, days);
        List<Map<String, Object>> trend = new ArrayList<>();
        for (Diary diary : diaries) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", diary.getCreatedAt());
            point.put("score", diary.getEmotionScore());
            point.put("category", diary.getEmotionCategory());
            point.put("content", diary.getContent() != null && diary.getContent().length() > 30
                    ? diary.getContent().substring(0, 30) + "..." : diary.getContent());
            trend.add(point);
        }
        return Result.success(trend);
    }

    /**
     * 孩子活动摘要（连续天数、统计等）
     */
    @GetMapping("/child/{childId}/activity-summary")
    public Result<Map<String, Object>> getActivitySummary(@PathVariable Long childId) {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();
        if (!bindingService.isBound(parentId, childId)) {
            return Result.forbidden("未绑定该孩子");
        }

        List<Diary> recent = diaryService.getRecentByUser(childId, 30);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDiaries", recent.size());

        // 计算连续天数
        int streak = 0;
        for (Diary d : recent) {
            if (d.getCreatedAt() != null) { streak++; } else { break; }
        }
        summary.put("streak", streak);
        summary.put("lastDiaryDate", recent.isEmpty() ? null : recent.get(0).getCreatedAt());

        return Result.success(summary);
    }

    /**
     * 家长首页仪表盘（所有孩子聚合数据）
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Long parentId = RequestContext.getCurrentUserId();
        if (parentId == null) return Result.unauthorized();

        List<User> children = bindingService.getBoundChildren(parentId);
        int totalChildren = children.size();
        int alertCount = 0;
        List<Map<String, Object>> childSummaries = new ArrayList<>();

        for (User child : children) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("id", child.getId());
            summary.put("nickname", child.getNickname());
            summary.put("username", child.getUsername());

            List<Diary> diaries = diaryService.getRecentByUser(child.getId(), 7);
            if (!diaries.isEmpty()) {
                summary.put("latestEmotion", diaries.get(0).getEmotionCategory());
                summary.put("latestEmotionScore", diaries.get(0).getEmotionScore());
            }

            Page<WarningRecord> warnings = warningService.getUserWarnings(child.getId(), 1, 3);
            boolean hasAlert = warnings.getRecords().stream()
                    .anyMatch(w -> w.getRiskLevel() != null && w.getRiskLevel() >= 3 && w.getHandled() == 0);
            if (hasAlert) alertCount++;
            summary.put("hasAlert", hasAlert);

            // 最后活跃时间
            if (!diaries.isEmpty()) {
                summary.put("lastDiaryDate", diaries.get(0).getCreatedAt());
            }
            childSummaries.add(summary);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalChildren", totalChildren);
        dashboard.put("alertCount", alertCount);
        dashboard.put("children", childSummaries);
        return Result.success(dashboard);
    }
}

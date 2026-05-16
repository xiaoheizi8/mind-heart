package com.mindrealm.api.controller.admin;

import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.UserService;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.warning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员统计控制器
 */
@RestController
@RequestMapping("/admin/v1/stats")
public class AdminStatsController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private WarningService warningService;

    /**
     * 获取情绪分布统计
     */
    @GetMapping("/emotion-distribution")
    public Result<Map<String, Object>> getEmotionDistribution(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> stats = new HashMap<>();

        // TODO: 实现真实的情绪分布统计
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("happy", 0L);
        distribution.put("sad", 0L);
        distribution.put("anxious", 0L);
        distribution.put("angry", 0L);
        distribution.put("calm", 0L);
        
        stats.put("distribution", distribution);
        stats.put("days", days);
        stats.put("total", 0L);

        return Result.success(stats);
    }

    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userService.count());
        stats.put("totalDiaries", diaryService.count());
        stats.put("highRiskWarnings", warningService.countHighRisk());
        stats.put("unhandledWarnings", warningService.countUnhandled());
        stats.put("activeUsers", userService.count());
        stats.put("newUsersToday", 0);

        return Result.success(stats);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserStats(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userService.count());
        stats.put("newUsers", userService.countNewUsers(days));

        return Result.success(stats);
    }

    /**
     * 获取日记统计信息
     */
    @GetMapping("/diaries")
    public Result<Map<String, Object>> getDiaryStats(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalDiaries", diaryService.count());
        stats.put("newDiaries", diaryService.countRecent(days));

        return Result.success(stats);
    }

    /**
     * 获取预警统计信息
     */
    @GetMapping("/warnings")
    public Result<Map<String, Object>> getWarningStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("highRisk", warningService.countHighRisk());
        stats.put("mediumRisk", warningService.countMediumRisk());
        stats.put("unhandled", warningService.countUnhandled());

        return Result.success(stats);
    }
}

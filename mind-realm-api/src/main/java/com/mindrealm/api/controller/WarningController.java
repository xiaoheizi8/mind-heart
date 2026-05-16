package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: WarningController
 * @description: 预警控制器,处理用户查看预警状态、历史预警记录等请求
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestController
@RequestMapping("/api/v1/warning")
public class WarningController {

    @Autowired
    private WarningService warningService;

    /**
     * 获取预警状态
     * 返回用户的未处理预警数量和高风险预警数量
     * @return 预警状态统计
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Map.of("code", 401, "message", "请先登录");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 查询用户最近的预警记录
        Page<WarningRecord> warnings = warningService.getUserWarnings(userId, 1, 10);
        
        // 统计数量
        int unhandled = 0;
        int highRisk = 0;
        if (warnings.getRecords() != null) {
            for (WarningRecord w : warnings.getRecords()) {
                if (w.getHandled() == 0) {
                    unhandled++;
                }
                if (w.getRiskLevel() != null && w.getRiskLevel() >= 3) {
                    highRisk++;
                }
            }
        }
        
        result.put("code", 200);
        result.put("data", Map.of(
            "unhandledCount", unhandled,
            "highRiskCount", highRisk,
            "hasHighRisk", highRisk > 0
        ));
        return result;
    }

    /**
     * 获取预警历史
     * @param pageNum 页码(默认1)
     * @param pageSize 每页数量(默认10)
     * @return 分页预警列表
     */
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Map.of("code", 401, "message", "请先登录");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        Page<WarningRecord> page = warningService.getUserWarnings(userId, pageNum, pageSize);
        
        result.put("code", 200);
        result.put("data", page);
        return result;
    }
}

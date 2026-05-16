package com.mindrealm.api.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.result.Result;
import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端预警管理控制器
 */
@RestController
@RequestMapping("/admin/v1/warning")
public class AdminWarningController {

    @Autowired
    private WarningService warningService;

    /**
     * 预警列表
     */
    @GetMapping("/list")
    @OperationLog(value = "查询预警列表", type = OperationType.SELECT)
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer riskLevel,
            @RequestParam(required = false) Integer handled) {
        
        Page<WarningRecord> page = warningService.getWarningList(userId, riskLevel, handled, pageNum, pageSize);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", page.getRecords());
        data.put("total", page.getTotal());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        
        return Result.success(data);
    }

    /**
     * 获取高风险预警
     */
    @GetMapping("/high-risk")
    @OperationLog(value = "查询高风险预警", type = OperationType.SELECT)
    public Result<List<WarningRecord>> getHighRisk() {
        List<WarningRecord> warnings = warningService.getUnhandledHighRisk();
        return Result.success(warnings);
    }

    /**
     * 处理预警
     */
    @PostMapping("/handle")
    @OperationLog(value = "处理预警", type = OperationType.UPDATE)
    public Result<Void> handle(@RequestBody Map<String, Object> body) {
        Long warningId = body.get("warningId") != null ? Long.valueOf(body.get("warningId").toString()) : null;
        String note = (String) body.get("note");
        
        if (warningId == null) {
            return Result.badRequest("预警ID不能为空");
        }
        
        Long handlerId = RequestContext.getCurrentUserId();
        boolean success = warningService.handleWarning(warningId, handlerId, note);
        
        if (success) {
            return Result.ok("处理成功");
        } else {
            return Result.error(404, "预警记录不存在");
        }
    }

    /**
     * 预警统计
     */
    @GetMapping("/stats")
    @OperationLog(value = "查询预警统计", type = OperationType.SELECT)
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("highRisk", warningService.countHighRisk());
        stats.put("mediumRisk", warningService.countMediumRisk());
        stats.put("unhandled", warningService.countUnhandled());
        return Result.success(stats);
    }
}

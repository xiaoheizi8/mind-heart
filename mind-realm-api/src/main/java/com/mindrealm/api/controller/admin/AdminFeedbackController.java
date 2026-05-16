package com.mindrealm.api.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.UserFeedback;
import com.mindrealm.common.mapper.UserFeedbackMapper;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端反馈管理控制器
 */
@RestController
@RequestMapping("/admin/v1/feedback")
public class AdminFeedbackController {

    @Autowired
    private UserFeedbackMapper feedbackMapper;

    /**
     * 反馈列表
     */
    @GetMapping("/list")
    @OperationLog(value = "查询反馈列表", type = OperationType.SELECT)
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        
        Page<UserFeedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserFeedback> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(UserFeedback::getStatus, status);
        }
        
        wrapper.orderByDesc(UserFeedback::getCreatedAt);
        
        Page<UserFeedback> result = feedbackMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
    }

    /**
     * 处理反馈
     */
    @PostMapping("/{id}/handle")
    @OperationLog(value = "处理反馈", type = OperationType.UPDATE)
    public Result<Void> handle(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        
        UserFeedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return Result.error(404, "反馈不存在");
        }
        
        feedback.setHandlerNote(reply);
        feedback.setStatus(1);
        feedback.setHandledAt(TimeUtil.now());
        feedback.setHandlerId(RequestContext.getCurrentUserId());
        
        feedbackMapper.updateById(feedback);
        return Result.ok("处理成功");
    }
}

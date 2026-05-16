package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.UserFeedback;
import com.mindrealm.common.mapper.UserFeedbackMapper;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户反馈控制器
 */
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private UserFeedbackMapper feedbackMapper;

    /**
     * 提交反馈
     */
    @PostMapping("")
    public Result<Void> submitFeedback(@RequestBody Map<String, String> request) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        String type = request.get("type");
        String title = request.get("title");
        String content = request.get("content");
        String contact = request.get("contact");

        if (type == null || content == null || content.isEmpty()) {
            return Result.badRequest("反馈类型和内容不能为空");
        }

        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(userId);
        feedback.setType(type);
        feedback.setTitle(title);
        feedback.setContent(content);
        feedback.setContact(contact);
        feedback.setStatus(0);
        feedback.setCreatedAt(TimeUtil.now());

        feedbackMapper.insert(feedback);

        log.info("用户 {} 提交反馈: {}", userId, type);
        return Result.ok("反馈提交成功");
    }

    /**
     * 获取我的反馈列表
     */
    @GetMapping("/list")
    public Result<PageResult<UserFeedback>> myFeedbacks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }

        Page<UserFeedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFeedback::getUserId, userId)
                .orderByDesc(UserFeedback::getCreatedAt);

        Page<UserFeedback> result = feedbackMapper.selectPage(page, wrapper);

        return Result.success(PageResult.of(
                result.getRecords(),
                (int) result.getCurrent(),
                (int) result.getSize(),
                (int) result.getTotal()
        ));
    }

    /**
     * 获取反馈详情
     */
    @GetMapping("/{id}")
    public Result<UserFeedback> getFeedback(@PathVariable Long id) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        UserFeedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return Result.notFound("反馈不存在");
        }

        if (!feedback.getUserId().equals(userId)) {
            return Result.forbidden("无权限查看");
        }

        return Result.success(feedback);
    }

    /**
     * 删除反馈
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFeedback(@PathVariable Long id) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        UserFeedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return Result.notFound("反馈不存在");
        }

        if (!feedback.getUserId().equals(userId)) {
            return Result.forbidden("无权限删除");
        }

        feedbackMapper.deleteById(id);
        log.info("用户 {} 删除反馈: {}", userId, id);
        return Result.ok("删除成功");
    }
}
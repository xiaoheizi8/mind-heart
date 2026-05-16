package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.Notification;
import com.mindrealm.common.mapper.NotificationMapper;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: NotificationController
 * @description: 用户通知控制器,处理通知列表查询、未读数量统计、标记已读等请求
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 分页查询用户通知列表(按创建时间降序)
     * @param pageNum 页码,默认1
     * @param pageSize 每页数量,默认10
     * @return 分页通知列表,未登录返回401
     */
    @GetMapping("/list")
    public Result<PageResult<Notification>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        
        var page = notificationMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        var result = PageResult.of(
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                page.getTotal()
        );
        
        return Result.success(result);
    }

    /**
     * 统计用户未读通知数量
     * @return 未读通知数,未登录返回401
     */
    @GetMapping("/unreadCount")
    public Result<Map<String, Object>> unreadCount() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        
        long count = notificationMapper.selectCount(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 标记单条通知为已读状态(需校验通知归属)
     * @param id 通知ID
     * @return 操作结果,通知不存在或无权访问返回404
     */
    @PostMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return Result.notFound("通知不存在");
        }
        
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
        
        return Result.ok("标记已读");
    }

    /**
     * 批量标记用户所有未读通知为已读状态
     * @return 操作结果,未登录返回401
     */
    @PostMapping("/readAll")
    public Result<Void> markAllRead() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var notification = new Notification();
        notification.setIsRead(1);
        
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        
        notificationMapper.update(notification, wrapper);
        
        return Result.ok("全部已读");
    }
}
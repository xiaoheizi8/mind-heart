package com.mindrealm.admin.controller;

import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.story.model.dto.AuditStoryRequest;
import com.mindrealm.story.model.vo.AdminStoryListVO;
import com.mindrealm.story.model.vo.AdminStoryVO;
import com.mindrealm.story.service.HeartStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @className: AdminStoryController
 * @description: 管理端故事审核控制器
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/story")
public class AdminStoryController {

    private final HeartStoryService heartStoryService;

    /**
     * 获取待审核故事列表
     */
    @GetMapping("/pending")
    public Result<PageResult<AdminStoryListVO>> getPendingStories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {

        PageResult<AdminStoryListVO> result = heartStoryService.getAdminStoryList(page, size, status);
        return Result.success(result);
    }

    /**
     * 获取故事详情
     */
    @GetMapping("/{storyId}")
    public Result<AdminStoryVO> getStoryDetail(@PathVariable Long storyId) {
        AdminStoryVO story = heartStoryService.getAdminStoryDetail(storyId);

        if (story == null) {
            return Result.error("故事不存在");
        }
        return Result.success(story);
    }

    /**
     * 审核故事
     */
    @PostMapping("/{storyId}/audit")
    public Result<Boolean> auditStory(
            @PathVariable Long storyId,
            @Validated @RequestBody AuditStoryRequest request) {
        
        boolean success = heartStoryService.auditStory(
                storyId,
                request.getAuditorId(),
                request.getApproved(),
                request.getRejectReason()
        );
        
        if (success) {
            return Result.success(true);
        }
        return Result.error("审核失败");
    }

    /**
     * 删除故事（管理员操作）
     */
    @DeleteMapping("/{storyId}")
    public Result<Boolean> deleteStory(@PathVariable Long storyId) {
        // 管理员删除，userId设为0标识管理端操作
        boolean success = heartStoryService.deleteStory(storyId, 0L);
        return Result.success(success);
    }
}
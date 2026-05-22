package com.mindrealm.api.controller;

import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.story.model.dto.AddCommentRequest;
import com.mindrealm.story.model.dto.AuditStoryRequest;
import com.mindrealm.story.model.dto.PublishStoryRequest;
import com.mindrealm.story.model.dto.StoryQueryDTO;
import com.mindrealm.story.model.vo.CommentVO;
import com.mindrealm.story.model.vo.StoryListVO;
import com.mindrealm.story.model.vo.StoryVO;
import com.mindrealm.story.model.vo.WarmReplyTemplateVO;
import com.mindrealm.story.service.HeartStoryService;
import com.mindrealm.story.service.StoryCommentService;
import com.mindrealm.story.service.WarmReplyTemplateService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.story.model.entity.StoryComment;
import com.mindrealm.story.model.entity.WarmReplyTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: HeartStoryController
 * @description: 前台匿名故事接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/story")
public class HeartStoryController {
    
    private final HeartStoryService heartStoryService;
    private final StoryCommentService storyCommentService;
    private final WarmReplyTemplateService warmReplyTemplateService;

    /**
     * 发布匿名故事
     */
    @PostMapping("/publish")
    public Result<Long> publishStory(@Validated @RequestBody PublishStoryRequest request) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        Long storyId = heartStoryService.publishStory(
                userId,
                request.getTitle(),
                request.getContent(),
                request.getEmotionType(),
                request.getTags(),
                request.getIsAnonymous()
        );
        
        if (storyId > 0) {
            return Result.success(storyId);
        }
        return Result.error("发布失败");
    }

    /**
     * 获取故事列表(分页)
     */
    @GetMapping("/list")
    public Result<PageResult<StoryListVO>> listStory(StoryQueryDTO queryDTO) {
        PageResult<StoryListVO> result = heartStoryService.getStoryList(
                queryDTO.getPage(),
                queryDTO.getSize(),
                queryDTO.getEmotionType(),
                queryDTO.getSortBy()
        );
        return Result.success(result);
    }

    /**
     * 获取故事详情
     */
    @GetMapping("/{storyId}")
    public Result<StoryVO> getStoryDetail(@PathVariable Long storyId) {
        Long userId = RequestContext.getCurrentUserId();
        StoryVO story = heartStoryService.getStoryDetail(storyId, userId);
        
        if (story == null) {
            return Result.error("故事不存在");
        }
        return Result.success(story);
    }

    /**
     * 点赞故事
     */
    @PostMapping("/{storyId}/like")
    public Result<Boolean> likeStory(@PathVariable Long storyId) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        boolean success = heartStoryService.likeStory(storyId, userId);
        return Result.success(success);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{storyId}/like")
    public Result<Boolean> unlikeStory(@PathVariable Long storyId) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        boolean success = heartStoryService.unlikeStory(storyId, userId);
        return Result.success(success);
    }

    /**
     * 发表评论
     */
    @PostMapping("/{storyId}/comment")
    public Result<Long> addComment(@PathVariable Long storyId, 
                                   @Validated @RequestBody AddCommentRequest request) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        Long commentId = heartStoryService.addComment(
                storyId,
                userId,
                request.getContent(),
                request.getParentId()
        );
        
        if (commentId > 0) {
            return Result.success(commentId);
        }
        return Result.error("评论失败");
    }

    /**
     * 获取评论列表
     */
    @GetMapping("/{storyId}/comments")
    public Result<PageResult<CommentVO>> getComments(
            @PathVariable Long storyId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Page<StoryComment> commentPage = storyCommentService.getCommentsByStoryId(storyId, page, size);
        
        // 转换为VO
        List<CommentVO> voList = commentPage.getRecords().stream()
                .map(comment -> CommentVO.builder()
                        .id(comment.getId())
                        .storyId(comment.getStoryId())
                        .content(comment.getContent())
                        .likeCount(comment.getLikeCount())
                        .isReply(comment.getParentId() != null)
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        PageResult<CommentVO> result = PageResult.of(
                voList,
                commentPage.getCurrent(),
                commentPage.getSize(),
                commentPage.getTotal()
        );
        
        return Result.success(result);
    }

    /**
     * 收藏故事
     */
    @PostMapping("/{storyId}/favorite")
    public Result<Boolean> favoriteStory(@PathVariable Long storyId) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        boolean success = heartStoryService.favoriteStory(storyId, userId);
        return Result.success(success);
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/favorites")
    public Result<PageResult<StoryListVO>> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        PageResult<StoryListVO> result = heartStoryService.getUserFavorites(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取温暖回复模板
     */
    @GetMapping("/templates")
    public Result<List<WarmReplyTemplateVO>> getTemplates(
            @RequestParam(required = false) String emotionType) {
        
        List<WarmReplyTemplate> templates;
        if (emotionType != null && !emotionType.isEmpty()) {
            templates = warmReplyTemplateService.getTemplatesByEmotion(emotionType);
        } else {
            templates = warmReplyTemplateService.getTemplatesByCategory("encourage");
        }
        
        List<WarmReplyTemplateVO> voList = templates.stream()
                .map(t -> WarmReplyTemplateVO.builder()
                        .id(t.getId())
                        .category(t.getCategory())
                        .content(t.getContent())
                        .build())
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    /**
     * 删除故事(仅作者)
     */
    @DeleteMapping("/{storyId}")
    public Result<Boolean> deleteStory(@PathVariable Long storyId) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        boolean success = heartStoryService.deleteStory(storyId, userId);
        return Result.success(success);
    }
}

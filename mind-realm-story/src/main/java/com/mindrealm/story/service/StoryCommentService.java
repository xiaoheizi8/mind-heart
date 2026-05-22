package com.mindrealm.story.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mindrealm.story.model.entity.StoryComment;

import java.util.List;

/**
 * @className: StoryCommentService
 * @description: 故事评论服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
public interface StoryCommentService extends IService<StoryComment> {
    
    /**
     * 获取故事的评论列表
     * @param storyId 故事ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页
     */
    Page<StoryComment> getCommentsByStoryId(Long storyId, Integer page, Integer size);
    
    /**
     * 获取评论的回复列表
     * @param parentId 父评论ID
     * @return 回复列表
     */
    List<StoryComment> getReplies(Long parentId);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, Long userId);
}

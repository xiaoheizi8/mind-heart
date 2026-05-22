package com.mindrealm.story.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mindrealm.story.model.entity.StoryLike;

/**
 * @className: StoryLikeService
 * @description: 故事点赞服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
public interface StoryLikeService extends IService<StoryLike> {
    
    /**
     * 检查是否已点赞
     * @param storyId 故事ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isLiked(Long storyId, Long userId);
    
    /**
     * 获取故事的点赞数
     * @param storyId 故事ID
     * @return 点赞数
     */
    long countByStoryId(Long storyId);
}

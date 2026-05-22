package com.mindrealm.story.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mindrealm.story.model.entity.StoryFavorite;

/**
 * @className: StoryFavoriteService
 * @description: 故事收藏服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
public interface StoryFavoriteService extends IService<StoryFavorite> {
    
    /**
     * 检查是否已收藏
     * @param storyId 故事ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long storyId, Long userId);
    
    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 收藏分页
     */
    Page<StoryFavorite> getUserFavorites(Long userId, Integer page, Integer size);
    
    /**
     * 取消收藏
     * @param storyId 故事ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelFavorite(Long storyId, Long userId);
}

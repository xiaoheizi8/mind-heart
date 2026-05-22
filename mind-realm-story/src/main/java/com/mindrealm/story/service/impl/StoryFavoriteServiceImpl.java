package com.mindrealm.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mindrealm.story.model.entity.StoryFavorite;
import com.mindrealm.story.service.StoryFavoriteService;
import com.mindrealm.story.mapper.StoryFavoriteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @className: StoryFavoriteServiceImpl
 * @description: 故事收藏服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Service
public class StoryFavoriteServiceImpl extends ServiceImpl<StoryFavoriteMapper, StoryFavorite>
    implements StoryFavoriteService {

    private static final Logger log = LoggerFactory.getLogger(StoryFavoriteServiceImpl.class);

    @Override
    public boolean isFavorited(Long storyId, Long userId) {
        LambdaQueryWrapper<StoryFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryFavorite::getStoryId, storyId)
               .eq(StoryFavorite::getUserId, userId);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Page<StoryFavorite> getUserFavorites(Long userId, Integer page, Integer size) {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 50);
        
        LambdaQueryWrapper<StoryFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryFavorite::getUserId, userId)
               .orderByDesc(StoryFavorite::getCreatedAt);
        
        return baseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelFavorite(Long storyId, Long userId) {
        LambdaQueryWrapper<StoryFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryFavorite::getStoryId, storyId)
               .eq(StoryFavorite::getUserId, userId);
        
        int deleted = baseMapper.delete(wrapper);
        
        if (deleted > 0) {
            log.info("cancelFavorite: 取消收藏成功, storyId={}, userId={}", storyId, userId);
            return true;
        }
        
        return false;
    }
}





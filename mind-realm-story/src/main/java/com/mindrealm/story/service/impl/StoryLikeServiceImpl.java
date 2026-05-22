package com.mindrealm.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mindrealm.story.model.entity.StoryLike;
import com.mindrealm.story.service.StoryLikeService;
import com.mindrealm.story.mapper.StoryLikeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @className: StoryLikeServiceImpl
 * @description: 故事点赞服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Service
public class StoryLikeServiceImpl extends ServiceImpl<StoryLikeMapper, StoryLike>
    implements StoryLikeService {

    private static final Logger log = LoggerFactory.getLogger(StoryLikeServiceImpl.class);

    @Override
    public boolean isLiked(Long storyId, Long userId) {
        LambdaQueryWrapper<StoryLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryLike::getStoryId, storyId)
               .eq(StoryLike::getUserId, userId);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long countByStoryId(Long storyId) {
        LambdaQueryWrapper<StoryLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryLike::getStoryId, storyId);
        return baseMapper.selectCount(wrapper);
    }
}





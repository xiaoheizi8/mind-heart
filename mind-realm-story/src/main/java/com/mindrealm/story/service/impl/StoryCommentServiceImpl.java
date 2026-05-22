package com.mindrealm.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mindrealm.story.model.entity.StoryComment;
import com.mindrealm.story.service.StoryCommentService;
import com.mindrealm.story.mapper.StoryCommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: StoryCommentServiceImpl
 * @description: 故事评论服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Service
public class StoryCommentServiceImpl extends ServiceImpl<StoryCommentMapper, StoryComment>
    implements StoryCommentService {

    private static final Logger log = LoggerFactory.getLogger(StoryCommentServiceImpl.class);

    @Override
    public Page<StoryComment> getCommentsByStoryId(Long storyId, Integer page, Integer size) {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 20 : Math.min(size, 50);
        
        LambdaQueryWrapper<StoryComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryComment::getStoryId, storyId)
               .eq(StoryComment::getStatus, 1)  // 只查询正常评论
               .orderByAsc(StoryComment::getCreatedAt);
        
        return baseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public List<StoryComment> getReplies(Long parentId) {
        LambdaQueryWrapper<StoryComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryComment::getParentId, parentId)
               .eq(StoryComment::getStatus, 1)
               .orderByAsc(StoryComment::getCreatedAt);
        
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        StoryComment comment = baseMapper.selectById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 检查是否是评论作者
        if (!comment.getUserId().equals(userId)) {
            log.warn("deleteComment: 无权限删除, commentId={}, userId={}", commentId, userId);
            return false;
        }
        
        // 软删除
        comment.setStatus(2);  // 已删除
        comment.setUpdatedAt(LocalDateTime.now());
        
        return baseMapper.updateById(comment) > 0;
    }
}





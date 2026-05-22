package com.mindrealm.story.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.story.model.entity.StoryComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: StoryCommentMapper
 * @description: 针对表【story_comment(故事评论表)】的数据库操作Mapper
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Mapper
public interface StoryCommentMapper extends BaseMapper<StoryComment> {
}


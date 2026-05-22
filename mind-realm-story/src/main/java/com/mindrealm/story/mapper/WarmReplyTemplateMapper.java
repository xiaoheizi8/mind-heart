package com.mindrealm.story.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.story.model.entity.WarmReplyTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: WarmReplyTemplateMapper
 * @description: 针对表【warm_reply_template(温暖回复模板表)】的数据库操作Mapper
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Mapper
public interface WarmReplyTemplateMapper extends BaseMapper<WarmReplyTemplate> {
}


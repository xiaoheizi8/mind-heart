package com.mindrealm.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.core.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: ConversationMapper
 * @description: AI对话记录Mapper接口,提供对话记录表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}

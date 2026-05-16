package com.mindrealm.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.core.entity.Conversation;
import com.mindrealm.core.mapper.ConversationMapper;
import com.mindrealm.core.service.IConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @className: ConversationServiceImpl
 * @description: 对话记录服务实现,提供AI对话历史的存储和查询功能
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class ConversationServiceImpl implements IConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);

    private final ConversationMapper conversationMapper;

    public ConversationServiceImpl(ConversationMapper conversationMapper) {
        this.conversationMapper = conversationMapper;
    }

    /**
     * 保存对话记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Conversation save(Conversation conversation) {
        if (conversation == null) {
            throw new IllegalArgumentException("对话对象不能为空");
        }
        logger.info("保存对话记录: userId={}", conversation.getUserId());
        conversationMapper.insert(conversation);
        return conversation;
    }

    /**
     * 获取用户对话历史(分页)
     */
    @Override
    public Page<Conversation> getHistory(Long userId, int pageNum, int pageSize) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        logger.debug("查询对话历史: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);
        Page<Conversation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .orderByDesc(Conversation::getCreatedAt);
        return conversationMapper.selectPage(page, wrapper);
    }

    /**
     * 获取用户最近的对话
     */
    @Override
    public List<Conversation> getRecent(Long userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        logger.debug("查询最近对话: userId={}, limit={}", userId, limit);
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .orderByDesc(Conversation::getCreatedAt)
                .last("LIMIT " + limit);
        return conversationMapper.selectList(wrapper);
    }

    /**
     * 清除用户对话历史
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearHistory(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        logger.info("清除对话历史: userId={}", userId);
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId);
        conversationMapper.delete(wrapper);
    }
}

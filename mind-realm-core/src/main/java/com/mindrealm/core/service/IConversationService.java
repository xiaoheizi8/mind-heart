package com.mindrealm.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.core.entity.Conversation;

import java.util.List;

/**
 * @className: IConversationService
 * @description: 对话记录服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface IConversationService {

    /**
     * 保存对话记录
     * @param conversation 对话对象
     * @return 保存后的对话对象
     */
    Conversation save(Conversation conversation);

    /**
     * 获取用户对话历史(分页)
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Conversation> getHistory(Long userId, int pageNum, int pageSize);

    /**
     * 获取用户最近的对话
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 对话列表
     */
    List<Conversation> getRecent(Long userId, int limit);

    /**
     * 清除用户对话历史
     * @param userId 用户ID
     */
    void clearHistory(Long userId);
}

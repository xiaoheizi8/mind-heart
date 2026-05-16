package com.mindrealm.core.service;

/**
 * @className: IAlibabaRagService
 * @description: 阿里云RAG服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IAlibabaRagService {

    /**
     * 初始化服务
     */
    void init();

    /**
     * RAG对话
     * @param userId 用户ID
     * @param message 消息
     * @return 回复
     */
    String chatWithRag(Long userId, String message);

    /**
     * 获取知识数量
     * @return 数量
     */
    int getKnowledgeCount();

    /**
     * 检查服务是否可用
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 清除历史
     * @param userId 用户ID
     */
    void clearHistory(Long userId);
}

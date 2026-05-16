package com.mindrealm.core.service;

/**
 * @className: IRagService
 * @description: 简单RAG服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IRagService {

    /**
     * 初始化服务
     */
    void init();

    /**
     * 回答问题
     * @param question 问题
     * @return 答案
     */
    String answer(String question);

    /**
     * 添加知识
     * @param content 知识内容
     */
    void addKnowledge(String content);

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
}

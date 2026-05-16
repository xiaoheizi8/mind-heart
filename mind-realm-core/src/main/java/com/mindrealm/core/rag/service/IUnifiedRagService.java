package com.mindrealm.core.rag.service;

import com.mindrealm.core.rag.config.RagProperties;
import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;

import java.util.Map;

/**
 * @className: IUnifiedRagService
 * @description: 统一RAG服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IUnifiedRagService {

    /**
     * RAG对话
     * @param request RAG请求
     * @return RAG响应
     */
    RagResponse chat(RagRequest request);

    /**
     * RAG对话(简化版)
     * @param userId 用户ID
     * @param question 问题
     * @return RAG响应
     */
    RagResponse chat(Long userId, String question);

    /**
     * RAG对话(带人格)
     * @param userId 用户ID
     * @param question 问题
     * @param persona 人格类型
     * @return RAG响应
     */
    RagResponse chat(Long userId, String question, String persona);

    /**
     * 使用指定模式进行RAG对话
     * @param mode RAG模式
     * @param request RAG请求
     * @return RAG响应
     */
    RagResponse chatWithMode(RagProperties.Mode mode, RagRequest request);

    /**
     * 切换RAG模式
     * @param mode RAG模式
     */
    void switchMode(RagProperties.Mode mode);

    /**
     * 清除用户历史
     * @param userId 用户ID
     */
    void clearHistory(Long userId);

    /**
     * 检查服务是否可用
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 获取当前模式
     * @return 当前模式名称
     */
    String getCurrentMode();

    /**
     * 获取知识库数量
     * @return 知识库数量
     */
    int getKnowledgeCount();

    /**
     * 获取可用模式列表
     * @return 模式可用性映射
     */
    Map<RagProperties.Mode, Boolean> getAvailableModes();

    /**
     * 获取当前模式枚举
     * @return 当前模式
     */
    RagProperties.Mode getMode();
}

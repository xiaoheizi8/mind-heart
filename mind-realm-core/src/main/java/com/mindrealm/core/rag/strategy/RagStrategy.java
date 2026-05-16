package com.mindrealm.core.rag.strategy;

import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;

/**
 * @className: RagStrategy
 * @description: RAG策略接口,定义不同RAG实现的统一契约,支持多种知识库检索增强生成策略
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
public interface RagStrategy {
    
    String getMode();
    
    boolean isAvailable();
    
    RagResponse chat(RagRequest request);
    
    default void clearHistory(Long userId) {}
    
    default int getKnowledgeCount() {
        return 0;
    }
}

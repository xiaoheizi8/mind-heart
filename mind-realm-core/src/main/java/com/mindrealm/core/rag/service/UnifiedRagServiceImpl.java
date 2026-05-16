package com.mindrealm.core.rag.service;

import com.mindrealm.core.rag.config.RagProperties;
import com.mindrealm.core.rag.factory.RagStrategyFactory;
import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;
import com.mindrealm.core.rag.strategy.RagStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @className: UnifiedRagServiceImpl
 * @description: 统一RAG服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class UnifiedRagServiceImpl implements IUnifiedRagService {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedRagServiceImpl.class);

    private final RagStrategyFactory strategyFactory;

    public UnifiedRagServiceImpl(RagStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    @Override
    public RagResponse chat(RagRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RAG请求不能为空");
        }
        RagStrategy strategy = strategyFactory.getStrategy();
        
        if (!strategy.isAvailable()) {
            logger.warn("当前 RAG 策略不可用: {}", strategy.getMode());
            return RagResponse.error("当前 RAG 服务不可用");
        }

        logger.debug("使用 RAG 策略: {}, 用户: {}, 问题: {}", 
                strategy.getMode(), request.getUserId(), truncate(request.getQuestion(), 50));

        try {
            return strategy.chat(request);
        } catch (Exception e) {
            logger.error("RAG 对话失败: {}", e.getMessage());
            return RagResponse.error("服务暂时不可用");
        }
    }

    @Override
    public RagResponse chat(Long userId, String question) {
        return chat(RagRequest.builder()
                .userId(userId)
                .question(question)
                .build());
    }

    @Override
    public RagResponse chat(Long userId, String question, String persona) {
        return chat(RagRequest.builder()
                .userId(userId)
                .question(question)
                .persona(persona)
                .build());
    }

    @Override
    public RagResponse chatWithMode(RagProperties.Mode mode, RagRequest request) {
        if (mode == null) {
            throw new IllegalArgumentException("RAG模式不能为空");
        }
        RagStrategy strategy = strategyFactory.getStrategy(mode);
        
        if (!strategy.isAvailable()) {
            return RagResponse.error("指定模式的 RAG 服务不可用: " + mode);
        }

        return strategy.chat(request);
    }

    @Override
    public void switchMode(RagProperties.Mode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("RAG模式不能为空");
        }
        strategyFactory.selectStrategy(mode);
        logger.info("RAG 模式切换为: {}", mode);
    }

    @Override
    public void clearHistory(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        strategyFactory.getStrategy().clearHistory(userId);
    }

    @Override
    public boolean isAvailable() {
        return strategyFactory.getStrategy().isAvailable();
    }

    @Override
    public String getCurrentMode() {
        return strategyFactory.getStrategy().getMode();
    }

    @Override
    public int getKnowledgeCount() {
        return strategyFactory.getStrategy().getKnowledgeCount();
    }

    @Override
    public Map<RagProperties.Mode, Boolean> getAvailableModes() {
        return strategyFactory.getAvailableModes();
    }

    @Override
    public RagProperties.Mode getMode() {
        return strategyFactory.getCurrentMode();
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen) + "..." : str;
    }
}

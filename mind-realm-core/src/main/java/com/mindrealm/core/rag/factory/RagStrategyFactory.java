package com.mindrealm.core.rag.factory;

import com.mindrealm.core.rag.config.RagProperties;
import com.mindrealm.core.rag.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @className: RagStrategyFactory
 * @description: RAG策略工厂,根据配置动态管理和切换不同的RAG实现策略
 *               支持百炼知识库、本地记忆、Milvus向量库等策略的初始化和切换
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Component
public class RagStrategyFactory {

    private static final Logger logger = LoggerFactory.getLogger(RagStrategyFactory.class);

    private final Map<RagProperties.Mode, RagStrategy> strategies = new EnumMap<>(RagProperties.Mode.class);
    private final RagProperties properties;
    private RagStrategy currentStrategy;

    public RagStrategyFactory(RagProperties properties) {
        this.properties = properties;
        initializeStrategies();
    }

    private void initializeStrategies() {
        // 初始化基础策略
        BailianKnowledgeStrategy bailianStrategy = new BailianKnowledgeStrategy(properties.getBailian());
        bailianStrategy.init(); // 手动调用初始化方法,因为不是Spring管理的Bean
        
        RagStrategy localMemoryStrategy = new LocalMemoryStrategy();
        
        strategies.put(RagProperties.Mode.BAILIAN_KNOWLEDGE, bailianStrategy);
        strategies.put(RagProperties.Mode.LOCAL_MEMORY, localMemoryStrategy);
        strategies.put(RagProperties.Mode.DEEPSEEK, localMemoryStrategy);
        
        // 初始化混合策略 - 传入所有可用策略
        List<RagStrategy> allStrategies = Arrays.asList(
            bailianStrategy,
            localMemoryStrategy
        );
        strategies.put(RagProperties.Mode.HYBRID, new HybridRagStrategy(properties, allStrategies));

        selectStrategy(properties.getMode());
    }

    /**
     * 切换到指定的RAG策略,如果策略不可用则降级到本地内存模式
     * @param mode RAG模式枚举,指定要使用的策略类型
     */
    public void selectStrategy(RagProperties.Mode mode) {
        RagStrategy strategy = strategies.get(mode);
        if (strategy != null && strategy.isAvailable()) {
            this.currentStrategy = strategy;
            logger.info("RAG 策略切换为: {}", mode);
        } else {
            this.currentStrategy = strategies.get(RagProperties.Mode.LOCAL_MEMORY);
            logger.warn("指定策略 {} 不可用，切换为本地内存模式", mode);
        }
    }

    public RagStrategy getStrategy() {
        return currentStrategy;
    }

    public RagStrategy getStrategy(RagProperties.Mode mode) {
        return strategies.getOrDefault(mode, currentStrategy);
    }

    public RagProperties.Mode getCurrentMode() {
        return properties.getMode();
    }

    public Map<RagProperties.Mode, Boolean> getAvailableModes() {
        Map<RagProperties.Mode, Boolean> modes = new EnumMap<>(RagProperties.Mode.class);
        for (Map.Entry<RagProperties.Mode, RagStrategy> entry : strategies.entrySet()) {
            modes.put(entry.getKey(), entry.getValue().isAvailable());
        }
        return modes;
    }

    public void refresh() {
        logger.info("刷新 RAG 策略配置");
        initializeStrategies();
    }
}

package com.mindrealm.core.rag.config;

import com.mindrealm.core.rag.factory.RagStrategyFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: RagAutoConfiguration
 * @description: RAG自动配置类,启用RagProperties配置并注册RagStrategyFactory Bean
 *               支持阿里云百炼、本地记忆、Milvus向量库等多种RAG策略
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Configuration
@EnableConfigurationProperties(RagProperties.class)
public class RagAutoConfiguration {

    /**
     * 创建RAG策略工厂实例,根据配置动态选择RAG实现策略
     * @param ragProperties RAG配置属性,包含模式选择和各策略配置
     * @return RagStrategyFactory实例,用于获取具体的RAG策略实现
     */
    @Bean
    public RagStrategyFactory ragStrategyFactory(RagProperties ragProperties) {
        return new RagStrategyFactory(ragProperties);
    }
}

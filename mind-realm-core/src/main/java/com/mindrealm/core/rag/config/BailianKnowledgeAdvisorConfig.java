package com.mindrealm.core.rag.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: BailianKnowledgeAdvisorConfig
 * @description: 基于阿里云百炼知识库的RAG检索配置
 *               使用Spring AI Alibaba提供的DashScopeDocumentRetriever
 * @author: 一朝风月
 * @code: 面向自己, 面向未来
 * @createTime: 2026.4.25
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "mind-realm.rag.bailian", name = "enabled", havingValue = "true")
public class BailianKnowledgeAdvisorConfig {

    @Value("${spring-ai-alibaba.dashscope.api-key}")
    private String dashScopeApiKey;

    @Value("${mind-realm.rag.bailian.knowledge-base-id:mind_realm_knowledge}")
    private String knowledgeBaseName;

    /**
     * 配置阿里云百炼知识库检索Advisor
     * @return RetrievalAugmentationAdvisor
     */
    @Bean
    public Advisor bailianKnowledgeAdvisor() {
        log.info("初始化阿里云百炼知识库Advisor，知识库名称: {}", knowledgeBaseName);
        
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(dashScopeApiKey)
                .build();
        
        DocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(
                dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(knowledgeBaseName)  // 使用知识库名称
                        .build()
        );
        
        log.info("阿里云百炼知识库Advisor初始化成功");
        
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(dashScopeDocumentRetriever)
                .build();
    }
}

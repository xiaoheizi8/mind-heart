package com.mindrealm.common.advisor;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @className: DashScopeKnowledgeAdvisor
 * @description: 基于百炼rag知识库检索的Advisor配置
 * @author: 风不止
 * @code: 面向自己, 面向未来
 * @createTime: 2026/4/7 14:13
 */
@Order(2)
@Configuration
public class DashScopeKnowledgeAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeKnowledgeAdvisor.class);

    @Value("${spring-ai-alibaba.dashscope.api-key:}")
    private String dashScopeApiKey;

    @Value("${vector.alibaba.knowledge-base.id:}")
    private String knowledgeBaseId;

    @Value("${vector.alibaba.knowledge-base.enabled:false}")
    private boolean knowledgeBaseEnabled;

    /**
     * 创建百炼知识库检索Advisor
     * 用于在ChatClient对话时自动从知识库中检索相关文档
     */
    @Bean
    public Advisor knowledgeDashScopeAdvisor() {
        // 检查API Key是否配置
        if (dashScopeApiKey == null || dashScopeApiKey.isEmpty()) {
            logger.warn("未配置DASHSCOPE_API_KEY，知识库Advisor将不可用");
            return RetrievalAugmentationAdvisor.builder().build();
        }

        try {
            // 创建DashScope API实例
            DashScopeApi dashScopeApi = DashScopeApi.builder()
                    .apiKey(dashScopeApiKey)
                    .build();

            // 使用配置文件中的知识库ID
            String indexName = knowledgeBaseEnabled && knowledgeBaseId != null 
                    ? knowledgeBaseId 
                    : "mind_realm_knowledge";

            logger.info("初始化百炼知识库Advisor，知识库ID: {}", indexName);

            // 创建文档检索器
            DocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                    DashScopeDocumentRetrieverOptions.builder()
                            .withIndexName(indexName)
                            .build());

            // 创建并返回RAG Advisor
            return RetrievalAugmentationAdvisor.builder()
                    .documentRetriever(dashScopeDocumentRetriever)
                    .build();
        } catch (Exception e) {
            logger.error("初始化百炼知识库Advisor失败: {}", e.getMessage(), e);
            // 返回空的Advisor，避免启动失败
            return RetrievalAugmentationAdvisor.builder().build();
        }
    }
}
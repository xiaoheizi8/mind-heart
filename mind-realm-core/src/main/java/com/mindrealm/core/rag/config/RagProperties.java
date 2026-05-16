package com.mindrealm.core.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @className: RagProperties
 * @description: RAG配置属性类,从配置文件读取RAG相关配置
 *               支持多种RAG模式:百炼知识库、本地记忆、Milvus向量库、混合模式等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Component
@ConfigurationProperties(prefix = "mind-realm.rag")
public class RagProperties {

    private Mode mode = Mode.LOCAL_MEMORY;
    private BailianConfig bailian = new BailianConfig();
    private LocalMemoryConfig localMemory = new LocalMemoryConfig();

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }
    public BailianConfig getBailian() { return bailian; }
    public void setBailian(BailianConfig bailian) { this.bailian = bailian; }
    public LocalMemoryConfig getLocalMemory() { return localMemory; }
    public void setLocalMemory(LocalMemoryConfig localMemory) { this.localMemory = localMemory; }

    public static class BailianConfig {
        private boolean enabled = false;
        private String apiKey;
        private String model = "qwen-plus";
        private String knowledgeBaseId;
        private boolean useKnowledgeBase = true;
        private float temperature = 0.9f;
        private int maxTokens = 4000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getKnowledgeBaseId() { return knowledgeBaseId; }
        public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }
        public boolean isUseKnowledgeBase() { return useKnowledgeBase; }
        public void setUseKnowledgeBase(boolean useKnowledgeBase) { this.useKnowledgeBase = useKnowledgeBase; }
        public float getTemperature() { return temperature; }
        public void setTemperature(float temperature) { this.temperature = temperature; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    }

    public static class LocalMemoryConfig {
        private boolean enabled = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public enum Mode {
        BAILIAN_KNOWLEDGE,
        LOCAL_MEMORY,
        DEEPSEEK,
        HYBRID
    }
}

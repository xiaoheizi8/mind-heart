package com.mindrealm.core.rag.model;

import java.util.List;

/**
 * @className: RagResponse
 * @description: RAG响应模型,封装AI回答、知识来源、模型信息等检索增强生成结果
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
public class RagResponse {
    
    private boolean success;
    private String answer;
    private String mode;
    private List<KnowledgeSnippet> sources;
    private String error;
    private ModelInfo modelInfo;

    public RagResponse() {}

    public RagResponse(boolean success, String answer, String mode, List<KnowledgeSnippet> sources, String error, ModelInfo modelInfo) {
        this.success = success;
        this.answer = answer;
        this.mode = mode;
        this.sources = sources;
        this.error = error;
        this.modelInfo = modelInfo;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public List<KnowledgeSnippet> getSources() { return sources; }
    public void setSources(List<KnowledgeSnippet> sources) { this.sources = sources; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public ModelInfo getModelInfo() { return modelInfo; }
    public void setModelInfo(ModelInfo modelInfo) { this.modelInfo = modelInfo; }

    public static RagResponseBuilder builder() { return new RagResponseBuilder(); }

    public static class RagResponseBuilder {
        private boolean success;
        private String answer;
        private String mode;
        private List<KnowledgeSnippet> sources;
        private String error;
        private ModelInfo modelInfo;

        public RagResponseBuilder success(boolean success) { this.success = success; return this; }
        public RagResponseBuilder answer(String answer) { this.answer = answer; return this; }
        public RagResponseBuilder mode(String mode) { this.mode = mode; return this; }
        public RagResponseBuilder sources(List<KnowledgeSnippet> sources) { this.sources = sources; return this; }
        public RagResponseBuilder error(String error) { this.error = error; return this; }
        public RagResponseBuilder modelInfo(ModelInfo modelInfo) { this.modelInfo = modelInfo; return this; }

        public RagResponse build() {
            RagResponse response = new RagResponse();
            response.success = this.success;
            response.answer = this.answer;
            response.mode = this.mode;
            response.sources = this.sources;
            response.error = this.error;
            response.modelInfo = this.modelInfo;
            return response;
        }
    }

    public static RagResponse success(String answer, String mode) {
        RagResponse response = new RagResponse();
        response.success = true;
        response.answer = answer;
        response.mode = mode;
        return response;
    }
    
    public static RagResponse error(String error) {
        RagResponse response = new RagResponse();
        response.success = false;
        response.error = error;
        response.mode = "unknown";
        return response;
    }

    public static class KnowledgeSnippet {
        private String content;
        private String source;
        private float score;

        public KnowledgeSnippet() {}
        
        public KnowledgeSnippet(String content, String source, float score) {
            this.content = content;
            this.source = source;
            this.score = score;
        }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }

        public static KnowledgeSnippetBuilder builder() { return new KnowledgeSnippetBuilder(); }

        public static class KnowledgeSnippetBuilder {
            private String content;
            private String source;
            private float score;

            public KnowledgeSnippetBuilder content(String content) { this.content = content; return this; }
            public KnowledgeSnippetBuilder source(String source) { this.source = source; return this; }
            public KnowledgeSnippetBuilder score(float score) { this.score = score; return this; }

            public KnowledgeSnippet build() {
                return new KnowledgeSnippet(content, source, score);
            }
        }
    }

    public static class ModelInfo {
        private String model;
        private long usedTokens;
        private long latencyMs;

        public ModelInfo() {}

        public ModelInfo(String model, long usedTokens, long latencyMs) {
            this.model = model;
            this.usedTokens = usedTokens;
            this.latencyMs = latencyMs;
        }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public long getUsedTokens() { return usedTokens; }
        public void setUsedTokens(long usedTokens) { this.usedTokens = usedTokens; }
        public long getLatencyMs() { return latencyMs; }
        public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

        public static ModelInfoBuilder builder() { return new ModelInfoBuilder(); }

        public static class ModelInfoBuilder {
            private String model;
            private long usedTokens;
            private long latencyMs;

            public ModelInfoBuilder model(String model) { this.model = model; return this; }
            public ModelInfoBuilder usedTokens(long usedTokens) { this.usedTokens = usedTokens; return this; }
            public ModelInfoBuilder latencyMs(long latencyMs) { this.latencyMs = latencyMs; return this; }

            public ModelInfo build() {
                return new ModelInfo(model, usedTokens, latencyMs);
            }
        }
    }
}

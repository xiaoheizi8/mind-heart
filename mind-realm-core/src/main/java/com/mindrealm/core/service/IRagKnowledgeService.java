package com.mindrealm.core.service;

import java.util.List;
import java.util.Map;

/**
 * @className: IRagKnowledgeService
 * @description: RAG知识检索服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IRagKnowledgeService {

    /**
     * 检索知识
     * @param query 查询文本
     * @param maxResults 最大结果数
     * @return 知识结果列表
     */
    List<IRagKnowledgeService.KnowledgeResult> retrieve(String query, int maxResults);

    /**
     * 检索知识(带分类)
     * @param query 查询文本
     * @param category 分类
     * @param maxResults 最大结果数
     * @return 知识结果列表
     */
    List<IRagKnowledgeService.KnowledgeResult> retrieve(String query, String category, int maxResults);

    /**
     * 带情感上下文的检索
     * @param query 查询文本
     * @param emotionCategory 情感分类
     * @param maxResults 最大结果数
     * @return 知识结果列表
     */
    List<IRagKnowledgeService.KnowledgeResult> retrieveWithEmotionContext(String query, String emotionCategory, int maxResults);

    /**
     * 构建上下文提示
     * @param query 查询文本
     * @param knowledgeResults 知识结果
     * @return 上下文提示文本
     */
    String buildContextPrompt(String query, List<IRagKnowledgeService.KnowledgeResult> knowledgeResults);

    /**
     * 获取知识统计信息
     * @return 统计信息
     */
    Map<String, Object> getKnowledgeStatistics();

    /**
     * 获取知识分类列表
     * @return 分类列表
     */
    List<Map<String, Object>> getKnowledgeCategories();

    /**
     * 知识结果内部类
     */
    class KnowledgeResult {
        private Long id;
        private String title;
        private String content;
        private String summary;
        private String category;
        private List<String> tags;
        private double relevance;

        public KnowledgeResult(Long id, String title, String content, String summary,
                            String category, List<String> tags, double relevance) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.summary = summary;
            this.category = category;
            this.tags = tags;
            this.relevance = relevance;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public double getRelevance() { return relevance; }
        public void setRelevance(double relevance) { this.relevance = relevance; }
    }
}

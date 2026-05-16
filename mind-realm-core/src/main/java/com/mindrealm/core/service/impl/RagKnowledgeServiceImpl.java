package com.mindrealm.core.service.impl;

import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.service.IKnowledgeService;
import com.mindrealm.core.entity.KnowledgeDocument;
import com.mindrealm.core.service.IRagKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: RagKnowledgeServiceImpl
 * @description: RAG知识检索服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class RagKnowledgeServiceImpl implements IRagKnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(RagKnowledgeServiceImpl.class);

    private final IKnowledgeService knowledgeService;
    private final ElasticsearchRagServiceImpl elasticsearchRagService;

    public RagKnowledgeServiceImpl(IKnowledgeService knowledgeService,
                               ElasticsearchRagServiceImpl elasticsearchRagService) {
        this.knowledgeService = knowledgeService;
        this.elasticsearchRagService = elasticsearchRagService;
    }

    @Override
    public List<KnowledgeResult> retrieve(String query, int maxResults) {
        return retrieve(query, null, maxResults);
    }

    @Override
    public List<KnowledgeResult> retrieve(String query, String category, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 优先使用 ES 向量搜索
        if (elasticsearchRagService.isInitialized()) {
            try {
                List<KnowledgeDocument> docs = elasticsearchRagService.hybridSearch(query, category, maxResults);
                if (!docs.isEmpty()) {
                    List<KnowledgeResult> results = docs.stream()
                            .map(this::convertToResult)
                            .collect(Collectors.toList());
                    log.debug("ES混合搜索完成: query={}, results={}", query, results.size());
                    return results;
                }
            } catch (Exception e) {
                log.warn("ES搜索失败,回退到MySQL: {}", e.getMessage());
            }
        }

        // 回退到 MySQL 检索
        return retrieveFromDb(query, category, maxResults);
    }

    private List<KnowledgeResult> retrieveFromDb(String query, String category, int maxResults) {
        try {
            List<Knowledge> knowledgeList = knowledgeService.list(category, null, query, 1, 1, 50).getRecords();

            if (knowledgeList.isEmpty()) {
                return new ArrayList<>();
            }

            List<KnowledgeResult> results = knowledgeList.stream()
                    .map(k -> new KnowledgeResult(
                            k.getId(),
                            k.getTitle(),
                            k.getContent(),
                            k.getSummary(),
                            k.getCategory(),
                            parseTags(k.getTags()),
                            calculateRelevance(query, k)
                    ))
                    .sorted(Comparator.comparingDouble(KnowledgeResult::getRelevance).reversed())
                    .limit(maxResults)
                    .collect(Collectors.toList());

            log.debug("MySQL检索完成: query={}, results={}", query, results.size());
            return results;

        } catch (Exception e) {
            log.error("MySQL检索失败: query={}, error={}", query, e.getMessage());
            return new ArrayList<>();
        }
    }

    private KnowledgeResult convertToResult(KnowledgeDocument doc) {
        return new KnowledgeResult(
                doc.getId(),
                doc.getTitle(),
                doc.getContent(),
                doc.getSummary(),
                doc.getCategory(),
                doc.getTags(),
                1.0
        );
    }

    @Override
    public List<KnowledgeResult> retrieveWithEmotionContext(String query, String emotionCategory, int maxResults) {
        List<KnowledgeResult> results = new ArrayList<>();

        results.addAll(retrieve(query, "CBT", maxResults / 2));

        results.addAll(retrieve(query, emotionCategory, maxResults / 2));

        if (results.size() < maxResults) {
            results.addAll(retrieve(query, null, maxResults - results.size()));
        }

        return results.stream()
                .sorted(Comparator.comparingDouble(KnowledgeResult::getRelevance).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    public String buildContextPrompt(String query, List<KnowledgeResult> knowledgeResults) {
        if (knowledgeResults == null || knowledgeResults.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("【相关心理知识参考】\n\n");

        for (int i = 0; i < knowledgeResults.size(); i++) {
            KnowledgeResult result = knowledgeResults.get(i);
            context.append(String.format("【知识 %d】%s\n", i + 1, result.getTitle()));
            if (result.getSummary() != null && !result.getSummary().isEmpty()) {
                context.append("摘要: ").append(result.getSummary()).append("\n");
            }
            context.append("内容: ").append(truncate(result.getContent(), 300)).append("\n\n");
        }

        context.append("【使用说明】\n");
        context.append("请结合上述心理知识，用温暖、专业的方式回答用户的问题。如果知识内容与用户情况相关，请给出具体的建议。\n");

        return context.toString();
    }

    @Override
    public Map<String, Object> getKnowledgeStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            List<Knowledge> all = knowledgeService.list(null, null, null, null, 1, 1000).getRecords();

            stats.put("totalCount", all.size());

            Map<String, Long> categoryCount = all.stream()
                    .filter(k -> k.getCategory() != null)
                    .collect(Collectors.groupingBy(Knowledge::getCategory, Collectors.counting()));
            stats.put("categoryCount", categoryCount);

            List<String> allTags = all.stream()
                    .filter(k -> k.getTags() != null)
                    .flatMap(k -> parseTags(k.getTags()).stream())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            stats.put("totalTags", allTags.size());
            stats.put("topTags", allTags.stream().limit(10).collect(Collectors.toList()));

            int totalViews = all.stream()
                    .mapToInt(k -> k.getViewCount() != null ? k.getViewCount() : 0)
                    .sum();
            stats.put("totalViews", totalViews);

        } catch (Exception e) {
            log.error("获取知识库统计失败: {}", e.getMessage());
        }

        return stats;
    }

    @Override
    public List<Map<String, Object>> getKnowledgeCategories() {
        return List.of(
                Map.of("code", "DSM5", "name", "DSM-5诊断标准", "desc", "精神疾病诊断与统计手册"),
                Map.of("code", "CBT", "name", "认知行为疗法", "desc", "认知重构与行为调节技术"),
                Map.of("code", "case", "name", "案例分析", "desc", "真实心理咨询案例参考"),
                Map.of("code", "skill", "name", "心理技巧", "desc", "实用情绪调节与应对技巧"),
                Map.of("code", "knowledge", "name", "心理健康知识", "desc", "心理健康科普内容")
        );
    }

    private double calculateRelevance(String query, Knowledge knowledge) {
        double score = 0.0;

        String lowerQuery = query.toLowerCase();
        String title = knowledge.getTitle() != null ? knowledge.getTitle().toLowerCase() : "";
        String content = knowledge.getContent() != null ? knowledge.getContent().toLowerCase() : "";
        String summary = knowledge.getSummary() != null ? knowledge.getSummary().toLowerCase() : "";

        String[] queryWords = lowerQuery.split("\\s+");
        for (String word : queryWords) {
            if (word.length() < 2) continue;

            if (title.contains(word)) {
                score += 0.4;
            }
            if (content.contains(word)) {
                score += 0.2;
            }
            if (summary.contains(word)) {
                score += 0.3;
            }

            int titleCount = countOccurrences(title, word);
            int contentCount = countOccurrences(content, word);
            score += Math.min(titleCount * 0.05, 0.2);
            score += Math.min(contentCount * 0.02, 0.1);
        }

        if (knowledge.getViewCount() != null && knowledge.getViewCount() > 0) {
            score += Math.log(knowledge.getViewCount() + 1) * 0.05;
        }

        return Math.min(score, 1.0);
    }

    private int countOccurrences(String text, String word) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }

    private List<String> parseTags(String tagsJson) {
        if (tagsJson == null || tagsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            if (tagsJson.startsWith("[")) {
                return Arrays.asList(tagsJson.replace("[", "").replace("]", "").replace("\"", "").split(","))
                        .stream().map(String::trim).filter(t -> !t.isEmpty()).collect(Collectors.toList());
            }
            return List.of(tagsJson);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}

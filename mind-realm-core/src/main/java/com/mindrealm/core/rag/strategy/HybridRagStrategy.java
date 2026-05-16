package com.mindrealm.core.rag.strategy;

import com.mindrealm.core.rag.config.RagProperties;
import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @className: HybridRagStrategy
 * @description: 多策略混合检索实现,并行检索多个数据源并综合排序
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
public class HybridRagStrategy implements RagStrategy {

    private static final Logger logger = LoggerFactory.getLogger(HybridRagStrategy.class);

    private final RagProperties properties;
    private final List<RagStrategy> availableStrategies;
    private final ExecutorService executorService;

    // 混合检索配置
    private final HybridConfig hybridConfig;

    public HybridRagStrategy(RagProperties properties, List<RagStrategy> strategies) {
        this.properties = properties;
        this.availableStrategies = strategies != null ? strategies : new ArrayList<>();
        this.hybridConfig = new HybridConfig();
        
        // 创建固定大小的线程池用于并行检索
        this.executorService = Executors.newFixedThreadPool(
            Math.min(4, Runtime.getRuntime().availableProcessors()),
            r -> {
                Thread t = new Thread(r);
                t.setName("hybrid-rag-worker");
                t.setDaemon(true);
                return t;
            }
        );
        
        logger.info("HybridRagStrategy 初始化完成,加载 {} 个策略", this.availableStrategies.size());
    }

    @Override
    public String getMode() {
        return "HYBRID";
    }

    @Override
    public boolean isAvailable() {
        // 只要有一个策略可用即可
        return availableStrategies.stream().anyMatch(RagStrategy::isAvailable);
    }

    @Override
    public RagResponse chat(RagRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("开始混合检索: userId={}, question={}", request.getUserId(), request.getQuestion());

        try {
            // 1. 并行检索所有可用的数据源
            List<CompletableFuture<SearchResult>> futures = new ArrayList<>();
            
            for (RagStrategy strategy : availableStrategies) {
                if (!strategy.isAvailable()) {
                    continue;
                }
                
                String strategyName = strategy.getClass().getSimpleName();
                if (!shouldIncludeStrategy(strategyName)) {
                    continue;
                }
                
                futures.add(CompletableFuture.supplyAsync(() -> {
                    long t0 = System.currentTimeMillis();
                    try {
                        RagResponse response = strategy.chat(request);
                        long latency = System.currentTimeMillis() - t0;
                        return new SearchResult(strategyName, response, latency);
                    } catch (Exception e) {
                        logger.warn("策略 {} 检索失败: {}", strategyName, e.getMessage());
                        return new SearchResult(strategyName, null, System.currentTimeMillis() - t0);
                    }
                }, executorService));
            }

            // 2. 等待所有检索完成(设置超时)
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(hybridConfig.getTimeoutSeconds(), TimeUnit.SECONDS);

            // 3. 收集结果
            List<SearchResult> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(sr -> sr.response != null && sr.response.isSuccess())
                .collect(Collectors.toList());

            if (results.isEmpty()) {
                logger.warn("所有混合检索策略均失败");
                return RagResponse.error("知识库检索失败");
            }

            // 4. 合并知识片段并去重排序
            List<RagResponse.KnowledgeSnippet> mergedSnippets = mergeAndRankSnippets(results);

            // 5. 选择最佳答案(从得分最高的策略中获取)
            SearchResult bestResult = results.stream()
                .max(Comparator.comparingDouble(sr -> calculateStrategyScore(sr)))
                .orElse(results.get(0));

            long totalLatency = System.currentTimeMillis() - startTime;

            // 6. 构建混合响应
            return RagResponse.builder()
                .success(true)
                .answer(bestResult.response.getAnswer())
                .mode("HYBRID(" + results.size() + " sources)")
                .sources(mergedSnippets)
                .modelInfo(RagResponse.ModelInfo.builder()
                    .model("hybrid-rag")
                    .latencyMs(totalLatency)
                    .build())
                .build();

        } catch (TimeoutException e) {
            logger.error("混合检索超时: {}秒", hybridConfig.getTimeoutSeconds());
            return RagResponse.error("检索超时,请稍后重试");
        } catch (Exception e) {
            logger.error("混合检索异常: {}", e.getMessage(), e);
            return RagResponse.error("检索失败: " + e.getMessage());
        }
    }

    /**
     * 判断是否应该包含某个策略
     */
    private boolean shouldIncludeStrategy(String strategyName) {
        return hybridConfig.getEnabledStrategies().contains(strategyName) ||
               hybridConfig.getEnabledStrategies().isEmpty(); // 空表示全部启用
    }

    /**
     * 合并知识片段并综合排序
     */
    private List<RagResponse.KnowledgeSnippet> mergeAndRankSnippets(List<SearchResult> results) {
        // 使用Map去重(基于内容相似度)
        Map<String, ScoredSnippet> snippetMap = new LinkedHashMap<>();

        for (SearchResult result : results) {
            if (result.response == null || result.response.getSources() == null) {
                continue;
            }

            double strategyWeight = hybridConfig.getStrategyWeights()
                .getOrDefault(result.strategyName, hybridConfig.getDefaultWeight());

            for (RagResponse.KnowledgeSnippet snippet : result.response.getSources()) {
                String key = normalizeContent(snippet.getContent());
                
                if (snippetMap.containsKey(key)) {
                    // 更新已有片段的得分(取最高)
                    ScoredSnippet existing = snippetMap.get(key);
                    double newScore = snippet.getScore() * strategyWeight;
                    if (newScore > existing.finalScore) {
                        existing.finalScore = newScore;
                        existing.source = snippet.getSource() + " + " + result.strategyName;
                    }
                } else {
                    // 添加新片段
                    double finalScore = snippet.getScore() * strategyWeight;
                    snippetMap.put(key, new ScoredSnippet(
                        snippet.getContent(),
                        snippet.getSource() + " [" + result.strategyName + "]",
                        finalScore
                    ));
                }
            }
        }

        // 按得分降序排序,返回TopK
        return snippetMap.values().stream()
            .sorted(Comparator.comparingDouble(ScoredSnippet::getFinalScore).reversed())
            .limit(hybridConfig.getMaxResults())
            .map(ss -> RagResponse.KnowledgeSnippet.builder()
                .content(ss.content)
                .source(ss.source)
                .score((float) ss.finalScore)
                .build())
            .collect(Collectors.toList());
    }

    /**
     * 计算策略综合得分
     */
    private double calculateStrategyScore(SearchResult result) {
        double weight = hybridConfig.getStrategyWeights()
            .getOrDefault(result.strategyName, hybridConfig.getDefaultWeight());
        
        // 考虑响应速度和知识质量
        double speedFactor = Math.max(0.5, 1.0 - (result.latencyMs / 5000.0));
        double qualityFactor = result.response.getSources() != null ? 
            Math.min(1.0, result.response.getSources().size() / 5.0) : 0;

        return weight * (0.6 * speedFactor + 0.4 * qualityFactor);
    }

    /**
     * 标准化内容用于去重
     */
    private String normalizeContent(String content) {
        if (content == null) return "";
        return content.trim()
            .replaceAll("\\s+", " ")
            .toLowerCase()
            .substring(0, Math.min(100, content.length()));
    }

    @Override
    public void clearHistory(Long userId) {
        availableStrategies.forEach(strategy -> {
            try {
                strategy.clearHistory(userId);
            } catch (Exception e) {
                logger.warn("清除历史失败: {}", e.getMessage());
            }
        });
    }

    @Override
    public int getKnowledgeCount() {
        return availableStrategies.stream()
            .mapToInt(RagStrategy::getKnowledgeCount)
            .sum();
    }

    /**
     * 搜索结果封装
     */
    private static class SearchResult {
        final String strategyName;
        final RagResponse response;
        final long latencyMs;

        SearchResult(String strategyName, RagResponse response, long latencyMs) {
            this.strategyName = strategyName;
            this.response = response;
            this.latencyMs = latencyMs;
        }
    }

    /**
     * 带得分的知识片段
     */
    private static class ScoredSnippet {
        String content;
        String source;
        double finalScore;

        ScoredSnippet(String content, String source, double finalScore) {
            this.content = content;
            this.source = source;
            this.finalScore = finalScore;
        }

        double getFinalScore() {
            return finalScore;
        }
    }

    /**
     * 混合检索配置
     */
    public static class HybridConfig {
        // 启用的策略列表(空表示全部)
        private List<String> enabledStrategies = Arrays.asList(
            "LocalMemoryStrategy",
            "BailianKnowledgeStrategy"
        );

        // 策略权重
        private Map<String, Double> strategyWeights = new HashMap<String, Double>() {{
            put("LocalMemoryStrategy", 1.0);
            put("BailianKnowledgeStrategy", 1.2);
        }};

        // 超时时间(秒)
        private int timeoutSeconds = 10;

        // 最大结果数
        private int maxResults = 5;

        // 默认权重
        private double defaultWeight = 1.0;

        public List<String> getEnabledStrategies() { return enabledStrategies; }
        public Map<String, Double> getStrategyWeights() { return strategyWeights; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public int getMaxResults() { return maxResults; }
        public double getDefaultWeight() { return defaultWeight; }
    }
}

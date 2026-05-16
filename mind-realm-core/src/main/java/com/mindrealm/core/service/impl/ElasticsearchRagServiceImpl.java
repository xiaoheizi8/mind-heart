package com.mindrealm.core.service.impl;

import co.elastic.clients.elasticsearch._types.KnnSearch;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.service.IKnowledgeService;
import com.mindrealm.core.entity.KnowledgeDocument;
import com.mindrealm.core.repository.KnowledgeDocRepository;
import com.mindrealm.core.service.EmbeddingService;
import com.mindrealm.core.service.IElasticsearchRagService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: ElasticsearchRagServiceImpl
 * @description: 基于Elasticsearch的向量存储与混合搜索服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
@RequiredArgsConstructor
public class ElasticsearchRagServiceImpl implements IElasticsearchRagService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchRagServiceImpl.class);
    private static final String INDEX_NAME = "mind_realm_knowledge";
    private static final int DIMENSION = 1024;

    private final KnowledgeDocRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final EmbeddingService embeddingService;
    private final IKnowledgeService knowledgeService;

    private boolean initialized = false;


    /**
     * 初始化ES索引
     */
    @PostConstruct
    public void init() {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(KnowledgeDocument.class);
            if (!indexOps.exists()) {
                indexOps.create();
                logger.info("ES索引 [{}] 创建成功", INDEX_NAME);
            }

            // 设置 dense_vector 字段映射
            String mappingJson = """
                {
                  "properties": {
                    "embedding": {
                      "type": "dense_vector",
                      "dims": %d,
                      "index": true,
                      "similarity": "cosine"
                    }
                  }
                }
                """.formatted(DIMENSION);

            indexOps.putMapping(Document.parse(mappingJson));
            logger.info("ES索引 [{}] dense_vector 映射已设置, 维度: {}", INDEX_NAME, DIMENSION);

            initialized = true;
        } catch (Exception e) {
            logger.error("ES索引初始化失败: {}", e.getMessage());
            initialized = false;
        }
    }

    /**
     * 保存或更新文档（自动生成embedding）
     */
    public void save(KnowledgeDocument doc) {
        if (doc == null || doc.getContent() == null) {
            return;
        }
        try {
            doc.setEmbedding(embeddingService.embed(doc.getContent()));
            repository.save(doc);
            logger.debug("文档保存到ES: id={}, title={}", doc.getId(), doc.getTitle());
        } catch (Exception e) {
            logger.error("文档保存到ES失败: id={}, error={}", doc.getId(), e.getMessage());
        }
    }

    /**
     * 批量保存文档
     */
    public void saveBatch(List<KnowledgeDocument> docs) {
        if (docs == null || docs.isEmpty()) {
            return;
        }
        List<String> contents = docs.stream()
                .map(KnowledgeDocument::getContent)
                .collect(Collectors.toList());
        List<float[]> embeddings = embeddingService.embedBatch(contents);

        for (int i = 0; i < docs.size(); i++) {
            docs.get(i).setEmbedding(embeddings.get(i));
        }
        repository.saveAll(docs);
        logger.info("批量保存文档到ES: count={}", docs.size());
    }

    /**
     * 混合搜索：BM25 + KNN
     * @param query 查询文本
     * @param category 分类过滤（可选）
     * @param topK 返回数量
     * @return 知识库文档列表
     */
    public List<KnowledgeDocument> hybridSearch(String query, String category, int topK) {
        if (!initialized || query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            float[] embedding = embeddingService.embed(query);

            NativeQueryBuilder queryBuilder = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> {
                        b.should(s -> s.match(m -> m.field("content").query(query).boost(1.0f)));
                        b.should(s -> s.match(m -> m.field("title").query(query).boost(2.0f)));
                        b.minimumShouldMatch("1");
                        return b;
                    }))
                    .withKnnSearches(KnnSearch.of(k -> k
                            .field("embedding")
                            .queryVector(toList(embedding))
                            .k(topK)
                            .numCandidates(topK * 10)
                    ))
                    .withPageable(PageRequest.of(0, topK));

            SearchHits<KnowledgeDocument> searchHits = elasticsearchOperations.search(
                    queryBuilder.build(), KnowledgeDocument.class, IndexCoordinates.of(INDEX_NAME));

            List<KnowledgeDocument> results = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            logger.debug("混合搜索完成: query={}, results={}", query, results.size());
            return results;

        } catch (Exception e) {
            logger.error("混合搜索失败: query={}, error={}", query, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 纯向量搜索（用于语义相似度检索）
     */
    public List<KnowledgeDocument> vectorSearch(String query, int topK) {
        if (!initialized || query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            float[] embedding = embeddingService.embed(query);

            NativeQuery searchQuery = NativeQuery.builder()
                    .withKnnSearches(KnnSearch.of(k -> k
                            .field("embedding")
                            .queryVector(toList(embedding))
                            .k(topK)
                            .numCandidates(topK * 10)
                    ))
                    .withPageable(PageRequest.of(0, topK))
                    .build();

            SearchHits<KnowledgeDocument> searchHits = elasticsearchOperations.search(
                    searchQuery, KnowledgeDocument.class, IndexCoordinates.of(INDEX_NAME));

            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("向量搜索失败: query={}, error={}", query, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID删除文档
     */
    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
            logger.debug("文档从ES删除: id={}", id);
        } catch (Exception e) {
            logger.error("文档删除失败: id={}, error={}", id, e.getMessage());
        }
    }

    /**
     * 从MySQL同步知识库数据到ES
     * @param batchSize 每批同步数量
     */
    public void syncFromDatabase(int batchSize) {
        if (!initialized) {
            logger.warn("ES未初始化,跳过同步");
            return;
        }

        try {
            logger.info("开始同步知识库数据到ES...");
            int page = 1;
            int totalSynced = 0;

            while (true) {
                List<Knowledge> records = knowledgeService.list(null, null, null, null, page, batchSize).getRecords();
                if (records.isEmpty()) {
                    break;
                }

                List<KnowledgeDocument> docs = records.stream()
                        .map(this::convertToDoc)
                        .collect(Collectors.toList());

                saveBatch(docs);
                totalSynced += docs.size();
                page++;
            }

            logger.info("知识库同步完成: totalSynced={}", totalSynced);
        } catch (Exception e) {
            logger.error("知识库同步失败: {}", e.getMessage());
        }
    }

    /**
     * 同步单条知识库记录
     */
    public void syncSingle(Knowledge knowledge) {
        if (knowledge == null) {
            return;
        }
        save(convertToDoc(knowledge));
    }

    private KnowledgeDocument convertToDoc(Knowledge knowledge) {
        return KnowledgeDocument.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .content(knowledge.getContent())
                .category(knowledge.getCategory())
                .tags(parseTags(knowledge.getTags()))
                .summary(knowledge.getSummary())
                .source(knowledge.getSource())
                .createdAt(knowledge.getCreatedAt())
                .updatedAt(knowledge.getUpdatedAt())
                .build();
    }

    private List<String> parseTags(String tagsJson) {
        if (tagsJson == null || tagsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return com.alibaba.fastjson2.JSON.parseArray(tagsJson, String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<Float> toList(float[] array) {
        List<Float> list = new ArrayList<>(array.length);
        for (float v : array) {
            list.add(v);
        }
        return list;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public long count() {
        return repository.count();
    }
}


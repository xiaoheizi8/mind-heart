package com.mindrealm.core.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.mindrealm.core.service.EsSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @className: EsSearchServiceImpl
 * @description: ES全文搜索服务实现，支持日记和故事的IK分词搜索
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Service
public class EsSearchServiceImpl implements EsSearchService {

    private static final Logger log = LoggerFactory.getLogger(EsSearchServiceImpl.class);

    private final ElasticsearchOperations elasticsearchOperations;

    private static final Map<String, IndexConfig> INDEX_CONFIGS = new LinkedHashMap<>();

    static {
        // 日记索引配置
        INDEX_CONFIGS.put("diary", new IndexConfig(
                "mind_realm_diary",
                new String[]{"content", "emotionTags", "aiAnalysis"}  // 搜索字段
        ));
        // 故事索引配置
        INDEX_CONFIGS.put("story", new IndexConfig(
                "mind_realm_story",
                new String[]{"title", "content", "tags"}  // 搜索字段
        ));
    }

    public EsSearchServiceImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public SearchResult search(String type, String keyword, Map<String, Object> filters, int page, int size) {
        IndexConfig config = INDEX_CONFIGS.get(type);
        if (config == null) {
            log.warn("不支持的搜索类型: {}", type);
            return new SearchResult(0, page, size, Collections.emptyList());
        }

        try {
            NativeQueryBuilder queryBuilder = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> {
                        // 关键词搜索（多字段匹配）
                        if (keyword != null && !keyword.isBlank()) {
                            b.must(m -> m.multiMatch(mm -> mm
                                    .fields(Arrays.asList(config.searchFields))
                                    .query(keyword)
                            ));
                        }
                        // 额外过滤条件（精确匹配）
                        if (filters != null && !filters.isEmpty()) {
                            filters.forEach((field, value) -> {
                                if (value != null) {
                                    b.filter(f -> f.term(t -> t.field(field).value(toFieldValue(value))));
                                }
                            });
                        }
                        // 如果既没有关键词也没有过滤条件，match_all
                        if ((keyword == null || keyword.isBlank())
                                && (filters == null || filters.isEmpty())) {
                            b.must(m -> m.matchAll(ma -> ma));
                        }
                        return b;
                    }))
                    .withPageable(PageRequest.of(page - 1, size));

            SearchHits<Document> searchHits = elasticsearchOperations.search(
                    queryBuilder.build(),
                    Document.class,
                    org.springframework.data.elasticsearch.core.mapping.IndexCoordinates.of(config.indexName)
            );

            List<Map<String, Object>> records = new ArrayList<>();
            for (SearchHit<Document> hit : searchHits.getSearchHits()) {
                Map<String, Object> source = new LinkedHashMap<>(hit.getContent());
                source.put("_score", hit.getScore());
                records.add(source);
            }

            log.debug("ES搜索完成: type={}, keyword={}, total={}", type, keyword, searchHits.getTotalHits());
            return new SearchResult(searchHits.getTotalHits(), page, size, records);

        } catch (Exception e) {
            log.error("ES搜索失败: type={}, keyword={}, error={}", type, keyword, e.getMessage());
            return new SearchResult(0, page, size, Collections.emptyList());
        }
    }

    @Override
    public Map<String, Object> getById(String type, Long id) {
        IndexConfig config = INDEX_CONFIGS.get(type);
        if (config == null) {
            return null;
        }
        try {
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.term(t -> t.field("id").value(FieldValue.of(id))))
                    .build();

            SearchHits<Document> hits = elasticsearchOperations.search(
                    query, Document.class,
                    org.springframework.data.elasticsearch.core.mapping.IndexCoordinates.of(config.indexName)
            );

            if (hits.hasSearchHits()) {
                return hits.getSearchHit(0).getContent();
            }
        } catch (Exception e) {
            log.error("ES查询单条失败: type={}, id={}, error={}", type, id, e.getMessage());
        }
        return null;
    }

    /**
     * 索引配置
     */
    private record IndexConfig(String indexName, String[] searchFields) {}

    /**
     * 将Object值转换为ES FieldValue
     */
    private static FieldValue toFieldValue(Object value) {
        if (value instanceof Integer i) {
            return FieldValue.of(i);
        } else if (value instanceof Long l) {
            return FieldValue.of(l);
        } else if (value instanceof Double d) {
            return FieldValue.of(d);
        } else if (value instanceof Boolean b) {
            return FieldValue.of(b);
        } else {
            return FieldValue.of(String.valueOf(value));
        }
    }
}

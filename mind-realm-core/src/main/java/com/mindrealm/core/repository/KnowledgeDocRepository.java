package com.mindrealm.core.repository;

import com.mindrealm.core.entity.KnowledgeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @className: KnowledgeDocRepository
 * @description: 知识库ES文档Repository
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
@Repository
public interface KnowledgeDocRepository extends ElasticsearchRepository<KnowledgeDocument, Long> {
}

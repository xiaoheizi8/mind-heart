package com.mindrealm.core.repository;

import com.mindrealm.core.entity.StoryEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @className: StoryEsRepository
 * @description: 故事ES文档Repository
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Repository
public interface StoryEsRepository extends ElasticsearchRepository<StoryEsDocument, Long> {

    /**
     * 根据状态查询（如待审核）
     */
    List<StoryEsDocument> findByStatus(Integer status);

    /**
     * 根据情绪类型查询
     */
    List<StoryEsDocument> findByEmotionType(String emotionType);
}

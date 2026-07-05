package com.mindrealm.core.repository;

import com.mindrealm.core.entity.DiaryEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @className: DiaryEsRepository
 * @description: 日记ES文档Repository
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Repository
public interface DiaryEsRepository extends ElasticsearchRepository<DiaryEsDocument, Long> {

    /**
     * 根据用户ID查询日记
     */
    List<DiaryEsDocument> findByUserId(Long userId);

    /**
     * 根据情绪分类查询
     */
    List<DiaryEsDocument> findByEmotionCategory(String emotionCategory);
}

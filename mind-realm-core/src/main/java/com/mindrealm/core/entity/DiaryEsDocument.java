package com.mindrealm.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @className: DiaryEsDocument
 * @description: Elasticsearch 日记文档实体，用于管理端全文搜索
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Document(indexName = "mind_realm_diary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryEsDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String emotionCategory;

    @Field(type = FieldType.Double)
    private BigDecimal emotionScore;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String emotionTags;

    @Field(type = FieldType.Text)
    private String aiAnalysis;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
}

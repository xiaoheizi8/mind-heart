package com.mindrealm.diary.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @className: Diary
 * @description: 情绪日记实体类,用于存储用户的日记记录和情感分析结果
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@TableName("diary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diary implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日记ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 日记内容
     */
    @TableField("content")
    private String content;

    /**
     * 媒体URL(JSON数组)
     */
    @TableField("media_urls")
    private String mediaUrls;

    /**
     * 情感标签(JSON数组)
     */
    @TableField("emotion_tags")
    private String emotionTags;

    /**
     * 情感得分(-1到1)
     */
    @TableField("emotion_score")
    private BigDecimal emotionScore;

    /**
     * 情感类别
     */
    @TableField("emotion_category")
    private String emotionCategory;

    /**
     * AI分析结果
     */
    @TableField("ai_analysis")
    private String aiAnalysis;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
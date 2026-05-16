package com.mindrealm.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: DiaryDTO
 * @description: 日记数据传输对象,用于API接口返回日记数据
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDTO {
    
    /**
     * 日记ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 日记内容
     */
    private String content;
    
    /**
     * 媒体URL列表
     */
    private List<String> mediaUrls;
    
    /**
     * 情感标签列表
     */
    private List<String> emotionTags;
    
    /**
     * 情感得分
     */
    private BigDecimal emotionScore;
    
    /**
     * 情感类别
     */
    private String emotionCategory;
    
    /**
     * AI分析结果
     */
    private String aiAnalysis;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
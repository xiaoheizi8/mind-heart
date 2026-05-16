package com.mindrealm.diary.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @className: EmotionReport
 * @description: 情绪报告实体类,存储用户的情绪报告数据
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@TableName("emotion_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报告ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报告类型: week/month/quarter
     */
    @TableField("report_type")
    private String reportType;

    /**
     * 周期开始日期
     */
    @TableField("period_start")
    private LocalDate periodStart;

    /**
     * 周期结束日期
     */
    @TableField("period_end")
    private LocalDate periodEnd;

    /**
     * 报告摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 情绪统计数据(JSON)
     */
    @TableField("emotion_stats")
    private String emotionStats;

    /**
     * 情绪分布(JSON)
     */
    @TableField("emotion_distribution")
    private String emotionDistribution;

    /**
     * 趋势分析
     */
    @TableField("trend_analysis")
    private String trendAnalysis;

    /**
     * AI分析报告
     */
    @TableField("ai_analysis")
    private String aiAnalysis;

    /**
     * 日记数量
     */
    @TableField("diary_count")
    private Integer diaryCount;

    /**
     * 平均情绪得分
     */
    @TableField("avg_emotion_score")
    private BigDecimal avgEmotionScore;

    /**
     * 主要情绪
     */
    @TableField("main_emotion")
    private String mainEmotion;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

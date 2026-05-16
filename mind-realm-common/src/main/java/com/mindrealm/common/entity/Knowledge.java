package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: Knowledge
 * @description: 心理知识库实体类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("knowledge_base")
public class Knowledge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 分类(DSM5/CBT/case/skill/knowledge)
     */
    @TableField("category")
    private String category;

    /**
     * 来源
     */
    @TableField("source")
    private String source;

    /**
     * 标签(JSON数组),例如: ["抑郁", "心理健康", "预防"]
     */
    @TableField("tags")
    private String tags;

    /**
     * 摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 状态 (0 草稿 1 已发布 2 已下线)
     */
    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    @Builder.Default
    private Integer viewCount = 0;
}

package com.mindrealm.story.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: WarmReplyTemplate
 * @description: 温暖回复模板实体类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@TableName("warm_reply_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarmReplyTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 分类：encourage/empathy/advice
     */
    @TableField("category")
    private String category;

    /**
     * 模板内容
     */
    @TableField("content")
    private String content;

    /**
     * 适用情绪类型
     */
    @TableField("emotion_type")
    private String emotionType;

    /**
     * 使用次数
     */
    @TableField("use_count")
    private Integer useCount;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Integer isActive;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

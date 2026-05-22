package com.mindrealm.story.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: StoryComment
 * @description: 故事评论实体类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@TableName("story_comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryComment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 故事ID
     */
    @TableField("story_id")
    private Long storyId;

    /**
     * 评论者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 父评论ID（支持回复）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 是否使用模板：1是 0否
     */
    @TableField("is_template")
    private Integer isTemplate;

    /**
     * 模板ID
     */
    @TableField("template_id")
    private Integer templateId;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 状态：1正常 2已删除 3已屏蔽
     */
    @TableField("status")
    private Integer status;

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
}

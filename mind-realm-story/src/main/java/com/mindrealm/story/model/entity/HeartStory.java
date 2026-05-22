package com.mindrealm.story.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: HeartStory
 * @description: 匿名故事实体类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@TableName("heart_story")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeartStory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 故事ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发布者ID（后台关联，前端匿名）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 故事标题
     */
    @TableField("title")
    private String title;

    /**
     * 故事内容
     */
    @TableField("content")
    private String content;

    /**
     * 情绪类型：happy/sad/anxious/angry/calm
     */
    @TableField("emotion_type")
    private String emotionType;

    /**
     * 标签，逗号分隔：#学业压力 #人际关系
     */
    @TableField("tags")
    private String tags;

    /**
     * 封面图片URL
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 分享数
     */
    @TableField("share_count")
    private Integer shareCount;

    /**
     * 浏览数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 状态：0待审核 1已通过 2已拒绝 3已下架
     */
    @TableField("status")
    private Integer status;

    /**
     * 拒绝原因
     */
    @TableField("reject_reason")
    private String rejectReason;

    /**
     * 审核人ID
     */
    @TableField("auditor_id")
    private Long auditorId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 是否匿名：1匿名 0实名（默认匿名）
     */
    @TableField("is_anonymous")
    private Integer isAnonymous;

    /**
     * 展示昵称（可自定义或使用系统生成）
     */
    @TableField("display_nickname")
    private String displayNickname;

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
     * 发布时间（审核通过后）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField("published_at")
    private LocalDateTime publishedAt;
}


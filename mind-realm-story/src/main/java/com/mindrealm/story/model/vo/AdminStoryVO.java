package com.mindrealm.story.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: AdminStoryVO
 * @description: 管理员用故事详情VO（包含userId）
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStoryVO {

    /**
     * 故事ID
     */
    private Long id;

    /**
     * 发布者ID（管理员可见）
     */
    private Long userId;

    /**
     * 故事标题
     */
    private String title;

    /**
     * 故事内容
     */
    private String content;

    /**
     * 情绪类型
     */
    private String emotionType;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 展示昵称
     */
    private String displayNickname;

    /**
     * 故事状态：0待审核 1已发布 2已拒绝 3已下架
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime auditTime;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime publishedAt;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;
}

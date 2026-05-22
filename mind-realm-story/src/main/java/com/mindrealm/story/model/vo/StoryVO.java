package com.mindrealm.story.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: StoryVO
 * @description: 故事详情VO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryVO {
    
    /**
     * 故事ID
     */
    private Long id;
    
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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime publishedAt;
    
    /**
     * 当前用户是否点赞
     */
    private Boolean liked;
    
    /**
     * 当前用户是否收藏
     */
    private Boolean favorited;
}
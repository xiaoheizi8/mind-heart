package com.mindrealm.story.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @className: CommentVO
 * @description: 评论VO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 故事ID
     */
    private Long storyId;
    
    /**
     * 评论者昵称
     */
    private String nickname;
    
    /**
     * 评论者头像
     */
    private String avatar;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 是否是回复
     */
    private Boolean isReply;
    
    /**
     * 回复目标昵称
     */
    private String replyToNickname;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
}
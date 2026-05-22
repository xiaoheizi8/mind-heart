package com.mindrealm.story.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @className: AddCommentRequest
 * @description: 添加评论请求DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.5.19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequest {
    
    /**
     * 故事ID
     */
    private Long storyId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论长度不能超过1000字符")
    private String content;
    
    /**
     * 父评论ID（回复时使用）
     */
    private Long parentId;
}
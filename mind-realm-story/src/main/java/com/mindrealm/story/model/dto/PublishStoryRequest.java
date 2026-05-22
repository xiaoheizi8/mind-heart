package com.mindrealm.story.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @className: PublishStoryRequest
 * @description: 发布故事请求DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishStoryRequest {
    
    /**
     * 故事标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
    
    /**
     * 故事内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 10000, message = "内容长度不能超过10000字符")
    private String content;
    
    /**
     * 情绪类型：happy/sad/anxious/angry/calm
     */
    private String emotionType;
    
    /**
     * 标签，逗号分隔
     */
    @Size(max = 500, message = "标签长度不能超过500字符")
    private String tags;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 是否匿名：true匿名 false实名
     */
    @Builder.Default
    private Boolean isAnonymous = true;
}
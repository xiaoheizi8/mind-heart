package com.mindrealm.story.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @className: StoryQueryDTO
 * @description: 故事查询DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryQueryDTO {
    
    /**
     * 情绪类型筛选
     */
    private String emotionType;
    
    /**
     * 排序方式：hot/new/random
     */
    @Builder.Default
    private String sortBy = "new";
    
    /**
     * 页码
     */
    @Builder.Default
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    @Builder.Default
    private Integer size = 10;
}
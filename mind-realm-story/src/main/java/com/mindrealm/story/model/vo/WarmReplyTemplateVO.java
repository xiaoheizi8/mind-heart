package com.mindrealm.story.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: WarmReplyTemplateVO
 * @description: 温暖回复模板VO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmReplyTemplateVO {
    
    /**
     * 模板ID
     */
    private Integer id;
    
    /**
     * 分类：encourage/empathy/advice
     */
    private String category;
    
    /**
     * 模板内容
     */
    private String content;
}
package com.mindrealm.story.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mindrealm.story.model.entity.WarmReplyTemplate;

import java.util.List;

/**
 * @className: WarmReplyTemplateService
 * @description: 温暖回复模板服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
public interface WarmReplyTemplateService extends IService<WarmReplyTemplate> {
    
    /**
     * 根据情绪类型获取模板列表
     * @param emotionType 情绪类型
     * @return 模板列表
     */
    List<WarmReplyTemplate> getTemplatesByEmotion(String emotionType);
    
    /**
     * 根据分类获取模板列表
     * @param category 分类
     * @return 模板列表
     */
    List<WarmReplyTemplate> getTemplatesByCategory(String category);
    
    /**
     * 获取随机模板
     * @param emotionType 情绪类型
     * @return 随机模板
     */
    WarmReplyTemplate getRandomTemplate(String emotionType);
    
    /**
     * 增加使用次数
     * @param templateId 模板ID
     */
    void incrementUseCount(Integer templateId);
}

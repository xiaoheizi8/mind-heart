package com.mindrealm.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mindrealm.story.model.entity.WarmReplyTemplate;
import com.mindrealm.story.service.WarmReplyTemplateService;
import com.mindrealm.story.mapper.WarmReplyTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @className: WarmReplyTemplateServiceImpl
 * @description: 温暖回复模板服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Service
public class WarmReplyTemplateServiceImpl extends ServiceImpl<WarmReplyTemplateMapper, WarmReplyTemplate>
    implements WarmReplyTemplateService {

    private static final Logger log = LoggerFactory.getLogger(WarmReplyTemplateServiceImpl.class);

    @Override
    public List<WarmReplyTemplate> getTemplatesByEmotion(String emotionType) {
        LambdaQueryWrapper<WarmReplyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarmReplyTemplate::getEmotionType, emotionType)
               .eq(WarmReplyTemplate::getIsActive, 1)
               .orderByDesc(WarmReplyTemplate::getUseCount);
        
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<WarmReplyTemplate> getTemplatesByCategory(String category) {
        LambdaQueryWrapper<WarmReplyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarmReplyTemplate::getCategory, category)
               .eq(WarmReplyTemplate::getIsActive, 1)
               .orderByDesc(WarmReplyTemplate::getUseCount);
        
        return baseMapper.selectList(wrapper);
    }

    @Override
    public WarmReplyTemplate getRandomTemplate(String emotionType) {
        LambdaQueryWrapper<WarmReplyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarmReplyTemplate::getEmotionType, emotionType)
               .eq(WarmReplyTemplate::getIsActive, 1)
               .last("ORDER BY RAND() LIMIT 1");
        
        List<WarmReplyTemplate> templates = baseMapper.selectList(wrapper);
        return templates.isEmpty() ? null : templates.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementUseCount(Integer templateId) {
        WarmReplyTemplate template = baseMapper.selectById(templateId);
        if (template != null) {
            template.setUseCount(template.getUseCount() + 1);
            baseMapper.updateById(template);
        }
    }
}





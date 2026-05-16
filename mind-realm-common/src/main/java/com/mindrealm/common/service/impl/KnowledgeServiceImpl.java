package com.mindrealm.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.mapper.KnowledgeMapper;
import com.mindrealm.common.service.IKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @className: KnowledgeServiceImpl
 * @description: 知识库服务实现类,提供知识查询、标签分类等功能
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class KnowledgeServiceImpl implements IKnowledgeService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeServiceImpl.class);

    private final KnowledgeMapper knowledgeMapper;

    public KnowledgeServiceImpl(KnowledgeMapper knowledgeMapper) {
        this.knowledgeMapper = knowledgeMapper;
    }

    /**
     * 分页查询知识列表(支持分类、关键词、标签、状态过滤)
     */
    @Override
    public Page<Knowledge> list(String category, String tag, String keyword, Integer status, int pageNum, int pageSize) {
        logger.debug("查询知识列表: category={}, tag={}, keyword={}, status={}, pageNum={}, pageSize={}", 
                category, tag, keyword, status, pageNum, pageSize);
        Page<Knowledge> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        
        // 分类过滤
        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            wrapper.eq(Knowledge::getCategory, category);
        }
        
        // 标签过滤 (JSON数组包含查询)
        if (tag != null && !tag.isEmpty()) {
            wrapper.like(Knowledge::getTags, "\"" + tag + "\"");
        }
        
        // 关键词搜索 (标题或内容)
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Knowledge::getTitle, keyword)
                    .or().like(Knowledge::getContent, keyword));
        }
        
        // 状态过滤 (默认查询已发布)
        if (status != null) {
            wrapper.eq(Knowledge::getStatus, status);
        } else {
            wrapper.eq(Knowledge::getStatus, 1); // 默认只查询已发布的知识
        }
        
        // 按创建时间降序
        wrapper.orderByDesc(Knowledge::getCreatedAt);
        
        return knowledgeMapper.selectPage(page, wrapper);
    }

    /**
     * 获取知识详情(增加浏览次数)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Knowledge getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("知识ID不能为空");
        }
        logger.debug("获取知识详情: id={}", id);
        Knowledge knowledge = knowledgeMapper.selectById(id);
        if (knowledge != null) {
            // 增加浏览次数
            knowledge.setViewCount(knowledge.getViewCount() == null ? 1 : knowledge.getViewCount() + 1);
            knowledgeMapper.updateById(knowledge);
        }
        return knowledge;
    }

    /**
     * 获取推荐知识(最新的已发布知识)
     */
    @Override
    public List<Knowledge> getRecommend(int limit) {
        logger.debug("获取推荐知识: limit={}", limit);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Knowledge::getStatus, 1) // 只查询已发布的
               .orderByDesc(Knowledge::getCreatedAt)
               .last("LIMIT " + limit);
        return knowledgeMapper.selectList(wrapper);
    }

    /**
     * 获取所有标签列表(从tags JSON字段中提取)
     */
    @Override
    public List<String> getAllTags() {
        // 查询所有已发布知识的tags字段
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Knowledge> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("status", 1)
               .select("tags")
               .isNotNull("tags");
        
        List<Knowledge> knowledgeList = knowledgeMapper.selectList(wrapper);
        
        // 提取并去重标签
        return knowledgeList.stream()
                .filter(k -> k.getTags() != null && !k.getTags().isEmpty())
                .flatMap(k -> {
                    // 解析JSON数组: ["标签1", "标签2"]
                    String tagsStr = k.getTags().replace("[", "").replace("]", "").replace("\"", "");
                    return List.of(tagsStr.split(",")).stream()
                            .map(String::trim)
                            .filter(t -> !t.isEmpty());
                })
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * 根据标签查询知识列表
     */
    @Override
    public Page<Knowledge> listByTag(String tag, int pageNum, int pageSize) {
        return list(null, tag, null, 1, pageNum, pageSize);
    }

    /**
     * 新增知识
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Knowledge knowledge) {
        if (knowledge == null) {
            throw new IllegalArgumentException("知识对象不能为空");
        }
        if (knowledge.getStatus() == null) {
            knowledge.setStatus(1); // 默认已发布
        }
        if (knowledge.getViewCount() == null) {
            knowledge.setViewCount(0);
        }
        logger.info("新增知识: title={}", knowledge.getTitle());
        return knowledgeMapper.insert(knowledge) > 0;
    }

    /**
     * 更新知识
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Knowledge knowledge) {
        if (knowledge == null || knowledge.getId() == null) {
            throw new IllegalArgumentException("知识对象和ID不能为空");
        }
        logger.info("更新知识: id={}", knowledge.getId());
        return knowledgeMapper.updateById(knowledge) > 0;
    }

    /**
     * 删除知识
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("知识ID不能为空");
        }
        logger.info("删除知识: id={}", id);
        return knowledgeMapper.deleteById(id) > 0;
    }
}
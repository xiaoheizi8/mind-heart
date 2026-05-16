package com.mindrealm.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.Knowledge;

import java.util.List;

/**
 * @className: IKnowledgeService
 * @description: 知识库服务接口,提供知识查询、标签分类等功能
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IKnowledgeService {

    /**
     * 分页查询知识列表(支持分类、关键词、标签、状态过滤)
     * 
     * @param category 分类 (DSM5/CBT/case/skill/knowledge)
     * @param tag 标签 (支持模糊匹配JSON数组中的标签)
     * @param keyword 关键词 (搜索标题和内容)
     * @param status 状态 (0 草稿 1 已发布 2 已下线),null表示查询已发布
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Knowledge> list(String category, String tag, String keyword, Integer status, int pageNum, int pageSize);

    /**
     * 获取知识详情(增加浏览次数)
     * 
     * @param id 知识ID
     * @return 知识详情
     */
    Knowledge getById(Long id);

    /**
     * 获取推荐知识(最新的已发布知识)
     * 
     * @param limit 数量限制
     * @return 推荐知识列表
     */
    List<Knowledge> getRecommend(int limit);

    /**
     * 获取所有标签列表(从tags JSON字段中提取)
     * 
     * @return 标签列表
     */
    List<String> getAllTags();

    /**
     * 根据标签查询知识列表
     * 
     * @param tag 标签名称
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Knowledge> listByTag(String tag, int pageNum, int pageSize);

    /**
     * 新增知识
     * 
     * @param knowledge 知识对象
     * @return 是否成功
     */
    boolean save(Knowledge knowledge);

    /**
     * 更新知识
     * 
     * @param knowledge 知识对象
     * @return 是否成功
     */
    boolean update(Knowledge knowledge);

    /**
     * 删除知识
     * 
     * @param id 知识ID
     * @return 是否成功
     */
    boolean delete(Long id);
}

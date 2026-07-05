package com.mindrealm.core.service;

import java.util.Map;

/**
 * @className: EsSearchService
 * @description: ES全文搜索服务接口，用于管理端搜索已同步的数据
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
public interface EsSearchService {

    /**
     * 全文搜索
     * @param type 索引类型: diary / story
     * @param keyword 搜索关键词
     * @param filters 额外过滤条件 (如 status, emotionCategory, emotionType)
     * @param page 页码(从1开始)
     * @param size 每页数量
     * @return 搜索结果
     */
    SearchResult search(String type, String keyword, Map<String, Object> filters, int page, int size);

    /**
     * 搜索单条记录（用于验证同步）
     * @param type 索引类型
     * @param id 文档ID
     * @return 文档Map，不存在返回null
     */
    Map<String, Object> getById(String type, Long id);

    /**
     * 搜索结果封装
     */
    record SearchResult(
            long total,
            int page,
            int size,
            java.util.List<Map<String, Object>> records
    ) {}
}

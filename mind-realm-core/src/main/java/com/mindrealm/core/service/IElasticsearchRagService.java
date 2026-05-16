package com.mindrealm.core.service;

import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.core.entity.KnowledgeDocument;

import java.util.List;

/**
 * @className: IElasticsearchRagService
 * @description: Elasticsearch RAG服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IElasticsearchRagService {

    /**
     * 初始化ES索引
     */
    void init();

    /**
     * 保存或更新文档
     * @param doc 知识文档
     */
    void save(KnowledgeDocument doc);

    /**
     * 批量保存文档
     * @param docs 文档列表
     */
    void saveBatch(List<KnowledgeDocument> docs);

    /**
     * 混合搜索
     * @param query 查询文本
     * @param category 分类
     * @param topK 返回数量
     * @return 文档列表
     */
    List<KnowledgeDocument> hybridSearch(String query, String category, int topK);

    /**
     * 向量搜索
     * @param query 查询文本
     * @param topK 返回数量
     * @return 文档列表
     */
    List<KnowledgeDocument> vectorSearch(String query, int topK);

    /**
     * 删除文档
     * @param id 文档ID
     */
    void deleteById(Long id);

    /**
     * 从数据库同步
     * @param batchSize 批次大小
     */
    void syncFromDatabase(int batchSize);

    /**
     * 同步单条记录
     * @param knowledge 知识对象
     */
    void syncSingle(Knowledge knowledge);

    /**
     * 检查是否已初始化
     * @return 是否已初始化
     */
    boolean isInitialized();

    /**
     * 获取文档数量
     * @return 数量
     */
    long count();
}

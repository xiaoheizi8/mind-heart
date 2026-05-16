package com.mindrealm.core.service;

import java.util.List;

/**
 * @className: EmbeddingService
 * @description: 文本向量嵌入服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
public interface EmbeddingService {

    /**
     * 将单条文本转换为向量
     * @param text 输入文本
     * @return 向量数组
     */
    float[] embed(String text);

    /**
     * 将多条文本批量转换为向量
     * @param texts 文本列表
     * @return 向量数组列表
     */
    List<float[]> embedBatch(List<String> texts);

    /**
     * 获取向量维度
     * @return 维度数
     */
    int getDimension();
}

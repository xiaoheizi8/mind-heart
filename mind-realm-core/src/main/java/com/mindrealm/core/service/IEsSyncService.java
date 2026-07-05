package com.mindrealm.core.service;

import java.util.Map;

/**
 * @className: IEsSyncService
 * @description: 通用ES数据同步服务接口，用于管理端手动触发同步操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
public interface IEsSyncService {

    /**
     * 全量同步：将MySQL数据分页读取并批量写入ES
     * @param batchSize 每批同步数量
     * @return 同步成功的记录数
     */
    int fullSync(int batchSize);

    /**
     * 根据ID同步单条记录
     * @param id 记录ID
     * @return 是否成功
     */
    boolean syncById(Long id);

    /**
     * 根据ID从ES删除记录
     * @param id 记录ID
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 重建索引：删除旧索引 → 创建新索引 → 全量同步
     * @param batchSize 每批同步数量
     * @return 同步成功的记录数
     */
    int rebuildIndex(int batchSize);

    /**
     * 获取同步状态
     * @return map包含 esCount(ES中的文档数), dbCount(MySQL中的记录数)
     */
    Map<String, Long> getSyncStatus();

    /**
     * 获取此同步服务对应的实体类型名称
     */
    String getType();
}

package com.mindrealm.core.service.impl;

import com.mindrealm.core.service.IEsSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @className: BaseEsSyncService
 * @description: ES同步服务抽象基类，提供全量同步、重建索引等通用逻辑。
 *               子类只需提供: ES Repository、DB分页查询函数、DB计数函数、文档类型。
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
public abstract class BaseEsSyncService<E, D> implements IEsSyncService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final ElasticsearchOperations elasticsearchOperations;
    protected final ElasticsearchRepository<D, Long> repository;
    protected final Class<D> documentClass;
    protected final String typeName;

    public BaseEsSyncService(ElasticsearchOperations elasticsearchOperations,
                             ElasticsearchRepository<D, Long> repository,
                             Class<D> documentClass,
                             String typeName) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.repository = repository;
        this.documentClass = documentClass;
        this.typeName = typeName;
    }

    /**
     * 从MySQL分页查询DB实体数据
     * @param page 页码(从1开始)
     * @param size 每页大小
     * @return DB实体列表
     */
    protected abstract List<E> fetchFromDb(int page, int size);

    /**
     * 将DB实体转换为ES文档
     */
    protected abstract D convertToDocument(E entity);

    /**
     * 根据ID从MySQL查询单条实体
     * @param id 实体ID
     * @return 实体或null
     */
    protected abstract E fetchByIdFromDb(Long id);

    /**
     * MySQL中的总记录数
     */
    protected abstract long countInDb();

    @Override
    public int fullSync(int batchSize) {
        log.info("[{}] 开始全量同步到ES, batchSize={}", typeName, batchSize);
        int page = 1;
        int totalSynced = 0;

        try {
            while (true) {
                List<E> records = fetchFromDb(page, batchSize);
                if (records == null || records.isEmpty()) {
                    break;
                }

                List<D> docs = records.stream()
                        .map(this::convertToDocument)
                        .collect(Collectors.toList());

                repository.saveAll(docs);
                totalSynced += docs.size();
                page++;

                log.debug("[{}] 同步进度: page={}, synced={}", typeName, page - 1, totalSynced);
            }

            log.info("[{}] 全量同步完成: totalSynced={}", typeName, totalSynced);
        } catch (Exception e) {
            log.error("[{}] 全量同步失败: {}", typeName, e.getMessage(), e);
        }

        return totalSynced;
    }

    @Override
    public boolean syncById(Long id) {
        try {
            E entity = fetchByIdFromDb(id);
            if (entity == null) {
                log.warn("[{}] 未找到ID={}的记录，跳过ES同步", typeName, id);
                return false;
            }
            D doc = convertToDocument(entity);
            repository.save(doc);
            log.debug("[{}] 单条同步成功: id={}", typeName, id);
            return true;
        } catch (Exception e) {
            log.error("[{}] 单条同步失败: id={}, error={}", typeName, id, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            repository.deleteById(id);
            log.debug("[{}] 从ES删除成功: id={}", typeName, id);
            return true;
        } catch (Exception e) {
            log.error("[{}] 从ES删除失败: id={}, error={}", typeName, id, e.getMessage());
            return false;
        }
    }

    @Override
    public int rebuildIndex(int batchSize) {
        log.info("[{}] 开始重建ES索引...", typeName);

        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(documentClass);
            if (indexOps.exists()) {
                indexOps.delete();
                log.info("[{}] 旧索引已删除", typeName);
            }
            indexOps.create();
            log.info("[{}] 新索引已创建", typeName);
        } catch (Exception e) {
            log.error("[{}] 索引重建失败: {}", typeName, e.getMessage(), e);
            return 0;
        }

        return fullSync(batchSize);
    }

    @Override
    public Map<String, Long> getSyncStatus() {
        Map<String, Long> status = new HashMap<>();
        try {
            status.put("esCount", repository.count());
        } catch (Exception e) {
            status.put("esCount", -1L);
        }
        try {
            status.put("dbCount", countInDb());
        } catch (Exception e) {
            status.put("dbCount", -1L);
        }
        return status;
    }

    @Override
    public String getType() {
        return typeName;
    }
}

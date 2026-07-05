package com.mindrealm.api.controller.admin;

import com.mindrealm.common.result.Result;
import com.mindrealm.core.service.EsSearchService;
import com.mindrealm.core.service.IEsSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: AdminEsSyncController
 * @description: 管理端ES数据同步控制器，提供手动触发MySQL→ES同步的API
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@RestController
@RequestMapping("/admin/v1/es")
@Tag(name = "ES数据同步管理")
public class AdminEsSyncController {

    private static final Logger log = LoggerFactory.getLogger(AdminEsSyncController.class);

    private final Map<String, IEsSyncService> syncServiceMap;
    private final EsSearchService esSearchService;

    /**
     * Spring自动注入所有 IEsSyncService 实现
     */
    public AdminEsSyncController(List<IEsSyncService> syncServices, EsSearchService esSearchService) {
        this.syncServiceMap = new LinkedHashMap<>();
        for (IEsSyncService service : syncServices) {
            syncServiceMap.put(service.getType(), service);
        }
        this.esSearchService = esSearchService;
        log.info("ES同步控制器已初始化，已注册的同步服务: {}", syncServiceMap.keySet());
    }

    /**
     * 获取所有索引的同步状态
     */
    @GetMapping("/status")
    @Operation(summary = "查看所有索引的同步状态")
    public Result<Map<String, Map<String, Long>>> getSyncStatus() {
        Map<String, Map<String, Long>> allStatus = new LinkedHashMap<>();
        for (Map.Entry<String, IEsSyncService> entry : syncServiceMap.entrySet()) {
            allStatus.put(entry.getKey(), entry.getValue().getSyncStatus());
        }
        return Result.success(allStatus);
    }

    /**
     * 获取单个索引的同步状态
     */
    @GetMapping("/status/{type}")
    @Operation(summary = "查看单个索引的同步状态")
    public Result<Map<String, Long>> getSyncStatusByType(@PathVariable String type) {
        IEsSyncService service = syncServiceMap.get(type);
        if (service == null) {
            return Result.error("不支持的同步类型: " + type + "，可选: " + syncServiceMap.keySet());
        }
        return Result.success(service.getSyncStatus());
    }

    /**
     * 全量同步
     */
    @PostMapping("/sync/{type}/full")
    @Operation(summary = "全量同步MySQL数据到ES")
    public Result<Map<String, Object>> fullSync(
            @PathVariable String type,
            @RequestParam(defaultValue = "500") int batchSize) {

        IEsSyncService service = syncServiceMap.get(type);
        if (service == null) {
            return Result.error("不支持的同步类型: " + type + "，可选: " + syncServiceMap.keySet());
        }

        long startTime = System.currentTimeMillis();
        int count = service.fullSync(batchSize);
        long elapsed = System.currentTimeMillis() - startTime;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("syncedCount", count);
        result.put("elapsedMs", elapsed);
        result.put("syncStatus", service.getSyncStatus());

        log.info("管理端手动全量同步完成: type={}, count={}, elapsed={}ms", type, count, elapsed);
        return Result.success(result);
    }

    /**
     * 单条同步
     */
    @PostMapping("/sync/{type}/{id}")
    @Operation(summary = "同步单条记录到ES")
    public Result<Map<String, Object>> syncById(
            @PathVariable String type,
            @PathVariable Long id) {

        IEsSyncService service = syncServiceMap.get(type);
        if (service == null) {
            return Result.error("不支持的同步类型: " + type + "，可选: " + syncServiceMap.keySet());
        }

        boolean success = service.syncById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("id", id);
        result.put("success", success);
        return Result.success(result);
    }

    /**
     * 从ES删除单条记录
     */
    @DeleteMapping("/sync/{type}/{id}")
    @Operation(summary = "从ES删除单条记录")
    public Result<Map<String, Object>> deleteById(
            @PathVariable String type,
            @PathVariable Long id) {

        IEsSyncService service = syncServiceMap.get(type);
        if (service == null) {
            return Result.error("不支持的同步类型: " + type + "，可选: " + syncServiceMap.keySet());
        }

        boolean success = service.deleteById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("id", id);
        result.put("success", success);
        return Result.success(result);
    }

    /**
     * 重建索引（危险操作）
     */
    @PostMapping("/sync/{type}/rebuild")
    @Operation(summary = "重建ES索引并全量同步（危险操作）")
    public Result<Map<String, Object>> rebuildIndex(
            @PathVariable String type,
            @RequestParam(defaultValue = "500") int batchSize) {

        IEsSyncService service = syncServiceMap.get(type);
        if (service == null) {
            return Result.error("不支持的同步类型: " + type + "，可选: " + syncServiceMap.keySet());
        }

        log.warn("管理端触发ES索引重建: type={}", type);

        long startTime = System.currentTimeMillis();
        int count = service.rebuildIndex(batchSize);
        long elapsed = System.currentTimeMillis() - startTime;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("syncedCount", count);
        result.put("elapsedMs", elapsed);
        result.put("syncStatus", service.getSyncStatus());

        log.info("ES索引重建完成: type={}, count={}, elapsed={}ms", type, count, elapsed);
        return Result.success(result);
    }

    /**
     * 列出所有支持的同步类型
     */
    @GetMapping("/types")
    @Operation(summary = "列出所有支持的同步类型")
    public Result<Map<String, Object>> getTypes() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("types", syncServiceMap.keySet());
        result.put("note", "使用 /admin/v1/es/status 查看各类型的同步状态");
        return Result.success(result);
    }

    // ==================== ES数据查询 ====================

    /**
     * 全文搜索ES数据
     */
    @GetMapping("/search")
    @Operation(summary = "全文搜索ES中的同步数据")
    public Result<Map<String, Object>> search(
            @RequestParam String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String emotionCategory,
            @RequestParam(required = false) String emotionType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!syncServiceMap.containsKey(type)) {
            return Result.error("不支持的搜索类型: " + type + "，可选: " + syncServiceMap.keySet());
        }

        // 构建过滤条件
        Map<String, Object> filters = new LinkedHashMap<>();
        if (status != null) filters.put("status", status);
        if (emotionCategory != null) filters.put("emotionCategory", emotionCategory);
        if (emotionType != null) filters.put("emotionType", emotionType);

        EsSearchService.SearchResult result = esSearchService.search(type, keyword, filters, page, size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", result.total());
        response.put("page", result.page());
        response.put("size", result.size());
        response.put("records", result.records());
        return Result.success(response);
    }

    /**
     * 根据ID查询ES中的单条文档（验证同步）
     */
    @GetMapping("/search/{type}/{id}")
    @Operation(summary = "根据ID查询ES中的单条文档")
    public Result<Map<String, Object>> getById(
            @PathVariable String type,
            @PathVariable Long id) {

        if (!syncServiceMap.containsKey(type)) {
            return Result.error("不支持的类型: " + type);
        }

        Map<String, Object> doc = esSearchService.getById(type, id);
        if (doc == null) {
            return Result.error("未找到该文档: type=" + type + ", id=" + id);
        }
        return Result.success(doc);
    }
}

package com.mindrealm.api.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.mapper.KnowledgeMapper;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.util.TimeUtil;
import com.mindrealm.core.service.IElasticsearchRagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端知识库控制器
 */
@RestController
@RequestMapping("/admin/v1/knowledge")
public class AdminKnowledgeController {

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    private final IElasticsearchRagService elasticsearchRagService;

    public AdminKnowledgeController(IElasticsearchRagService elasticsearchRagService) {
        this.elasticsearchRagService = elasticsearchRagService;
    }

    /**
     * 知识库列表
     */
    @GetMapping("/list")
    @OperationLog(value = "查询知识库列表", type = OperationType.SELECT)
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        
        Page<Knowledge> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Knowledge::getTitle, keyword)
                   .or()
                   .like(Knowledge::getContent, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Knowledge::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Knowledge::getStatus, status);
        }
        
        wrapper.orderByDesc(Knowledge::getCreatedAt);
        
        Page<Knowledge> result = knowledgeMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        
        return Result.success(data);
    }

    /**
     * 知识详情
     */
    @GetMapping("/{id}")
    @OperationLog(value = "查询知识详情", type = OperationType.SELECT)
    public Result<Knowledge> getById(@PathVariable Long id) {
        Knowledge knowledge = knowledgeMapper.selectById(id);
        return Result.success(knowledge);
    }

    /**
     * 创建知识
     */
    @PostMapping("/create")
    @OperationLog(value = "创建知识", type = OperationType.INSERT)
    public Result<Knowledge> create(@RequestBody Knowledge knowledge) {
        knowledge.setCreatedAt(TimeUtil.now());
        knowledge.setStatus(1);
        knowledgeMapper.insert(knowledge);
        // 同步到ES
        elasticsearchRagService.syncSingle(knowledge);
        return Result.success(knowledge);
    }

    /**
     * 更新知识
     */
    @PutMapping("/{id}")
    @OperationLog(value = "更新知识", type = OperationType.UPDATE)
    public Result<Void> update(@PathVariable Long id, @RequestBody Knowledge knowledge) {
        knowledge.setId(id);
        knowledgeMapper.updateById(knowledge);
        // 同步到ES
        Knowledge updated = knowledgeMapper.selectById(id);
        if (updated != null) {
            elasticsearchRagService.syncSingle(updated);
        }
        return Result.ok("更新成功");
    }

    /**
     * 删除知识
     */
    @DeleteMapping("/{id}")
    @OperationLog(value = "删除知识", type = OperationType.DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeMapper.deleteById(id);
        elasticsearchRagService.deleteById(id);
        return Result.ok("删除成功");
    }

    /**
     * 同步所有知识库到Elasticsearch
     */
    @PostMapping("/sync-to-es")
    @OperationLog(value = "同步知识库到ES", type = OperationType.INSERT)
    public Result<Map<String, Object>> syncToEs() {
        elasticsearchRagService.syncFromDatabase(50);
        Map<String, Object> data = new HashMap<>();
        data.put("esDocCount", elasticsearchRagService.count());
        data.put("message", "同步完成");
        return Result.success(data);
    }

    /**
     * 获取ES状态
     */
    @GetMapping("/es-status")
    @OperationLog(value = "查询ES状态", type = OperationType.SELECT)
    public Result<Map<String, Object>> getEsStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("initialized", elasticsearchRagService.isInitialized());
        data.put("docCount", elasticsearchRagService.count());
        return Result.success(data);
    }
}

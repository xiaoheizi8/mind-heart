package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.entity.UserCollect;
import com.mindrealm.common.mapper.KnowledgeMapper;
import com.mindrealm.common.mapper.UserCollectMapper;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.IKnowledgeService;
import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;
import com.mindrealm.core.rag.service.IUnifiedRagService;
import com.mindrealm.common.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识库控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/knowledge")
public class KnowledgeController {

    private final IKnowledgeService knowledgeService;
    private final IUnifiedRagService ragService;
    private final UserCollectMapper userCollectMapper;
    private final KnowledgeMapper knowledgeMapper;


    @GetMapping("/list")
    public Result<PageResult<Knowledge>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Page<Knowledge> page = knowledgeService.list(category, tag, keyword, status, pageNum, pageSize);
        
        PageResult<Knowledge> result = PageResult.of(
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                (int) page.getTotal()
        );
        
        return Result.success(result);
    }

    /**
     * 获取所有标签列表
     */
    @GetMapping("/tags")
    public Result<List<String>> getAllTags() {
        List<String> tags = knowledgeService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 根据标签查询知识列表
     */
    @GetMapping("/tag/{tag}")
    public Result<PageResult<Knowledge>> listByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Page<Knowledge> page = knowledgeService.listByTag(tag, pageNum, pageSize);
        
        PageResult<Knowledge> result = PageResult.of(
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                (int) page.getTotal()
        );
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Knowledge> getById(@PathVariable Long id) {
        Knowledge knowledge = knowledgeService.getById(id);
        if (knowledge == null) {
            return Result.notFound("知识不存在");
        }
        return Result.success(knowledge);
    }

    @PostMapping("/{id}/collect")
    public Result<Void> collect(@PathVariable Long id) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        Knowledge knowledge = knowledgeService.getById(id);
        if (knowledge == null) {
            return Result.notFound("知识不存在");
        }
        
        UserCollect collect = new UserCollect();
        collect.setUserId(userId);
        collect.setTargetType("knowledge");
        collect.setTargetId(id);
        collect.setCreatedAt(TimeUtil.now());
        
        userCollectMapper.insert(collect);
        
        return Result.ok("收藏成功");
    }

    @DeleteMapping("/{id}/collect")
    public Result<Void> cancelCollect(@PathVariable Long id) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCollect>()
                .eq(UserCollect::getUserId, userId)
                .eq(UserCollect::getTargetType, "knowledge")
                .eq(UserCollect::getTargetId, id);
        
        userCollectMapper.delete(wrapper);
        
        return Result.ok("取消收藏成功");
    }

    @GetMapping("/favorites")
    public Result<PageResult<Knowledge>> favorites(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var collectWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCollect>()
                .eq(UserCollect::getUserId, userId)
                .eq(UserCollect::getTargetType, "knowledge");
        
        List<UserCollect> collects = userCollectMapper.selectList(collectWrapper);
        
        if (collects.isEmpty()) {
            return Result.success(PageResult.of(new ArrayList<>(), pageNum, pageSize, 0));
        }
        
        List<Long> ids = collects.stream().map(UserCollect::getTargetId).toList();
        
        if (ids.isEmpty()) {
            return Result.success(PageResult.of(new ArrayList<>(), pageNum, pageSize, 0));
        }
        
        var knowledgeWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Knowledge>()
                .in(Knowledge::getId, ids);
        
        Page<Knowledge> page = knowledgeMapper.selectPage(new Page<>(pageNum, pageSize), knowledgeWrapper);
        
        PageResult<Knowledge> result = PageResult.of(
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                page.getTotal()
        );
        
        return Result.success(result);
    }

    @GetMapping("/recommend")
    public Result<Map<String, Object>> recommend(@RequestParam(defaultValue = "5") int limit) {
        List<Knowledge> list = knowledgeService.getRecommend(limit);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return Result.success(result);
    }

    @PostMapping("/qa")
    public Result<Map<String, Object>> qa(@RequestBody Map<String, String> request) {
        Long userId = RequestContext.getCurrentUserId();
        
        String question = request.get("question");
        if (!StringUtils.hasText(question)) {
            return Result.badRequest("问题不能为空");
        }

        try {
            RagResponse response = ragService.chat(userId, question);
            if (response.isSuccess()) {
                Map<String, Object> data = new HashMap<>();
                data.put("answer", response.getAnswer());
                data.put("mode", response.getMode());
                data.put("sources", response.getSources());
                return Result.success(data);
            } else {
                return Result.error(response.getError());
            }
        } catch (Exception e) {
            return Result.error("问答服务暂时不可用");
        }
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("knowledgeCount", ragService.getKnowledgeCount());
        data.put("available", ragService.isAvailable());
        data.put("mode", ragService.getCurrentMode());
        return Result.success(data);
    }

    @GetMapping("/modes")
    public Result<Map<String, Object>> getModes() {
        Map<String, Object> data = new HashMap<>();
        data.put("currentMode", ragService.getMode().name());
        data.put("availableModes", ragService.getAvailableModes());
        return Result.success(data);
    }

    @PostMapping("/switch-mode")
    public Result<Void> switchMode(@RequestParam String mode) {
        try {
            var ragMode = com.mindrealm.core.rag.config.RagProperties.Mode.valueOf(mode.toUpperCase());
            ragService.switchMode(ragMode);
            return Result.ok("模式切换成功");
        } catch (IllegalArgumentException e) {
            return Result.badRequest("无效的模式: " + mode);
        }
    }
}
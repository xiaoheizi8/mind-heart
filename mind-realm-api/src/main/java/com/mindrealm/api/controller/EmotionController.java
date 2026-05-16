package com.mindrealm.api.controller;

import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.result.Result;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.emotion.service.EmotionAnalysisService;
import com.mindrealm.emotion.service.IEmotionProfileService;
import com.mindrealm.emotion.service.EmotionProfileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: EmotionController
 * @description: 情感分析控制器,处理文本情感分析请求
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestController
@RequestMapping("/api/v1/emotion")
@Slf4j
public class EmotionController {

    @Autowired
    private EmotionAnalysisService emotionService;

    private final IEmotionProfileService emotionProfileService;

    public EmotionController(IEmotionProfileService emotionProfileService) {
        this.emotionProfileService = emotionProfileService;
    }

    @Autowired
    private DiaryService diaryService;

    /**
     * 分析文本情感
     * @param request 请求体,包含text字段
     * @return 情感分析结果(score和category)
     */
    @PostMapping("/analyze")
    public Result<Map<String, Object>> analyze(@RequestBody(required = false) Map<String, String> request) {
        String text = null;
        if (request != null) {
            text = request.get("text");
        }
        if (text == null || text.isEmpty()) {
            return Result.badRequest("文本不能为空");
        }
        
        var emotionResult = emotionService.analyzeText(text);
        
        Map<String, Object> data = new HashMap<>();
        data.put("score", emotionResult.getScore());
        data.put("category", emotionResult.getCategory());
        log.info("分析结果: %s".formatted(emotionResult));
        
        return Result.success(data);
    }

    /**
     * 获取情感画像
     * @param days 天数(默认30)
     * @return 情感画像数据
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(
            @RequestParam(defaultValue = "30") int days) {
        
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var diaries = diaryService.getRecentByUser(userId, days);
        
        var diaryDataList = diaries.stream().map(d -> {
            var diary = new EmotionProfileServiceImpl.DiaryData();
            diary.setId(d.getId());
            diary.setUserId(d.getUserId());
            diary.setContent(d.getContent());
            diary.setEmotionScore(d.getEmotionScore());
            diary.setEmotionCategory(d.getEmotionCategory());
            diary.setCreatedAt(d.getCreatedAt());
            return diary;
        }).toList();
        
        var profile = emotionProfileService.getProfile(diaryDataList, days);
        
        return Result.success(profile);
    }

    /**
     * 获取快速情感状态
     * @return 当前情感状态
     */
    @GetMapping("/profile/status")
    public Result<Map<String, String>> getQuickStatus() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        var diaries = diaryService.getRecentByUser(userId, 7);
        
        var diaryDataList = diaries.stream().map(d -> {
            var diary = new EmotionProfileServiceImpl.DiaryData();
            diary.setEmotionScore(d.getEmotionScore());
            return diary;
        }).toList();
        
        String status = emotionProfileService.getQuickStatus(diaryDataList);
        
        return Result.success(Map.of("status", status));
    }
}

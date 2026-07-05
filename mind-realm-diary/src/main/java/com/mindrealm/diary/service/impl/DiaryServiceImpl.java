package com.mindrealm.diary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.EsSync;
import com.mindrealm.common.util.AesUtil;
import com.mindrealm.common.util.ValidatorUtil;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.mapper.DiaryMapper;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.emotion.service.EmotionAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @className: DiaryServiceImpl
 * @description: 日记服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class DiaryServiceImpl implements DiaryService {

    private static final Logger log = LoggerFactory.getLogger(DiaryServiceImpl.class);

    private final DiaryMapper diaryMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmotionAnalysisService emotionAnalysisService;

    private static final String DIARY_LIST_CACHE = "diary:list:";

    @Autowired
    public DiaryServiceImpl(DiaryMapper diaryMapper,
                            RedisTemplate<String, Object> redisTemplate,
                            @Autowired(required = false) EmotionAnalysisService emotionAnalysisService) {
        this.diaryMapper = diaryMapper;
        this.redisTemplate = redisTemplate;
        this.emotionAnalysisService = emotionAnalysisService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EsSync("diary")
    public Diary create(Diary diary) {
        if (diary == null || !ValidatorUtil.isValidId(diary.getUserId())) {
            log.warn("create: 日记对象或用户ID无效");
            return null;
        }

        // 情感分析(加密前)
        analyzeEmotionInternal(diary);

        // 加密日记内容
        if (ValidatorUtil.isNotEmpty(diary.getContent())) {
            diary.setContent(AesUtil.encrypt(diary.getContent()));
        }

        // 保存日记
        diaryMapper.insert(diary);

        // 清除用户日记缓存
        clearCache(diary.getUserId());

        // 解密返回给前端
        if (AesUtil.isEncrypted(diary.getContent())) {
            diary.setContent(AesUtil.decrypt(diary.getContent()));
        }
        return diary;
    }

    @Override
    public Diary getById(Long id) {
        if (!ValidatorUtil.isValidId(id)) {
            return null;
        }
        
        Diary diary = diaryMapper.selectById(id);
        if (diary != null && AesUtil.isEncrypted(diary.getContent())) {
            diary.setContent(AesUtil.decrypt(diary.getContent()));
        }
        return diary;
    }

    @Override
    public Page<Diary> getListByUser(Long userId, int pageNum, int pageSize) {
        return getListByUser(userId, null, pageNum, pageSize);
    }

    @Override
    public Page<Diary> getListByUser(Long userId, String emotionCategory, int pageNum, int pageSize) {
        if (!ValidatorUtil.isValidId(userId)) {
            return new Page<>(1, 10);
        }

        pageNum = ValidatorUtil.validatePageNum(pageNum);
        pageSize = ValidatorUtil.validatePageSize(pageSize);

        Page<Diary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Diary> wrapper = new LambdaQueryWrapper<Diary>()
                .eq(Diary::getUserId, userId);
        
        // 情绪分类筛选
        if (emotionCategory != null && !emotionCategory.isEmpty()) {
            wrapper.eq(Diary::getEmotionCategory, emotionCategory);
        }
        
        wrapper.orderByDesc(Diary::getCreatedAt);

        Page<Diary> result = diaryMapper.selectPage(page, wrapper);

        // 解密内容
        result.getRecords().forEach(this::decryptIfNeeded);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EsSync(value = "diary", op = EsSync.Op.DELETE)
    public boolean delete(Long id, Long userId) {
        if (!ValidatorUtil.isValidId(id) || !ValidatorUtil.isValidId(userId)) {
            return false;
        }

        Diary diary = diaryMapper.selectById(id);
        if (diary != null && userId.equals(diary.getUserId())) {
            int result = diaryMapper.deleteById(id);
            clearCache(userId);
            return result > 0;
        }
        return false;
    }

    @Override
    public List<Diary> getRecentByUser(Long userId, int limit) {
        if (!ValidatorUtil.isValidId(userId)) {
            return Collections.emptyList();
        }

        limit = Math.max(1, Math.min(limit, 100));

        LambdaQueryWrapper<Diary> wrapper = new LambdaQueryWrapper<Diary>()
                .eq(Diary::getUserId, userId)
                .orderByDesc(Diary::getCreatedAt)
                .last("LIMIT " + limit);

        List<Diary> diaries = diaryMapper.selectList(wrapper);
        diaries.forEach(this::decryptIfNeeded);

        return diaries;
    }

    @Override
    public void clearCache(Long userId) {
        if (userId != null) {
            redisTemplate.delete(DIARY_LIST_CACHE + userId + ":*");
            log.debug("清除日记缓存: userId={}", userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EsSync("diary")
    public Diary update(Diary diary) {
        if (diary == null || !ValidatorUtil.isValidId(diary.getId())) {
            log.warn("update: 日记对象或ID无效");
            return null;
        }

        // 加密内容
        if (ValidatorUtil.isNotEmpty(diary.getContent()) && !AesUtil.isEncrypted(diary.getContent())) {
            diary.setContent(AesUtil.encrypt(diary.getContent()));
        }

        diaryMapper.updateById(diary);
        clearCache(diary.getUserId());

        // 解密返回
        decryptIfNeeded(diary);

        return diary;
    }

    @Override
    public EmotionResult analyzeEmotion(String text) {
        if (ValidatorUtil.isEmpty(text)) {
            return new EmotionResult(0.0, "calm");
        }

        if (emotionAnalysisService != null) {
            EmotionAnalysisService.EmotionResult result = emotionAnalysisService.analyzeText(text);
            return new EmotionResult(result.getScore(), result.getCategory());
        }

        // 降级处理
        Diary diary = new Diary();
        diary.setContent(text);
        analyzeEmotionSimple(diary);
        return new EmotionResult(diary.getEmotionScore().doubleValue(), diary.getEmotionCategory());
    }

    @Override
    public long count() {
        return diaryMapper.selectCount(null);
    }

    @Override
    public long countRecent(int days) {
        if (days <= 0) {
            days = 7;
        }
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<Diary> wrapper = new LambdaQueryWrapper<Diary>()
                .ge(Diary::getCreatedAt, start);
        return diaryMapper.selectCount(wrapper);
    }

    /**
     * 情感分析(内部方法)
     */
    private void analyzeEmotionInternal(Diary diary) {
        if (ValidatorUtil.isEmpty(diary.getContent())) {
            diary.setEmotionScore(BigDecimal.ZERO);
            diary.setEmotionCategory("calm");
            return;
        }

        if (emotionAnalysisService != null) {
            EmotionAnalysisService.EmotionResult result = emotionAnalysisService.analyzeText(diary.getContent());
            diary.setEmotionScore(new BigDecimal(String.format("%.2f", result.getScore())));
            diary.setEmotionCategory(result.getCategory());
        } else {
            analyzeEmotionSimple(diary);
        }
    }

    /**
     * 简单情感分析(降级方案)
     */
    private void analyzeEmotionSimple(Diary diary) {
        String content = diary.getContent().toLowerCase();
        double score = 0.0;
        String category = "calm";

        String[] positive = {"开心", "快乐", "高兴", "喜悦", "幸福", "满足", "感恩", "希望", "美好", "喜欢"};
        String[] negative = {"难过", "伤心", "痛苦", "焦虑", "担心", "害怕", "恐惧", "失望", "沮丧", "郁闷"};

        int posCount = 0, negCount = 0;
        for (String p : positive) { if (content.contains(p)) posCount++; }
        for (String n : negative) { if (content.contains(n)) negCount++; }

        if (posCount > negCount) {
            score = 0.5 + (posCount * 0.1);
            category = "happy";
        } else if (negCount > posCount) {
            score = -0.5 - (negCount * 0.1);
            category = negCount > 2 ? "anxious" : "sad";
        }

        score = Math.max(-1.0, Math.min(1.0, score));
        diary.setEmotionScore(new BigDecimal(String.format("%.2f", score)));
        diary.setEmotionCategory(category);
    }

    /**
     * 解密日记内容(如果需要)
     */
    private void decryptIfNeeded(Diary diary) {
        if (diary != null && AesUtil.isEncrypted(diary.getContent())) {
            diary.setContent(AesUtil.decrypt(diary.getContent()));
        }
    }
}

package com.mindrealm.emotion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @className: IEmotionProfileService
 * @description: 情感画像服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IEmotionProfileService {

    /**
     * 获取情感画像
     * @param diaries 日记数据列表
     * @param days 分析天数
     * @return 情感画像数据
     */
    Map<String, Object> getProfile(List<IEmotionProfileService.DiaryData> diaries, int days);

    /**
     * 获取快速状态
     * @param diaries 日记数据列表
     * @return 状态标识
     */
    String getQuickStatus(List<IEmotionProfileService.DiaryData> diaries);

    /**
     * 日记数据内部类
     */
    class DiaryData {
        private Long id;
        private Long userId;
        private String content;
        private BigDecimal emotionScore;
        private String emotionCategory;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public BigDecimal getEmotionScore() { return emotionScore; }
        public void setEmotionScore(BigDecimal emotionScore) { this.emotionScore = emotionScore; }
        public String getEmotionCategory() { return emotionCategory; }
        public void setEmotionCategory(String emotionCategory) { this.emotionCategory = emotionCategory; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}

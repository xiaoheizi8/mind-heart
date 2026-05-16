package com.mindrealm.emotion.service;

/**
 * @className: EmotionAnalysisService
 * @description: 情感分析服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface EmotionAnalysisService {

    /**
     * 分析文本情感
     * @param text 待分析文本
     * @return 情感分析结果
     */
    EmotionResult analyzeText(String text);

    /**
     * 分析文本情感(指定用户)
     * @param userId 用户ID
     * @param text 待分析文本
     * @return 情感分析结果
     */
    EmotionResult analyzeText(Long userId, String text);

    /**
     * 设置用户的情感分析策略
     * @param userId 用户ID
     * @param strategyName 策略名称
     */
    void setUserStrategy(Long userId, String strategyName);

    /**
     * 获取用户当前策略名称
     * @param userId 用户ID
     * @return 策略名称
     */
    String getUserStrategyName(Long userId);

    /**
     * 获取所有可用策略
     * @return 策略名称数组
     */
    String[] getAvailableStrategies();

    /**
     * 情感分析结果
     */
    class EmotionResult {
        private final double score;
        private final String category;

        public EmotionResult(double score, String category) {
            this.score = score;
            this.category = category;
        }

        public double getScore() {
            return score;
        }

        public String getCategory() {
            return category;
        }
    }
}

package com.mindrealm.emotion.strategy;

/**
 * @className: EmotionStrategy
 * @description: 情感分析策略接口,策略模式定义
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
public interface EmotionStrategy {

    /**
     * 分析情感
     * @param text 待分析文本
     * @return 情感结果
     */
    EmotionResult analyze(String text);

    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getName();

    /**
     * @className: EmotionStrategy.EmotionResult
     * @description: 情感分析结果
     */
    class EmotionResult {
        private double score;
        private String category;
        private double confidence;

        public EmotionResult(double score, String category, double confidence) {
            this.score = score;
            this.category = category;
            this.confidence = confidence;
        }

        public double getScore() {
            return score;
        }

        public String getCategory() {
            return category;
        }

        public double getConfidence() {
            return confidence;
        }
    }
}

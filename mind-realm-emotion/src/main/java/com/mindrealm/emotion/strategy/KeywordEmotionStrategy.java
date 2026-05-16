package com.mindrealm.emotion.strategy;

import java.util.*;

/**
 * @className: KeywordEmotionStrategy
 * @description: 基于关键词匹配的情感分析策略,实现策略模式
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
public class KeywordEmotionStrategy implements EmotionStrategy {

    /**
     * 情感词典:类别 -> 关键词及权重映射
     */
    private static final Map<String, List<KeywordEntry>> EMOTION_DICTIONARY = new HashMap<>();

    static {
        // 正面情绪
        EMOTION_DICTIONARY.put("happy", Arrays.asList(
            new KeywordEntry("开心", 0.8), new KeywordEntry("高兴", 0.8), new KeywordEntry("快乐", 0.9),
            new KeywordEntry("幸福", 0.9), new KeywordEntry("兴奋", 0.7), new KeywordEntry("愉快", 0.8),
            new KeywordEntry("愉悦", 0.8), new KeywordEntry("快乐", 0.9), new KeywordEntry("joy", 0.7),
            new KeywordEntry("happy", 0.7), new KeywordEntry("excited", 0.7), new KeywordEntry("love", 0.8),
            new KeywordEntry("棒", 0.7), new KeywordEntry("好", 0.5), new KeywordEntry("喜欢", 0.7)
        ));

        // 负面情绪
        EMOTION_DICTIONARY.put("sad", Arrays.asList(
            new KeywordEntry("难过", 0.8), new KeywordEntry("伤心", 0.8), new KeywordEntry("痛苦", 0.7),
            new KeywordEntry("抑郁", 0.9), new KeywordEntry("绝望", 0.9), new KeywordEntry("沮丧", 0.7),
            new KeywordEntry("低落", 0.6), new KeywordEntry("悲伤", 0.8), new KeywordEntry("哭", 0.7),
            new KeywordEntry("难受", 0.7), new KeywordEntry("心碎", 0.9), new KeywordEntry("失落", 0.7),
            new KeywordEntry("sad", 0.7), new KeywordEntry("depressed", 0.8),
            new KeywordEntry("heartbroken", 0.9), new KeywordEntry("miserable", 0.8)
        ));

        // 焦虑情绪
        EMOTION_DICTIONARY.put("anxious", Arrays.asList(
            new KeywordEntry("焦虑", 0.8), new KeywordEntry("紧张", 0.7), new KeywordEntry("害怕", 0.7),
            new KeywordEntry("担心", 0.6), new KeywordEntry("恐惧", 0.8), new KeywordEntry("不安", 0.7),
            new KeywordEntry("慌乱", 0.6), new KeywordEntry("压力", 0.7), new KeywordEntry("慌", 0.6),
            new KeywordEntry("anxious", 0.7), new KeywordEntry("afraid", 0.7),
            new KeywordEntry("worried", 0.6), new KeywordEntry("scared", 0.7), new KeywordEntry("fear", 0.8)
        ));

        // 愤怒情绪
        EMOTION_DICTIONARY.put("angry", Arrays.asList(
            new KeywordEntry("生气", 0.8), new KeywordEntry("愤怒", 0.9), new KeywordEntry("恼火", 0.7),
            new KeywordEntry("气愤", 0.8), new KeywordEntry("恼怒", 0.7), new KeywordEntry("恨", 0.7),
            new KeywordEntry("烦", 0.6), new KeywordEntry("angry", 0.8),
            new KeywordEntry("furious", 0.9), new KeywordEntry("mad", 0.7)
        ));

        // 平静情绪
        EMOTION_DICTIONARY.put("calm", Arrays.asList(
            new KeywordEntry("平静", 0.8), new KeywordEntry("放松", 0.7), new KeywordEntry("安心", 0.7),
            new KeywordEntry("安宁", 0.7), new KeywordEntry("舒适", 0.6), new KeywordEntry("淡定", 0.7),
            new KeywordEntry("calm", 0.7), new KeywordEntry("relaxed", 0.7), new KeywordEntry("peaceful", 0.7)
        ));
    }

    /**
     * 获取策略名称
     * @return 策略名称
     */
    @Override
    public String getName() {
        return "keyword";
    }

    /**
     * 分析文本情感
     * 通过关键词匹配和权重计算确定情感类别和得分
     * @param text 待分析文本
     * @return 情感分析结果
     */
    @Override
    public EmotionResult analyze(String text) {
        if (text == null || text.isEmpty()) {
            return new EmotionResult(0.0, "calm", 0.0);
        }

        String lowerText = text.toLowerCase();
        Map<String, Double> categoryScores = new HashMap<>();

        // 遍历词典计算各类别得分
        for (Map.Entry<String, List<KeywordEntry>> entry : EMOTION_DICTIONARY.entrySet()) {
            double score = 0.0;
            int matchCount = 0;

            for (KeywordEntry keyword : entry.getValue()) {
                if (lowerText.contains(keyword.keyword)) {
                    score += keyword.weight;
                    matchCount++;
                }
            }

            // 取平均得分,考虑匹配数量
            if (matchCount > 0) {
                // 匹配越多,得分越高,最高1.0
                score = Math.min(score, 1.0);
                categoryScores.put(entry.getKey(), score);
            }
        }

        // 找出最高得分类别
        if (categoryScores.isEmpty()) {
            return new EmotionResult(0.0, "calm", 0.0);
        }

        String maxCategory = Collections.max(categoryScores.entrySet(), Map.Entry.comparingByValue()).getKey();
        double maxScore = categoryScores.get(maxCategory);

        // 转换为标准得分范围
        double normalizedScore = convertToScore(maxCategory, maxScore);
        double confidence = Math.min(maxScore * 1.2, 1.0); // 置信度

        return new EmotionResult(normalizedScore, maxCategory, confidence);
    }

    /**
     * 将类别得分转换为标准得分[-1, 1]
     * @param category 情感类别
     * @param score 原始得分
     * @return 标准得分
     */
    private double convertToScore(String category, double score) {
        return switch (category) {
            case "happy" -> score * 0.7;
            case "sad" -> -score * 0.7;
            case "anxious" -> -score * 0.5;
            case "angry" -> -score * 0.6;
            case "calm" -> score * 0.3;
            default -> 0.0;
        };
    }

    /**
     * @className: KeywordEntry
     * @description: 关键词条目内部类
     */
    private static class KeywordEntry {
        String keyword;
        double weight;

        KeywordEntry(String keyword, double weight) {
            this.keyword = keyword.toLowerCase();
            this.weight = weight;
        }
    }
}

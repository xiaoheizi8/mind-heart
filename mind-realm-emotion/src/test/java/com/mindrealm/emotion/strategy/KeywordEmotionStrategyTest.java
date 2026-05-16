package com.mindrealm.emotion.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @className: KeywordEmotionStrategyTest
 * @description: 基于关键词的情感分析策略单元测试
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
class KeywordEmotionStrategyTest {

    private final KeywordEmotionStrategy strategy = new KeywordEmotionStrategy();

    /**
     * 测试正面情绪分析
     */
    @Test
    void testPositiveEmotion() {
        EmotionStrategy.EmotionResult result = strategy.analyze("今天我很开心,感到很高兴和幸福!");
        
        assertEquals("happy", result.getCategory());
        assertTrue(result.getScore() > 0);
        assertTrue(result.getConfidence() > 0.5);
    }

    /**
     * 测试负面情绪分析
     */
    @Test
    void testNegativeEmotion() {
        EmotionStrategy.EmotionResult result = strategy.analyze("我感到很难过,很抑郁,人生没有意义");
        
        assertEquals("sad", result.getCategory());
        assertTrue(result.getScore() < 0);
    }

    /**
     * 测试焦虑情绪分析
     */
    @Test
    void testAnxietyEmotion() {
        EmotionStrategy.EmotionResult result = strategy.analyze("我好焦虑,担心考试,害怕失败");
        
        assertEquals("anxious", result.getCategory());
        assertTrue(result.getScore() < 0);
    }

    /**
     * 测试愤怒情绪分析
     */
    @Test
    void testAngerEmotion() {
        EmotionStrategy.EmotionResult result = strategy.analyze("我很生气,愤怒,简直恼火");
        
        assertEquals("angry", result.getCategory());
        assertTrue(result.getScore() < 0);
    }

    /**
     * 测试平静情绪分析
     */
    @Test
    void testCalmEmotion() {
        EmotionStrategy.EmotionResult result = strategy.analyze("现在很平静,放松心情");
        
        assertEquals("calm", result.getCategory());
        assertTrue(result.getScore() > 0);
    }

    /**
     * 测试中性文本
     */
    @Test
    void testNeutralText() {
        EmotionStrategy.EmotionResult result = strategy.analyze("今天吃了饭,看了电视");
        
        assertEquals("calm", result.getCategory());
        assertEquals(0.0, result.getScore(), 0.1);
    }

    /**
     * 测试空文本
     */
    @Test
    void testEmptyText() {
        EmotionStrategy.EmotionResult result = strategy.analyze("");
        
        assertEquals("calm", result.getCategory());
        assertEquals(0.0, result.getScore(), 0.1);
    }

    /**
     * 测试英文正面情绪
     */
    @Test
    void testEnglishPositive() {
        EmotionStrategy.EmotionResult result = strategy.analyze("I am so happy and excited today!");
        
        assertEquals("happy", result.getCategory());
        assertTrue(result.getScore() > 0);
    }

    /**
     * 测试英文负面情绪
     */
    @Test
    void testEnglishNegative() {
        EmotionStrategy.EmotionResult result = strategy.analyze("I feel depressed and sad");
        
        assertEquals("sad", result.getCategory());
        assertTrue(result.getScore() < 0);
    }

    /**
     * 测试多情感混合
     */
    @Test
    void testMixedEmotion() {
        EmotionStrategy.EmotionResult result = strategy.analyze("虽然今天很开心,但还是有些担心");
        
        assertNotNull(result.getCategory());
    }
}

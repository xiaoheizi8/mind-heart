package com.mindrealm.emotion.strategy;

import org.springframework.stereotype.Component;

/**
 * @className: LlmEmotionStrategy
 * @description: 基于大语言模型的情感分析策略(预留实现),当前返回默认平静状态
 *               后续可集成LLM API实现更精准的情感识别
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Component
public class LlmEmotionStrategy implements EmotionStrategy {

    @Override
    public String getName() {
        return "llm";
    }

    @Override
    public EmotionResult analyze(String text) {
        return new EmotionResult(0.0, "calm", 0.0);
    }
}

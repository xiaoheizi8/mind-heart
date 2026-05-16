package com.mindrealm.emotion.service.impl;

import com.mindrealm.common.util.ValidatorUtil;
import com.mindrealm.emotion.service.EmotionAnalysisService;
import com.mindrealm.emotion.strategy.EmotionStrategy;
import com.mindrealm.emotion.strategy.EmotionStrategyFactory;
import com.mindrealm.emotion.strategy.KeywordEmotionStrategy;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: EmotionAnalysisServiceImpl
 * @description: 情感分析服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(EmotionAnalysisServiceImpl.class);

    private final Map<Long, String> userStrategyMap = new ConcurrentHashMap<>();
    private EmotionStrategy currentStrategy;
    private static final String DEFAULT_STRATEGY = "keyword";

    @PostConstruct
    public void init() {
        EmotionStrategyFactory.register(new KeywordEmotionStrategy());
        currentStrategy = EmotionStrategyFactory.getStrategy(DEFAULT_STRATEGY);
        log.info("情感分析服务初始化完成，默认策略: {}", DEFAULT_STRATEGY);
    }

    @Override
    public EmotionResult analyzeText(String text) {
        return analyzeText(null, text);
    }

    @Override
    public EmotionResult analyzeText(Long userId, String text) {
        if (ValidatorUtil.isEmpty(text)) {
            return new EmotionResult(0.0, "calm");
        }

        EmotionStrategy strategy = getStrategy(userId);
        if (strategy == null) {
            log.warn("未找到情感分析策略，使用默认值");
            return new EmotionResult(0.0, "calm");
        }

        EmotionStrategy.EmotionResult result = strategy.analyze(text);
        return new EmotionResult(result.getScore(), result.getCategory());
    }

    @Override
    public void setUserStrategy(Long userId, String strategyName) {
        if (!ValidatorUtil.isValidId(userId) || ValidatorUtil.isEmpty(strategyName)) {
            log.warn("setUserStrategy: 参数无效 userId={}, strategyName={}", userId, strategyName);
            return;
        }

        if (EmotionStrategyFactory.hasStrategy(strategyName)) {
            userStrategyMap.put(userId, strategyName);
            log.debug("设置用户策略: userId={}, strategy={}", userId, strategyName);
        } else {
            log.warn("策略不存在: {}", strategyName);
        }
    }

    @Override
    public String getUserStrategyName(Long userId) {
        return userStrategyMap.getOrDefault(userId, DEFAULT_STRATEGY);
    }

    @Override
    public String[] getAvailableStrategies() {
        return EmotionStrategyFactory.getStrategyNames();
    }

    private EmotionStrategy getStrategy(Long userId) {
        if (ValidatorUtil.isValidId(userId)) {
            String strategyName = userStrategyMap.get(userId);
            if (strategyName != null) {
                return EmotionStrategyFactory.getStrategy(strategyName);
            }
        }
        return currentStrategy;
    }
}

package com.mindrealm.emotion.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @className: EmotionStrategyFactory
 * @description: 情感分析策略工厂,工厂模式实现,根据策略名称获取对应策略
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
public class EmotionStrategyFactory {

    /**
     * 策略缓存
     */
    private static final Map<String, EmotionStrategy> STRATEGIES = new HashMap<>();

    /**
     * 默认策略
     */
    private static final EmotionStrategy DEFAULT_STRATEGY = new KeywordEmotionStrategy();

    static {
        // 注册默认策略
        register(new KeywordEmotionStrategy());
    }

    /**
     * 私有构造函数,防止实例化
     */
    private EmotionStrategyFactory() {
    }

    /**
     * 注册情感分析策略
     * @param strategy 策略实现
     */
    public static void register(EmotionStrategy strategy) {
        if (strategy != null && strategy.getName() != null) {
            STRATEGIES.put(strategy.getName(), strategy);
        }
    }

    /**
     * 获取策略(默认策略)
     * @return 情感分析策略
     */
    public static EmotionStrategy getStrategy() {
        return DEFAULT_STRATEGY;
    }

    /**
     * 根据名称获取策略
     * @param name 策略名称
     * @return 情感分析策略(未找到则返回默认策略)
     */
    public static EmotionStrategy getStrategy(String name) {
        return Optional.ofNullable(STRATEGIES.get(name)).orElse(DEFAULT_STRATEGY);
    }

    /**
     * 检查策略是否存在
     * @param name 策略名称
     * @return 是否存在
     */
    public static boolean hasStrategy(String name) {
        return STRATEGIES.containsKey(name);
    }

    /**
     * 获取所有已注册策略名称
     * @return 策略名称列表
     */
    public static String[] getStrategyNames() {
        return STRATEGIES.keySet().toArray(new String[0]);
    }
}

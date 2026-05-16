package com.mindrealm.warning.service;

import java.util.List;
import java.util.Map;

/**
 * @className: BehaviorAnomalyDetectionService
 * @description: 用户行为异常检测服务接口,提供沉默模式、情绪下降、活动波动等异常行为检测功能
 *               用于预警系统识别用户心理状态变化,及时发现潜在风险
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
public interface BehaviorAnomalyDetectionService {

    Map<String, Object> analyzeUserBehavior(Long userId);

    boolean detectSilentPattern(Long userId, int daysThreshold);

    boolean detectMoodDeclinePattern(Long userId);

    boolean detectActivitySpike(Long userId);

    Map<String, Object> getUserBehaviorProfile(Long userId);

    List<Map<String, Object>> getAnomalyWarnings(Long userId);

    void recordActivity(Long userId, String activityType);

    Map<String, Object> getRecentTrend(Long userId, int days);

    boolean shouldTriggerWarning(Long userId);
}

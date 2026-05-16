package com.mindrealm.warning.service;

import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.service.impl.WarningServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @className: WarningServiceTest
 * @description: 预警服务单元测试
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@ExtendWith(MockitoExtension.class)
class WarningServiceTest {

    @Mock
    private com.mindrealm.warning.mapper.WarningRecordMapper warningMapper;

    @InjectMocks
    private WarningServiceImpl warningService;

    /**
     * 测试高风险内容检测
     */
    @Test
    void testHighRiskDetection() {
        WarningRecord result = warningService.analyzeRisk(1L, "我不想活了,想自杀");
        
        assertNotNull(result);
        assertEquals(3, result.getRiskLevel());
        assertEquals("content", result.getTriggerType());
    }

    /**
     * 测试中风险内容检测
     */
    @Test
    void testMediumRiskDetection() {
        WarningRecord result = warningService.analyzeRisk(1L, "我感到绝望和无助");
        
        assertNotNull(result);
        assertEquals(2, result.getRiskLevel());
    }

    /**
     * 测试低风险内容(不触发预警)
     */
    @Test
    void testLowRiskContent() {
        WarningRecord result = warningService.analyzeRisk(1L, "今天天气很好");
        
        assertNull(result);
    }

    /**
     * 测试空内容(不触发预警)
     */
    @Test
    void testEmptyContent() {
        WarningRecord result = warningService.analyzeRisk(1L, "");
        
        assertNull(result);
    }

    /**
     * 测试null内容(不触发预警)
     */
    @Test
    void testNullContent() {
        WarningRecord result = warningService.analyzeRisk(1L, null);
        
        assertNull(result);
    }

    /**
     * 测试英文高风险关键词
     */
    @Test
    void testEnglishHighRisk() {
        WarningRecord result = warningService.analyzeRisk(1L, "I want to kill myself");
        
        assertNotNull(result);
        assertEquals(3, result.getRiskLevel());
    }

    /**
     * 测试isHighRiskLevel方法
     */
    @Test
    void testIsHighRiskLevel() {
        assertTrue(warningService.isHighRiskLevel(3));
        assertTrue(warningService.isHighRiskLevel(5));
        assertFalse(warningService.isHighRiskLevel(2));
        assertFalse(warningService.isHighRiskLevel(1));
        assertFalse(warningService.isHighRiskLevel(null));
    }
}

package com.mindrealm.common.service.impl;

import com.mindrealm.common.service.SmsService;
import com.mindrealm.common.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: MockSmsServiceImpl
 * @description: 模拟短信服务（开发环境使用）
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
@ConditionalOnMissingBean(SmsService.class)
@ConditionalOnProperty(name = "notification.sms.enabled", havingValue = "false", matchIfMissing = true)
public class MockSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsServiceImpl.class);

    @Override
    public SmsResult send(String phone, String templateCode, Map<String, String> params) {
        log.info("Mock SMS: phone={}, template={}, params={}", phone, templateCode, params);
        
        if (!ValidatorUtil.isPhone(phone)) {
            return SmsResult.fail("手机号格式错误");
        }
        
        // 模拟发送成功
        String mockBizId = "mock-" + System.currentTimeMillis();
        log.info("短信模拟发送成功: bizId={}", mockBizId);
        return SmsResult.success(mockBizId);
    }

    @Override
    public SmsResult sendWarningSms(String phone, String userName, int riskLevel) {
        String riskLevelDesc = switch (riskLevel) {
            case 3 -> "高风险";
            case 2 -> "中风险";
            default -> "低风险";
        };

        log.info("Mock Warning SMS: phone={}, userName={}, riskLevel={}", phone, userName, riskLevelDesc);
        
        Map<String, String> params = new HashMap<>();
        params.put("name", userName);
        params.put("level", riskLevelDesc);

        return send(phone, "WARNING_TEMPLATE", params);
    }

    @Override
    public SmsResult sendVerifyCode(String phone, String code) {
        log.info("Mock Verify Code SMS: phone={}, code={}", phone, code);
        
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        
        return send(phone, "VERIFY_CODE_TEMPLATE", params);
    }
}

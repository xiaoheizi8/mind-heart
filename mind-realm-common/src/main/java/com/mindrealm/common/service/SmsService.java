package com.mindrealm.common.service;

import java.util.Map;

/**
 * @className: SmsService
 * @description: 短信服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface SmsService {

    /**
     * 发送短信
     * @param phone 手机号
     * @param templateCode 短信模板code
     * @param params 模板参数
     * @return 发送结果
     */
    SmsResult send(String phone, String templateCode, Map<String, String> params);

    /**
     * 发送预警通知短信
     * @param phone 手机号
     * @param userName 用户名称
     * @param riskLevel 风险等级
     * @return 发送结果
     */
    SmsResult sendWarningSms(String phone, String userName, int riskLevel);

    /**
     * 发送验证码短信
     * @param phone 手机号
     * @param code 验证码
     * @return 发送结果
     */
    SmsResult sendVerifyCode(String phone, String code);

    /**
     * 短信发送结果
     */
    class SmsResult {
        private final boolean success;
        private final String message;
        private final String bizId;

        public SmsResult(boolean success, String message) {
            this(success, message, null);
        }

        public SmsResult(boolean success, String message, String bizId) {
            this.success = success;
            this.message = message;
            this.bizId = bizId;
        }

        public static SmsResult success(String bizId) {
            return new SmsResult(true, "发送成功", bizId);
        }

        public static SmsResult fail(String message) {
            return new SmsResult(false, message);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getBizId() { return bizId; }
    }
}

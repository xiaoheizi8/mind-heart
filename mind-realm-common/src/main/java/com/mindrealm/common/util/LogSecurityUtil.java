package com.mindrealm.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @className: LogSecurityUtil
 * @description: 日志安全工具类,过滤日志中的敏感信息,防止敏感数据泄露
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Component
public class LogSecurityUtil {

    private static final Logger log = LoggerFactory.getLogger(LogSecurityUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 敏感字段名称集合
     */
    private static final Set<String> SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
            "password", "pwd", "secret", "token", "accessToken", "refreshToken",
            "idCard", "idNumber", "bankCard", "cardNo", "phone", "mobile",
            "email", "address", "guardianPhone", "payPassword"
    ));

    /**
     * 过滤对象中的敏感字段
     * @param obj 待过滤对象
     * @return 过滤后的对象
     */
    public static <T> T filterSensitiveData(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(obj);
            String filtered = filterJson(json);
            return (T) objectMapper.readValue(filtered, obj.getClass());
        } catch (JsonProcessingException e) {
            log.warn("Failed to filter sensitive data", e);
            return obj;
        }
    }

    /**
     * 过滤JSON字符串中的敏感信息
     * @param json JSON字符串
     * @return 过滤后的JSON字符串
     */
    public static String filterJson(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        String result = json;

        // 遍历敏感字段进行替换
        for (String field : SENSITIVE_FIELDS) {
            // 匹配 "field": "value" 或 "field":value
            String pattern = "(\"" + field + "\"\\s*:\\s*\")([^\"]*)(\")";
            result = result.replaceAll(pattern, "$1******$3");

            // 匹配数字类型的值
            String numPattern = "(\"" + field + "\"\\s*:\\s*)([0-9]+)";
            result = result.replaceAll(numPattern, "$1******");
        }

        return result;
    }

    /**
     * 过滤请求参数日志
     * @param params 请求参数Map
     * @return 过滤后的参数字符串
     */
    public static String filterRequestParams(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "{}";
        }

        Map<String, Object> filtered = new HashMap<>(params);
        for (String key : filtered.keySet()) {
            if (isSensitiveField(key)) {
                filtered.put(key, "******");
            }
        }

        try {
            return objectMapper.writeValueAsString(filtered);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    /**
     * 判断是否为敏感字段
     */
    private static boolean isSensitiveField(String fieldName) {
        String lowerField = fieldName.toLowerCase();
        return SENSITIVE_FIELDS.stream().anyMatch(sf -> lowerField.contains(sf.toLowerCase()));
    }

    /**
     * 安全记录日志 - 用户登录
     */
    public static void logAuth(String action, String username, boolean success) {
        log.info("Auth: action={}, username={}, success={}", action, username, success);
    }

    /**
     * 安全记录日志 - 用户操作
     */
    public static void logUserAction(String action, Long userId, String detail) {
        log.info("UserAction: action={}, userId={}, detail={}", action, userId, detail);
    }

    /**
     * 安全记录日志 - 敏感操作
     */
    public static void logSensitiveOperation(String operation, Long userId, String target) {
        log.warn("SensitiveOp: operation={}, userId={}, target={}", operation, userId, target);
    }

    /**
     * 安全记录日志 - API请求
     */
    public static void logApiRequest(String method, String uri, Map<String, Object> params) {
        String filteredParams = filterRequestParams(params);
        log.info("API: method={}, uri={}, params={}", method, uri, filteredParams);
    }
}
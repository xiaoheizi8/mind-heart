package com.mindrealm.api.aspect;

import com.mindrealm.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * 输入安全校验切面
 */
@Aspect
@Component
public class InputSanitizationAspect {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_PATTERN = Pattern.compile(
            "('|(\\\\)(\\w)*)|(xp_)|(exec)|(execute)|(insert)|(delete)|(update)|(drop)|(union)|(select)", 
            Pattern.CASE_INSENSITIVE);
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "javascript:|vbscript:|on(load|click|error|mouse)", Pattern.CASE_INSENSITIVE);

    /**
     * 字符串类型参数校验切点
     */
    @Around("@annotation(com.mindrealm.api.annotation.Sanitize)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                String value = (String) args[i];
                args[i] = sanitizeInput(value);
            } else if (args[i] != null && isComplexObject(args[i])) {
                sanitizeObject(args[i]);
            }
        }
        return point.proceed(args);
    }

    /**
     * 清理输入
     */
    public static String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;

        // 移除脚本标签
        result = SCRIPT_PATTERN.matcher(result).replaceAll("");

        // 检测SQL注入特征
        if (SQL_PATTERN.matcher(result).find()) {
            throw BusinessException.builder().code(400).message("输入包含非法字符").build();
        }

        // 检测XSS特征
        if (XSS_PATTERN.matcher(result).find()) {
            throw BusinessException.builder().code(400).message("输入包含危险内容").build();
        }

        // 移除多余的空白
        result = result.trim();

        return result;
    }

    /**
     * 清理对象中的字符串字段
     */
    private void sanitizeObject(Object obj) {
        if (obj == null) return;

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof String) {
                    String sanitized = sanitizeInput((String) value);
                    field.set(obj, sanitized);
                }
            } catch (Exception e) {
                // 忽略字段访问异常
            }
        }
    }

    /**
     * 是否复杂对象
     */
    private boolean isComplexObject(Object obj) {
        return obj != null && 
               !obj.getClass().isPrimitive() && 
               !obj.getClass().getName().startsWith("java.lang") &&
               !obj.getClass().getName().startsWith("java.math") &&
               !obj.getClass().getName().startsWith("java.time");
    }
}
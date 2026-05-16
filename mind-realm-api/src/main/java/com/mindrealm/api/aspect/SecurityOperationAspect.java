package com.mindrealm.api.aspect;

import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.util.TimeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @className: SecurityOperationAspect
 * @description: 安全操作审计切面,记录用户敏感操作的日志,用于安全审计追溯
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
@Aspect
@Component
public class SecurityOperationAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityOperationAspect.class);
    private static final Logger securityLog = LoggerFactory.getLogger("SECURITY_LOG");

    @Around("@annotation(com.mindrealm.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;

        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logOperation(point, annotation, duration, error);
        }
    }

    private void logOperation(ProceedingJoinPoint point, OperationLog annotation, 
                            long duration, Throwable error) {
        try {
            String timestamp = TimeUtil.nowString();
            String className = point.getTarget().getClass().getSimpleName();
            String methodName = point.getSignature().getName();
            OperationType operationType = annotation.type();
            String description = annotation.value();
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n╔══════════════════════════════════════════════════════════════╗\n");
            logMessage.append("║                    安全操作审计日志                         ║\n");
            logMessage.append("╠══════════════════════════════════════════════════════════════╣\n");
            logMessage.append(String.format("║ 时间: %-55s ║\n", timestamp));
            logMessage.append(String.format("║ 操作类型: %-50s ║\n", operationType.name()));
            logMessage.append(String.format("║ 描述: %-53s ║\n", description));
            logMessage.append(String.format("║ 类方法: %s.%s()%-41s ║\n", className, methodName, ""));
            logMessage.append(String.format("║ 执行耗时: %d ms%-47s ║\n", duration, ""));
            
            if (error != null) {
                logMessage.append(String.format("║ 执行结果: %-51s ║\n", "失败"));
                logMessage.append(String.format("║ 错误信息: %-51s ║\n", truncate(error.getMessage(), 45)));
            } else {
                logMessage.append(String.format("║ 执行结果: %-51s ║\n", "成功"));
            }
            
            logMessage.append("╚══════════════════════════════════════════════════════════════╝");

            if (isSensitiveOperation(operationType)) {
                securityLog.warn(logMessage.toString());
                logger.warn("敏感操作: {} - {} - {}", operationType, className, methodName);
            } else {
                logger.info(logMessage.toString());
            }
        } catch (Exception e) {
            logger.error("记录操作日志失败", e);
        }
    }

    private boolean isSensitiveOperation(OperationType type) {
        return type == OperationType.DELETE 
            || type == OperationType.UPDATE_PASSWORD
            || type == OperationType.RESET_PASSWORD
            || type == OperationType.BIND_PHONE
            || type == OperationType.BIND_EMAIL
            || type == OperationType.SMS_SEND
            || type == OperationType.EXPORT
            || type == OperationType.IMPORT
            || type == OperationType.WARNING_HANDLE;
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() <= maxLen ? str : str.substring(0, maxLen) + "...";
    }
}

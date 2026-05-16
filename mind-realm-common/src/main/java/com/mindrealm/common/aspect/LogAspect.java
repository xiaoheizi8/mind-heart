package com.mindrealm.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.context.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * @className: LogAspect
 * @description: 日志切面，自动记录用户操作日志
 * @author: 一朝风月
 * @code: 面向自己，面向未来
 * @createTime: 2026.4.5
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 切点：所有Controller层方法
     */
    @Pointcut("execution(* com.mindrealm..controller..*(..))")
    public void controllerPointcut() {}

    /**
     * 切点：带有@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.mindrealm.common.annotation.OperationLog)")
    public void operationLogPointcut() {}

    /**
     * 环绕通知：记录Controller操作日志
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return point.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取用户信息
        Long userId = RequestContext.getCurrentUserId();
        String username = RequestContext.getCurrentUsername();
        String userRole = RequestContext.isAdmin() ? "管理员" : 
                         RequestContext.isCounselor() ? "咨询师" : "用户";
        
        // 设置MDC，让日志自动带上用户信息
        MDC.put("userId", userId != null ? userId.toString() : "匿名");
        MDC.put("username", username != null ? username : "匿名");
        MDC.put("role", userRole);
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // 构建日志前缀
        String logPrefix = buildLogPrefix(userId, username, userRole, className, methodName);
        
        try {
            // 记录请求信息
            log.info("{} 请求开始: {} {}", logPrefix, request.getMethod(), request.getRequestURI());
            
            // 执行方法
            Object result = point.proceed();
            
            // 计算耗时
            long costTime = System.currentTimeMillis() - startTime;
            
            // 记录成功日志
            log.info("{} 执行成功, 耗时: {}ms", logPrefix, costTime);
            
            return result;
            
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("{} 执行异常, 耗时: {}ms, 异常: {}", logPrefix, costTime, e.getMessage());
            throw e;
        } finally {
            // 清除MDC
            MDC.clear();
        }
    }

    /**
     * 环绕通知：处理带有@OperationLog注解的方法
     */
    @Around("operationLogPointcut()")
    public Object aroundOperationLog(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取用户信息
        Long userId = RequestContext.getCurrentUserId();
        String username = RequestContext.getCurrentUsername();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestUri = "";
        String requestMethod = "";
        String clientIp = "";
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestUri = request.getRequestURI();
            requestMethod = request.getMethod();
            clientIp = getClientIp(request);
        }
        
        // 设置MDC
        MDC.put("userId", userId != null ? userId.toString() : "匿名");
        MDC.put("username", username != null ? username : "匿名");
        
        String operation = operationLog.value();
        OperationType type = operationLog.type();
        
        // 构建日志信息
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("[用户: ").append(username != null ? username : "匿名");
        logBuilder.append("(").append(userId != null ? userId : "-").append(")]");
        logBuilder.append(" [IP: ").append(clientIp).append("]");
        logBuilder.append(" [操作: ").append(operation).append("]");
        logBuilder.append(" [类型: ").append(type).append("]");
        logBuilder.append(" [接口: ").append(requestMethod).append(" ").append(requestUri).append("]");
        
        // 记录参数
        if (operationLog.logParams()) {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(args);
                    // 截断过长的参数
                    if (params.length() > 500) {
                        params = params.substring(0, 500) + "...";
                    }
                    logBuilder.append(" [参数: ").append(params).append("]");
                } catch (Exception e) {
                    logBuilder.append(" [参数: 序列化失败]");
                }
            }
        }
        
        try {
            log.info("{} >>> 执行开始", logBuilder);
            
            Object result = point.proceed();
            
            long costTime = System.currentTimeMillis() - startTime;
            
            // 记录返回结果
            if (operationLog.logResult()) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    if (resultStr.length() > 500) {
                        resultStr = resultStr.substring(0, 500) + "...";
                    }
                    log.info("{} <<< 执行成功, 耗时: {}ms, 结果: {}", logBuilder, costTime, resultStr);
                } catch (Exception e) {
                    log.info("{} <<< 执行成功, 耗时: {}ms", logBuilder, costTime);
                }
            } else {
                log.info("{} <<< 执行成功, 耗时: {}ms", logBuilder, costTime);
            }
            
            return result;
            
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("{} <<< 执行失败, 耗时: {}ms, 异常: {}", logBuilder, costTime, e.getMessage());
            throw e;
        } finally {
            MDC.clear();
        }
    }
    
    /**
     * 构建日志前缀
     */
    private String buildLogPrefix(Long userId, String username, String role, String className, String methodName) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(username != null ? username : "匿名");
        sb.append("(").append(userId != null ? userId : "-").append(")");
        sb.append("|").append(role).append("]");
        sb.append("[").append(className).append(".").append(methodName).append("]");
        return sb.toString();
    }
    
    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

package com.mindrealm.api.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.common.entity.OperationLog;
import com.mindrealm.common.mapper.OperationLogMapper;
import com.mindrealm.common.util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * @className: OperationLogAspect
 * @description: 操作日志AOP切面,自动拦截Controller层请求并记录操作日志
 *               包括请求方式、URL、参数、IP、响应时间等信息,用于审计和监控
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private final OperationLogMapper operationLogMapper;

    public OperationLogAspect(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Pointcut("execution(* com.mindrealm..controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);
        
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        String requestParams = getRequestParams(point.getArgs());
        
        OperationLog operationLog = new OperationLog();
        operationLog.setMethod(className + "." + methodName);
        operationLog.setRequestUrl(uri);
        operationLog.setRequestMethod(method);
        operationLog.setRequestParams(requestParams);
        operationLog.setIp(ip);
        operationLog.setModule(className);
        operationLog.setOperation(methodName);
        
        log.info("=== 请求开始 ===");
        log.info("请求方式: {}, 请求地址: {}, 调用方法: {}.{}", method, uri, className, methodName);
        
        Object result = null;
        try {
            result = point.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            operationLog.setDuration(duration);
            operationLog.setStatus(1);
            operationLog.setCreatedAt(TimeUtil.now());
            
            log.info("请求成功, 耗时: {}ms", duration);
            log.info("=== 请求结束 ===");
            
            saveLog(operationLog);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            operationLog.setDuration(duration);
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            operationLog.setCreatedAt(TimeUtil.now());
            
            log.error("请求异常, 耗时: {}ms, 错误: {}", duration, e.getMessage());
            log.info("=== 请求结束 ===");
            
            saveLog(operationLog);
            throw e;
        }
    }

    private void saveLog(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getRequestParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                sb.append(arg.toString());
                sb.append(", ");
            }
        }
        if (sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }
}

//package com.mindrealm.api.aspect;
//
//import com.mindrealm.common.context.RequestContext;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
///**
// * 日志切面
// */
//@Aspect
//@Component
//
//public class LogAspect {
//
//    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
//
//    /**
//     * 控制器切点
//     */
//    @Pointcut("execution(* com.mindrealm.api.controller..*.*(..))")
//    public void controllerPointcut() {}
//
//    /**
//     * 记录日志
//     */
//    @Around("controllerPointcut()")
//    public Object around(ProceedingJoinPoint point) throws Throwable {
//        long startTime = System.currentTimeMillis();
//
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        String className = signature.getDeclaringType().getSimpleName();
//        String methodName = signature.getName();
//        Object[] args = point.getArgs();
//
//        Long userId = RequestContext.getCurrentUserId();
//        String userInfo = userId != null ? "userId=" + userId : "anonymous";
//
//        log.info(">>> {}.{} called by {}, args={}",
//                className, methodName, userInfo, sanitizeArgs(args));
//
//        Object result = null;
//        try {
//            result = point.proceed();
//            long duration = System.currentTimeMillis() - startTime;
//            log.info("<<< {}.{} completed in {}ms", className, methodName, duration);
//            return result;
//        } catch (Exception e) {
//            long duration = System.currentTimeMillis() - startTime;
//            log.error("!!! {}.{} failed after {}ms: {}",
//                    className, methodName, duration, e.getMessage());
//            throw e;
//        }
//    }
//
//    /**
//     * 清理敏感参数
//     */
//    private Object sanitizeArgs(Object[] args) {
//        if (args == null || args.length == 0) {
//            return "[]";
//        }
//        // 简单处理，不输出具体参数值
//        return "[args:" + args.length + "]";
//    }
//}
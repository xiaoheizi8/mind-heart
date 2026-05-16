package com.mindrealm.api.aspect;

import com.mindrealm.common.exception.RateLimitException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 */
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 短信验证码限流切点
     */
    @Pointcut("@annotation(com.mindrealm.api.annotation.RateLimit)")
    public void rateLimitPointcut() {}

    /**
     * 限流处理
     */
    @Around("rateLimitPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String methodName = point.getSignature().getName();
        String className = point.getTarget().getClass().getSimpleName();
        String key = "rate_limit:" + className + ":" + methodName;
        
        // 获取注解配置
        var annotation = point.getTarget().getClass()
                .getDeclaredMethod(methodName)
                .getAnnotation(com.mindrealm.api.annotation.RateLimit.class);
        
        if (annotation != null) {
            int limit = annotation.limit();
            int seconds = annotation.seconds();
            
            Boolean isLimited = redisTemplate.opsForValue().get(key) != null;
            if (Boolean.TRUE.equals(isLimited)) {
                throw new RateLimitException("请求过于频繁，请" + seconds + "秒后再试");
            }
            
            redisTemplate.opsForValue().set(key, "1", seconds, TimeUnit.SECONDS);
        }
        
        return point.proceed();
    }
}
package com.mindrealm.api.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 限制次数
     */
    int limit() default 1;
    
    /**
     * 时间周期(秒)
     */
    int seconds() default 60;
}
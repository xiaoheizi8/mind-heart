package com.mindrealm.api.annotation;

import java.lang.annotation.*;

/**
 * 输入安全校验注解
 * 标记在Controller方法上，自动对String类型参数进行安全过滤
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sanitize {
}
package com.mindrealm.common.annotation;

import java.lang.annotation.*;

/**
 * @className: OperationLog
 * @description: 操作日志注解，标记需要记录日志的方法
 * @author: 一朝风月
 * @code: 面向自己，面向未来
 * @createTime: 2026.4.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;
    
    /**
     * 是否记录请求参数
     */
    boolean logParams() default true;
    
    /**
     * 是否记录返回结果
     */
    boolean logResult() default false;
}

package com.mindrealm.common.exception;

/**
 * @className: RateLimitException
 * @description: 限流异常,当请求频率超过限制时抛出
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public class RateLimitException extends RuntimeException {
    
    /**
     * 构造方法
     * @param message 异常消息
     */
    public RateLimitException(String message) {
        super(message);
    }
}
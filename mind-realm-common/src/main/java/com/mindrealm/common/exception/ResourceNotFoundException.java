package com.mindrealm.common.exception;

/**
 * @className: ResourceNotFoundException
 * @description: 资源未找到异常,当查询的资源不存在时抛出
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * 构造方法
     * @param message 异常消息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 构造方法
     * @param resource 资源名称
     * @param id 资源ID
     */
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}
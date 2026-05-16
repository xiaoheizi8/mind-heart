package com.mindrealm.common.exception;

import com.mindrealm.common.result.Result;

/**
 * @className: BusinessException
 * @description: 业务异常类,用于抛出业务逻辑错误
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 私有构造函数
     */
    private BusinessException(Builder builder) {
        super(builder.message);
        this.code = builder.code;
        this.message = builder.message;
    }

    /**
     * 创建业务异常构建器
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public int getCode() {
        return code;
    }

    /**
     * @className: BusinessException.Builder
     * @description: 建造者模式构建器
     */
    public static class Builder {
        private int code = 500;
        private String message = "系统错误";

        /**
         * 设置错误码
         * @param code 错误码
         * @return Builder
         */
        public Builder code(int code) {
            this.code = code;
            return this;
        }

        /**
         * 设置错误消息
         * @param message 错误消息
         * @return Builder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 构建业务异常
         * @return BusinessException
         */
        public BusinessException build() {
            return new BusinessException(this);
        }

        /**
         * 快速构建:参数错误
         * @param message 错误消息
         * @return BusinessException
         */
        public BusinessException badRequest(String message) {
            return new BusinessException(new Builder().code(400).message(message));
        }

        /**
         * 快速构建:未授权
         * @param message 错误消息
         * @return BusinessException
         */
        public BusinessException unauthorized(String message) {
            return new BusinessException(new Builder().code(401).message(message));
        }

        /**
         * 快速构建:禁止访问
         * @param message 错误消息
         * @return BusinessException
         */
        public BusinessException forbidden(String message) {
            return new BusinessException(new Builder().code(403).message(message));
        }

        /**
         * 快速构建:资源不存在
         * @param message 错误消息
         * @return BusinessException
         */
        public BusinessException notFound(String message) {
            return new BusinessException(new Builder().code(404).message(message));
        }

        /**
         * 快速构建:服务器错误
         * @param message 错误消息
         * @return BusinessException
         */
        public BusinessException serverError(String message) {
            return new BusinessException(new Builder().code(500).message(message));
        }
    }

    /**
     * 转换为Result对象
     * @return Result
     */
    public Result<?> toResult() {
        return Result.error(code, message);
    }
}

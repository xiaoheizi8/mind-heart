package com.mindrealm.common.result;

import java.io.Serializable;

/**
 * @className: Result
 * @description: 统一响应结果包装类,所有API接口返回统一格式
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码:200-成功,400-客户端错误,401-未授权,403-禁止,500-服务器错误
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功结果(无数据)
     * @return Result
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        return result;
    }

    /**
     * 成功结果(无数据,带消息)
     * @param message 消息
     * @return Result
     */
    public static Result<Void> ok(String message) {
        Result<Void> result = new Result<>();
        result.code = 200;
        result.message = message;
        return result;
    }

    /**
     * 成功结果(带数据)
     * @param data 响应数据
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    /**
     * 成功结果(带消息和数据)
     * @param message 消息
     * @param data 数据
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    /**
     * 失败结果
     * @param message 错误消息
     * @return Result
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        return result;
    }

    /**
     * 失败结果(带错误码)
     * @param code 错误码
     * @param message 错误消息
     * @return Result
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    /**
     * 操作失败(无返回数据)
     * @param message 消息
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        return result;
    }

    /**
     * 参数错误(无返回数据)
     * @param message 错误消息
     * @return Result
     */
    public static <T> Result<T> badRequest(String message) {
        Result<T> result = new Result<>();
        result.code = 400;
        result.message = message;
        return result;
    }

    /**
     * 未授权(无返回数据)
     * @return Result
     */
    public static <T> Result<T> unauthorized() {
        Result<T> result = new Result<>();
        result.code = 401;
        result.message = "未授权,请先登录";
        return result;
    }

    /**
     * 禁止访问(无返回数据)
     * @param message 消息
     * @return Result
     */
    public static <T> Result<T> forbidden(String message) {
        Result<T> result = new Result<>();
        result.code = 403;
        result.message = message;
        return result;
    }

    /**
     * 资源不存在(无返回数据)
     * @param message 消息
     * @return Result
     */
    public static <T> Result<T> notFound(String message) {
        Result<T> result = new Result<>();
        result.code = 404;
        result.message = message;
        return result;
    }

    /**
     * 错误结果(带错误码)
     * @param code 错误码
     * @param message 错误消息
     * @return Result
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 判断是否成功
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}

package com.mindrealm.common.exception;

import com.mindrealm.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * @className: GlobalExceptionHandler
 * @description: 全局异常处理器,统一处理各类异常并返回统一格式
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * @param e 参数校验异常
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 处理绑定异常
     * @param e 绑定异常
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 处理运行时异常
     * @param e 运行时异常
     * @return Result
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error("服务器内部错误");
    }

    /**
     * 处理所有未捕获的异常
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统错误,请稍后重试");
    }

    /**
     * 处理限流异常
     * @param e 限流异常
     * @return Result
     */
    @ExceptionHandler(RateLimitException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Result<?> handleRateLimitException(RateLimitException e) {
        log.warn("请求限流: {}", e.getMessage());
        return Result.error(429, "请求过于频繁,请稍后重试");
    }

    /**
     * 处理资源未找到异常
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("资源未找到: {}", e.getMessage());
        return Result.notFound(e.getMessage());
    }

    /**
     * 处理约束违反异常
     * @param e 约束违反异常
     * @return Result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数约束违反: {}", message);
        return Result.badRequest(message);
    }

    /**
     * 处理404异常
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("接口不存在: {}", e.getRequestURL());
        return Result.notFound("接口不存在: " + e.getRequestURL());
    }

    /**
     * 处理非法参数异常
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return Result.badRequest(e.getMessage());
    }

    /**
     * 处理异步请求不可用异常(客户端主动断开连接)
     * 这是一个正常的客户端行为,不应该当作错误处理
     * @param e 异步请求异常
     * @return Result
     */
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public Result<?> handleAsyncRequestNotUsableException(AsyncRequestNotUsableException e) {
        // 降级为debug日志,避免日志污染
        log.debug("客户端主动断开SSE连接: {}", e.getMessage());
        // 不返回响应,因为连接已经断开
        return null;
    }
}

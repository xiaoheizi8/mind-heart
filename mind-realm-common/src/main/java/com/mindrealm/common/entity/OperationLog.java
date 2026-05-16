package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: OperationLog
 * @description: 操作日志实体类,记录用户操作行为包括模块、操作类型、请求信息、响应结果等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作模块(如user/diary/warning)
     */
    private String module;

    /**
     * 操作类型(如create/update/delete)
     */
    private String operation;

    /**
     * 请求方法(FullQualifiedMethodName)
     */
    private String method;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方法(GET/POST/PUT/DELETE)
     */
    private String requestMethod;

    /**
     * 请求参数(JSON格式)
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 执行时长(毫秒)
     */
    private Long duration;

    /**
     * 执行状态(1:成功,0:失败)
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime createdAt;
}

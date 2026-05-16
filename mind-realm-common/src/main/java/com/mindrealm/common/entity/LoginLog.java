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
 * @className: LoginLog
 * @description: 登录日志实体类,记录用户登录信息包括登录类型、IP地址、设备信息等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@TableName("login_log")
public class LoginLog implements Serializable {

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
     * 登录类型(username/password/code)
     */
    private String loginType;

    /**
     * IP地址
     */
    private String ip;

    /**
     * IP归属地
     */
    private String ipLocation;

    /**
     * 设备类型(PC/Mobile)
     */
    private String deviceType;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 登录状态(1:成功,0:失败)
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime createdAt;
}
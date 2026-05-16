package com.mindrealm.common.dto;

import lombok.Data;

/**
 * @className: LoginRequest
 * @description: 登录请求数据传输对象,包含用户名、密码、验证码等登录凭证
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
public class LoginRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码
     */
    private String code;
    /**
     * 登录类型(1:密码,2:验证码)
     */
    private Integer loginType;
}

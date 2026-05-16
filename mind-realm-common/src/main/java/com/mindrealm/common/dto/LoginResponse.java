package com.mindrealm.common.dto;

import lombok.Data;

/**
 * @className: LoginResponse
 * @description: 登录响应数据传输对象,包含token和用户信息
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
public class LoginResponse {
    /**
     * JWT令牌
     */
    private String token;
    /**
     * 用户信息
     */
    private UserDTO user;

    /**
     * 构造方法
     * @param token JWT令牌
     * @param user 用户信息
     */
    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}
package com.mindrealm.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @className: LoginResponse
 * @description: 登录响应结果
 * @author: 一朝风月
 * @code: 面向自己, 面向未来
 * @createTime: 2025
 */
@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private Integer role;
    private LocalDateTime expireTime;

    public LoginResponse(String token, Long userId, String username, String nickname, Integer role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }
}

package com.mindrealm.common.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: UserRegisterDTO
 * @description: 用户注册请求对象
 * @author: 一朝风月
 * @code: 面向自己, 面向未来
 * @createTime: 2026.07.04 15:40
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO implements Serializable {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 年龄
     */
    private Integer age;
    /**
     * 性别(1:男,2:女,3:未知)
     */
    private Integer gender;
    /**
     * 角色(1:普通用户,2:心理咨询师,3:学校管理员,4:超级管理员)
     */
    private Integer role;
    /**
     * 账号状态(0:禁用,1:正常)
     */
    private Integer status;

    private String password;

    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;





}

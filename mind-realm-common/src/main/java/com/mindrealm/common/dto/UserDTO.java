package com.mindrealm.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @className: UserDTO
 * @description: 用户数据传输对象,用于API接口返回用户基本信息
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
public class UserDTO {
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
     * 头像URL
     */
    private String avatar;
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
    /**
     * 监护人电话
     */
    private String guardianPhone;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

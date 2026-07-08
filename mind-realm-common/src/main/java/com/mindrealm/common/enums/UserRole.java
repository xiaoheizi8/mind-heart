package com.mindrealm.common.enums;

/**
 * @className: UserRole
 * @description: 用户角色枚举,定义系统中的用户角色及对应编码和描述
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public enum UserRole {
    /**
     * 普通用户
     */
    USER(1, "普通用户"),
    /**
     * 心理咨询师
     */
    COUNSELOR(2, "心理咨询师"),
    /**
     * 学校管理员
     */
    SCHOOL_ADMIN(3, "学校管理员"),
    /**
     * 超级管理员
     */
    ADMIN(4, "超级管理员"),
    /**
     * 家长
     */
    PARENT(5, "家长");

    /**
     * 角色编码
     */
    private final Integer code;
    /**
     * 角色描述
     */
    private final String desc;

    UserRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取角色编码
     * @return 角色编码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取角色描述
     * @return 角色描述
     */
    public String getDesc() {
        return desc;
    }
}

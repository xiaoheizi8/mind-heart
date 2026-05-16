package com.mindrealm.common.enums;

/**
 * @className: Gender
 * @description: 性别枚举,定义用户性别及对应编码和描述
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public enum Gender {
    /**
     * 男
     */
    MALE(1, "男"),
    /**
     * 女
     */
    FEMALE(2, "女"),
    /**
     * 未知
     */
    UNKNOWN(3, "未知");

    /**
     * 性别编码
     */
    private final Integer code;
    /**
     * 性别描述
     */
    private final String desc;

    Gender(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取性别编码
     * @return 性别编码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取性别描述
     * @return 性别描述
     */
    public String getDesc() {
        return desc;
    }
}

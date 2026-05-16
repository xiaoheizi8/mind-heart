package com.mindrealm.common.util;

/**
 * 敏感数据脱敏工具类
 */
public class MaskUtil {

    /**
     * 手机号脱敏
     * 138****8000
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * a***@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return parts[0] + "***@" + parts[1];
        }
        return parts[0].substring(0, 2) + "***@" + parts[1];
    }

    /**
     * 身份证号脱敏
     * 110101****1234
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "****" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 银行卡号脱敏
     * **** **** **** 1234
     */
    public static String maskBankCard(String cardNo) {
        if (cardNo == null || cardNo.length() < 8) {
            return cardNo;
        }
        String cleaned = cardNo.replaceAll("\\s", "");
        return "**** **** **** " + cleaned.substring(cleaned.length() - 4);
    }

    /**
     * 姓名脱敏
     * 张**
     */
    public static String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }
        return name.charAt(0) + "**";
    }

    /**
     * 密码脱敏
     * 返回固定长度的掩码
     */
    public static String maskPassword(String password) {
        if (password == null) {
            return null;
        }
        return "******";
    }

    /**
     * 通用脱敏
     * 保留前后各n位
     */
    public static String mask(String value, int prefixLen, int suffixLen) {
        if (value == null || value.length() <= prefixLen + suffixLen) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(value.substring(0, prefixLen));
        for (int i = 0; i < value.length() - prefixLen - suffixLen; i++) {
            sb.append("*");
        }
        sb.append(value.substring(value.length() - suffixLen));
        return sb.toString();
    }
}
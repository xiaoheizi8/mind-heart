package com.mindrealm.common.util;

import java.util.regex.Pattern;

/**
 * @className: ValidationUtil
 * @description: 验证工具类,提供手机号、邮箱、身份证号、URL、用户名、昵称、密码、验证码等的校验功能
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public class ValidationUtil {

    /**
     * 手机号验证正则
     */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱验证正则
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 身份证号验证正则
     */
    public static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /**
     * URL验证正则
     */
    public static final Pattern URL_PATTERN = Pattern.compile(
            "^(http|https)://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(/[^\\s]*)?$");

    /**
     * 验证手机号
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证邮箱
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证身份证号
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        return idCard != null && ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 验证URL
     * @param url URL地址
     * @return 是否有效
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    /**
     * 验证用户名（字母开头，字母数字下划线，4-20位）
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 4 || username.length() > 20) {
            return false;
        }
        return Pattern.matches("^[a-zA-Z][a-zA-Z0-9_]{3,19}$", username);
    }

    /**
     * 验证昵称（2-20位，中英文数字下划线）
     * @param nickname 昵称
     * @return 是否有效
     */
    public static boolean isValidNickname(String nickname) {
        if (nickname == null || nickname.length() < 2 || nickname.length() > 20) {
            return false;
        }
        return Pattern.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$", nickname);
    }

    /**
     * 验证密码（6-20位）
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 20;
    }

    /**
     * 验证验证码（4-6位数字）
     * @param code 验证码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        return code != null && code.length() >= 4 && code.length() <= 6 
                && Pattern.matches("^\\d+$", code);
    }

    /**
     * 清理字符串（去空格、控制字符）
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("[\\p{Cntrl}]", "");
    }
}
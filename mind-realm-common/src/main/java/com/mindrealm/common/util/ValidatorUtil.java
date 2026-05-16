package com.mindrealm.common.util;

import java.util.regex.Pattern;

/**
 * @className: ValidatorUtil
 * @description: 参数校验工具类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public class ValidatorUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z][a-zA-Z0-9_]{2,19}$"
    );

    private ValidatorUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断对象是否为空
     */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return obj != null;
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * 验证手机号格式
     */
    public static boolean isPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * 验证用户名格式（字母开头，3-20位，允许字母数字下划线）
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * 验证密码强度（至少6位）
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        return password.length() >= 6;
    }

    /**
     * 验证密码强度（包含数字和字母）
     */
    public static boolean isStrongPassword(String password) {
        if (isEmpty(password) || password.length() < 8) {
            return false;
        }
        boolean hasDigit = false;
        boolean hasLetter = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isLetter(c)) hasLetter = true;
        }
        return hasDigit && hasLetter;
    }

    /**
     * 验证ID是否有效
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * 验证分页参数
     */
    public static int validatePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    /**
     * 验证分页大小
     */
    public static int validatePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100); // 最大100条
    }

    /**
     * 验证字符串长度
     */
    public static boolean isLengthInRange(String str, int min, int max) {
        if (isEmpty(str)) {
            return min <= 0;
        }
        int len = str.trim().length();
        return len >= min && len <= max;
    }

    /**
     * 判断是否为数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 安全转换为Long
     */
    public static Long parseLong(String str, Long defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全转换为Integer
     */
    public static Integer parseInteger(String str, Integer defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

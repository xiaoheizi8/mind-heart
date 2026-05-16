package com.mindrealm.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @className: TimeUtil
 * @description: 时间工具类,提供获取当前时间、格式化时间等常用操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
public class TimeUtil {

    /**
     * 默认日期时间格式: yyyy-MM-dd HH:mm:ss
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 日期格式: yyyy-MM-dd
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 时间格式: HH:mm:ss
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * @description: 获取当前时间
     * @return: LocalDateTime 当前LocalDateTime对象
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * @description: 获取当前时间字符串(默认格式: yyyy-MM-dd HH:mm:ss)
     * @return: String 格式化后的时间字符串
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static String nowString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }

    /**
     * @description: 获取当前时间字符串(自定义格式)
     * @param: pattern 时间格式 pattern, 如 "yyyy-MM-dd HH:mm:ss"
     * @return: String 格式化后的时间字符串
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static String nowString(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * @description: 格式化指定时间(默认格式: yyyy-MM-dd HH:mm:ss)
     * @param: dateTime 待格式化的LocalDateTime对象
     * @return: String 格式化后的时间字符串
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }

    /**
     * @description: 格式化指定时间(自定义格式)
     * @param: dateTime 待格式化的LocalDateTime对象
     * @param: pattern 时间格式 pattern
     * @return: String 格式化后的时间字符串
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * @description: 解析时间字符串为LocalDateTime对象(默认格式)
     * @param: dateTimeStr 时间字符串
     * @return: LocalDateTime LocalDateTime对象
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }

    /**
     * @description: 解析时间字符串为LocalDateTime对象(自定义格式)
     * @param: dateTimeStr 时间字符串
     * @param: pattern 时间格式 pattern
     * @return: LocalDateTime LocalDateTime对象
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
}
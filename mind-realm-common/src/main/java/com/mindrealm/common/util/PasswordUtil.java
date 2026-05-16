package com.mindrealm.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @className: PasswordUtil
 * @description: 密码工具类,提供MD5加密功能
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public class PasswordUtil {

    /**
     * MD5加密
     * @param password 原始密码
     * @return MD5加密后的密码
     */
    public static String md5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("========== 密码MD5加密测试 ==========");
        System.out.println("12345678 -> " + md5("12345678"));
        System.out.println("请输入你的密码进行测试");
    }
}
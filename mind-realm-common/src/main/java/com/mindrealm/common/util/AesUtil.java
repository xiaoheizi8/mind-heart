package com.mindrealm.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @className: AesUtil
 * @description: AES加密解密工具类，用于日记内容加密存储
 *               使用AES-GCM模式，提供数据完整性和保密性
 * @author: 一朝风月
 * @code: 面向自己，面向未来
 * @createTime: 2026.4.5
 */
@Component
public class AesUtil {

    private static final Logger log = LoggerFactory.getLogger(AesUtil.class);

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;  // GCM推荐IV长度
    private static final int GCM_TAG_LENGTH = 128; // 认证标签长度(位)

    // 默认密钥，用于非Spring环境（如单元测试）
    private static String SECRET_KEY = "MindRealm2026SecretKeyForDiaryEncryption!@#";

    @Value("${app.aes.secret:MindRealm2026SecretKeyForDiaryEncryption!@#}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    /**
     * 加密文本
     * @param plainText 明文
     * @return Base64编码的密文(IV + 密文)
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // 初始化密钥
            SecretKeySpec keySpec = getKeySpec();
            
            // 初始化Cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // 加密
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 将IV和密文组合
            byte[] cipherTextWithIv = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
            System.arraycopy(cipherText, 0, cipherTextWithIv, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(cipherTextWithIv);
        } catch (Exception e) {
            log.error("AES加密失败: {}", e.getMessage());
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密文本
     * @param encryptedText Base64编码的密文(IV + 密文)
     * @return 明文
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        try {
            // Base64解码
            byte[] cipherTextWithIv = Base64.getDecoder().decode(encryptedText);

            // 提取IV和密文
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[cipherTextWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(cipherTextWithIv, 0, iv, 0, iv.length);
            System.arraycopy(cipherTextWithIv, iv.length, cipherText, 0, cipherText.length);

            // 初始化密钥
            SecretKeySpec keySpec = getKeySpec();

            // 初始化Cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // 解密
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败: {}", e.getMessage());
            throw new RuntimeException("解密失败", e);
        }
    }

    /**
     * 获取密钥规范
     * 密钥会被填充或截断到16字节(128位)
     */
    private static SecretKeySpec getKeySpec() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[16]; // AES-128
        int len = Math.min(keyBytes.length, 16);
        System.arraycopy(keyBytes, 0, key, 0, len);
        // 不足16字节则填充
        for (int i = len; i < 16; i++) {
            key[i] = (byte) i;
        }
        return new SecretKeySpec(key, "AES");
    }

    /**
     * 检查字符串是否已加密
     * 加密后的字符串是Base64格式
     */
    public static boolean isEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(text);
            // 加密后的数据至少包含IV(12字节) + GCM Tag(16字节) = 28字节
            return decoded.length >= 28;
        } catch (Exception e) {
            return false;
        }
    }
}

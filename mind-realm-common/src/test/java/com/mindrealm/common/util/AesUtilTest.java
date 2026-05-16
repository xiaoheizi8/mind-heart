package com.mindrealm.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AesUtil单元测试
 */
class AesUtilTest {

    @Test
    void testEncryptAndDecrypt() {
        String plainText = "今天心情很好，阳光明媚！";
        
        // 加密
        String encrypted = AesUtil.encrypt(plainText);
        
        // 验证加密后的内容不等于原文
        assertNotEquals(plainText, encrypted);
        
        // 验证加密后的内容是Base64格式
        assertTrue(AesUtil.isEncrypted(encrypted));
        
        // 解密
        String decrypted = AesUtil.decrypt(encrypted);
        
        // 验证解密后的内容等于原文
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptEmptyString() {
        String empty = "";
        assertEquals(empty, AesUtil.encrypt(empty));
        assertEquals(null, AesUtil.encrypt(null));
    }

    @Test
    void testDecryptEmptyString() {
        String empty = "";
        assertEquals(empty, AesUtil.decrypt(empty));
        assertEquals(null, AesUtil.decrypt(null));
    }

    @Test
    void testIsEncrypted() {
        // 加密后的字符串
        String encrypted = AesUtil.encrypt("测试内容");
        assertTrue(AesUtil.isEncrypted(encrypted));
        
        // 未加密的普通字符串
        assertFalse(AesUtil.isEncrypted("普通字符串"));
        assertFalse(AesUtil.isEncrypted(null));
        assertFalse(AesUtil.isEncrypted(""));
    }

    @Test
    void testMultipleEncrypt() {
        String plainText = "每次加密结果应该不同（因为IV随机）";
        
        String encrypted1 = AesUtil.encrypt(plainText);
        String encrypted2 = AesUtil.encrypt(plainText);
        
        // 两次加密结果不同（因为IV随机）
        assertNotEquals(encrypted1, encrypted2);
        
        // 但都能正确解密
        assertEquals(plainText, AesUtil.decrypt(encrypted1));
        assertEquals(plainText, AesUtil.decrypt(encrypted2));
    }

    @Test
    void testLongText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("这是一段很长的测试文本。");
        }
        String longText = sb.toString();
        
        String encrypted = AesUtil.encrypt(longText);
        String decrypted = AesUtil.decrypt(encrypted);
        
        assertEquals(longText, decrypted);
    }
}

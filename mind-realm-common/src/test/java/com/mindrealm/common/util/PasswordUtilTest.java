package com.mindrealm.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordUtil单元测试
 */
class PasswordUtilTest {

    @Test
    void testMd5() {
        // 测试标准MD5
        String password = "123456";
        String md5 = PasswordUtil.md5(password);
        
        // 验证MD5长度为32
        assertEquals(32, md5.length());
        
        // 验证相同密码生成相同MD5
        assertEquals(md5, PasswordUtil.md5(password));
    }

    @Test
    void testMd5Empty() {
        String empty = "";
        String md5 = PasswordUtil.md5(empty);
        
        // 空字符串也能生成MD5
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }

    @Test
    void testMd5Chinese() {
        String chinese = "中文密码";
        String md5 = PasswordUtil.md5(chinese);
        
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }
    
    @Test
    void testMd5KnownValue() {
        // 测试已知的MD5值
        // 123456 的MD5应该是 e10adc3949ba59abbe56e057f20f883e
        assertEquals("e10adc3949ba59abbe56e057f20f883e", PasswordUtil.md5("123456"));
    }
}

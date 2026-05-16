package com.mindrealm.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    private SecurityUtil securityUtil;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil();
    }

    @Test
    void testSanitizeXss_NormalInput() {
        String input = "这是一个正常的输入";
        String result = securityUtil.sanitizeXss(input);
        assertEquals(input, result);
    }

    @Test
    void testSanitizeXss_ScriptTag() {
        String input = "<script>alert('xss')</script>";
        String result = securityUtil.sanitizeXss(input);
        assertFalse(result.contains("<script>"));
        assertTrue(result.contains("&lt;script&gt;"));
    }

    @Test
    void testSanitizeXss_JavascriptProtocol() {
        String input = "javascript:alert('xss')";
        String result = securityUtil.sanitizeXss(input);
        assertFalse(result.contains("javascript:"));
    }

    @Test
    void testSanitizeXss_OnEventHandler() {
        String input = "<img onerror=\"alert('xss')\" src=\"x\">";
        String result = securityUtil.sanitizeXss(input);
        assertFalse(result.contains("onerror="));
    }

    @Test
    void testSanitizeXss_NullInput() {
        assertNull(securityUtil.sanitizeXss(null));
    }

    @Test
    void testSanitizeXss_EmptyInput() {
        assertEquals("", securityUtil.sanitizeXss(""));
    }

    @Test
    void testSanitizeSql_NormalInput() {
        String input = "这是一个正常的SQL输入";
        String result = securityUtil.sanitizeSql(input);
        assertEquals(input, result);
    }

    @Test
    void testSanitizeSql_SingleQuote() {
        String input = "user's name";
        String result = securityUtil.sanitizeSql(input);
        assertTrue(result.contains("''"));
    }

    @Test
    void testSanitizeSql_SqlKeywords() {
        String input = "user's name; DROP TABLE users";
        String result = securityUtil.sanitizeSql(input);
        assertTrue(result.contains("user''s name"));
        assertFalse(result.contains(";"));
    }

    @Test
    void testSanitizeSql_NullInput() {
        assertNull(securityUtil.sanitizeSql(null));
    }

    @Test
    void testContainsSqlInjection_SafeInput() {
        assertFalse(securityUtil.containsSqlInjection("正常输入"));
        assertFalse(securityUtil.containsSqlInjection("123456"));
    }

    @Test
    void testContainsSqlInjection_DangerousInput() {
        assertTrue(securityUtil.containsSqlInjection("'; DROP TABLE users; --"));
        assertTrue(securityUtil.containsSqlInjection("1 OR 1=1"));
        assertTrue(securityUtil.containsSqlInjection("admin'--"));
    }

    @Test
    void testContainsXss_SafeInput() {
        assertFalse(securityUtil.containsXss("正常输入"));
        assertFalse(securityUtil.containsXss("测试文本"));
    }

    @Test
    void testContainsXss_DangerousInput() {
        assertTrue(securityUtil.containsXss("<script>alert(1)</script>"));
        assertTrue(securityUtil.containsXss("javascript:alert(1)"));
        assertTrue(securityUtil.containsXss("<img onerror=alert(1)>"));
    }

    @Test
    void testContainsMaliciousContent() {
        assertTrue(securityUtil.containsMaliciousContent("<script>alert(1)</script>"));
        assertTrue(securityUtil.containsMaliciousContent("'; DROP TABLE --"));
        assertFalse(securityUtil.containsMaliciousContent("这是一个正常的测试"));
    }

    @Test
    void testStripHtml() {
        String input = "<p>这是<strong>加粗</strong>文本</p>";
        String result = securityUtil.stripHtml(input);
        assertFalse(result.contains("<p>"));
        assertFalse(result.contains("</p>"));
        assertTrue(result.contains("这是"));
        assertTrue(result.contains("加粗"));
    }

    @Test
    void testStripHtml_NullInput() {
        assertNull(securityUtil.stripHtml(null));
    }

    @Test
    void testMaskSensitiveData_Phone() {
        String phone = "13812345678";
        String result = securityUtil.maskSensitiveData(phone, "phone");
        assertTrue(result.startsWith("138"));
        assertTrue(result.endsWith("5678"));
        assertTrue(result.contains("****"));
    }

    @Test
    void testMaskSensitiveData_Email() {
        String email = "test@example.com";
        String result = securityUtil.maskSensitiveData(email, "email");
        assertTrue(result.contains("@example.com"));
        assertTrue(result.contains("***"));
    }

    @Test
    void testMaskSensitiveData_IdCard() {
        String idCard = "110101199001011234";
        String result = securityUtil.maskSensitiveData(idCard, "idcard");
        assertTrue(result.startsWith("1101"));
        assertTrue(result.endsWith("1234"));
        assertTrue(result.contains("**********"));
    }

    @Test
    void testMaskSensitiveData_BankCard() {
        String bankCard = "6222021234567890123";
        String result = securityUtil.maskSensitiveData(bankCard, "bankcard");
        assertTrue(result.endsWith("0123"));
        assertTrue(result.contains("****"));
    }

    @Test
    void testGenerateSecureToken() {
        String token1 = securityUtil.generateSecureToken(32);
        String token2 = securityUtil.generateSecureToken(32);
        
        assertNotNull(token1);
        assertEquals(32, token1.length());
        assertNotEquals(token1, token2);
    }

    @Test
    void testIsValidFileName_Valid() {
        assertTrue(securityUtil.isValidFileName("document.pdf"));
        assertTrue(securityUtil.isValidFileName("my_file.txt"));
        assertTrue(securityUtil.isValidFileName("报告-2024.docx"));
    }

    @Test
    void testIsValidFileName_Invalid() {
        assertFalse(securityUtil.isValidFileName("file<test>.txt"));
        assertFalse(securityUtil.isValidFileName("file:test.txt"));
        assertFalse(securityUtil.isValidFileName("file*test.txt"));
        assertFalse(securityUtil.isValidFileName(null));
        assertFalse(securityUtil.isValidFileName(""));
    }

    @Test
    void testSanitizeFileName() {
        String result = securityUtil.sanitizeFileName("file<test>.txt");
        assertFalse(result.contains("<"));
        assertFalse(result.contains(">"));
        assertTrue(result.contains("file_test_.txt"));
    }

    @Test
    void testSanitizeFileName_LongName() {
        String longName = "a".repeat(300) + ".txt";
        String result = securityUtil.sanitizeFileName(longName);
        assertTrue(result.length() <= 255);
        assertTrue(result.endsWith(".txt"));
    }

    @Test
    void testSanitizeFileName_Null() {
        assertEquals("unnamed", securityUtil.sanitizeFileName(null));
    }
}

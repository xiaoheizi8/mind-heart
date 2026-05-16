package com.mindrealm.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MaskUtil单元测试
 */
class MaskUtilTest {

    @Test
    void testMaskPhone() {
        assertEquals("138****8000", MaskUtil.maskPhone("13800138000"));
        assertEquals("150****1234", MaskUtil.maskPhone("15012341234"));
        assertEquals("123", MaskUtil.maskPhone("123")); // 短号码不处理
        assertNull(MaskUtil.maskPhone(null));
    }

    @Test
    void testMaskEmail() {
        assertEquals("ab***@example.com", MaskUtil.maskEmail("abcd@example.com"));
        assertEquals("ab***@test.com", MaskUtil.maskEmail("ab@test.com"));
        assertEquals("invalid", MaskUtil.maskEmail("invalid")); // 无@不处理
        assertNull(MaskUtil.maskEmail(null));
    }

    @Test
    void testMaskIdCard() {
        assertEquals("1101****1234", MaskUtil.maskIdCard("110101199001011234"));
        assertEquals("1234****5678", MaskUtil.maskIdCard("12345678"));
        assertEquals("123", MaskUtil.maskIdCard("123")); // 短的不处理
        assertNull(MaskUtil.maskIdCard(null));
    }

    @Test
    void testMaskBankCard() {
        assertEquals("**** **** **** 1234", MaskUtil.maskBankCard("6222021234567891234"));
        assertEquals("**** **** **** 5678", MaskUtil.maskBankCard("6222 0212 3456 785678"));
        assertNull(MaskUtil.maskBankCard(null));
    }

    @Test
    void testMaskName() {
        assertEquals("张**", MaskUtil.maskName("张三"));
        assertEquals("李**", MaskUtil.maskName("李四四"));
        assertEquals("王", MaskUtil.maskName("王")); // 单字不处理
        assertNull(MaskUtil.maskName(null));
    }

    @Test
    void testMaskPassword() {
        assertEquals("******", MaskUtil.maskPassword("123456"));
        assertEquals("******", MaskUtil.maskPassword("abcdefgh"));
        assertNull(MaskUtil.maskPassword(null));
    }

    @Test
    void testMask() {
        // 通用脱敏：保留前后各2位
        // 10位字符，保留前2后2，中间6位变成*号
        assertEquals("12******90", MaskUtil.mask("1234567890", 2, 2));
        // 字符串太短，不处理
        assertEquals("123", MaskUtil.mask("123", 2, 2));
        assertNull(MaskUtil.mask(null, 2, 2));
    }
}

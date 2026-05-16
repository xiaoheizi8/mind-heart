package com.mindrealm.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * IpLocationUtil单元测试
 */
class IpLocationUtilTest {

    @Test
    void testLocalIp() {
        assertEquals("本地", IpLocationUtil.getLocation("127.0.0.1"));
        assertEquals("本地", IpLocationUtil.getLocation("localhost"));
        assertEquals("本地", IpLocationUtil.getLocation("192.168.1.1"));
        assertEquals("本地", IpLocationUtil.getLocation("10.0.0.1"));
    }

    @Test
    void testChineseIp() {
        // 中国IP段
        assertEquals("中国", IpLocationUtil.getLocation("220.181.38.148")); // 百度
        assertEquals("中国", IpLocationUtil.getLocation("221.179.155.161")); // 中国电信
    }

    @Test
    void testForeignIp() {
        // 美国IP段
        assertEquals("美国", IpLocationUtil.getLocation("8.8.8.8")); // Google DNS
        assertEquals("美国", IpLocationUtil.getLocation("4.4.4.4"));
    }

    @Test
    void testNullIp() {
        assertEquals("未知", IpLocationUtil.getLocation(null));
        assertEquals("未知", IpLocationUtil.getLocation(""));
    }

    @Test
    void testInvalidIp() {
        assertEquals("未知", IpLocationUtil.getLocation("invalid"));
        assertEquals("未知", IpLocationUtil.getLocation("abc.def.ghi.jkl"));
    }

    @Test
    void testCacheClear() {
        IpLocationUtil.getLocation("8.8.8.8");
        IpLocationUtil.clearCache();
        // 清除缓存后仍能正常查询
        assertEquals("美国", IpLocationUtil.getLocation("8.8.8.8"));
    }
}

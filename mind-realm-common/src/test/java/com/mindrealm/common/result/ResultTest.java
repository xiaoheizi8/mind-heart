package com.mindrealm.common.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @className: ResultTest
 * @description: 统一响应结果测试类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
class ResultTest {

    /**
     * 测试成功结果(无数据)
     */
    @Test
    void testSuccessWithoutData() {
        Result<String> result = Result.success();
        
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 测试成功结果(带数据)
     */
    @Test
    void testSuccessWithData() {
        Result<String> result = Result.success("操作成功", "test data");
        
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("test data", result.getData());
    }

    /**
     * 测试失败结果
     */
    @Test
    void testError() {
        Result<String> result = Result.error("错误消息");
        
        assertFalse(result.isSuccess());
        assertEquals(500, result.getCode());
        assertEquals("错误消息", result.getMessage());
    }

    /**
     * 测试自定义错误码
     */
    @Test
    void testErrorWithCode() {
        Result<String> result = Result.error(400, "参数错误");
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
    }

    /**
     * 测试参数错误快捷方法(无返回数据)
     */
    @Test
    void testBadRequest() {
        Result<Void> result = Result.badRequest("参数不能为空");
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    /**
     * 测试未授权快捷方法(无返回数据)
     */
    @Test
    void testUnauthorized() {
        Result<Void> result = Result.unauthorized();
        
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    /**
     * 测试禁止访问快捷方法(无返回数据)
     */
    @Test
    void testForbidden() {
        Result<Void> result = Result.forbidden("无权限");
        
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
    }

    /**
     * 测试不存在快捷方法(无返回数据)
     */
    @Test
    void testNotFound() {
        Result<Void> result = Result.notFound("资源不存在");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    /**
     * 测试成功消息(无返回数据)
     */
    @Test
    void testOk() {
        Result<Void> result = Result.ok("操作完成");
        
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("操作完成", result.getMessage());
    }

    /**
     * 测试失败方法
     */
    @Test
    void testFail() {
        Result<Void> result = Result.fail("操作失败");
        
        assertFalse(result.isSuccess());
        assertEquals(500, result.getCode());
        assertEquals("操作失败", result.getMessage());
    }
}
package com.mindrealm.core.service;

import com.mindrealm.MindRealmApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @className: AiChatStreamTest
 * @description: AI对话流式输出单元测试
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
@Slf4j
@Tag("integration")
@SpringBootTest(classes = MindRealmApplication.class)
class AiChatStreamTest {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 测试流式对话是否正常输出
     */
    @Test
    void testChatStream() throws InterruptedException {
        Long userId = 1L;
        String message = "你好，请简单介绍一下你自己";
        String persona = "listener";

        log.info("开始测试流式对话...");

        // 用于收集所有chunk
        List<String> chunks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> fullResponse = new AtomicReference<>();

        // 调用流式对话
        CompletableFuture<String> future = aiChatService.chatStream(userId, message, persona, chunk -> {
            log.info("收到chunk: {}", chunk);
            chunks.add(chunk);
        });

        // 等待完成
        future.thenAccept(response -> {
            fullResponse.set(response);
            log.info("流式对话完成，完整回复: {}", response);
            latch.countDown();
        }).exceptionally(e -> {
            log.error("流式对话失败: {}", e.getMessage(), e);
            latch.countDown();
            return null;
        });

        // 最多等待60秒
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        assertTrue(completed, "流式对话超时");

        // 验证结果
        assertNotNull(fullResponse.get(), "回复不能为空");
        assertFalse(fullResponse.get().isEmpty(), "回复不能为空字符串");
        assertFalse(chunks.isEmpty(), "应该收到至少一个chunk");

        // 验证chunk拼接后等于完整回复
        String joinedChunks = String.join("", chunks);
        assertEquals(fullResponse.get(), joinedChunks, "chunk拼接后应该等于完整回复");

        log.info("测试通过！共收到{}个chunk", chunks.size());
    }

    /**
     * 测试流式对话的实时性（确保不是等全部生成完才返回）
     */
    @Test
    void testStreamRealTime() throws InterruptedException {
        Long userId = 1L;
        String message = "请写一篇不少于100字的文章";
        String persona = "analyst";

        log.info("开始测试流式实时性...");

        AtomicInteger chunkCount = new AtomicInteger(0);
        AtomicBoolean receivedFirstChunk = new AtomicBoolean(false);
        long[] firstChunkTime = new long[1];
        long[] startTime = new long[1];
        CountDownLatch latch = new CountDownLatch(1);

        startTime[0] = System.currentTimeMillis();

        CompletableFuture<String> future = aiChatService.chatStream(userId, message, persona, chunk -> {
            int count = chunkCount.incrementAndGet();
            if (count == 1) {
                firstChunkTime[0] = System.currentTimeMillis();
                receivedFirstChunk.set(true);
                long delay = firstChunkTime[0] - startTime[0];
                log.info("收到第一个chunk，延迟: {}ms", delay);
                // 第一个chunk应该在5秒内到达（真正的流式）
                assertTrue(delay < 5000, "第一个chunk应该在5秒内到达，当前延迟: " + delay + "ms");
            }
        });

        future.thenAccept(response -> {
            long totalTime = System.currentTimeMillis() - startTime[0];
            log.info("流式对话完成，总耗时: {}ms, chunk数量: {}", totalTime, chunkCount.get());
            latch.countDown();
        }).exceptionally(e -> {
            log.error("流式对话失败: {}", e.getMessage(), e);
            latch.countDown();
            return null;
        });

        boolean completed = latch.await(60, TimeUnit.SECONDS);
        assertTrue(completed, "流式对话超时");
        assertTrue(receivedFirstChunk.get(), "应该收到至少一个chunk");
        assertTrue(chunkCount.get() > 1, "应该收到多个chunk（当前: " + chunkCount.get() + "）");
    }

    /**
     * 测试不同人格的流式对话
     */
    @Test
    void testStreamWithDifferentPersonas() throws InterruptedException {
        String[] personas = {"listener", "analyst", "healer"};
        String message = "我感到很焦虑，怎么办？";
        Long userId = 1L;

        for (String persona : personas) {
            log.info("测试人格: {}", persona);

            List<String> chunks = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(1);

            aiChatService.chatStream(userId, message, persona, chunk -> {
                chunks.add(chunk);
            }).thenAccept(response -> {
                log.info("人格[{}]回复完成，chunk数: {}", persona, chunks.size());
                latch.countDown();
            }).exceptionally(e -> {
                log.error("人格[{}]流式对话失败: {}", persona, e.getMessage());
                latch.countDown();
                return null;
            });

            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertTrue(completed, "人格[" + persona + "]流式对话超时");
            assertFalse(chunks.isEmpty(), "人格[" + persona + "]应该收到chunk");
        }
    }

    /**
     * 测试错误处理 - 空消息
     */
    @Test
    void testStreamWithEmptyMessage() throws InterruptedException {
        Long userId = 1L;
        String message = "";
        String persona = "listener";

        List<String> chunks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> response = new AtomicReference<>();

        aiChatService.chatStream(userId, message, persona, chunk -> {
            chunks.add(chunk);
        }).thenAccept(resp -> {
            response.set(resp);
            latch.countDown();
        });

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "应该快速返回");
        assertNotNull(response.get(), "应该返回备用回复");
    }

    /**
     * 测试异常用户ID
     */
    @Test
    void testStreamWithInvalidUserId() throws InterruptedException {
        Long userId = -1L;
        String message = "你好";
        String persona = "listener";

        List<String> chunks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        aiChatService.chatStream(userId, message, persona, chunk -> {
            chunks.add(chunk);
        }).thenAccept(response -> {
            log.info("收到回复: {}", response);
            latch.countDown();
        });

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "应该快速返回备用回复");
    }
}

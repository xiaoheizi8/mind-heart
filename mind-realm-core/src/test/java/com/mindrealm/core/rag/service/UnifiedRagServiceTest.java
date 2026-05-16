package com.mindrealm.core.rag.service;

import com.mindrealm.core.rag.config.RagProperties;
import com.mindrealm.core.rag.factory.RagStrategyFactory;
import com.mindrealm.core.rag.model.RagRequest;
import com.mindrealm.core.rag.model.RagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnifiedRagServiceTest {

    @Mock
    private RagStrategyFactory strategyFactory;

    private UnifiedRagServiceImpl ragService;

    @BeforeEach
    void setUp() {
        ragService = new UnifiedRagService(strategyFactory);
    }

    @Test
    void testChat_Success() {
        RagRequest request = RagRequest.builder()
                .userId(1L)
                .question("如何缓解焦虑")
                .build();

        RagResponse mockResponse = RagResponse.builder()
                .success(true)
                .answer("放松技巧包括深呼吸、正念冥�?..")
                .mode("LOCAL_MEMORY")
                .build();

        when(strategyFactory.getStrategy()).thenReturn(new MockStrategy(true, mockResponse));

        RagResponse result = ragService.chat(request);

        assertTrue(result.isSuccess());
        assertEquals("放松技巧包括深呼吸、正念冥�?..", result.getAnswer());
    }

    @Test
    void testChat_ServiceUnavailable() {
        RagRequest request = RagRequest.builder()
                .userId(1L)
                .question("如何缓解焦虑")
                .build();

        when(strategyFactory.getStrategy()).thenReturn(new MockStrategy(false, null));

        RagResponse result = ragService.chat(request);

        assertFalse(result.isSuccess());
    }

    @Test
    void testChat_WithHistory() {
        RagRequest request = RagRequest.builder()
                .userId(1L)
                .question("还有其他的建议吗�?)
                .history(java.util.Arrays.asList(
                        RagRequest.ChatMessage.builder()
                                .role("user")
                                .content("我最近很焦虑")
                                .build(),
                        RagRequest.ChatMessage.builder()
                                .role("assistant")
                                .content("你可以尝试深呼吸")
                                .build()
                ))
                .build();

        RagResponse mockResponse = RagResponse.builder()
                .success(true)
                .answer("除了深呼吸，你还可以尝试...")
                .mode("LOCAL_MEMORY")
                .build();

        when(strategyFactory.getStrategy()).thenReturn(new MockStrategy(true, mockResponse));

        RagResponse result = ragService.chat(request);

        assertTrue(result.isSuccess());
        assertTrue(result.getAnswer().contains("除了深呼�?));
    }

    @Test
    void testChat_LongUserId() {
        RagRequest request = RagRequest.builder()
                .userId(null)
                .question("测试问题")
                .build();

        RagResponse mockResponse = RagResponse.builder()
                .success(true)
                .answer("测试回答")
                .mode("LOCAL_MEMORY")
                .build();

        when(strategyFactory.getStrategy()).thenReturn(new MockStrategy(true, mockResponse));

        RagResponse result = ragService.chat(request);

        assertTrue(result.isSuccess());
    }

    @Test
    void testChat_ShortQuestion() {
        RagRequest request = RagRequest.builder()
                .userId(1L)
                .question("焦虑")
                .build();

        RagResponse mockResponse = RagResponse.builder()
                .success(true)
                .answer("焦虑�?..")
                .mode("LOCAL_MEMORY")
                .build();

        when(strategyFactory.getStrategy()).thenReturn(new MockStrategy(true, mockResponse));

        RagResponse result = ragService.chat(request);

        assertTrue(result.isSuccess());
    }

    @Test
    void testChatWithMode() {
        RagRequest request = RagRequest.builder()
                .userId(1L)
                .question("测试问题")
                .build();

        RagResponse mockResponse = RagResponse.builder()
                .success(true)
                .answer("测试回答")
                .mode("BAILIAN_KNOWLEDGE")
                .build();

        when(strategyFactory.getStrategy(RagProperties.Mode.BAILIAN_KNOWLEDGE))
                .thenReturn(new MockStrategy(true, mockResponse));

        RagResponse result = ragService.chatWithMode(RagProperties.Mode.BAILIAN_KNOWLEDGE, request);

        assertTrue(result.isSuccess());
        assertEquals("BAILIAN_KNOWLEDGE", result.getMode());
    }

    @Test
    void testSwitchMode() {
        ragService.switchMode(RagProperties.Mode.LOCAL_MEMORY);
        verify(strategyFactory).selectStrategy(RagProperties.Mode.LOCAL_MEMORY);
    }

    @Test
    void testClearHistory() {
        when(strategyFactory.getStrategy()).thenReturn(new MockStrategy(true, null));
        ragService.clearHistory(1L);
        verify(strategyFactory).getStrategy();
    }

    @Test
    void testGetAvailableModes() {
        Map<RagProperties.Mode, Boolean> modes = new HashMap<>();
        modes.put(RagProperties.Mode.LOCAL_MEMORY, true);
        modes.put(RagProperties.Mode.BAILIAN_KNOWLEDGE, false);

        when(strategyFactory.getAvailableModes()).thenReturn(modes);

        Map<RagProperties.Mode, Boolean> result = ragService.getAvailableModes();

        assertEquals(2, result.size());
        assertTrue(result.get(RagProperties.Mode.LOCAL_MEMORY));
        assertFalse(result.get(RagProperties.Mode.BAILIAN_KNOWLEDGE));
    }

    private static class MockStrategy implements com.mindrealm.core.rag.strategy.RagStrategy {
        private final boolean available;
        private final RagResponse response;

        MockStrategy(boolean available, RagResponse response) {
            this.available = available;
            this.response = response;
        }

        @Override
        public String getMode() {
            return "MOCK";
        }

        @Override
        public boolean isAvailable() {
            return available;
        }

        @Override
        public RagResponse chat(RagRequest request) {
            return response != null ? response : RagResponse.error("Mock error");
        }
    }
}

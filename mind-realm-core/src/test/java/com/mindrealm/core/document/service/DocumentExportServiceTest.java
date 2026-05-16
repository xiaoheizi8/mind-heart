package com.mindrealm.core.document.service;

import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.core.document.service.impl.DocumentExportServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentExportServiceTest {

    private DocumentExportServiceImpl exportService;

    @Mock
    private HttpServletResponse response;

    private ByteArrayOutputStream outputStream;
    private ServletOutputStream servletOutputStream;

    @BeforeEach
    void setUp() {
        exportService = new DocumentExportService();
        outputStream = new ByteArrayOutputStream();
        servletOutputStream = new MockServletOutputStream(outputStream);
    }

    @Test
    void testExportToCsv() throws IOException {
        List<Knowledge> knowledgeList = createTestKnowledgeList();
        
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        exportService.exportToCsv(knowledgeList, response);

        String result = outputStream.toString("UTF-8");
        assertNotNull(result);
        assertTrue(result.contains("编号"));
        assertTrue(result.contains("如何应对考试焦虑"));
    }

    @Test
    void testExportToCsvWithEmptyList() throws IOException {
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        exportService.exportToCsv(Arrays.asList(), response);

        String result = outputStream.toString("UTF-8");
        assertNotNull(result);
        assertTrue(result.contains("编号"));
    }

    @Test
    void testExportToCsvWithSpecialCharacters() throws IOException {
        Knowledge knowledge = Knowledge.builder()
                .id(1L)
                .title("测试\"引号\",�?单引�?")
                .category("test")
                .content("内容�?逗号\n换行")
                .createdAt(LocalDateTime.now())
                .build();

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        exportService.exportToCsv(Arrays.asList(knowledge), response);

        String result = outputStream.toString("UTF-8");
        assertNotNull(result);
        assertTrue(result.contains("\""));
    }

    @Test
    void testExportToCsvHeaders() throws IOException {
        List<Knowledge> knowledgeList = createTestKnowledgeList();
        
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        exportService.exportToCsv(knowledgeList, response);

        verify(response).setContentType("text/csv");
        verify(response, atLeast(1)).setCharacterEncoding("UTF-8");
    }

    @Test
    void testExportToCsvWithNullFields() throws IOException {
        Knowledge knowledge = Knowledge.builder()
                .id(1L)
                .title("测试")
                .category(null)
                .summary(null)
                .content(null)
                .source(null)
                .viewCount(null)
                .status(null)
                .createdAt(null)
                .build();

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        assertDoesNotThrow(() -> exportService.exportToCsv(Arrays.asList(knowledge), response));

        String result = outputStream.toString("UTF-8");
        assertNotNull(result);
        assertTrue(result.contains("测试"));
    }

    private List<Knowledge> createTestKnowledgeList() {
        Knowledge k1 = Knowledge.builder()
                .id(1L)
                .title("如何应对考试焦虑")
                .category("skill")
                .summary("考试焦虑的应对方�?)
                .content("考试焦虑是很多学生都会面临的问题...")
                .tags("[\"焦虑\",\"考试\"]")
                .source("心域知识�?)
                .viewCount(100)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        Knowledge k2 = Knowledge.builder()
                .id(2L)
                .title("正念冥想入门指南")
                .category("skill")
                .summary("正念冥想基础教程")
                .content("正念冥想是一种有效的情绪调节方法...")
                .tags("[\"冥想\",\"放松\"]")
                .source("心域知识�?)
                .viewCount(200)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        return Arrays.asList(k1, k2);
    }

    private static class MockServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream baos;

        public MockServletOutputStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public void write(int b) throws IOException {
            baos.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            baos.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener listener) {
        }
    }
}

package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.common.entity.User;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.service.DiaryService;
import com.mindrealm.diary.service.EmotionReportService;
import com.mindrealm.mq.producer.KafkaEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DiaryController 单元测试
 * 覆盖: 创建日记、更新日记、查询日记、删除日记、情绪报告、统计
 */
@ExtendWith(MockitoExtension.class)
class DiaryControllerTest {

    @Mock
    private DiaryService diaryService;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @Mock
    private EmotionReportService reportService;

    @InjectMocks
    private DiaryController diaryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(diaryController).build();
        objectMapper = new ObjectMapper();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                1L, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateDiary() throws Exception {
        Diary diary = Diary.builder()
                .id(1L)
                .userId(1L)
                .content("今天心情不错")
                .emotionCategory("happy")
                .build();

        when(diaryService.create(any(Diary.class))).thenReturn(diary);
        doNothing().when(kafkaEventPublisher).publish(anyString(), anyString(), any(), any());

        mockMvc.perform(post("/api/v1/diary/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"今天心情不错\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("今天心情不错"));

        verify(kafkaEventPublisher).publish(anyString(), anyString(), any(), any());
    }

    @Test
    void testCreateDiaryEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/diary/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\",\"mediaUrls\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("日记内容或媒体不能为空"));
    }

    @Test
    void testUpdateDiary() throws Exception {
        Diary existing = Diary.builder()
                .id(1L)
                .userId(1L)
                .content("原来的内容")
                .build();

        Diary updated = Diary.builder()
                .id(1L)
                .userId(1L)
                .content("更新后的内容")
                .emotionCategory("calm")
                .build();

        when(diaryService.getById(1L)).thenReturn(existing);
        when(diaryService.analyzeEmotion(anyString())).thenReturn(
                new DiaryService.EmotionResult(0.0, "calm")
        );
        when(diaryService.update(any(Diary.class))).thenReturn(updated);
        doNothing().when(kafkaEventPublisher).publish(anyString(), anyString(), any(), any());

        mockMvc.perform(put("/api/v1/diary/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"更新后的内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("更新后的内容"));
    }

    @Test
    void testUpdateDiaryNotFound() throws Exception {
        when(diaryService.getById(999L)).thenReturn(null);

        mockMvc.perform(put("/api/v1/diary/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"更新内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("日记不存在"));
    }

    @Test
    void testUpdateDiaryNoPermission() throws Exception {
        Diary existing = Diary.builder()
                .id(1L)
                .userId(2L) // 其他用户的日记
                .content("别人的日记")
                .build();

        when(diaryService.getById(1L)).thenReturn(existing);

        mockMvc.perform(put("/api/v1/diary/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"更新内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("无权限修改他人的日记"));
    }

    @Test
    void testGetDiaryById() throws Exception {
        Diary diary = Diary.builder()
                .id(1L)
                .userId(1L)
                .content("日记内容")
                .emotionCategory("happy")
                .emotionScore(new BigDecimal("0.8"))
                .build();

        when(diaryService.getById(1L)).thenReturn(diary);

        mockMvc.perform(get("/api/v1/diary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("日记内容"));
    }

    @Test
    void testGetDiaryByIdNotFound() throws Exception {
        when(diaryService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/diary/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("日记不存在"));
    }

    @Test
    void testListDiaries() throws Exception {
        Diary diary1 = Diary.builder().id(1L).userId(1L).content("日记1").build();
        Diary diary2 = Diary.builder().id(2L).userId(1L).content("日记2").build();

        Page<Diary> page = new Page<>();
        page.setRecords(Arrays.asList(diary1, diary2));
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(2);

        when(diaryService.getListByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/diary/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    void testListDiariesWithEmotionFilter() throws Exception {
        Diary diary = Diary.builder().id(1L).userId(1L).content("日记1").emotionCategory("happy").build();

        Page<Diary> page = new Page<>();
        page.setRecords(Collections.singletonList(diary));
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(1);

        when(diaryService.getListByUser(anyLong(), eq("happy"), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/diary/list")
                        .param("emotionCategory", "happy")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].emotionCategory").value("happy"));
    }

    @Test
    void testDeleteDiary() throws Exception {
        when(diaryService.delete(eq(1L), eq(1L))).thenReturn(true);

        mockMvc.perform(delete("/api/v1/diary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testDeleteDiaryNoPermission() throws Exception {
        when(diaryService.delete(eq(1L), eq(1L))).thenReturn(false);

        mockMvc.perform(delete("/api/v1/diary/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("无权限或日记不存在"));
    }

    @Test
    void testGetEmotionReport() throws Exception {
        Diary diary1 = Diary.builder()
                .userId(1L)
                .content("开心的一天")
                .emotionCategory("happy")
                .emotionScore(new BigDecimal("0.8"))
                .build();
        Diary diary2 = Diary.builder()
                .userId(1L)
                .content("有点难过")
                .emotionCategory("sad")
                .emotionScore(new BigDecimal("-0.3"))
                .build();

        when(diaryService.getRecentByUser(eq(1L), eq(7))).thenReturn(Arrays.asList(diary1, diary2));

        mockMvc.perform(get("/api/v1/diary/report")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalDiaries").value(2))
                .andExpect(jsonPath("$.data.emotionStats.happy").value(1))
                .andExpect(jsonPath("$.data.emotionStats.sad").value(1));
    }

    @Test
    void testGetStats() throws Exception {
        Diary diary = Diary.builder()
                .userId(1L)
                .content("日记")
                .emotionCategory("happy")
                .build();

        when(diaryService.getRecentByUser(eq(1L), eq(30))).thenReturn(Collections.singletonList(diary));

        mockMvc.perform(get("/api/v1/diary/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.categoryCount.happy").value(1));
    }
}

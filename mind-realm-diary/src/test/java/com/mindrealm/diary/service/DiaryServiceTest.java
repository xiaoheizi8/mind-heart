package com.mindrealm.diary.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.diary.entity.Diary;
import com.mindrealm.diary.mapper.DiaryMapper;
import com.mindrealm.diary.service.impl.DiaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @className: DiaryServiceTest
 * @description: 日记服务单元测试
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryMapper diaryMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private DiaryServiceImpl diaryService;

    private Diary testDiary;

    @BeforeEach
    void setUp() {
        testDiary = Diary.builder()
                .id(1L)
                .userId(100L)
                .content("今天心情很好，和朋友一起出去玩")
                .emotionTags("[\"朋友\", \"快乐\"]")
                .emotionScore(new BigDecimal("0.85"))
                .emotionCategory("happy")
                .aiAnalysis("积极的情绪表达")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetListByUser_WithoutFilter() {
        // 准备数据
        Page<Diary> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testDiary));
        mockPage.setTotal(1);
        
        when(diaryMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - 不传情绪分类
        Page<Diary> result = diaryService.getListByUser(100L, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("happy", result.getRecords().get(0).getEmotionCategory());
        verify(diaryMapper).selectPage(any(Page.class), any());
    }

    @Test
    void testGetListByUser_WithEmotionFilter() {
        // 准备数据
        Page<Diary> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testDiary));
        mockPage.setTotal(1);
        
        when(diaryMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - 按情绪分类筛选
        Page<Diary> result = diaryService.getListByUser(100L, "happy", 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(diaryMapper).selectPage(any(Page.class), any());
    }

    @Test
    void testGetListByUser_EmptyEmotionFilter() {
        // 准备数据
        Page<Diary> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testDiary));
        mockPage.setTotal(1);
        
        when(diaryMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - 空字符串情绪分类（应不过滤）
        Page<Diary> result = diaryService.getListByUser(100L, "", 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testGetListByUser_NullEmotionFilter() {
        // 准备数据
        Page<Diary> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testDiary));
        mockPage.setTotal(1);
        
        when(diaryMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - null情绪分类（应不过滤）
        Page<Diary> result = diaryService.getListByUser(100L, null, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testGetListByUser_InvalidUserId() {
        // 执行测试 - 无效用户ID
        Page<Diary> result = diaryService.getListByUser(null, "happy", 1, 10);

        // 验证结果 - 应返回空页面
        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        verify(diaryMapper, never()).selectPage(any(), any());
    }

    @Test
    void testGetListByUser_MultipleEmotions() {
        // 准备数据 - 不同情绪的日记
        Diary sadDiary = Diary.builder()
                .id(2L)
                .userId(100L)
                .content("今天考试没考好，很难过")
                .emotionScore(new BigDecimal("-0.5"))
                .emotionCategory("sad")
                .build();
        
        List<Diary> diaries = Arrays.asList(testDiary, sadDiary);
        Page<Diary> mockPage = new Page<>(1, 10);
        mockPage.setRecords(diaries);
        mockPage.setTotal(2);
        
        when(diaryMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - 查询所有情绪
        Page<Diary> result = diaryService.getListByUser(100L, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
    }

    @Test
    void testGetRecentByUser() {
        // 准备数据
        List<Diary> mockList = Arrays.asList(testDiary);
        when(diaryMapper.selectList(any())).thenReturn(mockList);

        // 执行测试
        List<Diary> result = diaryService.getRecentByUser(100L, 7);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(diaryMapper).selectList(any());
    }

    @Test
    void testGetById() {
        // 准备数据
        when(diaryMapper.selectById(1L)).thenReturn(testDiary);

        // 执行测试
        Diary result = diaryService.getById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("happy", result.getEmotionCategory());
    }

    @Test
    void testCreate() {
        // 准备数据
        Diary newDiary = Diary.builder()
                .userId(100L)
                .content("新日记")
                .build();
        
        when(diaryMapper.insert(any(Diary.class))).thenReturn(1);

        // 执行测试
        Diary result = diaryService.create(newDiary);

        // 验证结果
        assertNotNull(result);
        verify(diaryMapper).insert(any(Diary.class));
    }

    @Test
    void testDelete() {
        // 准备数据
        when(diaryMapper.selectById(1L)).thenReturn(testDiary);
        when(diaryMapper.deleteById(1L)).thenReturn(1);

        // 执行测试
        boolean result = diaryService.delete(1L, 100L);

        // 验证结果
        assertTrue(result);
        verify(diaryMapper).deleteById(1L);
    }

    @Test
    void testDelete_WrongUser() {
        // 准备数据 - 日记属于其他用户
        when(diaryMapper.selectById(1L)).thenReturn(testDiary);

        // 执行测试 - 用错误的用户ID删除
        boolean result = diaryService.delete(1L, 200L);

        // 验证结果 - 应返回false
        assertFalse(result);
        verify(diaryMapper, never()).deleteById(any());
    }
}

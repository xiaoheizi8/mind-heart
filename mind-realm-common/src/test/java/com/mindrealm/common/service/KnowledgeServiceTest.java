package com.mindrealm.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.mapper.KnowledgeMapper;
import com.mindrealm.common.service.impl.KnowledgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @className: KnowledgeServiceTest
 * @description: 知识库服务单元测试
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    @Mock
    private KnowledgeMapper knowledgeMapper;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    private Knowledge testKnowledge;

    @BeforeEach
    void setUp() {
        testKnowledge = Knowledge.builder()
                .id(1L)
                .title("如何应对考试焦虑")
                .content("考试焦虑是很多学生都会面临的问题...")
                .category("skill")
                .source("心域知识库")
                .tags("[\"考试\", \"焦虑\", \"调节\"]")
                .summary("考试焦虑应对指南")
                .status(1)
                .viewCount(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testList_WithCategory() {
        // 准备数据
        Page<Knowledge> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testKnowledge));
        mockPage.setTotal(1);
        
        when(knowledgeMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        Page<Knowledge> result = knowledgeService.list("skill", null, null, 1, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("如何应对考试焦虑", result.getRecords().get(0).getTitle());
        verify(knowledgeMapper).selectPage(any(Page.class), any());
    }

    @Test
    void testList_WithTag() {
        // 准备数据
        Page<Knowledge> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testKnowledge));
        mockPage.setTotal(1);
        
        when(knowledgeMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - 按标签查询
        Page<Knowledge> result = knowledgeService.list(null, "焦虑", null, 1, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(knowledgeMapper).selectPage(any(Page.class), any());
    }

    @Test
    void testList_WithKeyword() {
        // 准备数据
        Page<Knowledge> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testKnowledge));
        mockPage.setTotal(1);
        
        when(knowledgeMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - 关键词搜索
        Page<Knowledge> result = knowledgeService.list(null, null, "考试", 1, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testList_DefaultStatus() {
        // 准备数据
        Page<Knowledge> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testKnowledge));
        mockPage.setTotal(1);
        
        when(knowledgeMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试 - status为null时默认查询已发布
        Page<Knowledge> result = knowledgeService.list(null, null, null, null, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testGetById_IncrementViewCount() {
        // 准备数据
        when(knowledgeMapper.selectById(1L)).thenReturn(testKnowledge);
        when(knowledgeMapper.updateById(any(Knowledge.class))).thenReturn(1);

        // 执行测试
        Knowledge result = knowledgeService.getById(1L);

        // 验证结果 - 浏览次数应该+1
        assertNotNull(result);
        assertEquals(11, result.getViewCount());
        verify(knowledgeMapper).updateById(any(Knowledge.class));
    }

    @Test
    void testGetRecommend() {
        // 准备数据
        List<Knowledge> mockList = Arrays.asList(testKnowledge);
        when(knowledgeMapper.selectList(any())).thenReturn(mockList);

        // 执行测试
        List<Knowledge> result = knowledgeService.getRecommend(5);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(knowledgeMapper).selectList(any());
    }

    @Test
    void testGetAllTags() {
        // 准备数据
        Knowledge k1 = Knowledge.builder()
                .id(1L)
                .tags("[\"焦虑\", \"考试\"]")
                .status(1)
                .build();
        Knowledge k2 = Knowledge.builder()
                .id(2L)
                .tags("[\"抑郁\", \"心理健康\"]")
                .status(1)
                .build();
        
        when(knowledgeMapper.selectList(any())).thenReturn(Arrays.asList(k1, k2));

        // 执行测试
        List<String> tags = knowledgeService.getAllTags();

        // 验证结果 - 应该返回去重排序后的标签
        assertNotNull(tags);
        assertEquals(4, tags.size());
        assertTrue(tags.contains("焦虑"));
        assertTrue(tags.contains("考试"));
        assertTrue(tags.contains("抑郁"));
        assertTrue(tags.contains("心理健康"));
        
        // 验证调用了selectList
        verify(knowledgeMapper).selectList(any());
    }

    @Test
    void testListByTag() {
        // 准备数据
        Page<Knowledge> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testKnowledge));
        mockPage.setTotal(1);
        
        when(knowledgeMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        Page<Knowledge> result = knowledgeService.listByTag("焦虑", 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void testSave() {
        // 准备数据
        Knowledge newKnowledge = Knowledge.builder()
                .title("新知识")
                .content("内容")
                .category("knowledge")
                .build();
        
        when(knowledgeMapper.insert(any(Knowledge.class))).thenReturn(1);

        // 执行测试
        boolean result = knowledgeService.save(newKnowledge);

        // 验证结果
        assertTrue(result);
        assertEquals(1, newKnowledge.getStatus()); // 默认已发布
        assertEquals(0, newKnowledge.getViewCount()); // 默认浏览0次
        verify(knowledgeMapper).insert(any(Knowledge.class));
    }

    @Test
    void testUpdate() {
        // 准备数据
        when(knowledgeMapper.updateById(any(Knowledge.class))).thenReturn(1);

        // 执行测试
        boolean result = knowledgeService.update(testKnowledge);

        // 验证结果
        assertTrue(result);
        verify(knowledgeMapper).updateById(testKnowledge);
    }

    @Test
    void testDelete() {
        // 准备数据
        when(knowledgeMapper.deleteById(1L)).thenReturn(1);

        // 执行测试
        boolean result = knowledgeService.delete(1L);

        // 验证结果
        assertTrue(result);
        verify(knowledgeMapper).deleteById(1L);
    }
}

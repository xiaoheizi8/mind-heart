package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.service.WarningService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WarningController 单元测试
 * 覆盖: 预警状态查询、预警历史列表
 */
@ExtendWith(MockitoExtension.class)
class WarningControllerTest {

    @Mock
    private WarningService warningService;

    @InjectMocks
    private WarningController warningController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(warningController).build();

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
    void testGetStatus() throws Exception {
        WarningRecord w1 = WarningRecord.builder()
                .id(1L)
                .userId(1L)
                .riskLevel(3)
                .handled(0)
                .build();
        WarningRecord w2 = WarningRecord.builder()
                .id(2L)
                .userId(1L)
                .riskLevel(2)
                .handled(1)
                .build();

        Page<WarningRecord> page = new Page<>();
        page.setRecords(Arrays.asList(w1, w2));

        when(warningService.getUserWarnings(eq(1L), eq(1), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/v1/warning/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.unhandledCount").value(1))
                .andExpect(jsonPath("$.data.highRiskCount").value(1))
                .andExpect(jsonPath("$.data.hasHighRisk").value(true));
    }

    @Test
    void testGetStatusNoWarnings() throws Exception {
        Page<WarningRecord> page = new Page<>();
        page.setRecords(Collections.emptyList());

        when(warningService.getUserWarnings(eq(1L), eq(1), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/v1/warning/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.unhandledCount").value(0))
                .andExpect(jsonPath("$.data.highRiskCount").value(0))
                .andExpect(jsonPath("$.data.hasHighRisk").value(false));
    }

    @Test
    void testGetStatusMultipleHighRisk() throws Exception {
        WarningRecord w1 = WarningRecord.builder().id(1L).userId(1L).riskLevel(3).handled(0).build();
        WarningRecord w2 = WarningRecord.builder().id(2L).userId(1L).riskLevel(3).handled(0).build();

        Page<WarningRecord> page = new Page<>();
        page.setRecords(Arrays.asList(w1, w2));

        when(warningService.getUserWarnings(eq(1L), eq(1), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/v1/warning/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.unhandledCount").value(2))
                .andExpect(jsonPath("$.data.highRiskCount").value(2))
                .andExpect(jsonPath("$.data.hasHighRisk").value(true));
    }

    @Test
    void testListWarnings() throws Exception {
        WarningRecord w1 = WarningRecord.builder()
                .id(1L)
                .userId(1L)
                .riskLevel(2)
                .triggerType("情绪低落")
                .content("日记内容")
                .handled(0)
                .build();

        Page<WarningRecord> page = new Page<>();
        page.setRecords(Collections.singletonList(w1));
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(1);

        when(warningService.getUserWarnings(eq(1L), eq(1), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/v1/warning/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(1))
                .andExpect(jsonPath("$.data.records[0].riskLevel").value(2))
                .andExpect(jsonPath("$.data.records[0].triggerType").value("情绪低落"));
    }

    @Test
    void testListWarningsPagination() throws Exception {
        Page<WarningRecord> page = new Page<>();
        page.setRecords(Collections.emptyList());
        page.setCurrent(2);
        page.setSize(5);
        page.setTotal(0);

        when(warningService.getUserWarnings(eq(1L), eq(2), eq(5))).thenReturn(page);

        mockMvc.perform(get("/api/v1/warning/list")
                        .param("pageNum", "2")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(2))
                .andExpect(jsonPath("$.data.size").value(5));
    }
}

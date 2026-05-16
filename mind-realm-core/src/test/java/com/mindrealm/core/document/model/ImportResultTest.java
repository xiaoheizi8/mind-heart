package com.mindrealm.core.document.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImportResultTest {

    @Test
    void testSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ImportResult result = ImportResult.success(3, ids);

        assertEquals(3, result.getTotalCount());
        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailCount());
        assertEquals(3, result.getImportedIds().size());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testPartial() {
        List<String> errors = Arrays.asList("导入失败: 文章1", "导入失败: 文章3");
        ImportResult result = ImportResult.partial(4, 2, 2, errors);

        assertEquals(4, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(2, result.getFailCount());
        assertEquals(2, result.getErrors().size());
        assertEquals("导入失败: 文章1", result.getErrors().get(0));
    }

    @Test
    void testSuccessWithEmptyIds() {
        ImportResult result = ImportResult.success(0, Arrays.asList());

        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
    }
}

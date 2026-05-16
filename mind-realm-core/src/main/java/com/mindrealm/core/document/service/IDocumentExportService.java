package com.mindrealm.core.document.service;

import com.mindrealm.common.entity.Knowledge;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @className: IDocumentExportService
 * @description: 文档导出服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IDocumentExportService {

    /**
     * 导出为Excel
     * @param knowledgeList 知识列表
     * @param response HTTP响应
     */
    void exportToExcel(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException;

    /**
     * 导出为Word
     * @param knowledgeList 知识列表
     * @param response HTTP响应
     */
    void exportToWord(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException;

    /**
     * 导出为PDF
     * @param knowledgeList 知识列表
     * @param response HTTP响应
     */
    void exportToPdf(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException;

    /**
     * 导出为CSV
     * @param knowledgeList 知识列表
     * @param response HTTP响应
     */
    void exportToCsv(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException;
}

package com.mindrealm.api.controller.admin;

import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.mapper.KnowledgeMapper;
import com.mindrealm.common.result.Result;
import com.mindrealm.core.document.model.ImportResult;
import com.mindrealm.core.document.service.IDocumentExportService;
import com.mindrealm.core.document.service.IDocumentImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @className: DocumentController
 * @description: 管理端文档管理控制器,提供知识库文档的导入导出功能
 *               支持Excel/Word/CSV导入,支持Excel/Word/PDF/CSV导出
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@RestController
@RequestMapping("/admin/v1/document")
@Tag(name = "文档管理", description = "文档导入导出接口")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".xlsx", ".xls", ".docx", ".doc", ".csv"
    ));
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    private static final int MAX_EXPORT_COUNT = 10000;

    private final IDocumentExportService exportService;
    private final IDocumentImportService importService;

    public DocumentController(IDocumentExportService exportService,
                              IDocumentImportService importService) {
        this.exportService = exportService;
        this.importService = importService;
    }

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    /**
     * 下载知识库导入模板(CSV格式)
     * @param response HTTP响应,用于输出文件流
     * @throws IOException 文件输出异常
     */
    @GetMapping("/export/template")
    @Operation(summary = "下载导入模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        String template = importService.getImportTemplate();
        
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=knowledge_template.csv");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write("\uFEFF".getBytes(StandardCharsets.UTF_8));
            outputStream.write(template.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }

    /**
     * 导出知识库数据(支持多种格式)
     * @param format 导出格式(excel/word/pdf/csv),默认excel
     * @param category 分类筛选(可选)
     * @param response HTTP响应,用于输出文件流
     * @throws IOException 文件输出异常,数据量超过10000条时返回400
     */
    @PostMapping("/export")
    @Operation(summary = "导出知识库")
    @OperationLog(value = "导出知识库文档", type = OperationType.EXPORT)
    public void export(
            @Parameter(description = "导出格式: excel, word, pdf, csv")
            @RequestParam(defaultValue = "excel") String format,
            @Parameter(description = "分类筛选")
            @RequestParam(required = false) String category,
            HttpServletResponse response) throws IOException {
        
        List<Knowledge> knowledgeList = getKnowledgeList(category);
        
        if (knowledgeList.size() > MAX_EXPORT_COUNT) {
            logger.warn("导出数据量过大: {}, 限制: {}", knowledgeList.size(), MAX_EXPORT_COUNT);
            response.setStatus(400);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"message\":\"导出数据量过大，最大支持" + MAX_EXPORT_COUNT + "条\"}");
            return;
        }
        
        logger.info("导出知识库: format={}, category={}, count={}", format, category, knowledgeList.size());
        
        switch (format.toLowerCase()) {
            case "word" -> exportService.exportToWord(knowledgeList, response);
            case "pdf" -> exportService.exportToPdf(knowledgeList, response);
            case "csv" -> exportService.exportToCsv(knowledgeList, response);
            default -> exportService.exportToExcel(knowledgeList, response);
        }
    }

    /**
     * 导入知识库文档(支持Excel/Word/CSV格式)
     * @param file 上传的文件,大小不能超过10MB
     * @return 导入结果,包含总数、成功数、失败数及失败原因,格式不正确返回400
     */
    @PostMapping("/import")
    @Operation(summary = "导入知识库")
    @OperationLog(value = "导入知识库文档", type = OperationType.IMPORT)
    public Result<ImportResult> importDocument(
            @Parameter(description = "导入文件")
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Result.badRequest("请选择要导入的文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("导入文件过大: {} bytes, 限制: {} bytes", file.getSize(), MAX_FILE_SIZE);
            return Result.badRequest("文件大小不能超过 10MB");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            return Result.badRequest("文件名不能为空");
        }

        if (!isAllowedExtension(filename)) {
            logger.warn("不支持的文件类型: {}", filename);
            return Result.badRequest("不支持的文件格式，请上传 xlsx、xls、docx、doc 或 csv 文件");
        }

        try {
            ImportResult result;
            String lowerName = filename.toLowerCase();
            
            if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
                result = importService.importFromExcel(file);
            } else if (lowerName.endsWith(".docx") || lowerName.endsWith(".doc")) {
                result = importService.importFromWord(file);
            } else if (lowerName.endsWith(".csv")) {
                result = importService.importFromCsv(file);
            } else {
                return Result.badRequest("不支持的文件格式");
            }

            logger.info("导入完成: total={}, success={}, fail={}", 
                    result.getTotalCount(), result.getSuccessCount(), result.getFailCount());
            
            return Result.success(result);
        } catch (IOException e) {
            logger.error("文件读取失败", e);
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    private List<Knowledge> getKnowledgeList(String category) {
        if (category != null && !category.isEmpty()) {
            return knowledgeMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Knowledge>()
                            .eq(Knowledge::getCategory, category)
                            .eq(Knowledge::getStatus, 1)
                            .orderByDesc(Knowledge::getCreatedAt)
            );
        }
        return knowledgeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Knowledge>()
                        .eq(Knowledge::getStatus, 1)
                        .orderByDesc(Knowledge::getCreatedAt)
        );
    }

    private boolean isAllowedExtension(String filename) {
        for (String ext : ALLOWED_EXTENSIONS) {
            if (filename.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}

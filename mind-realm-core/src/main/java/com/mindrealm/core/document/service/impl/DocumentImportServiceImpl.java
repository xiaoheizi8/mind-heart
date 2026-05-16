package com.mindrealm.core.document.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.common.mapper.KnowledgeMapper;
import com.mindrealm.common.util.TimeUtil;
import com.mindrealm.core.document.model.ImportResult;
import com.mindrealm.core.document.model.KnowledgeImportDTO;
import com.mindrealm.core.document.service.IDocumentImportService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @className: DocumentImportServiceImpl
 * @description: 文档导入服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class DocumentImportServiceImpl implements IDocumentImportService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentImportServiceImpl.class);

    private final KnowledgeMapper knowledgeMapper;

    public DocumentImportServiceImpl(KnowledgeMapper knowledgeMapper) {
        this.knowledgeMapper = knowledgeMapper;
    }

    @Override
    public ImportResult importFromExcel(MultipartFile file) throws IOException {
        List<KnowledgeImportDTO> dataList = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<Long> importedIds = new ArrayList<>();
        int[] successCount = {0};

        EasyExcel.read(file.getInputStream(), KnowledgeImportDTO.class, new ReadListener<KnowledgeImportDTO>() {
            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {}

            @Override
            public void invoke(KnowledgeImportDTO dto, AnalysisContext context) {
                try {
                    Long id = validateAndSave(dto);
                    successCount[0]++;
                    importedIds.add(id);
                } catch (Exception e) {
                    errors.add("导入失败: " + dto.getTitle() + " - " + e.getMessage());
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {}
        }).sheet().doRead();

        if (errors.isEmpty()) {
            return ImportResult.success(successCount[0], importedIds);
        }
        return ImportResult.partial(dataList.size(), successCount[0], errors.size(), errors);
    }

    public ImportResult importFromWord(MultipartFile file) throws IOException {
        List<KnowledgeImportDTO> dataList = parseWordDocument(file);
        List<String> errors = new ArrayList<>();
        List<Long> importedIds = new ArrayList<>();
        int successCount = 0;

        for (KnowledgeImportDTO dto : dataList) {
            try {
                Long id = validateAndSave(dto);
                successCount++;
                importedIds.add(id);
            } catch (Exception e) {
                errors.add("导入失败: " + dto.getTitle() + " - " + e.getMessage());
            }
        }

        if (errors.isEmpty()) {
            return ImportResult.success(successCount, importedIds);
        }
        return ImportResult.partial(dataList.size(), successCount, errors.size(), errors);
    }

    public ImportResult importFromCsv(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        List<Long> importedIds = new ArrayList<>();
        int successCount = 0;

        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    if (line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }
                    continue;
                }

                KnowledgeImportDTO dto = parseCsvLine(line);
                if (dto != null && dto.getTitle() != null) {
                    try {
                        Long id = validateAndSave(dto);
                        successCount++;
                        importedIds.add(id);
                    } catch (Exception e) {
                        errors.add("导入失败: " + dto.getTitle() + " - " + e.getMessage());
                    }
                }
            }
        }

        if (errors.isEmpty()) {
            return ImportResult.success(successCount, importedIds);
        }
        return ImportResult.partial(successCount + errors.size(), successCount, errors.size(), errors);
    }

    private List<KnowledgeImportDTO> parseWordDocument(MultipartFile file) throws IOException {
        List<KnowledgeImportDTO> result = new ArrayList<>();

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            
            StringBuilder contentBuilder = new StringBuilder();
            String title = null;
            String category = null;

            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                
                if (text.isEmpty()) {
                    continue;
                }

                if (title == null) {
                    title = text;
                } else if (category == null && text.startsWith("分类：")) {
                    category = text.substring(3).trim();
                } else if (text.startsWith("## ") || text.startsWith("分类")) {
                    continue;
                } else if (contentBuilder.length() > 0) {
                    contentBuilder.append("\n").append(text);
                } else {
                    contentBuilder.append(text);
                }
            }

            if (title != null && contentBuilder.length() > 0) {
                KnowledgeImportDTO dto = KnowledgeImportDTO.builder()
                        .title(title)
                        .category(category != null ? category : "knowledge")
                        .content(contentBuilder.toString())
                        .source("Word导入")
                        .status(1)
                        .build();
                result.add(dto);
            }
        }

        return result;
    }

    private KnowledgeImportDTO parseCsvLine(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().trim());

        if (fields.size() < 3) {
            return null;
        }

        return KnowledgeImportDTO.builder()
                .title(fields.size() > 0 ? fields.get(0) : null)
                .category(fields.size() > 1 ? fields.get(1) : "knowledge")
                .summary(fields.size() > 2 ? fields.get(2) : null)
                .content(fields.size() > 3 ? fields.get(3) : null)
                .tags(fields.size() > 4 ? fields.get(4) : null)
                .source(fields.size() > 5 ? fields.get(5) : "CSV导入")
                .status(fields.size() > 6 && "1".equals(fields.get(6)) ? 1 : 0)
                .build();
    }

    private Long validateAndSave(KnowledgeImportDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            dto.setCategory("knowledge");
        }

        Knowledge knowledge = new Knowledge();
        knowledge.setTitle(dto.getTitle().trim());
        knowledge.setCategory(dto.getCategory().trim());
        knowledge.setSummary(dto.getSummary() != null ? dto.getSummary().trim() : "");
        knowledge.setContent(dto.getContent().trim());
        knowledge.setTags(dto.getTags() != null ? dto.getTags().trim() : "");
        knowledge.setSource(dto.getSource() != null ? dto.getSource().trim() : "手动导入");
        knowledge.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        knowledge.setViewCount(0);
        knowledge.setCreatedAt(TimeUtil.now());
        knowledge.setUpdatedAt(TimeUtil.now());

        knowledgeMapper.insert(knowledge);
        
        logger.info("成功导入知识: {}", knowledge.getTitle());
        return knowledge.getId();
    }

    public String getImportTemplate() {
        return "编号,标题,分类,摘要,内容,标签,来源,状态\n" +
                "1,如何应对考试焦虑,knowledge,考试焦虑的应对方法,考试焦虑是一种常见的情绪反应...,焦虑;考试,心域知识库,1\n" +
                "2,正念冥想入门指南,knowledge,正念冥想基础教程,正念冥想是一种有效的放松技巧...,冥想;放松,心域知识库,1";
    }
}

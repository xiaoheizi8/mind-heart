package com.mindrealm.core.document.service.impl;

import com.alibaba.excel.EasyExcel;
import com.mindrealm.common.entity.Knowledge;
import com.mindrealm.core.document.model.DocumentType;
import com.mindrealm.core.document.model.KnowledgeExportDTO;
import com.mindrealm.core.document.service.IDocumentExportService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: DocumentExportServiceImpl
 * @description: 文档导出服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class DocumentExportServiceImpl implements IDocumentExportService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentExportServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void exportToExcel(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException {
        String fileName = "knowledge_export_" + System.currentTimeMillis();
        setResponseHeaders(response, fileName, DocumentType.EXCEL);

        List<KnowledgeExportDTO> exportData = convertToExportDTO(knowledgeList);

        try (OutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream, KnowledgeExportDTO.class)
                    .sheet("知识库")
                    .doWrite(exportData);
        }
    }

    public void exportToWord(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException {
        String fileName = "knowledge_export_" + System.currentTimeMillis();
        setResponseHeaders(response, fileName, DocumentType.WORD);

        try (XWPFDocument document = new XWPFDocument();
             OutputStream outputStream = response.getOutputStream()) {

            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("心理健康知识库");
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.addBreak();

            for (Knowledge knowledge : knowledgeList) {
                XWPFParagraph heading = document.createParagraph();
                XWPFRun headingRun = heading.createRun();
                headingRun.setText(knowledge.getTitle());
                headingRun.setBold(true);
                headingRun.setFontSize(14);

                XWPFParagraph meta = document.createParagraph();
                XWPFRun metaRun = meta.createRun();
                metaRun.setText(String.format("分类：%s | 来源：%s | 创建时间：%s",
                        knowledge.getCategory(),
                        knowledge.getSource() != null ? knowledge.getSource() : "-",
                        knowledge.getCreatedAt() != null ? knowledge.getCreatedAt().format(DATE_FORMATTER) : "-"));
                metaRun.setItalic(true);
                metaRun.setFontSize(10);

                if (knowledge.getContent() != null) {
                    XWPFParagraph content = document.createParagraph();
                    XWPFRun contentRun = content.createRun();
                    contentRun.setText(knowledge.getContent());
                    contentRun.setFontSize(11);
                }

                XWPFParagraph separator = document.createParagraph();
                separator.createRun().addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);
            }

            document.write(outputStream);
        }
    }

    public void exportToPdf(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException {
        String fileName = "knowledge_export_" + System.currentTimeMillis();
        setResponseHeaders(response, fileName, DocumentType.PDF);

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        
        try (OutputStream outputStream = response.getOutputStream()) {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("心理健康知识库", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            for (Knowledge knowledge : knowledgeList) {
                Paragraph heading = new Paragraph(knowledge.getTitle(), headingFont);
                document.add(heading);

                String meta = String.format("分类：%s | 来源：%s | 创建时间：%s",
                        knowledge.getCategory(),
                        knowledge.getSource() != null ? knowledge.getSource() : "-",
                        knowledge.getCreatedAt() != null ? knowledge.getCreatedAt().format(DATE_FORMATTER) : "-");
                Paragraph metaPara = new Paragraph(meta, metaFont);
                metaPara.setSpacingAfter(10);
                document.add(metaPara);

                if (knowledge.getContent() != null) {
                    Paragraph content = new Paragraph(knowledge.getContent(), contentFont);
                    content.setSpacingAfter(20);
                    document.add(content);
                }
            }

            document.close();
        }
    }

    public void exportToCsv(List<Knowledge> knowledgeList, HttpServletResponse response) throws IOException {
        String fileName = "knowledge_export_" + System.currentTimeMillis();
        setResponseHeaders(response, fileName, DocumentType.CSV);

        response.setCharacterEncoding("UTF-8");
        
        try (OutputStream outputStream = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

            writer.write('\uFEFF');
            writer.write("编号,标题,分类,摘要,内容,标签,来源,浏览量,状态,创建时间\n");

            for (Knowledge k : knowledgeList) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%d,%s,%s\n",
                        k.getId(),
                        escapeCsv(k.getTitle()),
                        escapeCsv(k.getCategory()),
                        escapeCsv(k.getSummary()),
                        escapeCsv(k.getContent()),
                        escapeCsv(k.getTags()),
                        escapeCsv(k.getSource()),
                        k.getViewCount() != null ? k.getViewCount() : 0,
                        k.getStatus() != null && k.getStatus() == 1 ? "已发布" : "草稿",
                        k.getCreatedAt() != null ? k.getCreatedAt().format(DATE_FORMATTER) : "-"
                ));
            }
            writer.flush();
        }
    }

    private List<KnowledgeExportDTO> convertToExportDTO(List<Knowledge> knowledgeList) {
        return knowledgeList.stream().map(k -> {
            KnowledgeExportDTO dto = new KnowledgeExportDTO();
            dto.setId(k.getId());
            dto.setTitle(k.getTitle());
            dto.setCategory(k.getCategory());
            dto.setSummary(k.getSummary());
            dto.setContent(k.getContent());
            dto.setTags(k.getTags());
            dto.setSource(k.getSource());
            dto.setViewCount(k.getViewCount() != null ? k.getViewCount() : 0);
            dto.setStatus(k.getStatus() != null && k.getStatus() == 1 ? "已发布" : "草稿");
            dto.setCreatedAt(k.getCreatedAt() != null ? k.getCreatedAt().format(DATE_FORMATTER) : "-");
            return dto;
        }).collect(Collectors.toList());
    }

    private void setResponseHeaders(HttpServletResponse response, String fileName, DocumentType type) {
        response.setContentType(type.getContentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        String encodedFileName;
        try {
            encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
            encodedFileName = fileName;
        }
        
        response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + encodedFileName + type.getExtension() + "\"; filename*=UTF-8''" + encodedFileName + type.getExtension());
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

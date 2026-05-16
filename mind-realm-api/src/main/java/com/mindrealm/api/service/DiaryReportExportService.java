package com.mindrealm.api.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.mindrealm.diary.entity.Diary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DiaryReportExportService {

    private static final Logger logger = LoggerFactory.getLogger(DiaryReportExportService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Color PRIMARY_COLOR = new Color(123, 104, 238);
    private static final Color TEXT_PRIMARY = new Color(51, 51, 51);
    private static final Color TEXT_SECONDARY = new Color(102, 102, 102);
    private static final Color BORDER_LIGHT = new Color(230, 230, 230);
    private static final Color BG_LIGHT = new Color(248, 246, 255);
    private static final Color WHITE = Color.WHITE;

    public void exportToPdf(List<Diary> diaries, Map<String, Object> reportData, 
                           String period, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

            addTitle(document, chineseFont, period);
            addOverviewSection(document, chineseFont, reportData, diaries.size());
            addEmotionDistribution(document, chineseFont, reportData);
            addTrendAnalysis(document, chineseFont, reportData);
            addAiAnalysis(document, chineseFont, reportData);
            addDiaryList(document, chineseFont, diaries);
            addFooter(document, chineseFont);

            document.close();
            logger.info("PDF报告导出成功, 包含{}条日记", diaries.size());
        } catch (Exception e) {
            logger.error("PDF导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("PDF导出失败: " + e.getMessage(), e);
        }
    }

    private void addTitle(Document document, BaseFont font, String period) throws Exception {
        Font titleFont = new Font(font, 24, Font.BOLD, PRIMARY_COLOR);
        Paragraph title = new Paragraph("心域 - 情绪报告", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Font subtitleFont = new Font(font, 12, Font.NORMAL, TEXT_SECONDARY);
        String now = java.time.LocalDateTime.now().format(DATE_FORMAT);
        Paragraph subtitle = new Paragraph(period + " | 生成时间: " + now, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);

        Paragraph line = new Paragraph("_______________________________________");
        line.setAlignment(Element.ALIGN_CENTER);
        line.setSpacingAfter(20);
        document.add(line);
    }

    private void addOverviewSection(Document document, BaseFont font, Map<String, Object> reportData, int totalDiaries) throws Exception {
        Font sectionFont = new Font(font, 16, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph("一、情绪概览", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        Font headerFont = new Font(font, 11, Font.BOLD, WHITE);
        String[] headers = {"记录天数", "平均情绪值", "主要情绪"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(PRIMARY_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            table.addCell(cell);
        }

        Font dataFont = new Font(font, 11, Font.NORMAL, TEXT_PRIMARY);
        double avgScore = reportData.containsKey("avgScore") ? ((Number) reportData.get("avgScore")).doubleValue() : 0;
        String dominantEmotion = (String) reportData.getOrDefault("dominantEmotion", "--");

        String[] values = {String.valueOf(totalDiaries), String.format("%.1f", avgScore), dominantEmotion};
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            cell.setBorderColor(BORDER_LIGHT);
            table.addCell(cell);
        }
        document.add(table);
    }

    private void addEmotionDistribution(Document document, BaseFont font, Map<String, Object> reportData) throws Exception {
        Font sectionFont = new Font(font, 16, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph("二、情绪分布", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        @SuppressWarnings("unchecked")
        Map<String, Integer> emotionStats = (Map<String, Integer>) reportData.get("emotionStats");

        if (emotionStats != null && !emotionStats.isEmpty()) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingAfter(20);

            Font headerFont = new Font(font, 11, Font.BOLD, WHITE);
            String[] headers = {"情绪类型", "出现次数"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(PRIMARY_COLOR);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(10);
                table.addCell(cell);
            }

            Font dataFont = new Font(font, 11, Font.NORMAL, TEXT_PRIMARY);
            for (Map.Entry<String, Integer> entry : emotionStats.entrySet()) {
                PdfPCell cell1 = new PdfPCell(new Phrase(entry.getKey(), dataFont));
                cell1.setPadding(10);
                cell1.setBorderColor(BORDER_LIGHT);
                table.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(entry.getValue()), dataFont));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setPadding(10);
                cell2.setBorderColor(BORDER_LIGHT);
                table.addCell(cell2);
            }
            document.add(table);
        }
    }

    private void addTrendAnalysis(Document document, BaseFont font, Map<String, Object> reportData) throws Exception {
        Font sectionFont = new Font(font, 16, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph("三、趋势分析", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        @SuppressWarnings("unchecked")
        Map<String, Object> trendAnalysis = (Map<String, Object>) reportData.get("trendAnalysis");

        if (trendAnalysis != null) {
            Font contentFont = new Font(font, 11, Font.NORMAL, TEXT_PRIMARY);
            String description = (String) trendAnalysis.getOrDefault("description", "暂无趋势数据");
            String trendType = (String) trendAnalysis.getOrDefault("trendType", "stable");

            String trendIcon = "improving".equals(trendType) ? "趋势: 上升" : 
                             "declining".equals(trendType) ? "趋势: 下降" : "趋势: 平稳";

            Paragraph trend = new Paragraph(trendIcon, contentFont);
            trend.setSpacingAfter(8);
            document.add(trend);

            Paragraph desc = new Paragraph(description, contentFont);
            desc.setSpacingAfter(15);
            document.add(desc);
        }
    }

    private void addAiAnalysis(Document document, BaseFont font, Map<String, Object> reportData) throws Exception {
        Font sectionFont = new Font(font, 16, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph("四、AI情绪洞察", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        Font contentFont = new Font(font, 11, Font.NORMAL, TEXT_PRIMARY);

        String aiReport = (String) reportData.get("aiReport");
        if (aiReport != null && !aiReport.isEmpty()) {
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            table.setSpacingAfter(15);

            PdfPCell cell = new PdfPCell(new Phrase(aiReport, contentFont));
            cell.setBackgroundColor(BG_LIGHT);
            cell.setPadding(12);
            cell.setBorderColor(BORDER_LIGHT);
            table.addCell(cell);
            document.add(table);
        }

        @SuppressWarnings("unchecked")
        List<String> suggestions = (List<String>) reportData.get("suggestions");
        if (suggestions != null && !suggestions.isEmpty()) {
            Font suggestionTitleFont = new Font(font, 12, Font.BOLD, TEXT_PRIMARY);
            Paragraph suggestionTitle = new Paragraph("建议:", suggestionTitleFont);
            suggestionTitle.setSpacingBefore(10);
            suggestionTitle.setSpacingAfter(10);
            document.add(suggestionTitle);

            for (int i = 0; i < suggestions.size(); i++) {
                Paragraph suggestion = new Paragraph((i + 1) + ". " + suggestions.get(i), contentFont);
                suggestion.setSpacingAfter(6);
                document.add(suggestion);
            }
        }
    }

    private void addDiaryList(Document document, BaseFont font, List<Diary> diaries) throws Exception {
        Font sectionFont = new Font(font, 16, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph("五、详细记录", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        if (diaries.isEmpty()) {
            Font emptyFont = new Font(font, 11, Font.NORMAL, TEXT_SECONDARY);
            Paragraph empty = new Paragraph("暂无日记记录", emptyFont);
            empty.setAlignment(Element.ALIGN_CENTER);
            document.add(empty);
            return;
        }

        Font contentFont = new Font(font, 10, Font.NORMAL, TEXT_PRIMARY);
        Font dateFont = new Font(font, 9, Font.BOLD, TEXT_SECONDARY);

        for (Diary diary : diaries) {
            Paragraph datePara = new Paragraph("日期: " + (diary.getCreatedAt() != null ? diary.getCreatedAt().format(DATE_FORMAT) : "未知"), dateFont);
            datePara.setSpacingAfter(5);
            document.add(datePara);

            String emotion = diary.getEmotionCategory() != null ? diary.getEmotionCategory() : "未知";
            String score = diary.getEmotionScore() != null ? String.format("%.1f", diary.getEmotionScore().doubleValue()) : "--";
            Paragraph emotionPara = new Paragraph("情绪: " + emotion + " | 分值: " + score, contentFont);
            emotionPara.setSpacingAfter(5);
            document.add(emotionPara);

            if (diary.getContent() != null && !diary.getContent().isEmpty()) {
                String content = diary.getContent().length() > 200 ? 
                    diary.getContent().substring(0, 200) + "..." : diary.getContent();
                Paragraph contentPara = new Paragraph("内容: " + content, contentFont);
                contentPara.setSpacingAfter(15);
                document.add(contentPara);
            }

            Paragraph line = new Paragraph("-----------------------------------");
            line.setSpacingAfter(10);
            document.add(line);
        }
    }

    private void addFooter(Document document, BaseFont font) throws Exception {
        Font footerFont = new Font(font, 9, Font.NORMAL, TEXT_SECONDARY);
        Paragraph footer = new Paragraph("\n\n本报告中情绪分析由AI自动生成,仅供参考。如有严重心理困扰,请及时寻求专业帮助。", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph heart = new Paragraph("心域 - 陪伴你的每一天", footerFont);
        heart.setAlignment(Element.ALIGN_CENTER);
        heart.setSpacingBefore(5);
        document.add(heart);
    }
}

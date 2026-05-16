package com.mindrealm.core.document.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTypeTest {

    @Test
    void testExtension() {
        assertEquals(".xlsx", DocumentType.EXCEL.getExtension());
        assertEquals(".docx", DocumentType.WORD.getExtension());
        assertEquals(".pdf", DocumentType.PDF.getExtension());
        assertEquals(".csv", DocumentType.CSV.getExtension());
    }

    @Test
    void testContentType() {
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
                DocumentType.EXCEL.getContentType());
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", 
                DocumentType.WORD.getContentType());
        assertEquals("application/pdf", DocumentType.PDF.getContentType());
        assertEquals("text/csv", DocumentType.CSV.getContentType());
    }

    @Test
    void testFromFileName() {
        assertEquals(DocumentType.EXCEL, DocumentType.fromFileName("test.xlsx"));
        assertEquals(DocumentType.EXCEL, DocumentType.fromFileName("test.XLSX"));
        assertEquals(DocumentType.EXCEL, DocumentType.fromFileName("test.xls"));
        assertEquals(DocumentType.WORD, DocumentType.fromFileName("test.docx"));
        assertEquals(DocumentType.WORD, DocumentType.fromFileName("test.DOC"));
        assertEquals(DocumentType.PDF, DocumentType.fromFileName("test.pdf"));
        assertEquals(DocumentType.CSV, DocumentType.fromFileName("test.csv"));
    }

    @Test
    void testFromFileName_Unknown() {
        assertEquals(DocumentType.EXCEL, DocumentType.fromFileName("test.txt"));
        assertEquals(DocumentType.EXCEL, DocumentType.fromFileName("test"));
        assertEquals(DocumentType.EXCEL, DocumentType.fromFileName(null));
    }
}

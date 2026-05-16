package com.mindrealm.core.document.model;

public enum DocumentType {
    EXCEL(".xlsx"),
    WORD(".docx"),
    PDF(".pdf"),
    CSV(".csv");

    private final String extension;

    DocumentType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return switch (this) {
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case PDF -> "application/pdf";
            case CSV -> "text/csv";
        };
    }

    public static DocumentType fromFileName(String fileName) {
        if (fileName == null) return EXCEL;
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) return EXCEL;
        if (lower.endsWith(".docx") || lower.endsWith(".doc")) return WORD;
        if (lower.endsWith(".pdf")) return PDF;
        if (lower.endsWith(".csv")) return CSV;
        return EXCEL;
    }
}

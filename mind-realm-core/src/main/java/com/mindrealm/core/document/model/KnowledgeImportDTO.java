package com.mindrealm.core.document.model;

public class KnowledgeImportDTO {

    private String title;
    private String category;
    private String summary;
    private String content;
    private String tags;
    private String source;
    private Integer status;

    public KnowledgeImportDTO() {}

    public KnowledgeImportDTO(String title, String category, String summary, String content, String tags, String source, Integer status) {
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.tags = tags;
        this.source = source;
        this.status = status;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private String category;
        private String summary;
        private String content;
        private String tags;
        private String source;
        private Integer status;

        public Builder title(String title) { this.title = title; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder tags(String tags) { this.tags = tags; return this; }
        public Builder source(String source) { this.source = source; return this; }
        public Builder status(Integer status) { this.status = status; return this; }

        public KnowledgeImportDTO build() {
            return new KnowledgeImportDTO(title, category, summary, content, tags, source, status);
        }
    }
}

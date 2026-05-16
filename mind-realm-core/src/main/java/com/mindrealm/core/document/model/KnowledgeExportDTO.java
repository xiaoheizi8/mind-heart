package com.mindrealm.core.document.model;

import java.time.LocalDateTime;

public class KnowledgeExportDTO {

    private Long id;
    private String title;
    private String category;
    private String summary;
    private String content;
    private String tags;
    private String source;
    private Integer viewCount;
    private String status;
    private String createdAt;

    public KnowledgeExportDTO() {}

    public KnowledgeExportDTO(Long id, String title, String category, String summary, String content, 
                             String tags, String source, Integer viewCount, String status, String createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.tags = tags;
        this.source = source;
        this.viewCount = viewCount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String category;
        private String summary;
        private String content;
        private String tags;
        private String source;
        private Integer viewCount;
        private String status;
        private String createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder tags(String tags) { this.tags = tags; return this; }
        public Builder source(String source) { this.source = source; return this; }
        public Builder viewCount(Integer viewCount) { this.viewCount = viewCount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(String createdAt) { this.createdAt = createdAt; return this; }

        public KnowledgeExportDTO build() {
            return new KnowledgeExportDTO(id, title, category, summary, content, tags, source, viewCount, status, createdAt);
        }
    }
}

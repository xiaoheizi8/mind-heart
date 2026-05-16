package com.mindrealm.core.rag.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @className: RagRequest
 * @description: RAG请求模型,封装用户问题、对话历史、AI人格等上下文信息,用于知识库检索增强生成
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
public class RagRequest {
    
    private Long userId;
    private String question;
    private String persona;
    private Map<String, Object> metadata;
    private List<ChatMessage> history;
    
    public RagRequest() {}

    public RagRequest(Long userId, String question, String persona, Map<String, Object> metadata, List<ChatMessage> history) {
        this.userId = userId;
        this.question = question;
        this.persona = persona;
        this.metadata = metadata;
        this.history = history;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getPersona() { return persona; }
    public void setPersona(String persona) { this.persona = persona; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public List<ChatMessage> getHistory() { return history; }
    public void setHistory(List<ChatMessage> history) { this.history = history; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private String question;
        private String persona;
        private Map<String, Object> metadata;
        private List<ChatMessage> history;

        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder question(String question) { this.question = question; return this; }
        public Builder persona(String persona) { this.persona = persona; return this; }
        public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public Builder history(List<ChatMessage> history) { this.history = history; return this; }

        public RagRequest build() {
            return new RagRequest(userId, question, persona, metadata, history);
        }
    }

    public static class ChatMessage {
        private String role;
        private String content;
        private LocalDateTime timestamp;

        public ChatMessage() {}
        
        public ChatMessage(String role, String content, LocalDateTime timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String role;
            private String content;
            private LocalDateTime timestamp;

            public Builder role(String role) { this.role = role; return this; }
            public Builder content(String content) { this.content = content; return this; }
            public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

            public ChatMessage build() {
                return new ChatMessage(role, content, timestamp);
            }
        }
    }
}

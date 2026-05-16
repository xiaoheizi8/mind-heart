package com.mindrealm.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: Conversation
 * @description: AI对话记录实体,存储用户与AI的对话历史,包括对话内容、AI人格、用户情绪状态等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@TableName("conversation")
public class Conversation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String aiPersona;
    private String role;
    private String content;
    private String emotionState;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    public Conversation() {}

    public Conversation(Long id, Long userId, String aiPersona, String role, String content, String emotionState, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.aiPersona = aiPersona;
        this.role = role;
        this.content = content;
        this.emotionState = emotionState;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAiPersona() { return aiPersona; }
    public void setAiPersona(String aiPersona) { this.aiPersona = aiPersona; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getEmotionState() { return emotionState; }
    public void setEmotionState(String emotionState) { this.emotionState = emotionState; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long userId;
        private String aiPersona;
        private String role;
        private String content;
        private String emotionState;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder aiPersona(String aiPersona) { this.aiPersona = aiPersona; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder emotionState(String emotionState) { this.emotionState = emotionState; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Conversation build() {
            return new Conversation(id, userId, aiPersona, role, content, emotionState, createdAt);
        }
    }
}

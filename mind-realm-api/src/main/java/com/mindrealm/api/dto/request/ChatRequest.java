package com.mindrealm.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: ChatRequest
 * @description: AI聊天请求DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户消息
     */
    @NotBlank(message = "消息不能为空")
    private String message;
    
    /**
     * AI人格类型(listener/analyst/healer)
     */
    private String persona;
}

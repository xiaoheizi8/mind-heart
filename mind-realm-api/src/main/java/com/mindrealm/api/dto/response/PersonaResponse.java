package com.mindrealm.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: PersonaResponse
 * @description: AI人格响应DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * AI人格类型
     */
    private String persona;
}

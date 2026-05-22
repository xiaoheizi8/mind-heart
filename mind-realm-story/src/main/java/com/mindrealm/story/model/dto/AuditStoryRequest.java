package com.mindrealm.story.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: AuditStoryRequest
 * @description: 审核故事请求DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditStoryRequest {
    
    /**
     * 审核人ID
     */
    private Long auditorId;
    
    /**
     * 是否通过
     */
    private Boolean approved;
    
    /**
     * 拒绝原因（不通过时填写）
     */
    private String rejectReason;
}
package com.mindrealm.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: SendCodeRequest
 * @description: 发送验证码请求DTO
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;
}

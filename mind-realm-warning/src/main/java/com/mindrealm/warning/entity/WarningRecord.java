package com.mindrealm.warning.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: WarningRecord
 * @description: 预警记录实体类,用于存储用户风险预警信息
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@TableName("warning_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningRecord implements Serializable {



    /**
     * 预警记录ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID,外键关联users表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 风险等级(1:低,2:中,3:高)
     */
    @TableField("risk_level")
    private Integer riskLevel;

    /**
     * 触发类型(keyword/emotion/manual)
     */
    @TableField("trigger_type")
    private String triggerType;

    /**
     * 触发内容
     */
    @TableField("content")
    private String content;

    /**
     * 是否已处理(0:否,1:是)
     */
    @TableField("handled")
    private Integer handled;

    /**
     * 处理人ID
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 处理备注
     */
    @TableField("handler_note")
    private String handlerNote;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField("handled_at")
    private LocalDateTime handledAt;
}

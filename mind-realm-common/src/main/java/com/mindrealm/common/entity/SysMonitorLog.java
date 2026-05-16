package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @className: SysMonitorLog
 * @description: 系统监控日志实体类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_monitor_log")
public class SysMonitorLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("monitor_type")
    private String monitorType;

    @TableField("metric_name")
    private String metricName;

    @TableField("metric_value")
    private BigDecimal metricValue;

    @TableField("metric_unit")
    private String metricUnit;

    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

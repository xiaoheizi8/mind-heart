package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @className: ParentChildBinding
 * @description: 家长-孩子绑定关系实体
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("parent_child_binding")
public class ParentChildBinding {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 家长用户ID */
    private Long parentId;

    /** 孩子用户ID */
    private Long childId;

    /** 绑定状态: PENDING/APPROVED/REJECTED */
    private String status;

    /** 请求时间 */
    private LocalDateTime requestTime;

    /** 响应时间 */
    private LocalDateTime responseTime;
}

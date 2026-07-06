package com.mindrealm.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: EsSyncEvent
 * @description: ES 数据同步事件载荷
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsSyncEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 实体类型: diary / story */
    private String entityType;

    /** 操作类型: SAVE / DELETE */
    private String operation;

    /** 实体主键ID */
    private Long entityId;
}

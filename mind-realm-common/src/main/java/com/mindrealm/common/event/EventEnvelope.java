package com.mindrealm.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @className: EventEnvelope
 * @description: 统一事件信封，所有 Kafka 消息均使用此包装器
 *               包含事件元数据和类型化的业务载荷
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 事件唯一ID，用于幂等去重 */
    private String eventId;

    /** 事件类型: ES_SYNC / WARNING_ANALYZE / NOTIFICATION_SEND */
    private String eventType;

    /** 业务数据载荷 */
    private T payload;

    /** 事件产生时间戳（毫秒） */
    private Long timestamp;

    /** 消息来源模块 */
    private String source;

    /** 重试次数（消费者自维护） */
    private Integer retryCount;

    /** 扩展元数据 */
    private Map<String, String> metadata;
}

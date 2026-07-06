package com.mindrealm.common.event;

import lombok.Getter;

/**
 * @className: EventType
 * @description: Kafka 事件类型枚举
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Getter
public enum EventType {

    /** MySQL → ES 数据同步 */
    ES_SYNC("es_sync"),

    /** 风险预警异步深度分析 */
    WARNING_ANALYZE("warning_analyze"),

    /** 通知分发（DB写入 + 短信 + 邮件） */
    NOTIFICATION_SEND("notification_send");

    private final String code;

    EventType(String code) {
        this.code = code;
    }
}

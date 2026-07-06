package com.mindrealm.mq.constant;

/**
 * @className: KafkaTopics
 * @description: Kafka Topic 名称常量
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    /** ES数据同步 Topic（3分区，按entityType路由） */
    public static final String ES_SYNC = "mind-realm.es.sync";



    /** 风险预警异步分析 Topic（3分区，按userId哈希路由） */
    public static final String WARNING_ANALYZE = "mind-realm.warning.analyze";

    /** 通知发送 Topic（3分区，按通知类型路由） */
    public static final String NOTIFICATION_SEND = "mind-realm.notification.send";


    // ========== 死信队列 (Dead Letter Topics) ==========

    /** ES同步死信队列 */
    public static final String ES_SYNC_DLT = ES_SYNC + ".DLT";


    /** 预警死信队列 */
    public static final String WARNING_ANALYZE_DLT = WARNING_ANALYZE + ".DLT";


    /** 通知死信队列 */
    public static final String NOTIFICATION_SEND_DLT = NOTIFICATION_SEND + ".DLT";
}

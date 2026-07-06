package com.mindrealm.api.consumer;

import com.mindrealm.mq.constant.KafkaTopics;
import com.mindrealm.mq.metrics.KafkaMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @className: DeadLetterHandler
 * @description: 死信队列处理器
 *               监听所有 .DLT 后缀的 Topic，记录失败消息并触发告警
 *               DLT 消费者不重试，只记录日志 + 通知管理员
 *               管理员可通过 /admin/v1/mq/dead-letters 接口查看和手动重放
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Slf4j
@Component
public class DeadLetterHandler {

    private final KafkaMetrics metrics;

    public DeadLetterHandler(KafkaMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * 监听所有死信队列的消息
     * 只记录日志，等待管理员手动处理
     */
    @KafkaListener(
            topics = {
                    KafkaTopics.ES_SYNC_DLT,
                    KafkaTopics.WARNING_ANALYZE_DLT,
                    KafkaTopics.NOTIFICATION_SEND_DLT
            },
            groupId = "${spring.kafka.consumer.group-id:mind-realm}-dlt-handler",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleDeadLetter(byte[] data,
                                  @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
                                  @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String errorMsg,
                                  Acknowledgment ack) {
        int size = data != null ? data.length : 0;
        metrics.incrementDlq();
        log.error("【死信队列】originalTopic={}, error={}, dataSize={} bytes",
                originalTopic, errorMsg, size);

        // TODO: 发送钉钉/企业微信/邮件告警给管理员
        // TODO: 写入 dead_letter_record 表用于管理端查询

        // DLT 消息直接确认，不重试
        ack.acknowledge();
    }
}

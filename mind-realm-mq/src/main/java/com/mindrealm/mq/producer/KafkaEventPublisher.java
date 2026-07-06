package com.mindrealm.mq.producer;

import com.mindrealm.common.event.EventEnvelope;
import com.mindrealm.common.event.EventType;
import com.mindrealm.mq.codec.EventEnvelopeCodec;
import com.mindrealm.mq.metrics.KafkaMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @className: KafkaEventPublisher
 * @description: 统一 Kafka 事件发布器
 *               所有业务模块通过此组件发布事件到 Kafka，
 *               自动构建 EventEnvelope 包装器，使用 Kryo 二进制序列化后异步发送
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Slf4j
@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final KafkaMetrics metrics;

    public KafkaEventPublisher(KafkaTemplate<String, byte[]> kafkaTemplate,
                               KafkaMetrics metrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.metrics = metrics;
    }

    /**
     * 发布事件到指定的 Kafka Topic
     */
    public void publish(String topic, String key, EventType eventType, Object payload) {
        publish(topic, key, eventType, payload, Map.of());
    }

    /**
     * 发布事件到指定的 Kafka Topic（带扩展元数据）
     *
     * @param topic     目标 Topic
     * @param key       分区键
     * @param eventType 事件类型
     * @param payload   业务载荷
     * @param metadata  扩展元数据（如紧急标记等）
     */
    public void publish(String topic, String key, EventType eventType, Object payload,
                        Map<String, String> metadata) {
        EventEnvelope<Object> envelope = EventEnvelope.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType.getCode())
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .source("mind-realm")
                .retryCount(0)
                .metadata(metadata)
                .build();

        // Kryo 二进制序列化
        byte[] data = EventEnvelopeCodec.encode(envelope);

        // 异步发送，带回调日志
        CompletableFuture<SendResult<String, byte[]>> future =
                kafkaTemplate.send(topic, key, data);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                metrics.incrementProducerFailure();
                log.error("Kafka 发送失败: topic={}, key={}, eventId={}, eventType={}, error={}",
                        topic, key, envelope.getEventId(), eventType.getCode(), ex.getMessage());
            } else {
                metrics.incrementProducerSuccess();
                if (log.isDebugEnabled()) {
                    log.debug("Kafka 发送成功: topic={}, partition={}, offset={}, size={} bytes, eventId={}",
                            topic, result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(), data.length,
                            envelope.getEventId());
                }
            }
        });
    }
}

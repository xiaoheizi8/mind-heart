package com.mindrealm.api.consumer;

import com.mindrealm.common.event.EventEnvelope;
import com.mindrealm.core.service.EsSyncHandler;
import com.mindrealm.mq.codec.EventEnvelopeCodec;
import com.mindrealm.mq.constant.KafkaTopics;
import com.mindrealm.mq.event.EsSyncEvent;
import com.mindrealm.mq.metrics.KafkaMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: EsSyncConsumer
 * @description: ES 数据同步 Kafka 消费者
 *               - 消费 mind-realm.es.sync 消息
 *               - Redis SETNX 幂等去重（24h TTL）
 *               - 调用 EsSyncHandler 执行实际的 ES 操作
 *               - 失败时释放幂等锁并抛出异常，触发 DefaultErrorHandler 重试→DLT
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Slf4j
@Component
public class EsSyncConsumer {

    private static final String IDEMPOTENT_KEY_PREFIX = "kafka:dedup:es:";
    private static final Duration DEDUP_TTL = Duration.ofHours(24);

    private final Map<String, EsSyncHandler> handlerMap;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaMetrics metrics;

    public EsSyncConsumer(List<EsSyncHandler> handlers, RedisTemplate<String, Object> redisTemplate,
                          KafkaMetrics metrics) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(EsSyncHandler::getType, h -> h));
        this.redisTemplate = redisTemplate;
        this.metrics = metrics;
        log.info("ES同步消费者已初始化，已注册的处理器: {}", handlerMap.keySet());
    }

    /**
     * 消费 ES 同步事件
     * concurrency=3 对应 3 个分区并行消费
     */
    @KafkaListener(
            topics = KafkaTopics.ES_SYNC,
            groupId = "${spring.kafka.consumer.group-id:mind-realm}-es-sync",
            concurrency = "3",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(byte[] data, Acknowledgment ack) {
        // 使用 Kryo Codec 进行类型安全二进制反序列化
        EventEnvelope<EsSyncEvent> envelope = EventEnvelopeCodec.decode(data, EsSyncEvent.class);
        if (envelope == null) {
            // 反序列化失败已在 Codec 中记录日志，直接跳过
            ack.acknowledge();
            return;
        }

        // 幂等去重
        if (!tryAcquireIdempotent(envelope.getEventId())) {
            log.debug("重复消息已忽略: eventId={}", envelope.getEventId());
            ack.acknowledge();
            return;
        }

        EsSyncEvent event = envelope.getPayload();
        EsSyncHandler handler = handlerMap.get(event.getEntityType());
        if (handler == null) {
            log.warn("未找到 EsSyncHandler: type={}, eventId={}", event.getEntityType(),
                    envelope.getEventId());
            ack.acknowledge();
            return;
        }

        try {
            if ("DELETE".equals(event.getOperation())) {
                handler.onDelete(event.getEntityId());
            } else {
                handler.onSave(event.getEntityId());
            }
            log.debug("ES同步消费成功: type={}, op={}, id={}, eventId={}",
                    event.getEntityType(), event.getOperation(), event.getEntityId(),
                    envelope.getEventId());
            metrics.incrementConsumerSuccess();
            ack.acknowledge();
        } catch (Exception e) {
            metrics.incrementConsumerFailure();
            log.error("ES同步消费失败: eventId={}, type={}, op={}, id={}, error={}",
                    envelope.getEventId(), event.getEntityType(), event.getOperation(),
                    event.getEntityId(), e.getMessage());
            // 释放幂等锁，允许重试
            releaseIdempotent(envelope.getEventId());
            // 抛出异常触发 DefaultErrorHandler 的重试→DLT 机制
            throw new RuntimeException("ES同步失败，触发重试: " + e.getMessage(), e);
        }
    }

    private boolean tryAcquireIdempotent(String eventId) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(IDEMPOTENT_KEY_PREFIX + eventId, "1", DEDUP_TTL);
        return Boolean.TRUE.equals(success);
    }

    private void releaseIdempotent(String eventId) {
        redisTemplate.delete(IDEMPOTENT_KEY_PREFIX + eventId);
    }
}

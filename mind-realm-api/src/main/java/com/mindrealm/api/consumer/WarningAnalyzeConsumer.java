package com.mindrealm.api.consumer;

import com.mindrealm.common.event.EventEnvelope;
import com.mindrealm.mq.codec.EventEnvelopeCodec;
import com.mindrealm.mq.constant.KafkaTopics;
import com.mindrealm.mq.event.WarningAnalyzeEvent;
import com.mindrealm.mq.metrics.KafkaMetrics;
import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @className: WarningAnalyzeConsumer
 * @description: 风险预警异步分析 Kafka 消费者
 *               - 消费 mind-realm.warning.analyze 消息
 *               - 对内容进行深度关键词/LLM分析
 *               - 高风险结果触发监护人通知
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Slf4j
@Component
public class WarningAnalyzeConsumer {

    private static final String IDEMPOTENT_KEY_PREFIX = "kafka:dedup:warn:";
    private static final Duration DEDUP_TTL = Duration.ofHours(24);

    private final WarningService warningService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaMetrics metrics;

    public WarningAnalyzeConsumer(WarningService warningService,
                                  RedisTemplate<String, Object> redisTemplate,
                                  KafkaMetrics metrics) {
        this.warningService = warningService;
        this.redisTemplate = redisTemplate;
        this.metrics = metrics;
        log.info("风险预警消费者已初始化");
    }

    /**
     * 消费风险预警分析事件
     * concurrency=3 对应 3 个分区并行消费
     */
    @KafkaListener(
            topics = KafkaTopics.WARNING_ANALYZE,
            groupId = "${spring.kafka.consumer.group-id:mind-realm}-warning-analyze",
            concurrency = "3",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(byte[] data, Acknowledgment ack) {
        EventEnvelope<WarningAnalyzeEvent> envelope =
                EventEnvelopeCodec.decode(data, WarningAnalyzeEvent.class);
        if (envelope == null) {
            ack.acknowledge();
            return;
        }

        // 幂等去重
        if (!tryAcquireIdempotent(envelope.getEventId())) {
            log.debug("重复预警消息已忽略: eventId={}", envelope.getEventId());
            ack.acknowledge();
            return;
        }

        WarningAnalyzeEvent event = envelope.getPayload();
        log.info("开始异步风险分析: userId={}, contextType={}, contextId={}, eventId={}",
                event.getUserId(), event.getContextType(), event.getContextId(),
                envelope.getEventId());

        try {
            WarningRecord record = warningService.analyzeRisk(event.getUserId(), event.getContent());
            if (record != null && warningService.isHighRiskLevel(record.getRiskLevel())) {
                log.warn("异步分析发现高风险内容: userId={}, riskLevel={}, triggerType={}, eventId={}",
                        event.getUserId(), record.getRiskLevel(), record.getTriggerType(),
                        envelope.getEventId());
            } else if (record != null) {
                log.info("异步分析发现中等风险: userId={}, riskLevel={}, eventId={}",
                        event.getUserId(), record.getRiskLevel(), envelope.getEventId());
            }
            metrics.incrementConsumerSuccess();
            ack.acknowledge();
        } catch (Exception e) {
            metrics.incrementConsumerFailure();
            log.error("预警分析消费失败: eventId={}, userId={}, error={}",
                    envelope.getEventId(), event.getUserId(), e.getMessage());
            releaseIdempotent(envelope.getEventId());
            throw new RuntimeException("预警分析失败，触发重试: " + e.getMessage(), e);
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

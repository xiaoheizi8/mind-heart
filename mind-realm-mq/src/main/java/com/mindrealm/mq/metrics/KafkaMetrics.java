package com.mindrealm.mq.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * @className: KafkaMetrics
 * @description: Kafka 消息队列 Micrometer 监控指标
 *               通过 /actuator/prometheus 暴露
 *               指标包括：
 *               - kafka_producer_success_total: 生产成功计数
 *               - kafka_producer_failure_total: 生产失败计数
 *               - kafka_consumer_success_total: 消费成功计数
 *               - kafka_consumer_failure_total: 消费失败计数
 *               - kafka_dlq_total: 死信队列消息计数
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Component
public class KafkaMetrics {

    private static final Logger log = LoggerFactory.getLogger(KafkaMetrics.class);

    private final MeterRegistry meterRegistry;

    private Counter producerSuccess;
    private Counter producerFailure;
    private Counter consumerSuccess;
    private Counter consumerFailure;
    private Counter dlqCounter;

    public KafkaMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        this.producerSuccess = Counter.builder("kafka.producer.success")
                .description("Kafka 生产者发送成功次数")
                .register(meterRegistry);

        this.producerFailure = Counter.builder("kafka.producer.failure")
                .description("Kafka 生产者发送失败次数")
                .register(meterRegistry);

        this.consumerSuccess = Counter.builder("kafka.consumer.success")
                .description("Kafka 消费者处理成功次数")
                .register(meterRegistry);

        this.consumerFailure = Counter.builder("kafka.consumer.failure")
                .description("Kafka 消费者处理失败次数")
                .register(meterRegistry);

        this.dlqCounter = Counter.builder("kafka.dlq.count")
                .description("进入死信队列的消息数量")
                .register(meterRegistry);

        log.info("Kafka 监控指标已注册: producer.success/failure, consumer.success/failure, dlq.count");
    }

    public void incrementProducerSuccess() {
        producerSuccess.increment();
    }

    public void incrementProducerFailure() {
        producerFailure.increment();
    }

    public void incrementConsumerSuccess() {
        consumerSuccess.increment();
    }

    public void incrementConsumerFailure() {
        consumerFailure.increment();
    }

    public void incrementDlq() {
        dlqCounter.increment();
    }
}

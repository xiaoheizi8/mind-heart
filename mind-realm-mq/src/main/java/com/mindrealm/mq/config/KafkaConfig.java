package com.mindrealm.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: KafkaConfig
 * @description: Kafka 核心配置类
 *               - 消息体使用 Kryo 二进制序列化（ByteArray），安全、高效
 *               - Producer: acks=all, 幂等, 压缩
 *               - Consumer: 手动提交, 3次指数退避重试后进入 DLT
 *               - 死信队列: DeadLetterPublishingRecoverer 自动将失败消息发送到 {原topic}.DLT
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * KafkaTemplate：key 为 String（分区键），value 为 byte[]（Kryo 序列化数据）
     */
    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate(
            ProducerFactory<String, byte[]> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // ======================== Producer ========================

    /**
     * Producer 工厂：byte[] 值序列化，acks=all + 幂等 + lz4 压缩
     */
    @Bean
    public ProducerFactory<String, byte[]> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(null));

        // 值序列化器：直接发送 byte[]
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.ByteArraySerializer.class);

        // ========== 消息可靠性 ==========
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // ========== 性能优化 ==========
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);

        log.info("Kafka Producer 已配置: serializer=ByteArray, acks=all, idempotent=true, compression=lz4");
        return new DefaultKafkaProducerFactory<>(props);
    }

    // ======================== Consumer ========================

    /**
     * Consumer 工厂：byte[] 值反序列化，手动提交
     */
    @Bean
    public ConsumerFactory<String, byte[]> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties(null));

        // 值反序列化器：直接获取 byte[]
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.ByteArrayDeserializer.class);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        log.info("Kafka Consumer 已配置: deserializer=ByteArray, enable.auto.commit=false");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // ======================== Listener Container ========================

    /**
     * Listener Container Factory
     * 包含错误处理策略：指数退避重试3次 → 进入DLT
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory(
            ConsumerFactory<String, byte[]> consumerFactory,
            KafkaTemplate<String, byte[]> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // 死信队列恢复器
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (cr, e) -> new org.apache.kafka.common.TopicPartition(
                        cr.topic() + ".DLT", cr.partition()));

        // 指数退避: 1s → 2s → 4s → 8s，3次重试后进入DLT
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(15000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(
                org.apache.kafka.common.errors.SerializationException.class,
                IllegalArgumentException.class,
                org.springframework.kafka.support.serializer.DeserializationException.class);

        factory.setCommonErrorHandler(errorHandler);

        log.info("Kafka ListenerContainerFactory 已配置: ackMode=MANUAL_IMMEDIATE, " +
                "重试=指数退避(1s→2s→4s→8s), DLT已启用");
        return factory;
    }
}

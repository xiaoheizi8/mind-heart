package com.mindrealm.mq.config;

import com.mindrealm.mq.constant.KafkaTopics;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * @className: KafkaTopicConfig
 * @description: Kafka Topic 自动创建配置
 *               通过 KafkaAdmin + NewTopic Bean，应用启动时自动创建所需Topic
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Configuration
public class KafkaTopicConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getBootstrapServers());
        log.info("KafkaAdmin 已配置, bootstrap-servers: {}", kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    /**
     * ES 数据同步 Topic: 3分区, 2副本
     * 按 entityType 路由，确保同类型消息有序
     */
    @Bean
    public NewTopic esSyncTopic() {
        return TopicBuilder.name(KafkaTopics.ES_SYNC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 风险预警分析 Topic: 3分区, 2副本
     * 按 userId 哈希路由，确保同一用户消息有序
     */
    @Bean
    public NewTopic warningAnalyzeTopic() {
        return TopicBuilder.name(KafkaTopics.WARNING_ANALYZE)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 通知发送 Topic: 3分区, 2副本
     * 按通知类型路由，隔离不同类型通知的流量
     */
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(KafkaTopics.NOTIFICATION_SEND)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

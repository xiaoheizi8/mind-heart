package com.mindrealm.api.aspect;

import com.mindrealm.common.annotation.EsSync;
import com.mindrealm.common.event.EventType;
import com.mindrealm.mq.constant.KafkaTopics;
import com.mindrealm.mq.event.EsSyncEvent;
import com.mindrealm.mq.producer.KafkaEventPublisher;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @className: EsSyncAspect
 * @description: ES同步切面，拦截 @EsSync 注解的方法，
 *               在方法成功返回后通过 Kafka 异步将数据同步到 Elasticsearch。
 *               Kafka 消费者负责实际调用 EsSyncHandler 执行 ES 操作，
 *               具备重试、幂等去重、死信队列等可靠性保障。
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Aspect
@Component
public class EsSyncAspect {

    private static final Logger log = LoggerFactory.getLogger(EsSyncAspect.class);

    private final KafkaEventPublisher publisher;

    public EsSyncAspect(KafkaEventPublisher publisher) {
        this.publisher = publisher;
        log.info("ES同步切面已初始化，使用 Kafka 异步同步模式");
    }

    /**
     * SAVE操作：方法成功返回后发布 Kafka 事件
     * 返回值可能是实体对象（Diary）、实体ID（Long）、或Boolean（此时从参数中取ID）
     */
    @AfterReturning(pointcut = "@annotation(esSync)", returning = "result")
    public void afterSave(JoinPoint joinPoint, EsSync esSync, Object result) {
        if (esSync.op() != EsSync.Op.SAVE || result == null) {
            return;
        }

        Long entityId = extractEntityId(result, joinPoint.getArgs());
        if (entityId == null) {
            log.warn("无法提取实体ID，跳过ES同步事件发布: type={}", esSync.value());
            return;
        }

        publishEvent(esSync.value(), "SAVE", entityId);
    }

    /**
     * DELETE操作：从方法参数中获取ID，在方法成功返回后发布 Kafka 删除事件
     */
    @AfterReturning(pointcut = "@annotation(esSync)", returning = "result")
    public void afterDelete(JoinPoint joinPoint, EsSync esSync, Object result) {
        if (esSync.op() != EsSync.Op.DELETE) {
            return;
        }

        // DELETE：返回值Boolean为false表示删除失败，跳过ES同步
        if (result instanceof Boolean && !(Boolean) result) {
            return;
        }

        // 从方法参数中取第一个 Long 类型参数作为ID
        Long id = extractId(joinPoint.getArgs());
        if (id == null) {
            log.warn("无法从方法参数中提取ID，跳过ES删除事件发布: type={}", esSync.value());
            return;
        }

        publishEvent(esSync.value(), "DELETE", id);
    }

    /**
     * 发布 ES 同步事件到 Kafka
     */
    private void publishEvent(String entityType, String operation, Long entityId) {
        try {
            EsSyncEvent event = EsSyncEvent.builder()
                    .entityType(entityType)
                    .operation(operation)
                    .entityId(entityId)
                    .build();

            // 按 entityType 路由分区，保证同类型消息有序
            publisher.publish(KafkaTopics.ES_SYNC, entityType, EventType.ES_SYNC, event);
            log.debug("ES同步事件已发布: type={}, op={}, id={}", entityType, operation, entityId);
        } catch (Exception e) {
            log.error("ES同步事件发布失败: type={}, op={}, id={}, error={}",
                    entityType, operation, entityId, e.getMessage());
        }
    }

    /**
     * 从方法返回值中提取实体ID
     */
    private Long extractEntityId(Object result, Object[] args) {
        if (result instanceof Long) {
            return (Long) result;
        }
        try {
            java.lang.reflect.Field idField = result.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(result);
            if (idValue instanceof Long) {
                return (Long) idValue;
            }
        } catch (Exception ignored) {
        }
        if (result instanceof Boolean && (Boolean) result) {
            return extractId(args);
        }
        return null;
    }

    /**
     * 从参数数组中提取第一个 Long 类型参数作为实体ID
     */
    private Long extractId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }
}

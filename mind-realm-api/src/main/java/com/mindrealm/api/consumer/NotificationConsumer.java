package com.mindrealm.api.consumer;

import com.mindrealm.common.entity.Notification;
import com.mindrealm.common.event.EventEnvelope;
import com.mindrealm.common.mapper.NotificationMapper;
import com.mindrealm.common.service.SmsService;
import com.mindrealm.common.service.impl.EmailService;
import com.mindrealm.mq.codec.EventEnvelopeCodec;
import com.mindrealm.mq.constant.KafkaTopics;
import com.mindrealm.mq.event.NotificationEvent;
import com.mindrealm.mq.metrics.KafkaMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @className: NotificationConsumer
 * @description: 通知分发 Kafka 消费者
 *               - 消费 mind-realm.notification.send 消息
 *               - 写入 notification 表（应用内通知）
 *               - 发送 SMS 短信（预警/紧急通知）
 *               - 发送 Email 邮件
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Slf4j
@Component
public class NotificationConsumer {

    private static final String IDEMPOTENT_KEY_PREFIX = "kafka:dedup:notif:";
    private static final Duration DEDUP_TTL = Duration.ofHours(24);

    private final NotificationMapper notificationMapper;
    private final SmsService smsService;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaMetrics metrics;

    public NotificationConsumer(NotificationMapper notificationMapper,
                                SmsService smsService,
                                EmailService emailService,
                                RedisTemplate<String, Object> redisTemplate,
                                KafkaMetrics metrics) {
        this.notificationMapper = notificationMapper;
        this.smsService = smsService;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
        this.metrics = metrics;
        log.info("通知消费者已初始化");
    }

    /**
     * 消费通知发送事件
     */
    @KafkaListener(
            topics = KafkaTopics.NOTIFICATION_SEND,
            groupId = "${spring.kafka.consumer.group-id:mind-realm}-notification",
            concurrency = "3",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(byte[] data, Acknowledgment ack) {
        EventEnvelope<NotificationEvent> envelope =
                EventEnvelopeCodec.decode(data, NotificationEvent.class);
        if (envelope == null) {
            ack.acknowledge();
            return;
        }

        // 幂等去重
        if (!tryAcquireIdempotent(envelope.getEventId())) {
            log.debug("重复通知消息已忽略: eventId={}", envelope.getEventId());
            ack.acknowledge();
            return;
        }

        NotificationEvent event = envelope.getPayload();

        try {
            // 1. 始终写入应用内通知表
            saveInAppNotification(event);

            // 2. 根据通知类型发送外部渠道
            switch (event.getNotificationType()) {
                case "warning":
                    sendWarningChannels(event);
                    break;
                case "emergency":
                    sendEmergencyChannels(event);
                    break;
                case "reminder":
                case "weekly_report":
                default:
                    // 日记提醒和周报只写应用内通知，不推外部渠道
                    break;
            }

            log.debug("通知消费成功: userId={}, type={}, eventId={}",
                    event.getUserId(), event.getNotificationType(), envelope.getEventId());
            metrics.incrementConsumerSuccess();
            ack.acknowledge();
        } catch (Exception e) {
            metrics.incrementConsumerFailure();
            log.error("通知消费失败: eventId={}, userId={}, type={}, error={}",
                    envelope.getEventId(), event.getUserId(), event.getNotificationType(),
                    e.getMessage());
            releaseIdempotent(envelope.getEventId());
            throw new RuntimeException("通知发送失败，触发重试: " + e.getMessage(), e);
        }
    }

    /**
     * 写入应用内通知表
     */
    private void saveInAppNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setType(event.getNotificationType());
        notification.setTitle(event.getTitle());
        notification.setContent(event.getContent());
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    /**
     * 发送预警通知的外部渠道（短信 + 邮件）
     */
    private void sendWarningChannels(NotificationEvent event) {
        if (event.getGuardianPhone() != null && !event.getGuardianPhone().isEmpty()) {
            try {
                smsService.sendWarningSms(event.getGuardianPhone(),
                        event.getUserName() != null ? event.getUserName() : "用户",
                        event.getRiskLevel() != null ? event.getRiskLevel() : 1);
                log.info("预警短信已发送: userId={}, phone={}", event.getUserId(),
                        maskPhone(event.getGuardianPhone()));
            } catch (Exception e) {
                log.error("预警短信发送失败: userId={}, error={}", event.getUserId(), e.getMessage());
            }
        }

        if (event.getGuardianEmail() != null && !event.getGuardianEmail().isEmpty()) {
            try {
                String riskLevelDesc = event.getRiskLevel() != null && event.getRiskLevel() >= 3
                        ? "高风险" : "中风险";
                String color = event.getRiskLevel() != null && event.getRiskLevel() >= 3
                        ? "#e74c3c" : "#f39c12";
                String userName = event.getUserName() != null ? event.getUserName() : "用户";
                String riskContent = event.getRiskContent() != null ? event.getRiskContent() : "";

                String htmlContent = buildWarningEmailHtml(userName, riskLevelDesc, color, riskContent);
                String subject = "【心域】预警通知 - " + riskLevelDesc;

                emailService.sendHtmlEmail(event.getGuardianEmail(), subject, htmlContent);
                log.info("预警邮件已发送: userId={}, email={}", event.getUserId(),
                        maskEmail(event.getGuardianEmail()));
            } catch (Exception e) {
                log.error("预警邮件发送失败: userId={}, error={}", event.getUserId(), e.getMessage());
            }
        }
    }

    /**
     * 发送紧急通知（优先短信）
     */
    private void sendEmergencyChannels(NotificationEvent event) {
        if (event.getGuardianPhone() != null && !event.getGuardianPhone().isEmpty()) {
            try {
                smsService.sendWarningSms(event.getGuardianPhone(),
                        event.getUserName() != null ? event.getUserName() : "用户",
                        event.getRiskLevel() != null ? event.getRiskLevel() : 3);
                log.warn("紧急通知短信已发送: userId={}, phone={}", event.getUserId(),
                        maskPhone(event.getGuardianPhone()));
            } catch (Exception e) {
                log.error("紧急通知短信发送失败: userId={}, error={}", event.getUserId(), e.getMessage());
            }
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

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return "***";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        int at = email.indexOf('@');
        return email.charAt(0) + "***" + email.substring(at);
    }

    /**
     * 构建预警通知邮件 HTML
     */
    private String buildWarningEmailHtml(String userName, String riskLevelDesc,
                                         String color, String riskContent) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; background-color: #f5f5f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                                <tr>
                                    <td style="padding: 30px 40px; text-align: center; border-bottom: 1px solid #eee;">
                                        <h1 style="margin: 0; color: #1a1a2e; font-size: 24px; font-weight: 600;">
                                            心域预警通知
                                        </h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px 0; color: #1a1a2e; font-size: 20px;">
                                            尊敬的监护人您好
                                        </h2>
                                        <p style="margin: 0 0 20px 0; color: #555; font-size: 15px; line-height: 1.6;">
                                            您关注的青少年 <strong>%s</strong> 在使用心域时检测到风险信息。
                                        </p>
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 20px 0;">
                                            <tr>
                                                <td style="padding: 16px; background-color: %s; border-radius: 8px; text-align: center;">
                                                    <span style="color: #ffffff; font-size: 18px; font-weight: 600;">
                                                        风险等级: %s
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin: 20px 0; color: #555; font-size: 15px; line-height: 1.6;">
                                            <strong>内容摘要:</strong><br/>
                                            %s
                                        </p>
                                        <p style="margin: 30px 0 0 0; color: #999; font-size: 14px;">
                                            请您及时关注孩子的情绪状态，给予关爱和支持。
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px; text-align: center; border-top: 1px solid #eee; background-color: #fafafa; border-radius: 0 0 12px 12px;">
                                        <p style="margin: 0; color: #999; font-size: 12px;">
                                            如需紧急帮助，请拨打: <strong>400-161-9995</strong>
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName, color, riskLevelDesc, riskContent);
    }
}

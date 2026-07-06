package com.mindrealm.warning.service.impl;

import com.mindrealm.common.event.EventType;
import com.mindrealm.mq.constant.KafkaTopics;
import com.mindrealm.mq.event.NotificationEvent;
import com.mindrealm.mq.producer.KafkaEventPublisher;
import com.mindrealm.warning.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @className: NotificationServiceImpl
 * @description: 通知服务实现类
 *               所有通知通过 Kafka 异步分发：
 *               1. 发布 NotificationEvent 到 mind-realm.notification.send
 *               2. NotificationConsumer 消费后写入 DB + 发送短信/邮件
 *               具备重试、幂等去重、死信队列等可靠性保障
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Value("${notification.emergency.hotline:400-161-9995}")
    private String emergencyHotline;

    private final KafkaEventPublisher publisher;

    public NotificationServiceImpl(KafkaEventPublisher publisher) {
        this.publisher = publisher;
        logger.info("通知服务已切换为 Kafka 异步模式");
    }

    /**
     * 发送日记提醒
     * 通过 Kafka 异步分发，由 NotificationConsumer 负责写入 DB
     */
    @Override
    public void sendDiaryReminder(Long userId) {
        NotificationEvent event = NotificationEvent.builder()
                .userId(userId)
                .notificationType("reminder")
                .title("日记提醒")
                .content("今天还没有写日记哦，记录一下今天的心情吧~")
                .build();

        publisher.publish(
                KafkaTopics.NOTIFICATION_SEND,
                "reminder:" + userId,
                EventType.NOTIFICATION_SEND,
                event
        );
        logger.debug("日记提醒事件已发布: userId={}", userId);
    }

    /**
     * 发送周报
     */
    @Override
    public void sendWeeklyReport(Long userId) {
        NotificationEvent event = NotificationEvent.builder()
                .userId(userId)
                .notificationType("weekly_report")
                .title("周报提醒")
                .content("本周的情绪报告已生成，点击查看详情~")
                .build();

        publisher.publish(
                KafkaTopics.NOTIFICATION_SEND,
                "weekly_report:" + userId,
                EventType.NOTIFICATION_SEND,
                event
        );
        logger.debug("周报事件已发布: userId={}", userId);
    }

    /**
     * 发送预警通知给监护人
     * 通过 Kafka 异步分发，由 NotificationConsumer 负责发送短信/邮件
     */
    @Override
    public boolean sendWarningNotification(Long userId, String guardianPhone, String guardianEmail,
                                           String userName, int riskLevel, String riskContent) {
        if (guardianPhone == null && (guardianEmail == null || guardianEmail.isEmpty())) {
            logger.warn("用户 {} 未设置监护人联系方式,无法发送通知", userId);
            return false;
        }

        NotificationEvent event = NotificationEvent.builder()
                .userId(userId)
                .notificationType("warning")
                .title("预警通知")
                .content("检测到风险内容，请关注孩子情绪状态")
                .guardianPhone(guardianPhone)
                .guardianEmail(guardianEmail)
                .userName(userName)
                .riskLevel(riskLevel)
                .riskContent(riskContent)
                .build();

        publisher.publish(
                KafkaTopics.NOTIFICATION_SEND,
                "warning:" + userId,
                EventType.NOTIFICATION_SEND,
                event
        );
        logger.info("预警通知事件已发布: userId={}, riskLevel={}", userId, riskLevel);
        return true;
    }

    /**
     * 发送紧急预警通知
     * 附加紧急标记到元数据
     */
    @Override
    public boolean sendEmergencyNotification(Long userId, String guardianPhone,
                                             String userName, String riskContent) {
        if (guardianPhone == null || guardianPhone.isEmpty()) {
            logger.warn("用户 {} 未设置监护人手机号,无法发送紧急通知", userId);
            return false;
        }

        NotificationEvent event = NotificationEvent.builder()
                .userId(userId)
                .notificationType("emergency")
                .title("紧急预警")
                .content("检测到高风险内容，请立即关注！")
                .guardianPhone(guardianPhone)
                .userName(userName)
                .riskLevel(3)
                .riskContent(riskContent)
                .build();

        // 紧急标记放入 metadata
        publisher.publish(
                KafkaTopics.NOTIFICATION_SEND,
                "emergency:" + userId,
                EventType.NOTIFICATION_SEND,
                event,
                Map.of("priority", "high", "urgent", "true")
        );
        logger.warn("紧急通知事件已发布: userId={}", userId);
        return true;
    }

    /**
     * 检查通知是否可用（Kafka 模式下始终可用）
     */
    @Override
    public boolean isNotificationEnabled() {
        return true;
    }

    /**
     * 获取紧急热线
     */
    @Override
    public String getEmergencyHotline() {
        return emergencyHotline;
    }
}

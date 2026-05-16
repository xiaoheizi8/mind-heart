package com.mindrealm.warning.service.impl;

import com.mindrealm.common.entity.Notification;
import com.mindrealm.common.mapper.NotificationMapper;
import com.mindrealm.common.service.impl.EmailService;
import com.mindrealm.common.service.SmsService;
import com.mindrealm.warning.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @className: NotificationServiceImpl
 * @description: 通知服务实现类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notification.sms.provider:aliyun}")
    private String smsProvider;

    @Value("${notification.emergency.hotline:400-161-9995}")
    private String emergencyHotline;

    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    @Autowired(required = false)
    private EmailService emailService;

    @Autowired(required = false)
    private SmsService smsService;

    /**
     * 发送日记提醒
     */
    public void sendDiaryReminder(Long userId) {
        if (notificationMapper == null) {
            logger.warn("NotificationMapper 未注入");
            return;
        }
        
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("reminder");
            notification.setTitle("日记提醒");
            notification.setContent("今天还没有写日记哦，记录一下今天的心情吧~");
            notification.setIsRead(0);
            notification.setCreatedAt(LocalDateTime.now());
            
            notificationMapper.insert(notification);
            logger.info("日记提醒发送成功, userId: {}", userId);
        } catch (Exception e) {
            logger.error("发送日记提醒失败: {}", e.getMessage());
        }
    }

    /**
     * 发送周报
     */
    public void sendWeeklyReport(Long userId) {
        if (notificationMapper == null) {
            logger.warn("NotificationMapper 未注入");
            return;
        }
        
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("system");
            notification.setTitle("周报提醒");
            notification.setContent("本周的情绪报告已生成，点击查看详情~");
            notification.setIsRead(0);
            notification.setCreatedAt(LocalDateTime.now());
            
            notificationMapper.insert(notification);
            logger.info("周报发送成功, userId: {}", userId);
        } catch (Exception e) {
            logger.error("发送周报失败: {}", e.getMessage());
        }
    }

    /**
     * 发送预警通知给监护人
     * @param userId 用户ID
     * @param guardianPhone 监护人手机号
     * @param guardianEmail 监护人邮箱
     * @param userName 用户名称
     * @param riskLevel 风险等级(1:低,2:中,3:高)
     * @param riskContent 风险内容摘要
     * @return 是否发送成功
     */
    public boolean sendWarningNotification(Long userId, String guardianPhone, String guardianEmail,
            String userName, int riskLevel, String riskContent) {
        
        if (guardianPhone == null && (guardianEmail == null || guardianEmail.isEmpty())) {
            logger.warn("用户 {} 未设置监护人联系方式,无法发送通知", userId);
            return false;
        }

        boolean smsSuccess = false;
        boolean emailSuccess = false;

        // 发送短信通知
        if (smsEnabled && guardianPhone != null && !guardianPhone.isEmpty()) {
            smsSuccess = sendSms(guardianPhone, buildSmsContent(userName, riskLevel, riskContent));
        }

        // 发送邮件通知
        if (emailEnabled && guardianEmail != null && !guardianEmail.isEmpty()) {
            emailSuccess = sendGuardianEmail(guardianEmail, userName, riskLevel, riskContent);
        }

        logger.info("预警通知发送结果 - 用户:{}, 短信:{}, 邮件:{}", 
                userId, smsSuccess, emailSuccess);

        return smsSuccess || emailSuccess;
    }

    /**
     * 发送紧急预警通知
     * @param userId 用户ID
     * @param guardianPhone 监护人手机号
     * @param userName 用户名称
     * @param riskContent 风险内容
     * @return 是否发送成功
     */
    public boolean sendEmergencyNotification(Long userId, String guardianPhone,
            String userName, String riskContent) {
        
        if (guardianPhone == null || guardianPhone.isEmpty()) {
            logger.warn("用户 {} 未设置监护人手机号,无法发送紧急通知", userId);
            return false;
        }

        String emergencyContent = buildEmergencyContent(userName, riskContent);
        
        // 优先发送短信
        if (smsEnabled) {
            return sendSms(guardianPhone, emergencyContent);
        }

        logger.warn("短信通知未启用,紧急通知发送失败");
        return false;
    }

    /**
     * 发送短信
     * @param phone 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    private boolean sendSms(String phone, String content) {
        if (smsService == null) {
            logger.warn("SmsService 未注入，短信通知失败");
            return false;
        }
        
        try {
            logger.info("发送短信到 {}, 内容: {}", phone, content);
            
            // 使用短信服务发送（内容模式转为预警模式）
            SmsService.SmsResult result = smsService.sendWarningSms(phone, "用户", 3);
            
            if (result.isSuccess()) {
                logger.info("短信发送成功: bizId={}", result.getBizId());
                return true;
            } else {
                logger.warn("短信发送失败: {}", result.getMessage());
                return false;
            }
        } catch (Exception e) {
            logger.error("短信发送失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 发送邮件
     * @param email 邮箱
     * @param userName 用户名称
     * @param riskLevel 风险等级
     * @param riskContent 风险内容
     * @return 是否发送成功
     */
    private boolean sendGuardianEmail(String email, String userName, int riskLevel, String riskContent) {
        if (emailService == null) {
            logger.warn("EmailService 未注入,邮件通知失败");
            return false;
        }
        
        try {
            String riskLevelDesc = switch (riskLevel) {
                case 3 -> "高风险";
                case 2 -> "中风险";
                default -> "低风险";
            };
            
            String subject = "【心域】预警通知 - " + riskLevelDesc;
            String htmlContent = buildGuardianEmailHtml(userName, riskLevelDesc, riskContent);
            
            emailService.sendHtmlEmail(email, subject, htmlContent);
            logger.info("预警邮件发送成功: to={}", email);
            return true;
        } catch (Exception e) {
            logger.error("预警邮件发送失败: to={}, error={}", email, e.getMessage());
            return false;
        }
    }

    private String buildGuardianEmailHtml(String userName, String riskLevel, String content) {
        String color = "3".equals(riskLevel) ? "#e74c3c" : "#f39c12";
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
            """.formatted(userName, color, riskLevel, content);
    }

    /**
     * 构建短信内容
     */
    private String buildSmsContent(String userName, int riskLevel, String riskContent) {
        String riskLevelDesc = switch (riskLevel) {
            case 3 -> "高风险";
            case 2 -> "中风险";
            default -> "低风险";
        };

        String truncatedContent = riskContent.length() > 20 
            ? riskContent.substring(0, 20) + "..." 
            : riskContent;

        return String.format("【心域】温馨提示: 您关注的%s孩子(%s)发布了%s内容，建议及时关注。如需帮助请拨打%s", 
                userName, truncatedContent, riskLevelDesc, emergencyHotline);
    }

    /**
     * 构建紧急短信内容
     */
    private String buildEmergencyContent(String userName, String riskContent) {
        return String.format("【心域】紧急提醒: 您关注的%s孩子可能存在自我伤害风险，请立即关注并联系孩子。如需紧急帮助请拨打%s", 
                userName, emergencyHotline);
    }

    /**
     * 检查通知是否可用
     * @return 是否可用
     */
    public boolean isNotificationEnabled() {
        return smsEnabled || emailEnabled;
    }

    /**
     * 获取紧急热线
     * @return 热线号码
     */
    public String getEmergencyHotline() {
        return emergencyHotline;
    }
}
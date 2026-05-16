package com.mindrealm.common.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @className: EmailService
 * @description: 邮件服务类,提供验证码发送、普通邮件发送、HTML邮件发送等功能
 *               验证码存储在Redis中,5分钟有效
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:spring_dev_ljw@163.com}")
    private String fromEmail;

    @Value("${spring.mail.nickname:心域}")
    private String nickname;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发送邮箱验证码
     * 验证码存储在Redis中,5分钟有效
     * @param email 目标邮箱地址
     */
    public void sendVerificationCode(String email) {
        if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        
        if (redisTemplate == null) {
            throw new IllegalStateException("Redis未配置,无法发送验证码");
        }

        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        
        redisTemplate.opsForValue().set("email:code:" + email, code, 5, TimeUnit.MINUTES);
        
        sendVerificationEmail(email, code);
        
        log.info("邮箱验证码已发送: email={}, code={}", email, code);
    }

    /**
     * 发送验证码邮件
     * @param email 目标邮箱
     * @param code 验证码
     */
    private void sendVerificationEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(nickname + " <" + fromEmail + ">");
            helper.setTo(email);
            helper.setSubject("【心域】验证码");
            helper.setText(buildVerificationHtml(code), true);
            
            mailSender.send(message);
            log.info("验证码邮件发送成功: to={}", email);
        } catch (MessagingException e) {
            log.error("验证码邮件发送失败: to={}", email, e);
            throw new RuntimeException("验证码邮件发送失败", e);
        }
    }

    /**
     * 构建验证码邮件HTML模板
     * @param code 验证码
     * @return HTML格式的邮件内容
     */
    private String buildVerificationHtml(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; background-color: #f5f5f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                                <!-- Header -->
                                <tr>
                                    <td style="padding: 30px 40px; text-align: center; border-bottom: 1px solid #eee;">
                                        <h1 style="margin: 0; color: #1a1a2e; font-size: 24px; font-weight: 600;">
                                            心域
                                        </h1>
                                        <p style="margin: 8px 0 0 0; color: #666; font-size: 14px;">
                                            青少年心理数字孪生系统
                                        </p>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px 0; color: #1a1a2e; font-size: 20px; font-weight: 500;">
                                            您好
                                        </h2>
                                        <p style="margin: 0 0 30px 0; color: #555; font-size: 15px; line-height: 1.6;">
                                            感谢使用心域！您的验证码如下，请在5分钟内完成验证。
                                        </p>
                                        <!-- Code Box -->
                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td style="padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); border-radius: 8px; text-align: center;">
                                                    <span style="color: #ffffff; font-size: 32px; font-weight: 700; letter-spacing: 8px;">
                                                        %s
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin: 24px 0 0 0; color: #999; font-size: 13px;">
                                            如非本人操作，请忽略此邮件
                                        </p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="padding: 20px 40px; text-align: center; border-top: 1px solid #eee; background-color: #fafafa; border-radius: 0 0 12px 12px;">
                                        <p style="margin: 0; color: #999; font-size: 12px;">
                                            此邮件由系统自动发送，请勿回复
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(code);
    }

    /**
     * 验证邮箱验证码
     * 验证成功后删除Redis中的验证码,确保验证码只能使用一次
     * @param email 邮箱地址
     * @param code 用户输入的验证码
     * @return 验证码正确返回true,否则返回false
     */
    public boolean verifyCode(String email, String code) {
        if (email == null || code == null || redisTemplate == null) {
            return false;
        }
        Object cachedCode = redisTemplate.opsForValue().get("email:code:" + email);
        if (cachedCode == null) {
            return false;
        }
        if (cachedCode.toString().equals(code)) {
            redisTemplate.delete("email:code:" + email);
            return true;
        }
        return false;
    }

    /**
     * 发送普通邮件
     * @param to 目标邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(nickname + " <" + fromEmail + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("<html><body><p>" + content + "</p></body></html>", true);
            
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("邮件发送失败: to={}", to, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送HTML格式邮件
     * @param to 目标邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML格式的邮件内容
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(nickname + " <" + fromEmail + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML邮件发送成功: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败: to={}", to, e);
            throw new RuntimeException("HTML邮件发送失败", e);
        }
    }
}
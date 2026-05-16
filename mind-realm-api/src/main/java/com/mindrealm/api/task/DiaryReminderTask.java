package com.mindrealm.api.task;

import com.mindrealm.common.entity.User;
import com.mindrealm.common.service.UserService;
import com.mindrealm.warning.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DiaryReminderTask {

    private static final Logger logger = LoggerFactory.getLogger(DiaryReminderTask.class);

    @Autowired
    private UserService userService;
    
    private final INotificationService notificationService;

    public DiaryReminderTask(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 20 * * ?")
    public void sendDiaryReminders() {
        logger.info("开始发送日记提醒...");
        
        try {
            List<User> users = userService.findAll();
            
            for (User user : users) {
                if (user.getStatus() == 1) {
                    notificationService.sendDiaryReminder(user.getId());
                }
            }
            
            logger.info("日记提醒发送完成，共 {} 用户", users.size());
        } catch (Exception e) {
            logger.error("发送日记提醒失败: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 8 ? * MON")
    public void sendWeeklyReports() {
        logger.info("开始发送周报...");
        
        try {
            List<User> users = userService.findAll();
            
            for (User user : users) {
                if (user.getStatus() == 1) {
                    notificationService.sendWeeklyReport(user.getId());
                }
            }
            
            logger.info("周报发送完成，共 {} 用户", users.size());
        } catch (Exception e) {
            logger.error("发送周报失败: {}", e.getMessage());
        }
    }
}
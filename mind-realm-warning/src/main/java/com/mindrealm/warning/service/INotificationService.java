package com.mindrealm.warning.service;

/**
 * @className: INotificationService
 * @description: 通知服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface INotificationService {

    /**
     * 发送日记提醒
     * @param userId 用户ID
     */
    void sendDiaryReminder(Long userId);

    /**
     * 发送周报
     * @param userId 用户ID
     */
    void sendWeeklyReport(Long userId);

    /**
     * 发送预警通知给监护人
     * @param userId 用户ID
     * @param guardianPhone 监护人手机号
     * @param guardianEmail 监护人邮箱
     * @param userName 用户名称
     * @param riskLevel 风险等级
     * @param riskContent 风险内容摘要
     * @return 是否发送成功
     */
    boolean sendWarningNotification(Long userId, String guardianPhone, String guardianEmail,
            String userName, int riskLevel, String riskContent);

    /**
     * 发送紧急预警通知
     * @param userId 用户ID
     * @param guardianPhone 监护人手机号
     * @param userName 用户名称
     * @param riskContent 风险内容
     * @return 是否发送成功
     */
    boolean sendEmergencyNotification(Long userId, String guardianPhone,
            String userName, String riskContent);

    /**
     * 检查通知是否可用
     * @return 是否可用
     */
    boolean isNotificationEnabled();

    /**
     * 获取紧急热线
     * @return 热线号码
     */
    String getEmergencyHotline();
}

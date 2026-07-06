package com.mindrealm.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: NotificationEvent
 * @description: 通知发送事件载荷
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 接收用户ID */
    private Long userId;

    /** 通知类型: reminder / weekly_report / warning / emergency */
    private String notificationType;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    // ========== 以下字段仅用于预警通知（notificationType=warning/emergency） ==========

    /** 监护人手机号 */
    private String guardianPhone;

    /** 监护人邮箱 */
    private String guardianEmail;

    /** 用户名（用于短信/邮件模板变量） */
    private String userName;

    /** 风险等级 (1-3)，仅预警通知 */
    private Integer riskLevel;

    /** 风险内容描述，仅预警通知 */
    private String riskContent;
}

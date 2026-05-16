package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: UserSettings
 * @description: 用户设置实体类,存储用户的个性化设置包括提醒开关、隐私设置等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@TableName("user_settings")
public class UserSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设置ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 日记提醒开关(0:关,1:开)
     */
    private Integer diaryReminder;

    /**
     * AI推送开关(0:关,1:开)
     */
    private Integer aiPush;

    /**
     * 周报开关(0:关,1:开)
     */
    private Integer weeklyReport;

    /**
     * 预警通知开关(0:关,1:开)
     */
    private Integer warningNotify;

    /**
     * 提醒时间(HH:mm格式)
     */
    private String reminderTime;

    /**
     * 日记密码保护(0:关,1:开)
     */
    private Integer privacyDiaryPassword;

    /**
     * 展示心情开关(0:关,1:开)
     */
    private Integer privacyShowMood;

    /**
     * 匿名数据开关(0:关,1:开)
     */
    private Integer privacyAnonymousData;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime updatedAt;
}
package com.mindrealm.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.UserSettings;
import com.mindrealm.common.mapper.UserSettingsMapper;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @className: UserSettingsController
 * @description: 用户设置控制器,处理用户偏好设置的查询和更新请求
 *               包括日记提醒、AI推送、周报、预警通知、隐私设置等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@RestController
@RequestMapping("/api/v1/user/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsMapper userSettingsMapper;

    /**
     * 获取用户设置
     * 如果用户没有设置,则创建默认设置并返回
     * @return 用户设置信息,未登录返回401
     */
    @GetMapping("")
    public Result<UserSettings> getSettings() {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        UserSettings settings = userSettingsMapper.selectOne(
                new LambdaQueryWrapper<UserSettings>()
                        .eq(UserSettings::getUserId, userId)
        );
        
        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setDiaryReminder(1);
            settings.setAiPush(1);
            settings.setWeeklyReport(1);
            settings.setWarningNotify(1);
            settings.setReminderTime("20:00");
            settings.setPrivacyDiaryPassword(0);
            settings.setPrivacyShowMood(1);
            settings.setPrivacyAnonymousData(0);
            userSettingsMapper.insert(settings);
        }
        
        return Result.success(settings);
    }

    /**
     * 更新用户设置
     * 支持部分更新,只更新传入的非空字段
     * @param settings 用户设置对象,包含需要更新的字段
     * @return 更新成功返回200,未登录返回401
     */
    @PutMapping("")
    public Result<Void> updateSettings(@RequestBody UserSettings settings) {
        Long userId = RequestContext.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        UserSettings existing = userSettingsMapper.selectOne(
                new LambdaQueryWrapper<UserSettings>()
                        .eq(UserSettings::getUserId, userId)
        );
        
        if (existing == null) {
            settings.setUserId(userId);
            settings.setCreatedAt(TimeUtil.now());
            settings.setUpdatedAt(TimeUtil.now());
            userSettingsMapper.insert(settings);
        } else {
            if (settings.getDiaryReminder() != null) {
                existing.setDiaryReminder(settings.getDiaryReminder());
            }
            if (settings.getAiPush() != null) {
                existing.setAiPush(settings.getAiPush());
            }
            if (settings.getWeeklyReport() != null) {
                existing.setWeeklyReport(settings.getWeeklyReport());
            }
            if (settings.getWarningNotify() != null) {
                existing.setWarningNotify(settings.getWarningNotify());
            }
            if (settings.getReminderTime() != null) {
                existing.setReminderTime(settings.getReminderTime());
            }
            if (settings.getPrivacyDiaryPassword() != null) {
                existing.setPrivacyDiaryPassword(settings.getPrivacyDiaryPassword());
            }
            if (settings.getPrivacyShowMood() != null) {
                existing.setPrivacyShowMood(settings.getPrivacyShowMood());
            }
            if (settings.getPrivacyAnonymousData() != null) {
                existing.setPrivacyAnonymousData(settings.getPrivacyAnonymousData());
            }
            existing.setUpdatedAt(TimeUtil.now());
            userSettingsMapper.updateById(existing);
        }
        
        return Result.ok("设置更新成功");
    }
}
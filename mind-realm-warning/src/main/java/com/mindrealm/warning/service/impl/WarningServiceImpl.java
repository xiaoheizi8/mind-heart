package com.mindrealm.warning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.service.UserService;
import com.mindrealm.common.util.ValidatorUtil;
import com.mindrealm.warning.entity.WarningRecord;
import com.mindrealm.warning.mapper.WarningRecordMapper;
import com.mindrealm.warning.service.INotificationService;
import com.mindrealm.warning.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @className: WarningServiceImpl
 * @description: 预警服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class WarningServiceImpl implements WarningService {

    private static final Logger log = LoggerFactory.getLogger(WarningServiceImpl.class);

    private final WarningRecordMapper warningMapper;
    private final UserService userService;
    private final INotificationService notificationService;

    @Value("${notification.email.enabled:false}")
    private boolean emailNotificationEnabled;

    @Autowired
    public WarningServiceImpl(WarningRecordMapper warningMapper,
                              UserService userService,
                              INotificationService notificationService) {
        this.warningMapper = warningMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarningRecord analyzeRisk(Long userId, String content) {
        if (!ValidatorUtil.isValidId(userId) || ValidatorUtil.isEmpty(content)) {
            return null;
        }

        int riskLevel = 1;
        String triggerType = "content";

        if (isHighRisk(content)) {
            riskLevel = 3;
        } else if (isMediumRisk(content)) {
            riskLevel = 2;
        }

        if (riskLevel > 1) {
            WarningRecord record = WarningRecord.builder()
                    .userId(userId)
                    .riskLevel(riskLevel)
                    .triggerType(triggerType)
                    .content(truncateContent(content))
                    .handled(0)
                    .createdAt(LocalDateTime.now())
                    .build();

            warningMapper.insert(record);
            log.warn("创建预警记录: userId={}, riskLevel={}", userId, riskLevel);

            if (riskLevel == 3 && emailNotificationEnabled) {
                notifyGuardian(userId, record);
            }

            return record;
        }

        return null;
    }

    @Override
    public Page<WarningRecord> getUserWarnings(Long userId, int pageNum, int pageSize) {
        if (!ValidatorUtil.isValidId(userId)) {
            return new Page<>(1, 10);
        }

        pageNum = ValidatorUtil.validatePageNum(pageNum);
        pageSize = ValidatorUtil.validatePageSize(pageSize);

        Page<WarningRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<WarningRecord>()
                .eq(WarningRecord::getUserId, userId)
                .orderByDesc(WarningRecord::getCreatedAt);
        return warningMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<WarningRecord> getWarningList(Long userId, Integer riskLevel, Integer handled,
                                               int pageNum, int pageSize) {
        pageNum = ValidatorUtil.validatePageNum(pageNum);
        pageSize = ValidatorUtil.validatePageSize(pageSize);

        Page<WarningRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<>();

        if (ValidatorUtil.isValidId(userId)) {
            wrapper.eq(WarningRecord::getUserId, userId);
        }
        if (riskLevel != null) {
            wrapper.eq(WarningRecord::getRiskLevel, riskLevel);
        }
        if (handled != null) {
            wrapper.eq(WarningRecord::getHandled, handled);
        }

        wrapper.orderByDesc(WarningRecord::getCreatedAt);
        return warningMapper.selectPage(page, wrapper);
    }

    @Override
    public List<WarningRecord> getUnhandledHighRisk() {
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<WarningRecord>()
                .eq(WarningRecord::getRiskLevel, 3)
                .eq(WarningRecord::getHandled, 0)
                .orderByDesc(WarningRecord::getCreatedAt);
        return warningMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleWarning(Long warningId, Long handlerId, String note) {
        if (!ValidatorUtil.isValidId(warningId)) {
            log.warn("handleWarning: 无效的预警ID {}", warningId);
            return false;
        }

        WarningRecord record = warningMapper.selectById(warningId);
        if (record != null) {
            record.setHandled(1);
            record.setHandlerId(handlerId);
            record.setHandlerNote(note);
            record.setHandledAt(LocalDateTime.now());
            
            boolean success = warningMapper.updateById(record) > 0;
            if (success) {
                log.info("预警已处理: warningId={}, handlerId={}", warningId, handlerId);
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean isHighRiskLevel(Integer riskLevel) {
        return riskLevel != null && riskLevel >= 3;
    }

    @Override
    public long countHighRisk() {
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<WarningRecord>()
                .eq(WarningRecord::getRiskLevel, 3);
        return warningMapper.selectCount(wrapper);
    }

    @Override
    public long countMediumRisk() {
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<WarningRecord>()
                .eq(WarningRecord::getRiskLevel, 2);
        return warningMapper.selectCount(wrapper);
    }

    @Override
    public long countUnhandled() {
        LambdaQueryWrapper<WarningRecord> wrapper = new LambdaQueryWrapper<WarningRecord>()
                .eq(WarningRecord::getHandled, 0);
        return warningMapper.selectCount(wrapper);
    }

    /**
     * 通知监护人
     */
    private void notifyGuardian(Long userId, WarningRecord record) {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                log.warn("用户不存在: {}", userId);
                return;
            }

            String guardianPhone = user.getGuardianPhone();
            String guardianEmail = user.getEmail();

            if (ValidatorUtil.isEmpty(guardianPhone) && ValidatorUtil.isEmpty(guardianEmail)) {
                log.debug("用户无监护人联系方式: {}", userId);
                return;
            }

            String content = record.getContent();
            if (content != null && content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }

            notificationService.sendWarningNotification(
                    userId,
                    guardianPhone,
                    guardianEmail,
                    ValidatorUtil.isNotEmpty(user.getNickname()) ? user.getNickname() : user.getUsername(),
                    record.getRiskLevel(),
                    content
            );
            log.info("已通知监护人: userId={}", userId);
        } catch (Exception e) {
            log.error("通知监护人失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 检查内容是否包含高风险关键词
     */
    private boolean isHighRisk(String content) {
        String lower = content.toLowerCase();
        return lower.contains("自杀") || lower.contains("轻生") || lower.contains("不想活") ||
               lower.contains("suicide") || lower.contains("kill myself") || lower.contains("want to die");
    }

    /**
     * 检查内容是否包含中风险关键词
     */
    private boolean isMediumRisk(String content) {
        String lower = content.toLowerCase();
        return lower.contains("绝望") || lower.contains("无助") || lower.contains("崩溃") ||
               lower.contains("抑郁") || lower.contains("痛苦") || lower.contains("worthless");
    }

    /**
     * 截断内容
     */
    private String truncateContent(String content) {
        if (content != null && content.length() > 500) {
            return content.substring(0, 500);
        }
        return content;
    }

}

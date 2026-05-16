package com.mindrealm.warning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.warning.entity.WarningRecord;

import java.util.List;

/**
 * @className: WarningService
 * @description: 预警服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface WarningService {

    /**
     * 分析用户内容风险等级
     * @param userId 用户ID
     * @param content 待分析内容
     * @return 预警记录对象(风险等级>1时),无需预警返回null
     */
    WarningRecord analyzeRisk(Long userId, String content);

    /**
     * 获取用户的预警记录列表(带分页)
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<WarningRecord> getUserWarnings(Long userId, int pageNum, int pageSize);

    /**
     * 获取预警列表(管理端)
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @param handled 处理状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<WarningRecord> getWarningList(Long userId, Integer riskLevel, Integer handled, 
                                        int pageNum, int pageSize);

    /**
     * 获取所有未处理的高风险预警
     * @return 高风险预警列表
     */
    List<WarningRecord> getUnhandledHighRisk();

    /**
     * 处理预警记录
     * @param warningId 预警ID
     * @param handlerId 处理人ID
     * @param note 处理备注
     * @return 处理成功返回true
     */
    boolean handleWarning(Long warningId, Long handlerId, String note);

    /**
     * 判断风险等级是否为高风险
     * @param riskLevel 风险等级
     * @return 是否高风险
     */
    boolean isHighRiskLevel(Integer riskLevel);

    /**
     * 统计高风险预警数量
     * @return 数量
     */
    long countHighRisk();

    /**
     * 统计中风险预警数量
     * @return 数量
     */
    long countMediumRisk();

    /**
     * 统计未处理预警数量
     * @return 数量
     */
    long countUnhandled();
}

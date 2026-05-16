package com.mindrealm.diary.service;

import com.mindrealm.diary.entity.EmotionReport;

import java.time.LocalDate;
import java.util.List;

/**
 * @className: EmotionReportService
 * @description: 情绪报告服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
public interface EmotionReportService {

    /**
     * 生成并保存情绪报告
     * @param userId 用户ID
     * @param reportType 报告类型: week/month/quarter
     * @return 生成的报告
     */
    EmotionReport generateAndSaveReport(Long userId, String reportType);

    /**
     * 查询用户的历史报告列表
     * @param userId 用户ID
     * @return 报告列表
     */
    List<EmotionReport> getUserReports(Long userId);

    /**
     * 查询用户的指定类型报告列表
     * @param userId 用户ID
     * @param reportType 报告类型: week/month/quarter
     * @return 报告列表
     */
    List<EmotionReport> getUserReportsByType(Long userId, String reportType);

    /**
     * 根据ID查询报告详情
     * @param reportId 报告ID
     * @return 报告详情
     */
    EmotionReport getReportById(Long reportId);

    /**
     * 查询用户在指定时间范围内的最新报告
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param reportType 报告类型
     * @return 报告(如果存在)
     */
    EmotionReport getLatestReportInPeriod(Long userId, LocalDate startDate, LocalDate endDate, String reportType);
}

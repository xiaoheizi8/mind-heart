package com.mindrealm.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.LoginLog;

import java.util.List;

/**
 * @className: LoginLogService
 * @description: 登录日志服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface LoginLogService {

    /**
     * 记录登录成功日志
     * @param userId 用户ID
     * @param username 用户名
     * @param loginType 登录类型(password/code)
     */
    void logSuccess(Long userId, String username, String loginType);

    /**
     * 记录登录失败日志
     * @param username 用户名
     * @param loginType 登录类型
     * @param failReason 失败原因
     */
    void logFail(String username, String loginType, String failReason);

    /**
     * 查询用户登录日志
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<LoginLog> findByUserId(Long userId, int pageNum, int pageSize);

    /**
     * 查询最近登录日志
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 日志列表
     */
    List<LoginLog> findRecentByUserId(Long userId, int limit);

    /**
     * 统计用户登录次数
     * @param userId 用户ID
     * @return 登录次数
     */
    long countByUserId(Long userId);

    /**
     * 统计今日登录次数
     * @return 今日登录次数
     */
    long countTodayLogin();
}

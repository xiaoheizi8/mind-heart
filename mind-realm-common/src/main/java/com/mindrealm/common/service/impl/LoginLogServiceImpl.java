package com.mindrealm.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.LoginLog;
import com.mindrealm.common.mapper.LoginLogMapper;
import com.mindrealm.common.service.LoginLogService;
import com.mindrealm.common.util.IpLocationUtil;
import com.mindrealm.common.util.ValidatorUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @className: LoginLogServiceImpl
 * @description: 登录日志服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {

    private static final Logger log = LoggerFactory.getLogger(LoginLogServiceImpl.class);

    private final LoginLogMapper loginLogMapper;

    @Autowired
    public LoginLogServiceImpl(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public void logSuccess(Long userId, String username, String loginType) {
        if (ValidatorUtil.isEmpty(userId) || ValidatorUtil.isEmpty(username)) {
            log.warn("logSuccess: 参数无效 userId={}, username={}", userId, username);
            return;
        }
        
        LoginLog loginLog = buildLoginLog(userId, username, loginType, 1, null);
        saveLog(loginLog);
        log.info("用户登录成功: userId={}, username={}, ip={}", userId, username, loginLog.getIp());
    }

    @Override
    public void logFail(String username, String loginType, String failReason) {
        if (ValidatorUtil.isEmpty(username)) {
            log.warn("logFail: 用户名为空");
            return;
        }
        
        LoginLog loginLog = buildLoginLog(null, username, loginType, 0, failReason);
        saveLog(loginLog);
        log.warn("用户登录失败: username={}, reason={}, ip={}", username, failReason, loginLog.getIp());
    }

    @Override
    public Page<LoginLog> findByUserId(Long userId, int pageNum, int pageSize) {
        if (!ValidatorUtil.isValidId(userId)) {
            return new Page<>(1, 10);
        }
        
        pageNum = ValidatorUtil.validatePageNum(pageNum);
        pageSize = ValidatorUtil.validatePageSize(pageSize);
        
        Page<LoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .eq(LoginLog::getUserId, userId)
                .orderByDesc(LoginLog::getCreatedAt);
        
        return loginLogMapper.selectPage(page, wrapper);
    }

    @Override
    public List<LoginLog> findRecentByUserId(Long userId, int limit) {
        if (!ValidatorUtil.isValidId(userId)) {
            return Collections.emptyList();
        }
        
        limit = Math.max(1, Math.min(limit, 50));
        
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .eq(LoginLog::getUserId, userId)
                .eq(LoginLog::getStatus, 1)
                .orderByDesc(LoginLog::getCreatedAt)
                .last("LIMIT " + limit);
        
        return loginLogMapper.selectList(wrapper);
    }

    @Override
    public long countByUserId(Long userId) {
        if (!ValidatorUtil.isValidId(userId)) {
            return 0;
        }
        
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .eq(LoginLog::getUserId, userId)
                .eq(LoginLog::getStatus, 1);
        
        return loginLogMapper.selectCount(wrapper);
    }

    @Override
    public long countTodayLogin() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .eq(LoginLog::getStatus, 1)
                .ge(LoginLog::getCreatedAt, todayStart);
        
        return loginLogMapper.selectCount(wrapper);
    }

    /**
     * 构建登录日志对象
     */
    private LoginLog buildLoginLog(Long userId, String username, String loginType,
                                    Integer status, String failReason) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setUsername(username != null ? username.trim() : null);
        loginLog.setLoginType(loginType != null ? loginType : "password");
        loginLog.setStatus(status);
        loginLog.setFailReason(failReason);
        loginLog.setCreatedAt(LocalDateTime.now());

        // 获取请求信息
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                loginLog.setIp(getClientIp(request));
                loginLog.setIpLocation(IpLocationUtil.getLocation(loginLog.getIp()));
                loginLog.setDeviceType(getDeviceType(request));
                loginLog.setDeviceInfo(getDeviceInfo(request));
            }
        } catch (Exception e) {
            log.debug("获取请求信息失败: {}", e.getMessage());
        }

        return loginLog;
    }

    /**
     * 保存登录日志
     */
    private void saveLog(LoginLog loginLog) {
        try {
            loginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("保存登录日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }

    /**
     * 获取设备类型
     */
    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        }
        return "PC";
    }

    /**
     * 获取设备信息
     */
    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        // 截断过长的设备信息
        if (userAgent.length() > 200) {
            userAgent = userAgent.substring(0, 200);
        }
        return userAgent;
    }
}

package com.mindrealm.common.service;

import java.util.List;
import java.util.Map;

/**
 * @className: SysMonitorService
 * @description: 系统监控服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface SysMonitorService {
    
    /**
     * 获取系统概览
     * @return 系统概览数据
     */
    Map<String, Object> getSystemOverview();
    
    /**
     * 获取Redis信息
     * @return Redis信息
     */
    Map<String, Object> getRedisInfo();
    
    /**
     * 获取MySQL信息
     * @return MySQL信息
     */
    Map<String, Object> getMysqlInfo();
    
    /**
     * 获取JVM信息
     * @return JVM信息
     */
    Map<String, Object> getJvmInfo();
    
    /**
     * 获取服务器信息
     * @return 服务器信息
     */
    Map<String, Object> getServerInfo();
    
    /**
     * 获取接口响应时间统计
     * @return 接口性能数据
     */
    Map<String, Object> getApiPerformance();
}

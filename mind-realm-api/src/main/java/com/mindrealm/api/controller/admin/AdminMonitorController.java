package com.mindrealm.api.controller.admin;

import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.SysMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @className: AdminMonitorController
 * @description: 管理端系统监控控制器
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@RestController
@RequestMapping("/admin/v1/monitor")
public class AdminMonitorController {

    @Autowired
    private SysMonitorService monitorService;

    /**
     * 获取系统概览信息(CPU、内存、磁盘、运行时间等)
     * @return 系统资源使用情况统计
     */
    @GetMapping("/overview")
    @OperationLog(value = "获取系统概览", type = OperationType.SELECT)
    public Result<Map<String, Object>> getSystemOverview() {
        return Result.success(monitorService.getSystemOverview());
    }

    /**
     * 获取Redis服务器信息(连接数、内存使用、命中率等)
     * @return Redis运行状态数据
     */
    @GetMapping("/redis")
    @OperationLog(value = "获取Redis信息", type = OperationType.SELECT)
    public Result<Map<String, Object>> getRedisInfo() {
        return Result.success(monitorService.getRedisInfo());
    }

    /**
     * 获取MySQL数据库信息(连接数、查询统计、版本等)
     * @return MySQL运行状态数据
     */
    @GetMapping("/mysql")
    @OperationLog(value = "获取MySQL信息", type = OperationType.SELECT)
    public Result<Map<String, Object>> getMysqlInfo() {
        return Result.success(monitorService.getMysqlInfo());
    }

    /**
     * 获取JVM虚拟机信息(堆内存、线程数、GC情况等)
     * @return JVM运行状态数据
     */
    @GetMapping("/jvm")
    @OperationLog(value = "获取JVM信息", type = OperationType.SELECT)
    public Result<Map<String, Object>> getJvmInfo() {
        return Result.success(monitorService.getJvmInfo());
    }

    /**
     * 获取服务器硬件信息(OS、CPU架构、系统负载等)
     * @return 服务器环境数据
     */
    @GetMapping("/server")
    @OperationLog(value = "获取服务器信息", type = OperationType.SELECT)
    public Result<Map<String, Object>> getServerInfo() {
        return Result.success(monitorService.getServerInfo());
    }

    /**
     * 获取API接口性能统计(响应时间、调用次数、慢查询等)
     * @return 接口性能分析数据
     */
    @GetMapping("/api-performance")
    @OperationLog(value = "获取接口性能", type = OperationType.SELECT)
    public Result<Map<String, Object>> getApiPerformance() {
        return Result.success(monitorService.getApiPerformance());
    }
}

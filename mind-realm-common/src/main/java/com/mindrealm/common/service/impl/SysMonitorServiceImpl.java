package com.mindrealm.common.service.impl;

import com.mindrealm.common.service.SysMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @className: SysMonitorServiceImpl
 * @description: 系统监控服务实现类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class SysMonitorServiceImpl implements SysMonitorService {

    private static final Logger log = LoggerFactory.getLogger(SysMonitorServiceImpl.class);

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // JVM信息
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1024 / 1024;
            long totalMemory = runtime.totalMemory() / 1024 / 1024;
            long freeMemory = runtime.freeMemory() / 1024 / 1024;
            long usedMemory = totalMemory - freeMemory;
            
            overview.put("jvmMaxMemory", maxMemory + "MB");
            overview.put("jvmUsedMemory", usedMemory + "MB");
            overview.put("jvmFreeMemory", freeMemory + "MB");
            overview.put("jvmUsagePercent", Math.round((double) usedMemory / maxMemory * 100));
            
            // 服务器信息
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            overview.put("cpuCores", osBean.getAvailableProcessors());
            overview.put("systemLoad", osBean.getSystemLoadAverage());
            
            // 磁盘信息
            File root = new File("/");
            long diskTotal = root.getTotalSpace() / 1024 / 1024 / 1024;
            long diskFree = root.getFreeSpace() / 1024 / 1024 / 1024;
            long diskUsed = diskTotal - diskFree;
            
            overview.put("diskTotal", diskTotal + "GB");
            overview.put("diskUsed", diskUsed + "GB");
            overview.put("diskFree", diskFree + "GB");
            overview.put("diskUsagePercent", diskTotal > 0 ? Math.round((double) diskUsed / diskTotal * 100) : 0);
            
            // 运行时间
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
            long days = uptime / 86400;
            long hours = (uptime % 86400) / 3600;
            long minutes = (uptime % 3600) / 60;
            overview.put("uptime", String.format("%d天%d小时%d分钟", days, hours, minutes));
            
        } catch (Exception e) {
            log.error("获取系统概览信息失败", e);
        }
        
        return overview;
    }

    @Override
    public Map<String, Object> getRedisInfo() {
        Map<String, Object> info = new HashMap<>();
        
        if (redisTemplate == null) {
            info.put("status", "未配置");
            return info;
        }
        
        try {
            Properties redisProps = redisTemplate.getConnectionFactory()
                    .getConnection().info();
            
            info.put("status", "正常");
            info.put("version", redisProps.getProperty("redis_version"));
            info.put("usedMemory", redisProps.getProperty("used_memory_human"));
            info.put("usedMemoryPeak", redisProps.getProperty("used_memory_peak_human"));
            info.put("connectedClients", redisProps.getProperty("connected_clients"));
            info.put("totalCommands", redisProps.getProperty("total_commands_processed"));
            info.put("totalConnections", redisProps.getProperty("total_connections_received"));
            info.put("uptimeDays", redisProps.getProperty("uptime_in_days"));
            info.put("keyspaceHits", redisProps.getProperty("keyspace_hits"));
            info.put("keyspaceMisses", redisProps.getProperty("keyspace_misses"));
            
            // 计算命中率
            String hits = redisProps.getProperty("keyspace_hits");
            String misses = redisProps.getProperty("keyspace_misses");
            if (hits != null && misses != null) {
                long hitCount = Long.parseLong(hits);
                long missCount = Long.parseLong(misses);
                long total = hitCount + missCount;
                info.put("hitRate", total > 0 ? String.format("%.2f%%", (double) hitCount / total * 100) : "0%");
            }
            
        } catch (Exception e) {
            log.error("获取Redis信息失败", e);
            info.put("status", "异常");
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    @Override
    public Map<String, Object> getMysqlInfo() {
        Map<String, Object> info = new HashMap<>();
        
        if (jdbcTemplate == null) {
            info.put("status", "未配置");
            return info;
        }
        
        try {
            // 数据库大小
            String dbSize = jdbcTemplate.queryForObject(
                "SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS size_mb " +
                "FROM information_schema.tables WHERE table_schema = DATABASE()",
                String.class
            );
            info.put("databaseSize", dbSize != null ? dbSize + "MB" : "未知");
            
            // 当前连接数
            String connections = jdbcTemplate.queryForObject(
                "SHOW STATUS LIKE 'Threads_connected'",
                (rs, rowNum) -> rs.getString("Value")
            );
            info.put("connections", connections);
            
            // 总连接数
            String totalConnections = jdbcTemplate.queryForObject(
                "SHOW STATUS LIKE 'Threads_created'",
                (rs, rowNum) -> rs.getString("Value")
            );
            info.put("totalConnections", totalConnections);
            
            // 查询次数
            String queries = jdbcTemplate.queryForObject(
                "SHOW STATUS LIKE 'Queries'",
                (rs, rowNum) -> rs.getString("Value")
            );
            info.put("queries", queries);
            
            // 表数量
            String tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()",
                String.class
            );
            info.put("tableCount", tableCount);
            
            info.put("status", "正常");
            
        } catch (Exception e) {
            log.error("获取MySQL信息失败", e);
            info.put("status", "异常");
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    @Override
    public Map<String, Object> getJvmInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            Runtime runtime = Runtime.getRuntime();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            
            // 堆内存
            long heapMax = memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024;
            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
            long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted() / 1024 / 1024;
            
            info.put("heapMax", heapMax + "MB");
            info.put("heapUsed", heapUsed + "MB");
            info.put("heapCommitted", heapCommitted + "MB");
            info.put("heapUsagePercent", heapMax > 0 ? Math.round((double) heapUsed / heapMax * 100) : 0);
            
            // 非堆内存
            long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024;
            info.put("nonHeapUsed", nonHeapUsed + "MB");
            
            // 线程信息
            info.put("threadCount", threadBean.getThreadCount());
            info.put("peakThreadCount", threadBean.getPeakThreadCount());
            info.put("daemonThreadCount", threadBean.getDaemonThreadCount());
            
            // 类加载信息
            info.put("loadedClassCount", ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
            info.put("totalLoadedClassCount", ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount());
            info.put("unloadedClassCount", ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount());
            
            // GC信息
            info.put("gcCount", "可通过JMX获取");
            
        } catch (Exception e) {
            log.error("获取JVM信息失败", e);
        }
        
        return info;
    }

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            // CPU信息
            info.put("cpuCores", osBean.getAvailableProcessors());
            info.put("systemLoadAverage", osBean.getSystemLoadAverage());
            
            // 磁盘信息
            File root = new File("/");
            info.put("diskTotal", root.getTotalSpace() / 1024 / 1024 / 1024 + "GB");
            info.put("diskUsed", (root.getTotalSpace() - root.getFreeSpace()) / 1024 / 1024 / 1024 + "GB");
            info.put("diskFree", root.getFreeSpace() / 1024 / 1024 / 1024 + "GB");
            
            // Java信息
            info.put("javaVersion", System.getProperty("java.version"));
            info.put("javaVendor", System.getProperty("java.vendor"));
            info.put("osName", System.getProperty("os.name"));
            info.put("osVersion", System.getProperty("os.version"));
            info.put("osArch", System.getProperty("os.arch"));
            info.put("userDir", System.getProperty("user.dir"));
            
        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
        }
        
        return info;
    }

    @Override
    public Map<String, Object> getApiPerformance() {
        Map<String, Object> info = new HashMap<>();
        
        // 这里可以从日志系统或APM工具获取接口性能数据
        // 暂时返回示例数据
        info.put("message", "接口性能监控需要集成APM工具（如SkyWalking、Pinpoint）");
        info.put("suggestion", "建议配置Spring Boot Actuator + Micrometer进行接口监控");
        
        return info;
    }
}

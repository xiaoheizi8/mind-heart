package com.mindrealm.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @className: AsyncConfig
 * @description: 异步任务线程池配置
 *               为 AI 聊天、文件处理等非 Kafka 的异步操作提供线程池
 *               ES 同步已迁移至 Kafka，不再使用 @Async
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * 通用异步任务线程池
     * - 核心线程数: 5
     * - 最大线程数: 10
     * - 队列容量: 100
     * - 拒绝策略: CallerRunsPolicy（任务过多时由调用线程执行，防止丢失）
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("async-task-");
        // CallerRunsPolicy: 线程池满时由主线程执行，既防止任务丢失，又能自然限流
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("异步任务线程池已初始化: corePoolSize=5, maxPoolSize=10, queueCapacity=100, " +
                "rejectedPolicy=CallerRunsPolicy");
        return executor;
    }
}

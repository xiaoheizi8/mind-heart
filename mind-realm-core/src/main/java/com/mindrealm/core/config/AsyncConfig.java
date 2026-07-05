package com.mindrealm.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @className: AsyncConfig
 * @description: 启用Spring异步支持，用于ES同步等非阻塞操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.5
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

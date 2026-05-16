package com.mindrealm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @className: MindRealmApplication
 * @description: 心域应用启动类
 * @author: 一朝风月
 * @code: 面向自己, 面向未来
 * @createTime: 2026.4.2
 */
@SpringBootApplication(scanBasePackages = "com.mindrealm")
@MapperScan("com.mindrealm.**.mapper")
@EnableScheduling

public class MindRealmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MindRealmApplication.class, args);
    }
}

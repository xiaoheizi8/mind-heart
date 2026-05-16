package com.mindrealm.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: SecurityProperties
 * @description: 安全配置属性类,从配置文件读取安全相关配置
 *               包括:频率限制、防爬虫、防重复提交、IP控制、签名验证等
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Data
@Component
@ConfigurationProperties(prefix = "mind-realm.security")
public class SecurityProperties {

    private boolean enabled = true;
    
    private RateLimit rateLimit = new RateLimit();
    
    private AntiCrawler antiCrawler = new AntiCrawler();
    
    private RepeatSubmit repeatSubmit = new RepeatSubmit();
    
    private IpControl ipControl = new IpControl();
    
    private SignVerify signVerify = new SignVerify();
    
    @Data
    public static class RateLimit {
        private boolean enabled = true;
        private int defaultLimit = 100;
        private int defaultSeconds = 60;
        private int authLimit = 5;
        private int authSeconds = 300;
        private int smsLimit = 3;
        private int smsSeconds = 3600;
        private int loginLimit = 10;
        private int loginSeconds = 600;
    }
    
    @Data
    public static class AntiCrawler {
        private boolean enabled = true;
        private List<String> blockedUserAgents = new ArrayList<>();
        private List<String> allowedUserAgents = new ArrayList<>();
        private int minUserAgentLength = 10;
        private boolean blockEmptyUserAgent = true;
    }
    
    @Data
    public static class RepeatSubmit {
        private boolean enabled = true;
        private int tokenExpireSeconds = 300;
        private boolean checkRequestBody = true;
    }
    
    @Data
    public static class IpControl {
        private boolean enabled = false;
        private List<String> whiteList = new ArrayList<>();
        private List<String> blackList = new ArrayList<>();
        private boolean blockProxyHeaders = false;
    }
    
    @Data
    public static class SignVerify {
        private boolean enabled = false;
        private String secretKey = "mind-realm-sign-key";
        private int timestampLimit = 300;
    }
}

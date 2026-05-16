package com.mindrealm.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.api.config.SecurityProperties;
import com.mindrealm.common.result.Result;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @className: AntiCrawlerFilter
 * @description: 防爬虫过滤器,通过检测User-Agent、请求频率等特征识别并拦截恶意爬虫
 *               支持自定义拦截规则和白名单配置
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Component
public class AntiCrawlerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AntiCrawlerFilter.class);
    
    private static final List<String> COMMON_CRAWLER_PATTERNS = Arrays.asList(
            "crawl", "spider", "bot", "scraper", "curl", "wget", "python", 
            "java/", "okhttp", "httpclient", "apache-http", "libwww", 
            "browser", "phantomjs", "selenium", "puppeteer", "playwright"
    );
    
    private static final List<String> SUSPICIOUS_USER_AGENTS = Arrays.asList(
            "Mozilla/5.0", "Mozilla/4.0", "Mozilla", ""
    );

    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    public AntiCrawlerFilter(SecurityProperties securityProperties, ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!securityProperties.getAntiCrawler().isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestUri = httpRequest.getRequestURI();
        
        // 放行Swagger/Knife4j文档路径
        if (requestUri.startsWith("/swagger-ui") || 
            requestUri.startsWith("/v3/api-docs") ||
            requestUri.startsWith("/swagger-resources") ||
            requestUri.startsWith("/webjars") ||
            requestUri.equals("/swagger-ui.html") ||
            requestUri.equals("/doc.html") ||
            requestUri.startsWith("/api/doc.html")) {
            chain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (isBlockedIp(clientIp)) {
            logger.warn("Blocked IP access attempt: ip={}, uri={}", clientIp, requestUri);
            writeResponse(httpResponse, Result.forbidden("访问被拒绝"));
            return;
        }

        if (isSuspiciousRequest(httpRequest)) {
            logger.warn("Suspicious request detected: ip={}, uri={}, userAgent={}", 
                    clientIp, requestUri, userAgent);
            writeResponse(httpResponse, Result.forbidden("请求异常"));
            return;
        }

        if (!isValidUserAgent(userAgent)) {
            logger.warn("Invalid User-Agent: ip={}, userAgent={}", clientIp, userAgent);
            writeResponse(httpResponse, Result.forbidden("User-Agent无效"));
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isBlockedIp(String ip) {
        List<String> blackList = securityProperties.getIpControl().getBlackList();
        return blackList != null && blackList.contains(ip);
    }

    private boolean isSuspiciousRequest(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String accept = request.getHeader("Accept");
        
        if (referer == null && accept == null) {
            return true;
        }
        
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            String lowerUa = userAgent.toLowerCase();
            for (String pattern : COMMON_CRAWLER_PATTERNS) {
                if (lowerUa.contains(pattern) && !lowerUa.contains("chrome") && !lowerUa.contains("firefox")) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private boolean isValidUserAgent(String userAgent) {
        SecurityProperties.AntiCrawler config = securityProperties.getAntiCrawler();
        
        if (config.isBlockEmptyUserAgent() && (userAgent == null || userAgent.trim().isEmpty())) {
            return false;
        }
        
        if (userAgent != null && userAgent.length() < config.getMinUserAgentLength()) {
            return false;
        }
        
        if (config.getBlockedUserAgents() != null) {
            for (String blocked : config.getBlockedUserAgents()) {
                if (userAgent != null && userAgent.toLowerCase().contains(blocked.toLowerCase())) {
                    return false;
                }
            }
        }
        
        if (config.getAllowedUserAgents() != null && !config.getAllowedUserAgents().isEmpty()) {
            boolean userAgentAllowed = false;
            for (String allowedPattern : config.getAllowedUserAgents()) {
                if (userAgent != null && userAgent.contains(allowedPattern)) {
                    userAgentAllowed = true;
                    break;
                }
            }
            if (!userAgentAllowed) {
                return false;
            }
        }
        
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    private void writeResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

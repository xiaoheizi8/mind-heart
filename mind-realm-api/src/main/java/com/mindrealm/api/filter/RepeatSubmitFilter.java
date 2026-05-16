package com.mindrealm.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.api.config.SecurityProperties;
import com.mindrealm.common.result.Result;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @className: RepeatSubmitFilter
 * @description: 防重复提交过滤器,通过Token机制防止用户短时间内重复提交相同请求
 *               使用Redis存储Token,支持自定义过期时间
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Component
public class RepeatSubmitFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RepeatSubmitFilter.class);
    
    public static final String SUBMIT_TOKEN_HEADER = "X-Submit-Token";
    public static final String SUBMIT_TOKEN_PARAM = "submitToken";

    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RepeatSubmitFilter(SecurityProperties securityProperties, 
                              RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!securityProperties.getRepeatSubmit().isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isRepeatSubmitRequest(httpRequest)) {
            logger.warn("Repeat submit detected: ip={}, uri={}", 
                    getClientIp(httpRequest), httpRequest.getRequestURI());
            writeResponse(httpResponse, Result.badRequest("请勿重复提交"));
            return;
        }

        String token = generateSubmitToken(httpRequest);
        httpResponse.setHeader(SUBMIT_TOKEN_HEADER, token);

        RepeatSubmitRequestWrapper wrappedRequest = new RepeatSubmitRequestWrapper(httpRequest, token);
        chain.doFilter(wrappedRequest, response);
    }

    private boolean isRepeatSubmitRequest(HttpServletRequest request) {
        String token = getSubmitToken(request);
        if (token == null) {
            return false;
        }

        String clientIp = getClientIp(request);
        String userId = getUserId(request);
        String requestBody = securityProperties.getRepeatSubmit().isCheckRequestBody() 
                ? getRequestBody(request) : "";
        String key = buildRepeatKey(token, clientIp, userId, requestBody);

        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return true;
        }

        int expireSeconds = securityProperties.getRepeatSubmit().getTokenExpireSeconds();
        redisTemplate.opsForValue().set(key, "1", expireSeconds, TimeUnit.SECONDS);
        
        return false;
    }

    private String getSubmitToken(HttpServletRequest request) {
        String token = request.getHeader(SUBMIT_TOKEN_HEADER);
        if (token == null) {
            token = request.getParameter(SUBMIT_TOKEN_PARAM);
        }
        return token;
    }

    private String generateSubmitToken(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        return UUID.nameUUIDFromBytes((clientIp + request.getRequestURI() + System.currentTimeMillis()).getBytes())
                .toString();
    }

    private String buildRepeatKey(String token, String ip, String userId, String body) {
        return String.format("repeat_submit:%s:%s:%s:%s", token, ip, userId, 
                body != null ? String.valueOf(body.hashCode()) : "0");
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR", "HTTP_CLIENT_IP", "REMOTE_ADDR"
        };
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private String getUserId(HttpServletRequest request) {
        try {
            Object userId = request.getAttribute("userId");
            return userId != null ? userId.toString() : "anonymous";
        } catch (Exception e) {
            return "anonymous";
        }
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            CachedBodyHttpServletRequestWrapper wrapper = 
                    request instanceof CachedBodyHttpServletRequestWrapper 
                    ? (CachedBodyHttpServletRequestWrapper) request : null;
            if (wrapper != null) {
                return new String(wrapper.getCachedBody());
            }
        } catch (Exception e) {
            logger.debug("Failed to read request body", e);
        }
        return "";
    }

    private void writeResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

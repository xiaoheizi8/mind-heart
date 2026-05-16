package com.mindrealm.api.config;

import com.mindrealm.api.config.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
                Long userId = jwtUtils.getUserId(jwt);
                String username = jwtUtils.getUsername(jwt);
                Integer role = jwtUtils.getRole(jwt);
                
                // 设置认证信息
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + getRoleName(role))
                );
                
                // 使用UserPrincipal存储用户ID和用户名
                UserPrincipal principal = new UserPrincipal(userId, username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("用户 {} (ID:{}) 认证成功, 角色: {}", username, userId, role);
            }
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 获取角色名称
     */
    private String getRoleName(Integer role) {
        if (role == null) return "USER";
        return switch (role) {
            case 1 -> "USER";
            case 2 -> "COUNSELOR";
            case 3 -> "ADMIN";
            default -> "USER";
        };
    }
    
    /**
     * 用户主体类，存储用户ID和用户名
     */
    public static class UserPrincipal {
        private final Long userId;
        private final String username;
        
        public UserPrincipal(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        @Override
        public String toString() {
            return username + "(" + userId + ")";
        }
    }
}
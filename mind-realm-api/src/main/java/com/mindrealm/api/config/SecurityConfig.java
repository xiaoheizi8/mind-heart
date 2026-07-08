package com.mindrealm.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.api.filter.AntiCrawlerFilter;
import com.mindrealm.api.filter.RepeatSubmitFilter;
import com.mindrealm.common.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @className: SecurityConfig
 * @description: Spring Security安全配置类,配置JWT认证、CORS跨域、请求授权等安全策略
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;
    private final AntiCrawlerFilter antiCrawlerFilter;
    private final RepeatSubmitFilter repeatSubmitFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                          ObjectMapper objectMapper,
                          AntiCrawlerFilter antiCrawlerFilter,
                          RepeatSubmitFilter repeatSubmitFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
        this.antiCrawlerFilter = antiCrawlerFilter;
        this.repeatSubmitFilter = repeatSubmitFilter;
    }

    /**
     * @description: 配置密码编码器,使用BCrypt加密
     * @return: PasswordEncoder BCryptPasswordEncoder实例
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @description: 获取AuthenticationManager用于认证
     * @param: config AuthenticationConfiguration配置
     * @return: AuthenticationManager 认证管理器
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * @description: 配置安全过滤器链,包括JWT认证、CORS、请求授权等
     * @param: http HttpSecurity配置对象
     * @return: SecurityFilterChain 安全过滤器链
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 为SSE请求配置特殊的请求缓存,防止异步请求的Security上下文丢失
            .requestCache(cache -> cache.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/admin/v1/auth/**",
                    "/api/v1/knowledge/qa",
                    "/api/v1/knowledge/info",
                    "/uploads/**",
                    "/error",
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/api/doc.html",
                    "/doc.html",
                    "/doc.html/**",
                    // SSE流式对话接口 - 在Controller层进行认证,避免异步分派的Security问题
                    "/api/v1/chat/stream"

                ).permitAll()
                .requestMatchers("/admin/v1/user/**", "/admin/v1/role/**", "/admin/v1/menu/**", "/admin/v1/monitor/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/v1/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_COUNSELOR")
                // 家长端接口 - 仅家长角色可访问
                .requestMatchers("/api/v1/parent/**").hasAuthority("ROLE_PARENT")
                // 用户端接口需要认证(包括SSE流式对话)
                .requestMatchers("/api/v1/chat/**", "/api/v1/user/**", "/api/v1/diary/**", "/api/v1/emotion/**",
                                 "/api/v1/warning/**", "/api/v1/knowledge/**", "/api/v1/notification/**", "/api/v1/file/**",
                                 "/api/v1/story/**", "/api/v1/feedback/**", "/api/v1/upload/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(antiCrawlerFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(repeatSubmitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    try {
                        // 如果响应已经提交,不再写入,避免异常(特别是SSE请求)
                        if (response.isCommitted()) {
                            log.debug("响应已提交,跳过401错误写入 [URI: {}]", request.getRequestURI());
                            return;
                        }
                        Result<?> result = Result.<String>unauthorized();
                        result.setMessage("请先登录");
                        writeJsonResponse(response, result, 401);
                    } catch (Exception e) {
                        log.error("写入401响应失败: {}", e.getMessage());
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    try {
                        // 如果响应已经提交,不再写入,避免异常(特别是SSE请求)
                        if (response.isCommitted()) {
                            log.debug("响应已提交,跳过403错误写入 [URI: {}]", request.getRequestURI());
                            return;
                        }
                        Result<?> result = Result.<String>forbidden("没有权限");
                        writeJsonResponse(response, result, 403);
                    } catch (Exception e) {
                        log.error("写入403响应失败: {}", e.getMessage());
                    }
                })
            );

        return http.build();
    }

    /**
     * @description: 配置CORS跨域资源共享,允许所有来源、方法和头
     * @return: CorsConfigurationSource CORS配置源
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Submit-Token"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * @description: 写入JSON响应到HttpServletResponse
     * @param: response HttpServletResponse响应对象
     * @param: result Result结果对象
     * @param: status HTTP状态码
     * @author: 一朝风月
     * @createTime: 2026.4.7
     */
    private void writeJsonResponse(HttpServletResponse response, Result<?> result, int status) throws Exception {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

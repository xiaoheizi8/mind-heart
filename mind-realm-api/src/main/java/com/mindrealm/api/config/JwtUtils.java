package com.mindrealm.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: JwtUtils
 * @description: JWT工具类,用于生成和解析JWT令牌,实现无状态认证
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@Component
public class JwtUtils {

    /**
     * JWT密钥,默认使用配置文件中的值
     */
    @Value("${jwt.secret:mind-realm-secret-key-2025}")
    private String secret;
    
    /**
     * 令牌过期时间(秒),默认24小时
     */
    @Value("${jwt.expiration:86400}")
    private Long expiration;

    /**
     * 获取签名密钥
     * @return HMAC-SHA密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * 包含用户ID、用户名、角色等信息
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return JWT令牌字符串
     */
    public String generateToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token 令牌字符串
     * @return Claims对象(包含payload数据)
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证令牌是否有效
     * 检查令牌是否过期
     * @param token 令牌字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取令牌中的用户ID
     * @param token 令牌字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 获取令牌中的用户名
     * @param token 令牌字符串
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取令牌中的角色
     * @param token 令牌字符串
     * @return 角色编码
     */
    public Integer getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }

    /**
     * 刷新令牌
     * @param token 旧令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        Integer role = claims.get("role", Integer.class);
        return generateToken(userId, username, role);
    }
}

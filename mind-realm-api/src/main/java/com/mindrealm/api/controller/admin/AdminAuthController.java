package com.mindrealm.api.controller.admin;

import com.mindrealm.api.config.JwtUtils;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.UserService;
import com.mindrealm.common.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 管理后台认证控制器
 */
@RestController
@RequestMapping("/admin/v1/auth")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 管理员登录
     * 仅允许 role >= 2 (咨询师/管理员) 的用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.badRequest("用户名和密码不能为空");
        }

        username = username.trim();
        log.info("管理端登录请求: username={}", username);

        // 查找用户
        User user = userService.findByUsername(username);
        if (user == null && username.contains("@")) {
            user = userService.findByEmail(username);
        }
        if (user == null && username.matches("^1[3-9]\\d{9}$")) {
            user = userService.findByPhone(username);
        }

        if (user == null) {
            log.warn("用户不存在: {}", username);
            return Result.error(401, "用户名或密码错误");
        }

        // 密码验证
        String md5pwd = PasswordUtil.md5(password);
        if (!md5pwd.equals(user.getPassword())) {
            log.warn("密码错误: username={}", username);
            return Result.error(401, "用户名或密码错误");
        }

        // 权限验证 - 仅允许管理员和咨询师登录
        if (user.getRole() == null || user.getRole() < 2) {
            log.warn("权限不足: username={}, role={}", username, user.getRole());
            return Result.forbidden("无管理员权限");
        }

        // 账号状态检查
        if (user.getStatus() == 0) {
            return Result.forbidden("账号已被禁用");
        }

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        redisTemplate.opsForValue().set("admin:token:" + token, user.getId(), 24, TimeUnit.HOURS);

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());

        log.info("管理端登录成功: username={}, role={}", username, user.getRole());
        return Result.success(data);
    }

    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            redisTemplate.delete("admin:token:" + token);
        }
        return Result.ok("登出成功");
    }

    /**
     * 获取当前登录管理员信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info(@RequestHeader("Authorization") String token) {
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            return Result.unauthorized();
        }

        token = token.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return Result.unauthorized();
        }

        Long userId = jwtUtils.getUserId(token);
        User user = userService.findById(userId);

        if (user == null || user.getStatus() == 0) {
            return Result.unauthorized();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());

        return Result.success(data);
    }
}

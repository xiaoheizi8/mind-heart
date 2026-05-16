package com.mindrealm.api.controller;

import com.mindrealm.api.config.JwtUtils;
import com.mindrealm.api.dto.LoginRequest;
import com.mindrealm.api.dto.LoginResponse;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.impl.EmailService;
import com.mindrealm.common.service.LoginLogService;
import com.mindrealm.common.service.UserService;
import com.mindrealm.common.util.PasswordUtil;
import com.mindrealm.common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @className: AuthController
 * @description: 认证控制器,处理用户登录、注册、验证码等认证相关请求
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private LoginLogService loginLogService;

    /**
     * 用户名密码登录
     * 支持用户名或邮箱登录,密码MD5加密比对
     * @param request 登录请求(username/email + password)
     * @return 登录结果(token + userInfo)
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 参数校验
        if (!StringUtils.hasText(request.getPassword())) {
            return Result.badRequest("密码不能为空");
        }
        
        // 调试日志
        log.info("登录请求: username={}, email={}, password={}", 
                request.getUsername(), request.getEmail(), 
                request.getPassword() != null ? "***" : "null");
        
        // 去空格
        String username = request.getUsername() != null ? request.getUsername().trim() : null;
        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        
        // 调试日志 - 原始请求参数
        log.info("=== 登录请求 ===");
        log.info("原始请求: username={}, email={}, password长度={}", 
                request.getUsername(), request.getEmail(), 
                request.getPassword() != null ? request.getPassword().length() : 0);
        log.info("处理后: username={}, email={}", username, email);
        
        // 支持用户名、邮箱、手机号登录
        User user = null;
        
        // 优先用email字段登录
        if (StringUtils.hasText(email)) {
            log.info("尝试邮箱登录: {}", email);
            user = userService.findByEmail(email);
        }
        
        // 如果email没找到，尝试username字段（可能是用户名或手机号）
        if (user == null && StringUtils.hasText(username)) {
            // 判断是手机号还是用户名
            if (username.matches("^1[3-9]\\d{9}$")) {
                log.info("尝试手机号登录: {}", username);
                user = userService.findByPhone(username);
            } else {
                log.info("尝试用户名登录: {}", username);
                user = userService.findByUsername(username);
            }
        }
        
        // 如果username看起来像邮箱，也尝试用它登录
        if (user == null && StringUtils.hasText(username) && username.contains("@")) {
            log.info("尝试用username作为邮箱登录: {}", username);
            user = userService.findByEmail(username);
        }
        
        if (user == null) {
            log.info("用户不存在, username={}, email={}", username, email);
            loginLogService.logFail(username != null ? username : email, "password", "用户不存在");
            return Result.error(401, "用户名或密码错误");
        }
        
        log.info("找到用户: id={}, username={}, email={}, dbPassword={}", 
                user.getId(), user.getUsername(), user.getEmail(), 
                user.getPassword() != null ? user.getPassword().substring(0, Math.min(8, user.getPassword().length())) + "..." : "null");
        
        // 密码验证 (数据库密码是MD5加密的)
        String md5pwd = PasswordUtil.md5(request.getPassword());
        log.info("密码验证: 输入密码MD5={}, 数据库密码={}", md5pwd, user.getPassword());
        
        if (!md5pwd.equals(user.getPassword())) {
            log.warn("密码不匹配! 输入MD5={}, 数据库={}", md5pwd, user.getPassword());
            loginLogService.logFail(user.getUsername(), "password", "密码错误");
            return Result.error(401, "用户名或密码错误");
        }
        
        log.info("密码验证通过, 用户登录成功: {}", user.getUsername());
        
        // 账号状态检查
        if (user.getStatus() == 0) {
            loginLogService.logFail(user.getUsername(), "password", "账号已被禁用");
            return Result.forbidden("账号已被禁用");
        }
        
        loginLogService.logSuccess(user.getId(), user.getUsername(), "password");

        return generateLoginResult(user);
    }

    /**
     * 邮箱验证码登录
     * @param email 邮箱
     * @param code 验证码
     * @return 登录结果
     */
    @PostMapping("/loginByCode")
    public Result<LoginResponse> loginByCode(@RequestParam(required = false) String email,
                                             @RequestParam(required = false) String code,
                                             @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取，其次从请求参数获取
        if (email == null && body != null) {
            email = body.get("email");
        }
        if (code == null && body != null) {
            code = body.get("code");
        }
        // 参数校验
        if (!StringUtils.hasText(email)) {
            return Result.badRequest("邮箱不能为空");
        }
        if (!StringUtils.hasText(code)) {
            return Result.badRequest("验证码不能为空");
        }
        
        // 验证码校验
        if (!emailService.verifyCode(email, code)) {
            return Result.badRequest("验证码错误或已过期");
        }
        
        // 查询或创建用户
        User user = userService.findByEmail(email);
        if (user == null) {
            // 自动注册
            user = User.builder()
                    .email(email)
                    .username("user_" + System.currentTimeMillis())
                    .nickname("新用户")
                    .password(PasswordUtil.md5("123456"))
                    .status(1)
                    .role(1)
                    .createdAt(TimeUtil.now())
                    .build();
            userService.register(user);
        }
        
        if (user.getStatus() == 0) {
            loginLogService.logFail(email, "code", "账号已被禁用");
            return Result.forbidden("账号已被禁用");
        }
        
        loginLogService.logSuccess(user.getId(), user.getEmail(), "code");

        return generateLoginResult(user);
    }

    /**
     * 用户注册
     * 用户名和邮箱必须唯一
     * @param user 注册信息(username, password, email等)
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        // 参数校验
        if (!StringUtils.hasText(user.getUsername())) {
            return Result.badRequest("用户名不能为空");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            return Result.badRequest("密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            return Result.badRequest("密码长度不能少于6位");
        }
        
        // 用户名重复检查
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.badRequest("用户名已存在");
        }
        
        // 邮箱重复检查(如果提供)
        if (StringUtils.hasText(user.getEmail()) && userService.findByEmail(user.getEmail()) != null) {
            return Result.badRequest("邮箱已注册");
        }
        
        // 密码MD5加密(使用UTF-8编码)
        user.setPassword(PasswordUtil.md5(user.getPassword()));
        user.setStatus(1);
        user.setRole(1);
        user.setCreatedAt(TimeUtil.now());
        
        if (userService.register(user)) {
            log.info("用户注册成功: {}", user.getUsername());
            return Result.ok("注册成功");
        }
        
        return Result.fail("注册失败,请稍后重试");
    }

    /**
     * 发送邮箱验证码
     * 验证码存入Redis,5分钟有效
     * @param email 邮箱
     * @return 发送结果
     */
    @PostMapping("/sendCode")
    public Result<Void> sendCode(@RequestParam(required = false) String email,
                                  @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取，其次从请求参数获取
        if (email == null && body != null) {
            email = body.get("email");
        }
        
        // 邮箱格式校验
        if (!StringUtils.hasText(email)) {
            return Result.badRequest("邮箱不能为空");
        }
        
        email = email.trim();
        
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return Result.badRequest("邮箱格式不正确");
        }
        
        // 检查邮箱是否已注册(验证码只能发给未注册用户)
        if (userService.findByEmail(email) != null) {
            return Result.badRequest("该邮箱已注册");
        }
        
        // 发送频率限制(1分钟内只能发送一次)
        Boolean hasKey = redisTemplate.hasKey("email:limit:" + email);
        if (Boolean.TRUE.equals(hasKey)) {
            return Result.badRequest("发送过于频繁,请1分钟后重试");
        }
        
        // 发送验证码
        emailService.sendVerificationCode(email);
        
        // 存入Redis频率限制
        redisTemplate.opsForValue().set("email:limit:" + email, "1", 1, TimeUnit.MINUTES);
        
        log.info("邮箱验证码已发送: email={}", email);
        
        return Result.ok("验证码已发送到您的邮箱");
    }

    /**
     * 用户登出
     * 删除Redis中的令牌
     * @param token Authorization头中的Bearer令牌
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            redisTemplate.delete("token:" + token);
        }
        return Result.ok("登出成功");
    }

    /**
     * 刷新令牌
     * @param token 旧令牌
     * @return 新令牌
     */
    @PostMapping("/refreshToken")
    public Result<LoginResponse> refreshToken(@RequestHeader("Authorization") String token) {
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            return Result.unauthorized();
        }
        
        token = token.substring(7);
        
        // 验证旧令牌
        if (!jwtUtils.validateToken(token)) {
            return Result.unauthorized();
        }
        
        Long userId = jwtUtils.getUserId(token);
        User user = userService.findById(userId);
        
        if (user == null || user.getStatus() == 0) {
            return Result.unauthorized();
        }
        
        return generateLoginResult(user);
    }

    /**
     * 生成登录结果
     * @param user 用户对象
     * @return 登录响应
     */
    private Result<LoginResponse> generateLoginResult(User user) {
        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 令牌存入Redis,24小时过期
        redisTemplate.opsForValue().set("token:" + token, user.getId(), 24, TimeUnit.HOURS);
        
        LoginResponse response = new LoginResponse(token, user.getId(), user.getUsername(), 
                user.getNickname(), user.getRole());
        
        log.info("用户登录成功: {}", user.getUsername());
        
        return Result.success(response);
    }
}
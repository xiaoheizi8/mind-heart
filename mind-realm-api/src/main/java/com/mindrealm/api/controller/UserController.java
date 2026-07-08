package com.mindrealm.api.controller;

import com.mindrealm.api.service.ParentChildBindingService;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.ParentChildBinding;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.impl.EmailService;
import com.mindrealm.common.service.UserService;
import com.mindrealm.common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @className: UserController
 * @description: 用户控制器,处理用户信息查询、修改、密码修改等请求
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ParentChildBindingService parentChildBindingService;

    /**
     * 获取当前用户信息
     * @return 当前登录用户信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile() {
        try {
            Long userId = RequestContext.getCurrentUserId();
            log.info("获取用户信息, userId={}", userId);
            
            if (userId == null) {
                log.warn("用户未登录");
                return Result.unauthorized();
            }
            
            User user = userService.findById(userId);
            if (user == null) {
                log.warn("用户不存在, userId={}", userId);
                return Result.notFound("用户不存在");
            }
            user.setPassword(null);
            log.info("获取用户信息成功: {}", user.getUsername());
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            return Result.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * 支持更新昵称、头像、年龄、性别等基本信息
     * @param user 用户对象(需要包含userId)
     * @return 更新结果
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        if (user.getId() == null) {
            user.setId(currentUserId);
        }
        
        if (!currentUserId.equals(user.getId())) {
            return Result.forbidden("无权限修改他人信息");
        }
        
        User existingUser = userService.findById(currentUserId);
        if (existingUser == null) {
            return Result.notFound("用户不存在");
        }
        
        if (StringUtils.hasText(user.getNickname())) {
            existingUser.setNickname(user.getNickname());
        }
        if (StringUtils.hasText(user.getAvatar())) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getAge() != null) {
            existingUser.setAge(user.getAge());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        
        existingUser.setUpdatedAt(TimeUtil.now());
        
        log.info("更新用户信息: id={}, nickname={}, avatar={}", 
                existingUser.getId(), existingUser.getNickname(), existingUser.getAvatar());
        
        if (userService.updateUser(existingUser)) {
            userService.clearCache(currentUserId);
            log.info("用户信息更新成功: id={}", currentUserId);
            return Result.ok("更新成功");
        }
        
        return Result.fail("更新失败");
    }

    /**
     * 修改密码
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestParam(required = false) String oldPassword,
                                        @RequestParam(required = false) String newPassword,
                                        @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (oldPassword == null && body != null) {
            oldPassword = body.get("oldPassword");
        }
        if (newPassword == null && body != null) {
            newPassword = body.get("newPassword");
        }
        
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        if (!StringUtils.hasText(oldPassword)) {
            return Result.badRequest("原密码不能为空");
        }
        if (!StringUtils.hasText(newPassword)) {
            return Result.badRequest("新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return Result.badRequest("新密码长度不能少于6位");
        }
        
        User user = userService.findById(currentUserId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldMd5.equals(user.getPassword())) {
            return Result.badRequest("原密码错误");
        }
        
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        user.setUpdatedAt(TimeUtil.now());
        
        if (userService.updateUser(user)) {
            userService.clearCache(currentUserId);
            return Result.ok("密码修改成功");
        }
        
        return Result.fail("密码修改失败");
    }

    /**
     * 重置密码(通过邮箱验证码)
     * @param email 邮箱
     * @param code 验证码
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestParam(required = false) String email,
                                       @RequestParam(required = false) String code,
                                       @RequestParam(required = false) String newPassword,
                                       @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (email == null && body != null) {
            email = body.get("email");
        }
        if (code == null && body != null) {
            code = body.get("code");
        }
        if (newPassword == null && body != null) {
            newPassword = body.get("newPassword");
        }
        
        if (!StringUtils.hasText(email)) {
            return Result.badRequest("邮箱不能为空");
        }
        if (!StringUtils.hasText(newPassword)) {
            return Result.badRequest("新密码不能为空");
        }
        
        // 验证码校验
        if (!emailService.verifyCode(email, code)) {
            return Result.badRequest("验证码错误或已过期");
        }
        
        User user = userService.findByEmail(email);
        if (user == null) {
            return Result.notFound("该邮箱未注册");
        }
        
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        user.setUpdatedAt(TimeUtil.now());
        
        if (userService.updateUser(user)) {
            userService.clearCache(user.getId());
            return Result.ok("密码重置成功");
        }
        
        return Result.fail("密码重置失败");
    }

    /**
     * 绑定邮箱
     * @param email 邮箱
     * @param code 验证码
     * @return 绑定结果
     */
    @PostMapping("/bindEmail")
    public Result<Void> bindEmail(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) String code,
                                    @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (email == null && body != null) {
            email = body.get("email");
        }
        if (code == null && body != null) {
            code = body.get("code");
        }
        
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        if (!StringUtils.hasText(email)) {
            return Result.badRequest("邮箱不能为空");
        }
        
        email = email.trim();
        
        // 检查邮箱格式
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return Result.badRequest("邮箱格式不正确");
        }
        
        // 检查邮箱是否已被他人绑定
        User otherUser = userService.findByEmail(email);
        if (otherUser != null && !otherUser.getId().equals(currentUserId)) {
            return Result.badRequest("该邮箱已被其他账号绑定");
        }
        
        // 验证码校验
        if (!emailService.verifyCode(email, code)) {
            return Result.badRequest("验证码错误或已过期");
        }
        
        User user = userService.findById(currentUserId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        
        user.setEmail(email);
        user.setUpdatedAt(TimeUtil.now());
        
        if (userService.updateUser(user)) {
            userService.clearCache(currentUserId);
            return Result.ok("邮箱绑定成功");
        }
        
        return Result.fail("邮箱绑定失败");
    }

    /**
     * 绑定手机号
     * @param phone 手机号
     * @return 绑定结果
     */
    @PostMapping("/bindPhone")
    public Result<Void> bindPhone(@RequestParam(required = false) String phone,
                                   @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (phone == null && body != null) {
            phone = body.get("phone");
        }
        
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        if (!StringUtils.hasText(phone)) {
            return Result.badRequest("手机号不能为空");
        }
        
        User existingUser = userService.findByPhone(phone);
        if (existingUser != null && !existingUser.getId().equals(currentUserId)) {
            return Result.badRequest("该手机号已被其他账号绑定");
        }
        
        User user = userService.findById(currentUserId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        
        user.setPhone(phone);
        user.setUpdatedAt(TimeUtil.now());
        
        if (userService.updateUser(user)) {
            userService.clearCache(currentUserId);
            return Result.ok("手机号绑定成功");
        }
        
        return Result.fail("手机号绑定失败");
    }

    /**
     * 设置监护人信息
     * @param guardianPhone 监护人电话
     * @return 设置结果
     */
    @PostMapping("/guardian")
    public Result<Void> setGuardian(@RequestParam(required = false) String guardianPhone,
                                      @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (guardianPhone == null && body != null) {
            guardianPhone = body.get("guardianPhone");
        }
        
        Long currentUserId = RequestContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.unauthorized();
        }
        
        if (!StringUtils.hasText(guardianPhone)) {
            return Result.badRequest("监护人电话不能为空");
        }
        
        User user = userService.findById(currentUserId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        
        user.setGuardianPhone(guardianPhone);
        user.setUpdatedAt(TimeUtil.now());
        
        if (userService.updateUser(user)) {
            userService.clearCache(currentUserId);
            return Result.ok("监护人信息设置成功");
        }
        
        return Result.fail("监护人信息设置失败");
    }

    /**
     * 获取待处理的家长绑定请求（孩子端）
     */
    @GetMapping("/bind/requests")
    public Result<List<ParentChildBinding>> getBindRequests() {
        Long childId = RequestContext.getCurrentUserId();
        if (childId == null) {
            return Result.unauthorized();
        }
        List<ParentChildBinding> requests = parentChildBindingService.getIncomingRequests(childId);
        return Result.success(requests);
    }

    /**
     * 同意家长绑定请求（孩子端）
     */
    @PostMapping("/bind/approve/{bindingId}")
    public Result<Void> approveBind(@PathVariable Long bindingId) {
        Long childId = RequestContext.getCurrentUserId();
        if (childId == null) {
            return Result.unauthorized();
        }
        parentChildBindingService.approveBindRequest(bindingId, childId);
        return Result.ok("绑定成功");
    }

    /**
     * 拒绝家长绑定请求（孩子端）
     */
    @PostMapping("/bind/reject/{bindingId}")
    public Result<Void> rejectBind(@PathVariable Long bindingId) {
        Long childId = RequestContext.getCurrentUserId();
        if (childId == null) {
            return Result.unauthorized();
        }
        parentChildBindingService.rejectBindRequest(bindingId, childId);
        return Result.ok("已拒绝");
    }
}
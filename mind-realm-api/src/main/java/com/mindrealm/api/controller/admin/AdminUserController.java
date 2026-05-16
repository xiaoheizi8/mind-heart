package com.mindrealm.api.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.UserService;
import com.mindrealm.common.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端用户管理控制器
 */
@RestController
@RequestMapping("/admin/v1/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 用户列表
     */
    @GetMapping("/list")
    @OperationLog(value = "查询用户列表", type = OperationType.SELECT)
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = userService.listUsers(pageNum, pageSize, keyword, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", page.getRecords());
        data.put("total", page.getTotal());
        data.put("current", page.getCurrent());
        data.put("size", page.getSize());
        
        return Result.success(data);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    @OperationLog(value = "查询用户详情", type = OperationType.SELECT)
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping("/create")
    @OperationLog(value = "创建用户", type = OperationType.INSERT)
    public Result<User> create(@RequestBody User user) {
        // 检查用户名是否存在
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }
        
        // 加密密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(PasswordUtil.md5(user.getPassword()));
        } else {
            user.setPassword(PasswordUtil.md5("123456")); // 默认密码
        }
        
        userService.save(user);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @OperationLog(value = "更新用户信息", type = OperationType.UPDATE)
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.findById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }
        
        user.setId(id);
        // 不更新密码
        user.setPassword(null);
        
        userService.updateById(user);
        return Result.ok("更新成功");
    }

    /**
     * 切换用户状态
     */
    @PostMapping("/{id}/status")
    @OperationLog(value = "切换用户状态", type = OperationType.UPDATE)
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            return Result.badRequest("状态不能为空");
        }
        
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        
        return Result.ok(status == 1 ? "启用成功" : "禁用成功");
    }

    /**
     * 重置密码
     */
    @PostMapping("/{id}/resetPassword")
    @OperationLog(value = "重置用户密码", type = OperationType.UPDATE)
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (!StringUtils.hasText(newPassword)) {
            newPassword = "123456"; // 默认密码
        }
        
        User user = new User();
        user.setId(id);
        user.setPassword(PasswordUtil.md5(newPassword));
        userService.updateById(user);
        
        return Result.ok("密码重置成功");
    }
}

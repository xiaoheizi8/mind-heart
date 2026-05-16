package com.mindrealm.api.controller.admin;

import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.context.RequestContext;
import com.mindrealm.common.entity.SysMenu;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className: AdminMenuController
 * @description: 管理端菜单控制器
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@RestController
@RequestMapping("/admin/v1/menu")
public class AdminMenuController {

    @Autowired
    private SysMenuService menuService;

    /**
     * 获取当前用户的菜单树(根据角色权限过滤)
     * @return 用户可访问的菜单列表,树形结构
     */
    @GetMapping("/user")
    @OperationLog(value = "获取用户菜单", type = OperationType.SELECT)
    public Result<List<SysMenu>> getUserMenus() {
        Long userId = RequestContext.getCurrentUserId();
        List<SysMenu> menus = menuService.getUserMenuTree(userId);
        return Result.success(menus);
    }

    /**
     * 获取所有菜单树(管理端使用)
     * @return 完整的菜单树,包含所有启用的菜单
     */
    @GetMapping("/tree")
    @OperationLog(value = "获取所有菜单", type = OperationType.SELECT)
    public Result<List<SysMenu>> getAllMenuTree() {
        return Result.success(menuService.getAllMenuTree());
    }

    /**
     * 保存角色的菜单权限配置
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表,表示该角色可访问的菜单
     * @return 操作结果
     */
    @PostMapping("/role/{roleId}")
    @OperationLog(value = "保存角色菜单权限", type = OperationType.UPDATE)
    public Result<Void> saveRoleMenus(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        menuService.saveRoleMenus(roleId, menuIds);
        return Result.ok("保存成功");
    }

    /**
     * 获取指定角色的菜单ID列表(用于前端回显)
     * @param roleId 角色ID
     * @return 该角色关联的菜单ID列表
     */
    @GetMapping("/role/{roleId}")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        return Result.success(menuService.getMenuIdsByRoleId(roleId));
    }
}

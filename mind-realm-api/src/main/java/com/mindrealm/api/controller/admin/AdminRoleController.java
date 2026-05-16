package com.mindrealm.api.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.annotation.OperationLog;
import com.mindrealm.common.annotation.OperationType;
import com.mindrealm.common.entity.SysRole;
import com.mindrealm.common.result.PageResult;
import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @className: AdminRoleController
 * @description: 管理端角色控制器
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@RestController
@RequestMapping("/admin/v1/role")
public class AdminRoleController {

    @Autowired
    private SysRoleService roleService;

    /**
     * 分页查询角色列表(支持关键词搜索)
     * @param pageNum 页码,默认1
     * @param pageSize 每页数量,默认10
     * @param keyword 搜索关键词(角色名称或编码)
     * @return 分页角色列表
     */
    @GetMapping("/list")
    @OperationLog(value = "查询角色列表", type = OperationType.SELECT)
    public Result<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<SysRole> page = roleService.listRoles(pageNum, pageSize, keyword);
        return Result.success(PageResult.of(
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                (int) page.getTotal()
        ));
    }

    /**
     * 根据ID查询角色详情
     * @param id 角色ID
     * @return 角色详细信息
     */
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    /**
     * 新增角色
     * @param role 角色对象,包含角色名称、编码、描述等信息
     * @return 新增后的角色(包含生成的ID)
     */
    @PostMapping
    @OperationLog(value = "新增角色", type = OperationType.INSERT)
    public Result<SysRole> save(@RequestBody SysRole role) {
        return Result.success(roleService.save(role));
    }

    /**
     * 更新角色信息
     * @param id 角色ID
     * @param role 角色对象,包含需要更新的字段
     * @return 更新后的角色信息
     */
    @PutMapping("/{id}")
    @OperationLog(value = "更新角色", type = OperationType.UPDATE)
    public Result<SysRole> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        return Result.success(roleService.update(role));
    }

    /**
     * 删除角色(物理删除)
     * @param id 角色ID
     * @return 删除结果,成功返回200,失败返回500
     */
    @DeleteMapping("/{id}")
    @OperationLog(value = "删除角色", type = OperationType.DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        if (roleService.delete(id)) {
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取所有启用的角色(用于下拉选择等场景)
     * @return 启用状态的角色列表,按排序字段升序排列
     */
    @GetMapping("/all")
    public Result<java.util.List<SysRole>> getAllRoles() {
        return Result.success(roleService.getAllEnabledRoles());
    }
}

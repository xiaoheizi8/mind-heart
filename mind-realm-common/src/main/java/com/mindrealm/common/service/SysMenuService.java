package com.mindrealm.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.SysMenu;

import java.util.List;

/**
 * @className: SysMenuService
 * @description: 菜单服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface SysMenuService {
    
    /**
     * 获取用户菜单树（根据角色过滤）
     * @param userId 用户ID
     * @return 菜单树
     */
    List<SysMenu> getUserMenuTree(Long userId);
    
    /**
     * 获取所有菜单树
     * @return 菜单树
     */
    List<SysMenu> getAllMenuTree();
    
    /**
     * 获取角色的菜单ID列表
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
    
    /**
     * 保存角色菜单关联
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void saveRoleMenus(Long roleId, List<Long> menuIds);
    
    /**
     * 根据ID获取菜单
     * @param id 菜单ID
     * @return 菜单对象
     */
    SysMenu getById(Long id);
    
    /**
     * 保存菜单
     * @param menu 菜单对象
     * @return 保存后的菜单
     */
    SysMenu save(SysMenu menu);
    
    /**
     * 更新菜单
     * @param menu 菜单对象
     * @return 更新后的菜单
     */
    SysMenu update(SysMenu menu);
    
    /**
     * 删除菜单
     * @param id 菜单ID
     * @return 是否成功
     */
    boolean delete(Long id);
}

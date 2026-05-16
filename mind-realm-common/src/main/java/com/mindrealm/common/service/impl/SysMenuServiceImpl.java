package com.mindrealm.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.common.entity.SysMenu;
import com.mindrealm.common.entity.SysRoleMenu;
import com.mindrealm.common.entity.SysUserRole;
import com.mindrealm.common.mapper.SysMenuMapper;
import com.mindrealm.common.mapper.SysRoleMenuMapper;
import com.mindrealm.common.mapper.SysUserRoleMapper;
import com.mindrealm.common.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: SysMenuServiceImpl
 * @description: 菜单服务实现类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        // 获取用户的所有角色
        List<SysUserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取角色的所有菜单ID
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
            new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds)
        );

        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        // 去重菜单ID
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());

        // 查询菜单
        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);

        // 构建树结构
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<SysMenu> getAllMenuTree() {
        List<SysMenu> menus = menuMapper.selectList(
            new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort)
        );
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
            new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)
        );
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除旧关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));

        // 添加新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenus = menuIds.stream().map(menuId -> 
                SysRoleMenu.builder().roleId(roleId).menuId(menuId).build()
            ).collect(Collectors.toList());

            roleMenus.forEach(roleMenuMapper::insert);
        }
    }

    @Override
    public SysMenu getById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public SysMenu save(SysMenu menu) {
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    public SysMenu update(SysMenu menu) {
        menuMapper.updateById(menu);
        return menu;
    }

    @Override
    public boolean delete(Long id) {
        return menuMapper.deleteById(id) > 0;
    }

    /**
     * 构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> {
                    List<SysMenu> children = buildMenuTree(menus, menu.getId());
                    menu.setChildren(children.isEmpty() ? null : children);
                })
                .collect(Collectors.toList());
    }
}

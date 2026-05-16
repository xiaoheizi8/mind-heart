package com.mindrealm.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.SysRole;

import java.util.List;

/**
 * @className: SysRoleService
 * @description: 角色服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface SysRoleService {
    
    /**
     * 分页查询角色列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @return 分页结果
     */
    Page<SysRole> listRoles(int pageNum, int pageSize, String keyword);
    
    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色对象
     */
    SysRole getById(Long id);
    
    /**
     * 保存角色
     * @param role 角色对象
     * @return 保存后的角色
     */
    SysRole save(SysRole role);
    
    /**
     * 更新角色
     * @param role 角色对象
     * @return 更新后的角色
     */
    SysRole update(SysRole role);
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 获取所有启用的角色
     * @return 角色列表
     */
    List<SysRole> getAllEnabledRoles();
}

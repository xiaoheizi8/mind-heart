package com.mindrealm.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.SysRole;
import com.mindrealm.common.mapper.SysRoleMapper;
import com.mindrealm.common.service.SysRoleService;
import com.mindrealm.common.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @className: SysRoleServiceImpl
 * @description: 角色服务实现类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public Page<SysRole> listRoles(int pageNum, int pageSize, String keyword) {
        pageNum = ValidatorUtil.validatePageNum(pageNum);
        pageSize = ValidatorUtil.validatePageSize(pageSize);

        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getRoleName, keyword)
                   .or()
                   .like(SysRole::getRoleCode, keyword);
        }

        wrapper.eq(SysRole::getStatus, 1)
               .orderByAsc(SysRole::getSort);

        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public SysRole getById(Long id) {
        if (!ValidatorUtil.isValidId(id)) {
            return null;
        }
        return roleMapper.selectById(id);
    }

    @Override
    public SysRole save(SysRole role) {
        roleMapper.insert(role);
        return role;
    }

    @Override
    public SysRole update(SysRole role) {
        roleMapper.updateById(role);
        return role;
    }

    @Override
    public boolean delete(Long id) {
        if (!ValidatorUtil.isValidId(id)) {
            return false;
        }
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public List<SysRole> getAllEnabledRoles() {
        return roleMapper.selectList(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSort)
        );
    }
}

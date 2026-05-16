package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: SysUserRoleMapper
 * @description: 用户角色关联Mapper接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}

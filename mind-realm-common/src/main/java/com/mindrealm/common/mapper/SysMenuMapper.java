package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: SysMenuMapper
 * @description: 菜单Mapper接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}

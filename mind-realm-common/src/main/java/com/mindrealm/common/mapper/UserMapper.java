package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: UserMapper
 * @description: 用户Mapper接口,继承BaseMapper提供用户表 CRUD 操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

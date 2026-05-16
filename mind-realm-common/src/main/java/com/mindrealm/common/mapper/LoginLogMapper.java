package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: LoginLogMapper
 * @description: 登录日志Mapper接口,提供登录日志表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
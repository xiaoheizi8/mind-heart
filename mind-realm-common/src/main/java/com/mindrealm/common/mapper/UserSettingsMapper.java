package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: UserSettingsMapper
 * @description: 用户设置Mapper接口,提供用户设置记录的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
}
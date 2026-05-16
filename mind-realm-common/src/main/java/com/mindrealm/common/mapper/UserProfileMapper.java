package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: UserProfileMapper
 * @description: 用户画像Mapper接口,提供用户画像表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}

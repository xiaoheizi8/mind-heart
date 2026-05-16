package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.UserCollect;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: UserCollectMapper
 * @description: 用户收藏Mapper接口,提供用户收藏记录的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface UserCollectMapper extends BaseMapper<UserCollect> {
}
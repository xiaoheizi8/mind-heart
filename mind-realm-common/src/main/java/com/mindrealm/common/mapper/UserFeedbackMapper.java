package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.UserFeedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: UserFeedbackMapper
 * @description: 用户反馈Mapper接口,提供用户反馈表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface UserFeedbackMapper extends BaseMapper<UserFeedback> {
}
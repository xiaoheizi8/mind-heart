package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: NotificationMapper
 * @description: 通知Mapper接口,提供通知记录的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
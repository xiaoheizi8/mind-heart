package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: OperationLogMapper
 * @description: 操作日志Mapper接口,提供操作日志表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}

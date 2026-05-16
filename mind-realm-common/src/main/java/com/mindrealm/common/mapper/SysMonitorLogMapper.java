package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.SysMonitorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: SysMonitorLogMapper
 * @description: 系统监控日志Mapper接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Mapper
public interface SysMonitorLogMapper extends BaseMapper<SysMonitorLog> {
}

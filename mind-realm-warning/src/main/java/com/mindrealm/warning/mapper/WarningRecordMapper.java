package com.mindrealm.warning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.warning.entity.WarningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: WarningRecordMapper
 * @description: 预警记录Mapper接口,提供预警记录表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface WarningRecordMapper extends BaseMapper<WarningRecord> {
}

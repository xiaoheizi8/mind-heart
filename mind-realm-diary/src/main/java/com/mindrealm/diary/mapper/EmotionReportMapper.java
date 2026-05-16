package com.mindrealm.diary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.diary.entity.EmotionReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: EmotionReportMapper
 * @description: 情绪报告Mapper接口,提供情绪报告表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.25
 */
@Mapper
public interface EmotionReportMapper extends BaseMapper<EmotionReport> {
}

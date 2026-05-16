package com.mindrealm.diary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.diary.entity.Diary;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: DiaryMapper
 * @description: 日记Mapper接口,提供日记表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {
}

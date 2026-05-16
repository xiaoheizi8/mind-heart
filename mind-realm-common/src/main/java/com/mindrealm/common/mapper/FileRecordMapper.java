package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: FileRecordMapper
 * @description: 文件记录Mapper接口,提供文件记录表的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {
}
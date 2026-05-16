package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: KnowledgeMapper
 * @description: 知识库Mapper接口,提供心理知识内容的增删改查操作
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}
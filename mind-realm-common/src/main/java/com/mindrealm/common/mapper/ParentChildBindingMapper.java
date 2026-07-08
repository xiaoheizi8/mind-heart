package com.mindrealm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindrealm.common.entity.ParentChildBinding;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: ParentChildBindingMapper
 * @description: 家长-孩子绑定关系 Mapper
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.8
 */
@Mapper
public interface ParentChildBindingMapper extends BaseMapper<ParentChildBinding> {
}

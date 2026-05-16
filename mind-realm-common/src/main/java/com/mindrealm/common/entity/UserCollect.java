package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: UserCollect
 * @description: 用户收藏实体类,记录用户收藏的知识库内容
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_collect")
public class UserCollect implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏目标类型(knowledge)
     */
    private String targetType;

    /**
     * 收藏目标ID
     */
    private Long targetId;

    /**
     * 收藏时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime createdAt;
}
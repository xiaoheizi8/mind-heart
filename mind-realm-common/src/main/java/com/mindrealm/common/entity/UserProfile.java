package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: UserProfile
 * @description: 用户画像实体类,存储用户的数字孪生画像数据
 *               包含人格标签、情绪模式、压力触发点等心理特征
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_profile")
public class UserProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 画像ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(唯一)
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 人格标签(JSON数组)
     * 存储用户的人格特征标签，如["内向", "敏感", "责任心强"]
     */
    @TableField("personality_tags")
    private String personalityTags;

    /**
     * 情绪模式(JSON)
     * 存储用户的情绪波动规律和模式分析
     */
    @TableField("emotional_pattern")
    private String emotionalPattern;

    /**
     * 压力触发点(JSON数组)
     * 存储容易引发用户压力的因素
     */
    @TableField("stress_triggers")
    private String stressTriggers;

    /**
     * 最后分析结果(JSON)
     * 存储最近一次AI分析的完整结果
     */
    @TableField("last_analysis")
    private String lastAnalysis;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

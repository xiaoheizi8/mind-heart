package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @className: UserFeedback
 * @description: 用户反馈实体类,存储用户的反馈信息包括类型、标题、内容、联系方式及处理状态
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@TableName("user_feedback")
public class UserFeedback implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 反馈类型(bug/feature/complaint/suggestion)
     */
    private String type;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 处理状态(0:待处理,1:已处理)
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理备注
     */
    private String handlerNote;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime createdAt;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private LocalDateTime handledAt;
}
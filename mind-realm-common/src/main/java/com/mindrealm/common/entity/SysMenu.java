package com.mindrealm.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: SysMenu
 * @description: 菜单实体类
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_menu")
public class SysMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("parent_id")
    @Builder.Default
    private Long parentId = 0L;

    @TableField("menu_name")
    private String menuName;

    @TableField("menu_code")
    private String menuCode;

    @TableField("menu_type")
    private Integer menuType;

    @TableField("path")
    private String path;

    @TableField("component")
    private String component;

    @TableField("icon")
    private String icon;

    @TableField("sort")
    @Builder.Default
    private Integer sort = 0;

    @TableField("visible")
    @Builder.Default
    private Integer visible = 1;

    @TableField("permission")
    private String permission;

    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 子菜单列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysMenu> children;
}

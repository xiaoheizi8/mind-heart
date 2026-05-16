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
 * @className: FileRecord
 * @description: 文件记录实体类,存储用户上传的文件信息包括文件名、URL、类型、大小及OSS键值
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@TableName("file_record")
public class FileRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID,主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件类型(image/audio/video)
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * OSS存储键
     */
    private String ossKey;

    /**
     * 创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

     private LocalDateTime createdAt;
}
package com.mindrealm.api.controller;

import com.mindrealm.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @className: FileController
 * @description: 文件上传控制器
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.7
 */
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    // 允许的图片格式
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传图片
     * @param file 图片文件
     * @param request HTTP请求
     * @return 图片访问URL
     */
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.badRequest("请选择要上传的文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.badRequest("文件大小不能超过10MB");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            return Result.badRequest("仅支持 JPG、PNG、GIF、WEBP 格式的图片");
        }

        try {
            // 创建上传目录
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;

            // 保存文件
            File destFile = new File(dir, newFilename);
            file.transferTo(destFile);

            // 构建访问URL
            String url = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/uploads/" + newFilename;

            log.info("文件上传成功: {}", url);
            return Result.success(url);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}

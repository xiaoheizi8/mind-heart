package com.mindrealm.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.UUID;

/**
 * 文件上传配置
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    /**
     * 上传文件存储根目录
     */
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 允许的图片类型
     */
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "jpg", "jpeg", "png", "gif", "webp", "bmp"
    };

    /**
     * 允许的最大文件大小 (5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问
        String path = System.getProperty("user.dir") + File.separator + uploadPath;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path + "/");
    }

    /**
     * 获取上传路径
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * 检查文件类型是否允许
     */
    public static boolean isAllowedImageType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        for (String allowed : ALLOWED_IMAGE_TYPES) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查文件大小是否允许
     */
    public static boolean isAllowedSize(long size) {
        return size > 0 && size <= MAX_FILE_SIZE;
    }

    /**
     * 生成安全的新文件名
     */
    public static String generateSafeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }

        // 获取文件扩展名
        String extension = "";
        if (originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 生成随机文件名
        return System.currentTimeMillis() + "_" 
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8)
                + extension;
    }
}
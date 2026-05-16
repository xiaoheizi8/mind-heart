package com.mindrealm.common.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.mindrealm.common.service.IOssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @className: OssServiceImpl
 * @description: 阿里云OSS文件上传服务实现
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
@Service
public class OssServiceImpl implements IOssService {

    private static final Logger log = LoggerFactory.getLogger(OssServiceImpl.class);

    @Value("${aliyun.oss.end-point:oss-cn-beijing.aliyuncs.com}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret:}")
    private String accessKeySecret;;

    @Value("${aliyun.oss.bucket-name:mind-heart}")
    private String bucketName;

    @Value("${aliyun.oss.domain:}")
    private String domain;

    private OSS createOssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传图片
     */
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, "images");
    }

    /**
     * 上传音频
     */
    @Override
    public String uploadAudio(MultipartFile file) throws IOException {
        return uploadFile(file, "audio");
    }

    /**
     * 上传视频
     */
    @Override
    public String uploadVideo(MultipartFile file) throws IOException {
        return uploadFile(file, "video");
    }

    /**
     * 通用文件上传
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFileName = generateFileName(extension);

        String objectKey = folder + "/" + newFileName;

        OSS ossClient = createOssClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectResult result = ossClient.putObject(bucketName, objectKey, file.getInputStream(), metadata);

            log.info("文件上传成功: {}", objectKey);

            String url = getFileUrl(objectKey);
            return url;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传 Base64 图片
     */
    @Override
    public String uploadBase64Image(String base64Data, String extension) throws IOException {
        if (base64Data == null || base64Data.isEmpty()) {
            throw new IllegalArgumentException("Base64数据不能为空");
        }

        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }

        String[] parts = base64Data.split(",");
        if (parts.length > 1) {
            base64Data = parts[1];
        }

        byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
        java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(bytes);

        String newFileName = generateFileName(extension);
        String objectKey = "images/" + newFileName;

        OSS ossClient = createOssClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + extension);
            metadata.setContentLength(bytes.length);

            ossClient.putObject(bucketName, objectKey, inputStream, metadata);

            log.info("Base64图片上传成功: {}", objectKey);

            return getFileUrl(objectKey);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 删除文件
     */
    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        String objectKey = getObjectKeyFromUrl(fileUrl);
        if (objectKey == null) {
            return false;
        }

        OSS ossClient = createOssClient();
        try {
            ossClient.deleteObject(bucketName, objectKey);
            log.info("文件删除成功: {}", objectKey);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}", objectKey, e);
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 获取文件访问URL
     */
    @Override
    public String getFileUrl(String objectKey) {
        if (domain != null && !domain.isEmpty()) {
            return "https://" + domain + "/" + objectKey;
        }
        return "https://" + bucketName + "." + endpoint + "/" + objectKey;
    }

    /**
     * 从URL中提取ObjectKey
     */
    private String getObjectKeyFromUrl(String url) {
        try {
            if (url.contains(bucketName)) {
                int index = url.indexOf(bucketName);
                return url.substring(url.indexOf("/", index) + 1);
            } else if (url.contains("mind-heart")) {
                int index = url.indexOf("mind-heart");
                return url.substring(url.indexOf("/", index) + 1);
            }
        } catch (Exception e) {
            log.error("解析文件URL失败: {}", url, e);
        }
        return null;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String ext = (extension == null || extension.isEmpty()) ? "" : "." + extension;
        return uuid + ext;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查文件类型是否允许
     */
    @Override
    public boolean isAllowedImageType(String extension) {
        if (extension == null) return false;
        java.util.Set<String> allowed = new java.util.HashSet<>(java.util.Arrays.asList(
                "jpg", "jpeg", "png", "gif", "webp", "bmp"
        ));
        return allowed.contains(extension.toLowerCase());
    }

    /**
     * 检查文件类型是否允许（音频）
     */
    @Override
    public boolean isAllowedAudioType(String extension) {
        if (extension == null) return false;
        java.util.Set<String> allowed = new java.util.HashSet<>(java.util.Arrays.asList(
                "mp3", "wav", "aac", "m4a", "ogg"
        ));
        return allowed.contains(extension.toLowerCase());
    }

    /**
     * 检查文件类型是否允许（视频）
     */
    @Override
    public boolean isAllowedVideoType(String extension) {
        if (extension == null) return false;
        java.util.Set<String> allowed = new java.util.HashSet<>(java.util.Arrays.asList(
                "mp4", "avi", "mov", "wmv", "flv"
        ));
        return allowed.contains(extension.toLowerCase());
    }
}
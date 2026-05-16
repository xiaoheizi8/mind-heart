package com.mindrealm.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @className: IOssService
 * @description: 阿里云OSS文件上传服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IOssService {

    /**
     * 上传图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file) throws IOException;

    /**
     * 上传音频
     * @param file 音频文件
     * @return 音频访问URL
     */
    String uploadAudio(MultipartFile file) throws IOException;

    /**
     * 上传视频
     * @param file 视频文件
     * @return 视频访问URL
     */
    String uploadVideo(MultipartFile file) throws IOException;

    /**
     * 通用文件上传
     * @param file 文件
     * @param folder 文件夹
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * 上传Base64图片
     * @param base64Data Base64数据
     * @param extension 文件扩展名
     * @return 图片访问URL
     */
    String uploadBase64Image(String base64Data, String extension) throws IOException;

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 获取文件访问URL
     * @param objectKey 对象键
     * @return 文件URL
     */
    String getFileUrl(String objectKey);

    /**
     * 检查文件类型是否允许（图片）
     * @param extension 扩展名
     * @return 是否允许
     */
    boolean isAllowedImageType(String extension);

    /**
     * 检查文件类型是否允许（音频）
     * @param extension 扩展名
     * @return 是否允许
     */
    boolean isAllowedAudioType(String extension);

    /**
     * 检查文件类型是否允许（视频）
     * @param extension 扩展名
     * @return 是否允许
     */
    boolean isAllowedVideoType(String extension);
}

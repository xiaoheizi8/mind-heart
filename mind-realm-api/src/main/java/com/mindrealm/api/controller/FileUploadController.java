package com.mindrealm.api.controller;

import com.mindrealm.common.result.Result;
import com.mindrealm.common.service.IOssService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 文件上传控制器 - 阿里云OSS版本
 */
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    private final IOssService ossService;



    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.badRequest("文件不能为空");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            return Result.badRequest("图片大小不能超过5MB");
        }

        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!ossService.isAllowedImageType(extension)) {
            return Result.badRequest("不支持的图片类型");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.badRequest("无效的图片文件");
        }

        try {
            String url = ossService.uploadImage(file);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", getFileNameFromUrl(url));
            result.put("size", String.valueOf(file.getSize()));

            log.info("图片上传成功: {}", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败");
        }
    }

    /**
     * 上传音频
     */
    @PostMapping("/upload/audio")
    public Result<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.badRequest("文件不能为空");
        }

        if (file.getSize() > MAX_AUDIO_SIZE) {
            return Result.badRequest("音频大小不能超过10MB");
        }

        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!ossService.isAllowedAudioType(extension)) {
            return Result.badRequest("不支持的音频类型");
        }

        try {
            String url = ossService.uploadAudio(file);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", getFileNameFromUrl(url));
            result.put("size", String.valueOf(file.getSize()));
            result.put("duration", "");

            log.info("音频上传成功: {}", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("音频上传失败", e);
            return Result.error("音频上传失败");
        }
    }

    /**
     * 上传视频
     */
    @PostMapping("/upload/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.badRequest("文件不能为空");
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {
            return Result.badRequest("视频大小不能超过50MB");
        }

        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!ossService.isAllowedVideoType(extension)) {
            return Result.badRequest("不支持的视频类型");
        }

        try {
            String url = ossService.uploadVideo(file);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", getFileNameFromUrl(url));
            result.put("size", String.valueOf(file.getSize()));

            log.info("视频上传成功: {}", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("视频上传失败", e);
            return Result.error("视频上传失败");
        }
    }

    /**
     * 上传Base64图片
     */
    @PostMapping("/upload/base64")
    public Result<Map<String, String>> uploadBase64Image(@RequestBody Map<String, String> request) {
        String base64Data = request.get("data");
        String extension = request.getOrDefault("extension", "jpg");

        if (base64Data == null || base64Data.isEmpty()) {
            return Result.badRequest("图片数据不能为空");
        }

        if (!ossService.isAllowedImageType(extension)) {
            return Result.badRequest("不支持的图片类型");
        }

        try {
            String url = ossService.uploadBase64Image(base64Data, extension);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", getFileNameFromUrl(url));

            log.info("Base64图片上传成功: {}", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Base64图片上传失败", e);
            return Result.error("图片上传失败");
        }
    }

    /**
     * 删除文件
     * @param url 文件URL(从请求参数或请求体获取)
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam(required = false) String url,
                                    @RequestBody(required = false) Map<String, String> body) {
        // 优先从请求体获取
        if (url == null && body != null) {
            url = body.get("url");
        }
        
        try {
            boolean success = ossService.deleteFile(url);
            if (success) {
                log.info("文件删除成功: {}", url);
                return Result.ok("删除成功");
            } else {
                return Result.notFound("文件不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", url, e);
            return Result.error("删除失败");
        }
    }

    /**
     * 批量上传图片
     */
    @PostMapping("/upload/images")
    public Result<List<Map<String, String>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<Map<String, String>> results = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
            if (!ossService.isAllowedImageType(extension)) continue;

            try {
                String url = ossService.uploadImage(file);
                Map<String, String> result = new HashMap<>();
                result.put("url", url);
                result.put("filename", getFileNameFromUrl(url));
                results.add(result);
            } catch (Exception e) {
                log.error("图片上传失败: {}", file.getOriginalFilename(), e);
            }
        }

        if (results.isEmpty()) {
            return Result.error("没有成功上传任何文件");
        }

        return Result.success(results);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        int lastSlash = url.lastIndexOf("/");
        return lastSlash >= 0 ? url.substring(lastSlash + 1) : url;
    }
}
package com.agriculture.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FileUploadService {

    @Value("${app.file.base-path:./uploads}")
    private String basePath;

    @Value("${app.file.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> IMAGE_EXTS = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/webm", "video/quicktime");
    private static final Set<String> VIDEO_EXTS = Set.of(".mp4", ".webm", ".mov");
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;  // 5MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    public Map<String, Object> uploadImage(MultipartFile file, String bizType) {
        validateFile(file, IMAGE_TYPES, IMAGE_EXTS, MAX_IMAGE_SIZE);
        return doUpload(file, "image", bizType);
    }

    public Map<String, Object> uploadVideo(MultipartFile file, String bizType) {
        validateFile(file, VIDEO_TYPES, VIDEO_EXTS, MAX_VIDEO_SIZE);
        return doUpload(file, "video", bizType);
    }

    private void validateFile(MultipartFile file, Set<String> allowedTypes, Set<String> allowedExts, long maxSize) {
        if (file == null || file.isEmpty()) throw new RuntimeException("文件不能为空");
        if (file.getSize() > maxSize) throw new RuntimeException("文件大小超限，最大" + (maxSize / 1024 / 1024) + "MB");

        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename();
        String ext = originalName != null ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase() : "";

        // 危险文件拦截
        Set<String> dangerous = Set.of(".exe", ".bat", ".sh", ".jar", ".cmd", ".vbs", ".ps1", ".dll", ".so", ".run");
        if (dangerous.contains(ext)) throw new RuntimeException("不允许上传危险文件类型");

        if (!allowedTypes.contains(contentType) && !allowedExts.contains(ext)) {
            throw new RuntimeException("不支持的文件格式: " + ext);
        }
    }

    private Map<String, Object> doUpload(MultipartFile file, String mediaType, String bizType) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null ? originalName.substring(originalName.lastIndexOf('.')) : ".bin";
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String newName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 目录: uploads/{mediaType}/{bizType}/{yyyy/MM}/
            String subDir = mediaType + "/" + (bizType != null ? bizType + "/" : "") + dateDir;
            Path dir = Paths.get(basePath, subDir);
            Files.createDirectories(dir);
            Path targetPath = dir.resolve(newName);

            // 防路径穿越
            if (!targetPath.normalize().startsWith(Paths.get(basePath).normalize())) {
                throw new RuntimeException("非法文件路径");
            }

            file.transferTo(targetPath.toFile());

            String fileUrl = baseUrl + "/" + subDir + "/" + newName;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("fileName", newName);
            result.put("originalName", originalName);
            result.put("fileUrl", fileUrl);
            result.put("fileSize", file.getSize());
            result.put("contentType", file.getContentType());
            return result;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}

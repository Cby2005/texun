package com.agriculture.common.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/common/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传")
public class FileUploadController {

    private final FileUploadService uploadService;

    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", defaultValue = "article_cover") String bizType) {
        return Result.ok(uploadService.uploadImage(file, bizType));
    }

    @PostMapping("/video")
    public Result<Map<String, Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", defaultValue = "video_file") String bizType) {
        return Result.ok(uploadService.uploadVideo(file, bizType));
    }
}

package com.agriculture.yolo.controller;

import com.agriculture.common.result.Result;
import com.agriculture.yolo.dto.YoloDetectResponse;
import com.agriculture.yolo.service.YoloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/yolo")
@RequiredArgsConstructor
@Tag(name = "YOLO病虫害检测")
public class YoloController {

    private final YoloService yoloService;

    @PostMapping("/detect")
    @Operation(summary = "检测病虫害图片")
    public Result<YoloDetectResponse> detect(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "questionId", required = false) Long questionId,
                                             @RequestParam(value = "imageId", required = false) Long imageId) {
        return Result.ok(yoloService.detectDisease(file, questionId, imageId));
    }
}

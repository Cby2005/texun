package com.agriculture.yolo.service;

import com.agriculture.yolo.dto.YoloDetectResponse;
import org.springframework.web.multipart.MultipartFile;

public interface YoloService {
    YoloDetectResponse detectDisease(MultipartFile file);
    YoloDetectResponse detectDisease(MultipartFile file, Long questionId, Long imageId);
}

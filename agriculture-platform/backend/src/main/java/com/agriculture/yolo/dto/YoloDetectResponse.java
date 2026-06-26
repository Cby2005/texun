package com.agriculture.yolo.dto;

import lombok.Data;
import java.util.List;

@Data
public class YoloDetectResponse {
    private boolean success;
    private String message;
    private String modelId;
    private String topDiseaseName;
    private Double topConfidence;
    private String provider;
    private String rawJson;
    private Long resultId;
    private List<YoloPredictionDTO> predictions;
    public boolean hasDetection() { return success && topDiseaseName != null && predictions != null && !predictions.isEmpty(); }
}

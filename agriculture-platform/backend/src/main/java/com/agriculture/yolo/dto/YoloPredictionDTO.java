package com.agriculture.yolo.dto;

import lombok.Data;

@Data
public class YoloPredictionDTO {
    private String className;
    private Double confidence;
    private Integer x, y, width, height;
}

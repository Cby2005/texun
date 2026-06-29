package com.agriculture.drone.image.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ImageAddRequest {
    private Long taskId;
    private String imageUrl;
    private String capturePoint;
}

package com.agriculture.drone.image.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("drone_inspection_image")
public class DroneInspectionImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String imageUrl;
    private String capturePoint;
    private String detectResult;
    private String diseaseType;
    private BigDecimal confidence;
    private String suggestion;
    private LocalDateTime createTime;
}

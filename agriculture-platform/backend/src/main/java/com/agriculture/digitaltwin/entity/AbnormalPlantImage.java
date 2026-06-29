package com.agriculture.digitaltwin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("digital_twin_abnormal_plant_image")
public class AbnormalPlantImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inspectionRecordId;
    private Long plotId;
    private Long plantingRecordId;
    private Long baseId;
    private Long greenhouseId;
    private String areaCode;
    private String plotCode;
    private String imageUrl;
    private String abnormalReason;
    private String detectResult;
    private BigDecimal confidence;
    private String growthStage;
    private Long savedBy;
    private LocalDateTime saveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

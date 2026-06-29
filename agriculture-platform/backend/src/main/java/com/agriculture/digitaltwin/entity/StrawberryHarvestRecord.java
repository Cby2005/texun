package com.agriculture.digitaltwin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("strawberry_harvest_record")
public class StrawberryHarvestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String harvestCode;
    private Long plantingRecordId;
    private Long plotId;
    private Long greenhouseId;
    private Long robotId;
    private String variety;
    private Integer harvestCount;
    private BigDecimal harvestWeight;
    private String qualityGrade;
    private String postHarvestAction;
    private LocalDate harvestDate;
    private String operatorName;
    private String harvestStatus;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

package com.agriculture.digitaltwin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("strawberry_plot")
public class StrawberryPlot {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String plotCode;
    private Long greenhouseId;
    private String areaCode;
    private String areaName;
    private Integer rowNo;
    private Integer colNo;
    private String plotStatus;
    private Long currentPlantingRecordId;
    private BigDecimal coordinateX;
    private BigDecimal coordinateY;
    private BigDecimal coordinateZ;
    private LocalDateTime lastInspectionTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

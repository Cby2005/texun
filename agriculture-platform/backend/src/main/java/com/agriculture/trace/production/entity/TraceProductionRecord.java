package com.agriculture.trace.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_production_record")
public class TraceProductionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private LocalDateTime sowTime;
    private String fertilizerRecord;
    private String pesticideRecord;
    private String irrigationRecord;
    private String soilTemperature;
    private String soilHumidity;
    private LocalDateTime harvestTime;
    private String responsiblePerson;
    private String imageUrl;
    private Long operatorId;
    private String operatorName;
    private String chainHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}

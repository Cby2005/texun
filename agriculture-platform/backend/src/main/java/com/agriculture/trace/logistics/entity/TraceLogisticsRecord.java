package com.agriculture.trace.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_logistics_record")
public class TraceLogisticsRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String logisticsCompany;
    private String transportVehicle;
    private String driverName;
    private String driverPhone;
    private String fromAddress;
    private String toAddress;
    private LocalDateTime shipTime;
    private LocalDateTime arrivalTime;
    private String transportTemperature;
    private String transportHumidity;
    private String logisticsStatus;
    private Long operatorId;
    private String operatorName;
    private String chainHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}

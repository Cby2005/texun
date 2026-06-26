package com.agri.vehicle.entity;

import com.agri.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 设备故障记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vehicle_fault_record")
public class VehicleFaultRecord extends BaseEntity {

    private Long vehicleId;
    private String faultCode;
    private String faultType;
    private String faultLevel;
    private String description;
    private String resolution;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedTime;
}

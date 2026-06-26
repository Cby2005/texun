package com.agri.vehicle.entity;

import com.agri.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 无人车设备实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vehicle_device")
public class VehicleDevice extends BaseEntity {

    private String deviceCode;
    private String deviceName;
    private Long modelId;
    private Long farmId;
    private String status;
    private String firmwareVersion;
    private String simCard;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activationTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOnlineTime;
    private String configParams;
}

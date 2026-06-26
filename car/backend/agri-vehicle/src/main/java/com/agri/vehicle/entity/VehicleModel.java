package com.agri.vehicle.entity;

import com.agri.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备型号实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vehicle_model")
public class VehicleModel extends BaseEntity {

    private String modelCode;
    private String modelName;
    private String manufacturer;
    private String vehicleType;
    private Double maxSpeed;
    private Double batteryCapacity;
    private Double maxPayload;
    private Double workWidth;
    private Double turnRadius;
    private String specs;
}

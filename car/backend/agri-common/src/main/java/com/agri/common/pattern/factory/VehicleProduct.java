package com.agri.common.pattern.factory;

import lombok.Builder;
import lombok.Data;

/**
 * 工厂模式：无人车产品
 */
@Data
@Builder
public class VehicleProduct {
    private String deviceCode;
    private String deviceName;
    private String vehicleType;
    private double maxSpeed;
    private double batteryCapacity;
    private double workWidth;
    private double turnRadius;
    private String sensorConfig;
}

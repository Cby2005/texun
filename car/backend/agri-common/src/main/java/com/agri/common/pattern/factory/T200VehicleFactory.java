package com.agri.common.pattern.factory;

import org.springframework.stereotype.Component;

/**
 * 工厂模式：T200 型号无人车工厂
 */
@Component
public class T200VehicleFactory implements VehicleFactory {

    @Override
    public VehicleProduct createVehicle(String deviceCode, String deviceName) {
        return VehicleProduct.builder()
                .deviceCode(deviceCode)
                .deviceName(deviceName)
                .vehicleType("T200")
                .maxSpeed(15.0)
                .batteryCapacity(100.0)
                .workWidth(4.0)
                .turnRadius(2.5)
                .sensorConfig("GPS+IMU+LIDAR+Camera")
                .build();
    }

    @Override
    public String vehicleType() {
        return "T200";
    }
}

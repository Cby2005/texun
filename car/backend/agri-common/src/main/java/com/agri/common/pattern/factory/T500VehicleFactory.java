package com.agri.common.pattern.factory;

import org.springframework.stereotype.Component;

/**
 * 工厂模式：T500 型号无人车工厂（大型车辆）
 */
@Component
public class T500VehicleFactory implements VehicleFactory {

    @Override
    public VehicleProduct createVehicle(String deviceCode, String deviceName) {
        return VehicleProduct.builder()
                .deviceCode(deviceCode)
                .deviceName(deviceName)
                .vehicleType("T500")
                .maxSpeed(20.0)
                .batteryCapacity(200.0)
                .workWidth(8.0)
                .turnRadius(4.0)
                .sensorConfig("GPS+IMU+LIDAR+Camera+Radar")
                .build();
    }

    @Override
    public String vehicleType() {
        return "T500";
    }
}

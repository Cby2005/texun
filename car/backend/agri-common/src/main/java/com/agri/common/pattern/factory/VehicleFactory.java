package com.agri.common.pattern.factory;

/**
 * 工厂模式：无人车工厂接口
 * 用于创建不同类型无人车实例
 */
public interface VehicleFactory {
    /** 创建无人车实例 */
    VehicleProduct createVehicle(String deviceCode, String deviceName);
    /** 支持的车辆类型 */
    String vehicleType();
}

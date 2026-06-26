package com.agri.common.pattern.factory;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工厂模式：无人车工厂注册中心（单例）
 * 自动发现并注册所有 VehicleFactory 实现
 */
@Component
public class VehicleFactoryRegistry {

    private final Map<String, VehicleFactory> factoryMap = new HashMap<>();
    private final List<VehicleFactory> factories;

    public VehicleFactoryRegistry(List<VehicleFactory> factories) {
        this.factories = factories;
    }

    @PostConstruct
    public void init() {
        for (VehicleFactory factory : factories) {
            factoryMap.put(factory.vehicleType().toUpperCase(), factory);
        }
    }

    /**
     * 根据类型获取工厂
     */
    public VehicleFactory getFactory(String vehicleType) {
        VehicleFactory factory = factoryMap.get(vehicleType.toUpperCase());
        if (factory == null) {
            throw new IllegalArgumentException("不支持的车辆类型: " + vehicleType);
        }
        return factory;
    }

    /**
     * 创建无人车
     */
    public VehicleProduct create(String vehicleType, String deviceCode, String deviceName) {
        return getFactory(vehicleType).createVehicle(deviceCode, deviceName);
    }
}

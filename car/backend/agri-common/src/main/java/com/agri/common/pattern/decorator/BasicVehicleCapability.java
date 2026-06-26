package com.agri.common.pattern.decorator;

/**
 * 装饰器模式：基础设备能力（被装饰对象）
 */
public class BasicVehicleCapability implements VehicleCapability {

    @Override
    public String describe() {
        return "基础设备能力";
    }

    @Override
    public Object execute(Object input) {
        return input;
    }
}

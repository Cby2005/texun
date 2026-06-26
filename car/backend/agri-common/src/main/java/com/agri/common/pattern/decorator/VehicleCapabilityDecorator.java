package com.agri.common.pattern.decorator;

/**
 * 装饰器模式：设备能力装饰器抽象基类
 */
public abstract class VehicleCapabilityDecorator implements VehicleCapability {

    protected final VehicleCapability delegate;

    protected VehicleCapabilityDecorator(VehicleCapability delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object execute(Object input) {
        return delegate.execute(input);
    }
}

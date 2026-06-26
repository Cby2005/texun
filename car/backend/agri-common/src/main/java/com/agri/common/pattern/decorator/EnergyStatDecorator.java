package com.agri.common.pattern.decorator;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 装饰器模式：能耗统计装饰器
 * 在基础能力上叠加能耗统计功能
 */
@Slf4j
public class EnergyStatDecorator extends VehicleCapabilityDecorator {

    public EnergyStatDecorator(VehicleCapability delegate) {
        super(delegate);
    }

    @Override
    public String describe() {
        return delegate.describe() + " + 能耗统计";
    }

    @Override
    public Object execute(Object input) {
        log.info("[能耗统计] 开始记录能耗数据");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("original", delegate.execute(input));
        result.put("energyConsumption", 12.5);
        result.put("energyUnit", "kWh");
        return result;
    }
}

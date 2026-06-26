package com.agri.common.pattern.adapter;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 适配器模式：通信适配器注册中心
 */
@Component
public class VehicleCommAdapterRegistry {

    private final Map<String, VehicleCommAdapter> adapterMap = new HashMap<>();
    private final List<VehicleCommAdapter> adapters;

    public VehicleCommAdapterRegistry(List<VehicleCommAdapter> adapters) {
        this.adapters = adapters;
    }

    @PostConstruct
    public void init() {
        for (VehicleCommAdapter adapter : adapters) {
            adapterMap.put(adapter.name().toUpperCase(), adapter);
        }
    }

    public VehicleCommAdapter getAdapter(String protocol) {
        for (VehicleCommAdapter adapter : adapterMap.values()) {
            if (adapter.support(protocol)) return adapter;
        }
        throw new IllegalArgumentException("不支持的通信协议: " + protocol);
    }
}

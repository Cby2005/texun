package com.agri.common.pattern.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 适配器模式：HTTP 协议适配器
 * 将 HTTP REST 协议转换为统一接口
 */
@Slf4j
@Component
public class HttpProtocolAdapter implements VehicleCommAdapter {

    @Override
    public String name() {
        return "HTTP";
    }

    @Override
    public boolean support(String protocol) {
        return "HTTP".equalsIgnoreCase(protocol) || "REST".equalsIgnoreCase(protocol);
    }

    @Override
    public boolean sendCommand(String deviceCode, String command, String data) {
        log.info("[HTTP适配器] 发送指令: device={}, cmd={}, data={}", deviceCode, command, data);
        // 实际项目中这里调用 HTTP 客户端发送请求
        return true;
    }

    @Override
    public String receiveData(String deviceCode) {
        log.debug("[HTTP适配器] 接收数据: device={}", deviceCode);
        return null;
    }
}

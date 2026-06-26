package com.agri.common.pattern.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 适配器模式：MQTT 协议适配器
 * 将 MQTT 协议转换为统一接口
 */
@Slf4j
@Component
public class MqttProtocolAdapter implements VehicleCommAdapter {

    @Override
    public String name() {
        return "MQTT";
    }

    @Override
    public boolean support(String protocol) {
        return "MQTT".equalsIgnoreCase(protocol);
    }

    @Override
    public boolean sendCommand(String deviceCode, String command, String data) {
        log.info("[MQTT适配器] 发送指令: device={}, cmd={}, data={}", deviceCode, command, data);
        // 实际项目中这里调用 MQTT 客户端发送消息
        return true;
    }

    @Override
    public String receiveData(String deviceCode) {
        log.debug("[MQTT适配器] 接收数据: device={}", deviceCode);
        return null;
    }
}

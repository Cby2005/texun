package com.agri.common.pattern.adapter;

/**
 * 适配器模式：无人车通信协议适配器接口
 * 适配不同厂商的通信协议
 */
public interface VehicleCommAdapter {
    /** 适配器名称 */
    String name();
    /** 是否支持该协议 */
    boolean support(String protocol);
    /** 发送指令 */
    boolean sendCommand(String deviceCode, String command, String data);
    /** 接收数据 */
    String receiveData(String deviceCode);
}

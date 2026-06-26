package com.agri.common.pattern.observer;

/**
 * 观察者模式：车辆事件监听器接口
 * 设备状态变化时通知告警模块和监控大屏
 */
public interface VehicleEventListener {
    /** 处理车辆事件 */
    void onEvent(VehicleEvent event);
    /** 监听器名称 */
    String name();
}

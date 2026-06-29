package com.agriculture.drone.common.enums;

/**
 * 无人机状态枚举
 */
public enum DroneStatus {
    IDLE("空闲"),
    RUNNING("飞行中"),
    CHARGING("充电中"),
    ERROR("故障");

    private final String desc;

    DroneStatus(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}

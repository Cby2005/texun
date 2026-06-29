package com.agriculture.drone.common.enums;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    PENDING("待执行"),
    RUNNING("执行中"),
    FINISHED("已完成"),
    CANCELED("已取消"),
    ERROR("异常");

    private final String desc;

    TaskStatus(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}

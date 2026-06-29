package com.agriculture.drone.common.enums;

/**
 * 任务类型枚举
 */
public enum TaskType {
    DAILY_INSPECTION("日常巡检"),
    DISEASE_INSPECTION("病害巡检"),
    ABNORMAL_RECHECK("异常复核");

    private final String desc;

    TaskType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}

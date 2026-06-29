package com.agriculture.drone.common.enums;

/**
 * 巡检点类型枚举
 */
public enum PointType {
    NORMAL("普通巡检点"),
    DISEASE_HOTSPOT("病害高发点"),
    ENV_ABNORMAL("环境异常点");

    private final String desc;

    PointType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}

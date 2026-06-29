package com.agriculture.drone.common.enums;

/**
 * 路径类型枚举
 */
public enum RouteType {
    FULL_COVER("全覆盖巡检"),
    FIXED_POINT("定点巡检"),
    ABNORMAL_RECHECK("异常复核");

    private final String desc;

    RouteType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}

package com.agriculture.drone.common.enums;

/**
 * 算法类型枚举
 */
public enum AlgorithmType {
    ORDER("按顺序"),
    NEAREST("最近邻");

    private final String desc;

    AlgorithmType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}

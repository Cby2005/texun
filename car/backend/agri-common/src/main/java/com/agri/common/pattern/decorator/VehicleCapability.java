package com.agri.common.pattern.decorator;

/**
 * 装饰器模式：设备能力接口
 * 支持动态扩展设备能力（如能耗统计、故障预测）
 */
public interface VehicleCapability {
    /** 获取能力描述 */
    String describe();
    /** 执行能力 */
    Object execute(Object input);
}

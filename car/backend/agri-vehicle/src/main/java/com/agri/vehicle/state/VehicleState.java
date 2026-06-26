package com.agri.vehicle.state;

import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;

/**
 * 无人车状态接口 - 状态模式
 * 定义设备在不同状态下的行为
 */
public interface VehicleState {

    /**
     * 获取状态名称
     */
    String getStateName();

    /**
     * 上线操作
     */
    void online(VehicleDevice device, VehicleStateContext context);

    /**
     * 下线操作
     */
    void offline(VehicleDevice device, VehicleStateContext context);

    /**
     * 开始作业
     */
    void startWork(VehicleDevice device, VehicleStateContext context);

    /**
     * 停止作业
     */
    void stopWork(VehicleDevice device, VehicleStateContext context);

    /**
     * 返航
     */
    void returnToBase(VehicleDevice device, VehicleStateContext context);

    /**
     * 紧急停车
     */
    void emergencyStop(VehicleDevice device, VehicleStateContext context);

    /**
     * 报告故障
     */
    void reportFault(VehicleDevice device, VehicleStateContext context);

    /**
     * 进入维护
     */
    void startMaintenance(VehicleDevice device, VehicleStateContext context);

    /**
     * 完成维护
     */
    void completeMaintenance(VehicleDevice device, VehicleStateContext context);

    /**
     * 报废
     */
    void scrap(VehicleDevice device, VehicleStateContext context);
}

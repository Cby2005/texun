package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 在线状态 - 设备已上线，等待任务
 */
@Slf4j
public class OnlineState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_ONLINE;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 已经在线", device.getDeviceCode());
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_OFFLINE);
        log.info("设备 {} 已离线", device.getDeviceCode());
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_WORKING);
        log.info("设备 {} 开始作业", device.getDeviceCode());
    }

    @Override
    public void stopWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备未在作业中");
    }

    @Override
    public void returnToBase(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备未在作业中，无需返航");
    }

    @Override
    public void emergencyStop(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备未运行，无需紧急停车");
    }

    @Override
    public void reportFault(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_FAULT);
        log.warn("设备 {} 报告故障", device.getDeviceCode());
    }

    @Override
    public void startMaintenance(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_MAINTENANCE);
    }

    @Override
    public void completeMaintenance(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备未在维护中");
    }

    @Override
    public void scrap(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_SCRAPPED);
    }
}

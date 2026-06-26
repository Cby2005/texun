package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 故障状态
 */
@Slf4j
public class FaultState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_FAULT;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备故障中，无法上线，请先修复故障");
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备故障中，无法下线");
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备故障中，无法开始作业");
    }

    @Override
    public void stopWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备故障中，未在作业");
    }

    @Override
    public void returnToBase(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备故障中，无法返航");
    }

    @Override
    public void emergencyStop(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 已经处于故障状态", device.getDeviceCode());
    }

    @Override
    public void reportFault(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 已经处于故障状态", device.getDeviceCode());
    }

    @Override
    public void startMaintenance(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_MAINTENANCE);
        log.info("设备 {} 开始维护", device.getDeviceCode());
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

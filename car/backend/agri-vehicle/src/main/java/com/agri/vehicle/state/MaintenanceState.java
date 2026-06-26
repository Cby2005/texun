package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 维护中状态
 */
@Slf4j
public class MaintenanceState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_MAINTENANCE;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备维护中，无法上线");
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备维护中，无法下线");
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备维护中，无法开始作业");
    }

    @Override
    public void stopWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备维护中，未在作业");
    }

    @Override
    public void returnToBase(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备维护中，无法返航");
    }

    @Override
    public void emergencyStop(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 处于维护状态，无需紧急停车", device.getDeviceCode());
    }

    @Override
    public void reportFault(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备维护中，无法报告故障");
    }

    @Override
    public void startMaintenance(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 已经在维护中", device.getDeviceCode());
    }

    @Override
    public void completeMaintenance(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_IDLE);
        log.info("设备 {} 维护完成，恢复空闲状态", device.getDeviceCode());
    }

    @Override
    public void scrap(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_SCRAPPED);
    }
}

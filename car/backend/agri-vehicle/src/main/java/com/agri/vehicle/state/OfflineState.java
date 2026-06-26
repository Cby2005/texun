package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 离线状态
 */
@Slf4j
public class OfflineState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_OFFLINE;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_ONLINE);
        log.info("设备 {} 重新上线", device.getDeviceCode());
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 已经离线", device.getDeviceCode());
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备离线，无法开始作业");
    }

    @Override
    public void stopWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备离线，无法停止作业");
    }

    @Override
    public void returnToBase(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备离线，无法返航");
    }

    @Override
    public void emergencyStop(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备离线，无法紧急停车");
    }

    @Override
    public void reportFault(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_FAULT);
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

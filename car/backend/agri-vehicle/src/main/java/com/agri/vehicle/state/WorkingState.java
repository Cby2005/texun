package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 作业中状态
 */
@Slf4j
public class WorkingState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_WORKING;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 正在作业中，无需重复上线", device.getDeviceCode());
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备正在作业中，无法直接下线，请先停止作业");
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已在作业中");
    }

    @Override
    public void stopWork(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_ONLINE);
        log.info("设备 {} 停止作业", device.getDeviceCode());
    }

    @Override
    public void returnToBase(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_ONLINE);
        log.info("设备 {} 返航中", device.getDeviceCode());
    }

    @Override
    public void emergencyStop(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_IDLE);
        log.warn("设备 {} 紧急停车", device.getDeviceCode());
    }

    @Override
    public void reportFault(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_FAULT);
        log.error("设备 {} 作业中发生故障", device.getDeviceCode());
    }

    @Override
    public void startMaintenance(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备正在作业中，无法进入维护，请先停止作业");
    }

    @Override
    public void completeMaintenance(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备未在维护中");
    }

    @Override
    public void scrap(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备正在作业中，无法报废");
    }
}

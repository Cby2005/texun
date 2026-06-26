package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 空闲状态 - 设备已注册但未上线
 */
@Slf4j
public class IdleState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_IDLE;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        context.transition(device, Constants.VEHICLE_ONLINE);
        log.info("设备 {} 已上线", device.getDeviceCode());
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备当前处于空闲状态，无法下线");
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备未上线，无法开始作业");
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
        throw new BusinessException("设备未运行，无法紧急停车");
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

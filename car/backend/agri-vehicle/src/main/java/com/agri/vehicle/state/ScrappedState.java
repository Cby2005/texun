package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * 报废状态 - 终态，不允许任何操作
 */
@Slf4j
public class ScrappedState implements VehicleState {

    @Override
    public String getStateName() {
        return Constants.VEHICLE_SCRAPPED;
    }

    @Override
    public void online(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void offline(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void startWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void stopWork(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void returnToBase(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void emergencyStop(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void reportFault(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void startMaintenance(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void completeMaintenance(VehicleDevice device, VehicleStateContext context) {
        throw new BusinessException("设备已报废，无法操作");
    }

    @Override
    public void scrap(VehicleDevice device, VehicleStateContext context) {
        log.warn("设备 {} 已经报废", device.getDeviceCode());
    }
}

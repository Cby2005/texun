package com.agri.vehicle.service;

import com.agri.vehicle.dto.DeviceRegisterDTO;
import com.agri.vehicle.dto.DeviceUpdateDTO;
import com.agri.vehicle.entity.VehicleDevice;
import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.vehicle.vo.DeviceStatsVO;

import java.util.List;

/**
 * 无人车设备服务接口
 */
public interface VehicleService {

    /**
     * 设备注册
     */
    void register(DeviceRegisterDTO dto);

    /**
     * 分页查询设备
     */
    PageResult<VehicleDevice> listDevices(PageQuery query, String keyword, String status, Long farmId);

    /**
     * 获取设备详情
     */
    VehicleDevice getDevice(Long id);

    /**
     * 更新设备信息
     */
    void updateDevice(DeviceUpdateDTO dto);

    /**
     * 删除设备
     */
    void deleteDevice(Long id);

    /**
     * 设备上线
     */
    void online(Long deviceId);

    /**
     * 设备离线
     */
    void offline(Long deviceId);

    /**
     * 开始作业
     */
    void startWork(Long deviceId);

    /**
     * 停止作业
     */
    void stopWork(Long deviceId);

    /**
     * 返航
     */
    void returnToBase(Long deviceId);

    /**
     * 紧急停车
     */
    void emergencyStop(Long deviceId);

    /**
     * 报告故障
     */
    void reportFault(Long deviceId, String faultCode, String faultType, String description);

    /**
     * 进入维护
     */
    void startMaintenance(Long deviceId);

    /**
     * 完成维护
     */
    void completeMaintenance(Long deviceId);

    /**
     * 报废
     */
    void scrap(Long deviceId);

    /**
     * 获取在线设备列表
     */
    List<VehicleDevice> getOnlineDevices();

    /**
     * 获取指定农场的设备列表
     */
    List<VehicleDevice> getDevicesByFarm(Long farmId);

    /**
     * 设备心跳上报
     */
    void heartbeat(Long deviceId);

    /**
     * 获取设备统计信息
     */
    DeviceStatsVO getDeviceStats();
}

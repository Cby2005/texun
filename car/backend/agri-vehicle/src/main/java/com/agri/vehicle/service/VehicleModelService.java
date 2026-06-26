package com.agri.vehicle.service;

import com.agri.vehicle.dto.ModelDTO;
import com.agri.vehicle.entity.VehicleModel;

import java.util.List;

/**
 * 设备型号服务接口
 */
public interface VehicleModelService {

    /**
     * 获取所有设备型号
     */
    List<VehicleModel> listAll();

    /**
     * 根据车辆类型查询
     */
    List<VehicleModel> listByType(String vehicleType);

    /**
     * 获取型号详情
     */
    VehicleModel getById(Long id);

    /**
     * 创建型号
     */
    void create(ModelDTO dto);

    /**
     * 更新型号
     */
    void update(ModelDTO dto);

    /**
     * 删除型号
     */
    void delete(Long id);
}

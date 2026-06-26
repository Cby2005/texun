package com.agri.vehicle.service;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.vehicle.entity.VehicleFaultRecord;

/**
 * 故障记录服务接口
 */
public interface VehicleFaultService {

    /**
     * 分页查询故障记录
     */
    PageResult<VehicleFaultRecord> listFaults(PageQuery query, Long vehicleId, String faultType, String status);

    /**
     * 获取故障详情
     */
    VehicleFaultRecord getById(Long id);

    /**
     * 解决故障
     */
    void resolveFault(Long id, String resolution);
}

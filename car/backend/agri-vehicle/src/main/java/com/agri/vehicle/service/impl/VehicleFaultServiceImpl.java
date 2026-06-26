package com.agri.vehicle.service.impl;

import com.agri.common.dto.PageQuery;
import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.common.vo.PageResult;
import com.agri.vehicle.entity.VehicleFaultRecord;
import com.agri.vehicle.mapper.VehicleFaultRecordMapper;
import com.agri.vehicle.service.VehicleFaultService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 故障记录服务实现
 */
@Service
@RequiredArgsConstructor
public class VehicleFaultServiceImpl implements VehicleFaultService {

    private final VehicleFaultRecordMapper faultRecordMapper;

    @Override
    public PageResult<VehicleFaultRecord> listFaults(PageQuery query, Long vehicleId, String faultType, String status) {
        LambdaQueryWrapper<VehicleFaultRecord> wrapper = new LambdaQueryWrapper<>();
        if (vehicleId != null) {
            wrapper.eq(VehicleFaultRecord::getVehicleId, vehicleId);
        }
        if (StringUtils.hasText(faultType)) {
            wrapper.eq(VehicleFaultRecord::getFaultType, faultType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(VehicleFaultRecord::getStatus, status);
        }
        wrapper.orderByDesc(VehicleFaultRecord::getReportedTime);

        Page<VehicleFaultRecord> page = faultRecordMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), query.getPageNum(), query.getPageSize(), page.getRecords());
    }

    @Override
    public VehicleFaultRecord getById(Long id) {
        VehicleFaultRecord record = faultRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return record;
    }

    @Override
    public void resolveFault(Long id, String resolution) {
        VehicleFaultRecord record = getById(id);
        record.setStatus("RESOLVED");
        record.setResolution(resolution);
        record.setResolvedTime(LocalDateTime.now());
        faultRecordMapper.updateById(record);
    }
}

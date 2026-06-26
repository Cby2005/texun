package com.agri.vehicle.service.impl;

import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.dto.ModelDTO;
import com.agri.vehicle.entity.VehicleDevice;
import com.agri.vehicle.entity.VehicleModel;
import com.agri.vehicle.mapper.VehicleDeviceMapper;
import com.agri.vehicle.mapper.VehicleModelMapper;
import com.agri.vehicle.service.VehicleModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备型号服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleModelServiceImpl implements VehicleModelService {

    private final VehicleModelMapper modelMapper;
    private final VehicleDeviceMapper deviceMapper;

    @Override
    public List<VehicleModel> listAll() {
        return modelMapper.selectList(new LambdaQueryWrapper<VehicleModel>()
                .orderByDesc(VehicleModel::getCreateTime));
    }

    @Override
    public List<VehicleModel> listByType(String vehicleType) {
        return modelMapper.selectList(new LambdaQueryWrapper<VehicleModel>()
                .eq(VehicleModel::getVehicleType, vehicleType)
                .orderByDesc(VehicleModel::getCreateTime));
    }

    @Override
    public VehicleModel getById(Long id) {
        VehicleModel model = modelMapper.selectById(id);
        if (model == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return model;
    }

    @Override
    public void create(ModelDTO dto) {
        // 校验型号编码唯一性
        Long count = modelMapper.selectCount(
                new LambdaQueryWrapper<VehicleModel>()
                        .eq(VehicleModel::getModelCode, dto.getModelCode()));
        if (count > 0) {
            throw new BusinessException("型号编码已存在");
        }

        VehicleModel model = new VehicleModel();
        model.setModelCode(dto.getModelCode());
        model.setModelName(dto.getModelName());
        model.setManufacturer(dto.getManufacturer());
        model.setVehicleType(dto.getVehicleType());
        model.setMaxSpeed(dto.getMaxSpeed());
        model.setBatteryCapacity(dto.getBatteryCapacity());
        model.setMaxPayload(dto.getMaxPayload());
        model.setWorkWidth(dto.getWorkWidth());
        model.setTurnRadius(dto.getTurnRadius());
        model.setSpecs(dto.getSpecs());
        modelMapper.insert(model);

        log.info("设备型号创建成功: code={}, name={}", dto.getModelCode(), dto.getModelName());
    }

    @Override
    public void update(ModelDTO dto) {
        VehicleModel existing = modelMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 如果修改了编码，检查唯一性
        if (!existing.getModelCode().equals(dto.getModelCode())) {
            Long count = modelMapper.selectCount(
                    new LambdaQueryWrapper<VehicleModel>()
                            .eq(VehicleModel::getModelCode, dto.getModelCode())
                            .ne(VehicleModel::getId, dto.getId()));
            if (count > 0) {
                throw new BusinessException("型号编码已存在");
            }
        }

        existing.setModelCode(dto.getModelCode());
        existing.setModelName(dto.getModelName());
        existing.setManufacturer(dto.getManufacturer());
        existing.setVehicleType(dto.getVehicleType());
        existing.setMaxSpeed(dto.getMaxSpeed());
        existing.setBatteryCapacity(dto.getBatteryCapacity());
        existing.setMaxPayload(dto.getMaxPayload());
        existing.setWorkWidth(dto.getWorkWidth());
        existing.setTurnRadius(dto.getTurnRadius());
        existing.setSpecs(dto.getSpecs());
        modelMapper.updateById(existing);

        log.info("设备型号更新成功: id={}", dto.getId());
    }

    @Override
    public void delete(Long id) {
        // 检查是否有设备使用此型号
        Long deviceCount = deviceMapper.selectCount(
                new LambdaQueryWrapper<VehicleDevice>()
                        .eq(VehicleDevice::getModelId, id));
        if (deviceCount > 0) {
            throw new BusinessException("该型号下存在设备，无法删除");
        }
        modelMapper.deleteById(id);
        log.info("设备型号删除成功: id={}", id);
    }
}

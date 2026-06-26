package com.agri.vehicle.service.impl;

import com.agri.common.constant.Constants;
import com.agri.common.dto.PageQuery;
import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.common.vo.PageResult;
import com.agri.vehicle.dto.DeviceRegisterDTO;
import com.agri.vehicle.dto.DeviceUpdateDTO;
import com.agri.vehicle.entity.VehicleDevice;
import com.agri.vehicle.entity.VehicleFaultRecord;
import com.agri.vehicle.mapper.VehicleDeviceMapper;
import com.agri.vehicle.mapper.VehicleFaultRecordMapper;
import com.agri.vehicle.service.VehicleService;
import com.agri.vehicle.state.VehicleStateContext;
import com.agri.vehicle.vo.DeviceStatsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 无人车设备服务实现
 *
 * Redis Key 设计：
 *   agri:vehicle:status:{deviceId}  ->  设备状态字符串  过期时间: 无（持久缓存）
 *   agri:vehicle:heartbeat:{deviceId} -> "1"           过期时间: 60秒（心跳检测）
 *
 * 索引设计：
 *   vehicle_device.device_code  ->  UNIQUE（设备编码查询）
 *   vehicle_device.status       ->  INDEX（状态筛选）
 *   vehicle_device.farm_id      ->  INDEX（农场筛选）
 *   vehicle_device.model_id     ->  INDEX（型号筛选）
 *   vehicle_fault_record.vehicle_id -> INDEX（设备故障查询）
 *   vehicle_fault_record.status     -> INDEX（故障状态筛选）
 *
 * Kafka Topic 说明：
 *   vehicle.alarm      -> 设备状态变更 + 故障告警
 *   vehicle.command.ack -> 指令确认回执
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleDeviceMapper deviceMapper;
    private final VehicleFaultRecordMapper faultRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private VehicleStateContext stateContext;

    @PostConstruct
    public void init() {
        this.stateContext = new VehicleStateContext(deviceMapper, redisTemplate, kafkaTemplate);
    }

    @Override
    @Transactional
    public void register(DeviceRegisterDTO dto) {
        // 校验设备编码唯一性
        Long count = deviceMapper.selectCount(
                new LambdaQueryWrapper<VehicleDevice>()
                        .eq(VehicleDevice::getDeviceCode, dto.getDeviceCode()));
        if (count > 0) {
            throw new BusinessException("设备编码已存在");
        }

        VehicleDevice device = new VehicleDevice();
        device.setDeviceCode(dto.getDeviceCode());
        device.setDeviceName(dto.getDeviceName());
        device.setModelId(dto.getModelId());
        device.setFarmId(dto.getFarmId());
        device.setSimCard(dto.getSimCard());
        device.setConfigParams(dto.getConfigParams());
        device.setStatus(Constants.VEHICLE_IDLE);
        deviceMapper.insert(device);

        // 初始化Redis缓存
        redisTemplate.opsForValue().set(
                Constants.REDIS_VEHICLE_STATUS + device.getId(),
                Constants.VEHICLE_IDLE
        );

        log.info("设备注册成功: code={}, id={}", dto.getDeviceCode(), device.getId());
    }

    @Override
    public PageResult<VehicleDevice> listDevices(PageQuery query, String keyword, String status, Long farmId) {
        LambdaQueryWrapper<VehicleDevice> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(VehicleDevice::getDeviceCode, keyword)
                    .or().like(VehicleDevice::getDeviceName, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(VehicleDevice::getStatus, status);
        }
        if (farmId != null) {
            wrapper.eq(VehicleDevice::getFarmId, farmId);
        }
        wrapper.orderByDesc(VehicleDevice::getCreateTime);

        Page<VehicleDevice> page = deviceMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), query.getPageNum(), query.getPageSize(), page.getRecords());
    }

    @Override
    public VehicleDevice getDevice(Long id) {
        VehicleDevice device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ResultCode.VEHICLE_NOT_FOUND);
        }
        return device;
    }

    @Override
    public void updateDevice(DeviceUpdateDTO dto) {
        VehicleDevice existing = deviceMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.VEHICLE_NOT_FOUND);
        }

        if (dto.getDeviceName() != null) existing.setDeviceName(dto.getDeviceName());
        if (dto.getFarmId() != null) existing.setFarmId(dto.getFarmId());
        if (dto.getFirmwareVersion() != null) existing.setFirmwareVersion(dto.getFirmwareVersion());
        if (dto.getSimCard() != null) existing.setSimCard(dto.getSimCard());
        if (dto.getConfigParams() != null) existing.setConfigParams(dto.getConfigParams());

        deviceMapper.updateById(existing);
        log.info("设备信息更新成功: id={}", dto.getId());
    }

    @Override
    public void deleteDevice(Long id) {
        VehicleDevice device = getDevice(id);
        // 工作中或在线设备不允许删除
        if (Constants.VEHICLE_WORKING.equals(device.getStatus()) ||
                Constants.VEHICLE_ONLINE.equals(device.getStatus())) {
            throw new BusinessException("设备处于工作/在线状态，无法删除，请先下线");
        }
        deviceMapper.deleteById(id);
        redisTemplate.delete(Constants.REDIS_VEHICLE_STATUS + id);
        log.info("设备删除成功: id={}", id);
    }

    @Override
    @Transactional
    public void online(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).online(device, stateContext);
        device.setLastOnlineTime(LocalDateTime.now());
        deviceMapper.updateById(device);
    }

    @Override
    @Transactional
    public void offline(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).offline(device, stateContext);
    }

    @Override
    @Transactional
    public void startWork(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).startWork(device, stateContext);
    }

    @Override
    @Transactional
    public void stopWork(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).stopWork(device, stateContext);
    }

    @Override
    @Transactional
    public void returnToBase(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).returnToBase(device, stateContext);
    }

    @Override
    @Transactional
    public void emergencyStop(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).emergencyStop(device, stateContext);
    }

    @Override
    @Transactional
    public void reportFault(Long deviceId, String faultCode, String faultType, String description) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).reportFault(device, stateContext);

        VehicleFaultRecord record = new VehicleFaultRecord();
        record.setVehicleId(deviceId);
        record.setFaultCode(faultCode);
        record.setFaultType(faultType);
        record.setFaultLevel("MEDIUM");
        record.setDescription(description);
        record.setStatus("OPEN");
        record.setReportedTime(LocalDateTime.now());
        faultRecordMapper.insert(record);

        // 发送故障告警到Kafka
        kafkaTemplate.send(Constants.TOPIC_VEHICLE_ALARM,
                String.format("{\"vehicleId\":%d,\"faultCode\":\"%s\",\"faultType\":\"%s\",\"description\":\"%s\",\"timestamp\":%d}",
                        deviceId, faultCode, faultType, description, System.currentTimeMillis()));

        log.warn("设备故障报告: deviceId={}, faultCode={}", deviceId, faultCode);
    }

    @Override
    @Transactional
    public void startMaintenance(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).startMaintenance(device, stateContext);
    }

    @Override
    @Transactional
    public void completeMaintenance(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).completeMaintenance(device, stateContext);
    }

    @Override
    @Transactional
    public void scrap(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        stateContext.getState(device.getStatus()).scrap(device, stateContext);
    }

    @Override
    public List<VehicleDevice> getOnlineDevices() {
        return deviceMapper.selectList(new LambdaQueryWrapper<VehicleDevice>()
                .in(VehicleDevice::getStatus, Arrays.asList(
                        Constants.VEHICLE_ONLINE, Constants.VEHICLE_WORKING)));
    }

    @Override
    public List<VehicleDevice> getDevicesByFarm(Long farmId) {
        return deviceMapper.selectList(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getFarmId, farmId)
                .orderByDesc(VehicleDevice::getCreateTime));
    }

    @Override
    public void heartbeat(Long deviceId) {
        VehicleDevice device = getDevice(deviceId);
        // 更新心跳（Key: agri:vehicle:heartbeat:{deviceId}，过期60秒）
        redisTemplate.opsForValue().set(
                "agri:vehicle:heartbeat:" + deviceId,
                "1",
                60,
                TimeUnit.SECONDS
        );
        // 更新最后上线时间
        device.setLastOnlineTime(LocalDateTime.now());
        deviceMapper.updateById(device);
    }

    @Override
    public DeviceStatsVO getDeviceStats() {
        DeviceStatsVO stats = new DeviceStatsVO();

        Long total = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .ne(VehicleDevice::getStatus, Constants.VEHICLE_SCRAPPED));
        stats.setTotalCount(total);

        Long online = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getStatus, Constants.VEHICLE_ONLINE));
        stats.setOnlineCount(online);

        Long working = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getStatus, Constants.VEHICLE_WORKING));
        stats.setWorkingCount(working);

        Long offline = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getStatus, Constants.VEHICLE_OFFLINE));
        stats.setOfflineCount(offline);

        Long fault = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getStatus, Constants.VEHICLE_FAULT));
        stats.setFaultCount(fault);

        Long maintenance = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getStatus, Constants.VEHICLE_MAINTENANCE));
        stats.setMaintenanceCount(maintenance);

        Long idle = deviceMapper.selectCount(new LambdaQueryWrapper<VehicleDevice>()
                .eq(VehicleDevice::getStatus, Constants.VEHICLE_IDLE));
        stats.setIdleCount(idle);

        // 计算在线率
        if (total > 0) {
            stats.setOnlineRate(Math.round((online + working) * 10000.0 / total) / 100.0);
        } else {
            stats.setOnlineRate(0.0);
        }

        return stats;
    }
}

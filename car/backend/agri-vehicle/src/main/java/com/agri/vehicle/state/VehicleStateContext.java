package com.agri.vehicle.state;

import com.agri.common.constant.Constants;
import com.agri.common.exception.BusinessException;
import com.agri.vehicle.entity.VehicleDevice;
import com.agri.vehicle.mapper.VehicleDeviceMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 无人车状态上下文 - 管理状态流转
 */
@Slf4j
@Data
public class VehicleStateContext {

    private final VehicleDeviceMapper deviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Map<String, VehicleState> STATE_MAP = new HashMap<>();

    static {
        STATE_MAP.put(Constants.VEHICLE_IDLE, new IdleState());
        STATE_MAP.put(Constants.VEHICLE_ONLINE, new OnlineState());
        STATE_MAP.put(Constants.VEHICLE_OFFLINE, new OfflineState());
        STATE_MAP.put(Constants.VEHICLE_WORKING, new WorkingState());
        STATE_MAP.put(Constants.VEHICLE_FAULT, new FaultState());
        STATE_MAP.put(Constants.VEHICLE_MAINTENANCE, new MaintenanceState());
        STATE_MAP.put(Constants.VEHICLE_SCRAPPED, new ScrappedState());
    }

    public VehicleStateContext(VehicleDeviceMapper deviceMapper,
                                RedisTemplate<String, Object> redisTemplate,
                                KafkaTemplate<String, String> kafkaTemplate) {
        this.deviceMapper = deviceMapper;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 获取当前状态处理器
     */
    public VehicleState getState(String status) {
        VehicleState state = STATE_MAP.get(status);
        if (state == null) {
            throw new BusinessException("未知的设备状态: " + status);
        }
        return state;
    }

    /**
     * 执行状态转换
     */
    public void transition(VehicleDevice device, String newStatus) {
        String oldStatus = device.getStatus();
        device.setStatus(newStatus);
        deviceMapper.updateById(device);

        // 更新Redis缓存
        redisTemplate.opsForValue().set(
                Constants.REDIS_VEHICLE_STATUS + device.getId(),
                newStatus
        );

        log.info("设备 {} 状态变更: {} -> {}", device.getDeviceCode(), oldStatus, newStatus);

        // 发送状态变更事件到Kafka
        kafkaTemplate.send(Constants.TOPIC_VEHICLE_ALARM,
                String.format("{\"vehicleId\":%d,\"oldStatus\":\"%s\",\"newStatus\":\"%s\",\"timestamp\":%d}",
                        device.getId(), oldStatus, newStatus, System.currentTimeMillis()));
    }
}

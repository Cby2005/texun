package com.agri.telemetry.kafka;

import com.agri.common.constant.Constants;
import com.agri.telemetry.entity.TelemetryData;
import com.agri.telemetry.mapper.TelemetryDataMapper;
import com.agri.telemetry.websocket.TelemetryWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 遥测数据 Kafka 消费者
 * 消费无人车上报的遥测数据，写入MySQL和Redis，并通过WebSocket推送
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryKafkaConsumer {

    private final TelemetryDataMapper telemetryDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TelemetryWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    /**
     * 消费原始遥测数据
     */
    @KafkaListener(topics = Constants.TOPIC_TELEMETRY_RAW, groupId = "agri-telemetry-raw")
    public void consumeRawTelemetry(ConsumerRecord<String, String> record) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(record.value(), Map.class);

            Long vehicleId = Long.valueOf(data.get("vehicleId").toString());

            // 构建遥测数据实体
            TelemetryData telemetry = new TelemetryData();
            telemetry.setVehicleId(vehicleId);
            telemetry.setLatitude(getDouble(data, "latitude"));
            telemetry.setLongitude(getDouble(data, "longitude"));
            telemetry.setAltitude(getDouble(data, "altitude"));
            telemetry.setSpeed(getDouble(data, "speed"));
            telemetry.setDirection(getDouble(data, "direction"));
            telemetry.setBatteryLevel(getDouble(data, "batteryLevel"));
            telemetry.setBatteryVoltage(getDouble(data, "batteryVoltage"));
            telemetry.setCpuUsage(getDouble(data, "cpuUsage"));
            telemetry.setMemoryUsage(getDouble(data, "memoryUsage"));
            telemetry.setNetworkSignal(getInt(data, "networkSignal"));
            telemetry.setTaskProgress(getDouble(data, "taskProgress"));

            if (data.containsKey("sensorStatus")) {
                telemetry.setSensorStatus(objectMapper.writeValueAsString(data.get("sensorStatus")));
            }
            if (data.containsKey("extraData")) {
                telemetry.setExtraData(objectMapper.writeValueAsString(data.get("extraData")));
            }

            telemetry.setReportTime(LocalDateTime.now());
            telemetry.setCreateTime(LocalDateTime.now());

            // 写入MySQL
            telemetryDataMapper.insert(telemetry);

            // 更新Redis中的最新状态
            String redisKey = Constants.REDIS_VEHICLE_TELEMETRY + vehicleId;
            redisTemplate.opsForValue().set(redisKey, data, 5, TimeUnit.MINUTES);

            // 更新位置缓存
            if (telemetry.getLatitude() != null && telemetry.getLongitude() != null) {
                String locationKey = Constants.REDIS_VEHICLE_LOCATION + vehicleId;
                redisTemplate.opsForValue().set(locationKey,
                        String.format("{\"lat\":%f,\"lng\":%f}", telemetry.getLatitude(), telemetry.getLongitude()),
                        5, TimeUnit.MINUTES);
            }

            // 通过WebSocket推送实时数据
            webSocketHandler.pushTelemetryData(vehicleId, objectMapper.writeValueAsString(data));

            log.debug("遥测数据处理完成: vehicleId={}", vehicleId);

        } catch (Exception e) {
            log.error("处理遥测数据失败: {}", record.value(), e);
        }
    }

    /**
     * 消费告警数据
     */
    @KafkaListener(topics = Constants.TOPIC_VEHICLE_ALARM, groupId = "agri-telemetry-alarm")
    public void consumeAlarm(ConsumerRecord<String, String> record) {
        try {
            log.info("收到告警数据: {}", record.value());
            // 广播告警到所有WebSocket客户端
            webSocketHandler.broadcastAlarm(record.value());
        } catch (Exception e) {
            log.error("处理告警数据失败: {}", record.value(), e);
        }
    }

    private Double getDouble(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? Double.parseDouble(value.toString()) : null;
    }

    private Integer getInt(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? Integer.parseInt(value.toString()) : null;
    }
}

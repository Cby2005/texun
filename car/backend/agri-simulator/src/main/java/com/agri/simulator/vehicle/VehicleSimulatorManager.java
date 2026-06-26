package com.agri.simulator.vehicle;

import com.agri.common.constant.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 无人车模拟器管理器
 *
 * Kafka Topic 说明：
 *   vehicle.telemetry.raw  -> 模拟器发送，agri-telemetry消费
 *   vehicle.location       -> 模拟器发送，agri-vehicle消费
 *   vehicle.command.ack    -> agri-task-command发送，模拟器消费并执行
 *
 * 调度说明：
 *   reportTelemetry: 每5秒执行一次，上报所有运行中设备的遥测数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleSimulatorManager {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${simulator.vehicle-count:10}")
    private int vehicleCount;

    @Value("${simulator.base-latitude:39.9042}")
    private double baseLatitude;

    @Value("${simulator.base-longitude:116.4074}")
    private double baseLongitude;

    private final Map<Long, VirtualVehicle> vehicles = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (int i = 1; i <= vehicleCount; i++) {
            VirtualVehicle vehicle = new VirtualVehicle(
                    (long) i,
                    "AGRI-" + String.format("%04d", i),
                    baseLatitude,
                    baseLongitude
            );
            vehicles.put((long) i, vehicle);
        }
        log.info("模拟器初始化完成: {} 台虚拟无人车", vehicleCount);
    }

    /**
     * 定时上报遥测数据（每5秒）
     */
    @Scheduled(fixedDelayString = "${simulator.report-interval-ms:5000}")
    public void reportTelemetry() {
        vehicles.values().stream()
                .filter(VirtualVehicle::isRunning)
                .forEach(vehicle -> {
                    vehicle.updateTelemetry();
                    try {
                        Map<String, Object> data = new HashMap<>();
                        data.put("vehicleId", vehicle.getVehicleId());
                        data.put("deviceCode", vehicle.getDeviceCode());
                        data.put("status", vehicle.getStatus());
                        data.put("latitude", vehicle.getLatitude());
                        data.put("longitude", vehicle.getLongitude());
                        data.put("altitude", vehicle.getAltitude());
                        data.put("speed", vehicle.getSpeed());
                        data.put("direction", vehicle.getDirection());
                        data.put("batteryLevel", vehicle.getBatteryLevel());
                        data.put("cpuUsage", vehicle.getCpuUsage());
                        data.put("memoryUsage", vehicle.getMemoryUsage());
                        data.put("networkSignal", vehicle.getNetworkSignal());
                        data.put("taskProgress", vehicle.getTaskProgress());
                        data.put("timestamp", System.currentTimeMillis());

                        String json = objectMapper.writeValueAsString(data);
                        kafkaTemplate.send(Constants.TOPIC_TELEMETRY_RAW, json);

                        // 发送位置信息
                        Map<String, Object> location = new HashMap<>();
                        location.put("vehicleId", vehicle.getVehicleId());
                        location.put("latitude", vehicle.getLatitude());
                        location.put("longitude", vehicle.getLongitude());
                        location.put("direction", vehicle.getDirection());
                        location.put("speed", vehicle.getSpeed());
                        kafkaTemplate.send(Constants.TOPIC_VEHICLE_LOCATION,
                                objectMapper.writeValueAsString(location));

                    } catch (Exception e) {
                        log.error("上报遥测数据失败: vehicleId={}", vehicle.getVehicleId(), e);
                    }
                });
    }

    /**
     * 消费指令消息
     */
    @KafkaListener(topics = Constants.TOPIC_COMMAND_ACK, groupId = "agri-simulator-cmd")
    public void consumeCommand(ConsumerRecord<String, String> record) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(record.value(), Map.class);
            Long vehicleId = Long.valueOf(data.get("vehicleId").toString());
            String commandType = (String) data.get("commandType");
            String commandCode = (String) data.get("commandCode");

            VirtualVehicle vehicle = vehicles.get(vehicleId);
            if (vehicle == null) {
                log.warn("模拟器: 未找到设备 {}", vehicleId);
                return;
            }

            log.info("模拟器: 设备 {} 收到指令 {} ({})", vehicle.getDeviceCode(), commandType, commandCode);

            // 处理指令
            switch (commandType != null ? commandType : "") {
                case "START":
                    vehicle.startTask(baseLatitude + (new Random().nextDouble() - 0.5) * 0.01,
                            baseLongitude + (new Random().nextDouble() - 0.5) * 0.01);
                    sendAck(commandCode, vehicleId, "RECEIVED", null);
                    sendAck(commandCode, vehicleId, "EXECUTING", null);
                    break;
                case "STOP":
                    vehicle.stopTask();
                    sendAck(commandCode, vehicleId, "SUCCESS", null);
                    break;
                case "PAUSE":
                    vehicle.stopTask();
                    sendAck(commandCode, vehicleId, "SUCCESS", null);
                    break;
                case "RESUME":
                    vehicle.startTask(baseLatitude + (new Random().nextDouble() - 0.5) * 0.01,
                            baseLongitude + (new Random().nextDouble() - 0.5) * 0.01);
                    sendAck(commandCode, vehicleId, "EXECUTING", null);
                    break;
                case "EMERGENCY_STOP":
                    vehicle.emergencyStop();
                    sendAck(commandCode, vehicleId, "SUCCESS", null);
                    break;
                case "RETURN":
                    vehicle.stopTask();
                    sendAck(commandCode, vehicleId, "SUCCESS", null);
                    break;
                default:
                    sendAck(commandCode, vehicleId, "RECEIVED", null);
            }
        } catch (Exception e) {
            log.error("模拟器: 处理指令失败", e);
        }
    }

    private void sendAck(String commandCode, Long vehicleId, String status, String errorMessage) {
        try {
            Map<String, Object> ack = new HashMap<>();
            ack.put("commandCode", commandCode);
            ack.put("vehicleId", vehicleId);
            ack.put("status", status);
            ack.put("errorMessage", errorMessage);
            ack.put("timestamp", System.currentTimeMillis());
            kafkaTemplate.send(Constants.TOPIC_COMMAND_ACK, objectMapper.writeValueAsString(ack));
        } catch (Exception e) {
            log.error("发送ACK失败", e);
        }
    }

    // ========== 控制接口 ==========

    public void startVehicle(Long vehicleId) {
        VirtualVehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) vehicle.start();
    }

    public void stopVehicle(Long vehicleId) {
        VirtualVehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) vehicle.stop();
    }

    public void startTask(Long vehicleId, double targetLat, double targetLng) {
        VirtualVehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) vehicle.startTask(targetLat, targetLng);
    }

    public void stopTask(Long vehicleId) {
        VirtualVehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) vehicle.stopTask();
    }

    public void emergencyStop(Long vehicleId) {
        VirtualVehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) vehicle.emergencyStop();
    }

    public void startAll() {
        vehicles.values().forEach(VirtualVehicle::start);
        log.info("模拟器: 启动所有设备");
    }

    public void stopAll() {
        vehicles.values().forEach(VirtualVehicle::stop);
        log.info("模拟器: 停止所有设备");
    }

    public Collection<VirtualVehicle> getAllVehicles() {
        return vehicles.values();
    }

    public VirtualVehicle getVehicle(Long vehicleId) {
        return vehicles.get(vehicleId);
    }

    public int getRunningCount() {
        return (int) vehicles.values().stream().filter(VirtualVehicle::isRunning).count();
    }
}

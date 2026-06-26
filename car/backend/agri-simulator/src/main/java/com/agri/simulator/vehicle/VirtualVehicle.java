package com.agri.simulator.vehicle;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 虚拟无人车 - 模拟真实车辆的状态和行为
 *
 * 模拟行为：
 * 1. 每5秒上报遥测数据（位置/速度/电量/系统状态）
 * 2. 作业时模拟移动（向目标位置移动）
 * 3. 随机漂移模拟GPS抖动
 * 4. 电量随作业消耗
 * 5. 随机故障模拟（0.1%概率）
 * 6. 低电量告警（<20%）
 * 7. 任务进度自动递增
 */
@Slf4j
@Data
public class VirtualVehicle {

    private Long vehicleId;
    private String deviceCode;
    private String status;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double direction;
    private double batteryLevel;
    private double cpuUsage;
    private double memoryUsage;
    private int networkSignal;
    private double taskProgress;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Random random = new Random();

    public boolean isRunning() {
        return running.get();
    }

    // 目标位置（用于模拟移动）
    private double targetLatitude;
    private double targetLongitude;
    private boolean hasTarget = false;

    public VirtualVehicle(Long vehicleId, String deviceCode, double baseLat, double baseLng) {
        this.vehicleId = vehicleId;
        this.deviceCode = deviceCode;
        this.latitude = baseLat + (random.nextDouble() - 0.5) * 0.01;
        this.longitude = baseLng + (random.nextDouble() - 0.5) * 0.01;
        this.altitude = 50 + random.nextDouble() * 20;
        this.speed = 0;
        this.direction = random.nextDouble() * 360;
        this.batteryLevel = 80 + random.nextDouble() * 20;
        this.status = "IDLE";
        this.cpuUsage = 10 + random.nextDouble() * 30;
        this.memoryUsage = 20 + random.nextDouble() * 40;
        this.networkSignal = -60 - random.nextInt(40);
        this.taskProgress = 0;
    }

    /**
     * 更新遥测数据（每5秒调用一次）
     */
    public void updateTelemetry() {
        if (!running.get()) return;

        // 更新位置
        if (hasTarget) {
            moveTowardsTarget();
        } else {
            // 随机漂移（模拟GPS抖动）
            latitude += (random.nextDouble() - 0.5) * 0.00001;
            longitude += (random.nextDouble() - 0.5) * 0.00001;
        }

        // 更新速度
        if ("WORKING".equals(status)) {
            speed = 3 + random.nextDouble() * 8; // 3-11 km/h
            taskProgress = Math.min(100, taskProgress + random.nextDouble() * 2);
            batteryLevel = Math.max(0, batteryLevel - random.nextDouble() * 0.5);
        } else if ("ONLINE".equals(status)) {
            speed = random.nextDouble() * 2;
        } else {
            speed = 0;
        }

        // 更新方向
        if (speed > 0) {
            direction = (direction + (random.nextDouble() - 0.5) * 20 + 360) % 360;
        }

        // 更新系统状态
        cpuUsage = Math.min(100, Math.max(0, cpuUsage + (random.nextDouble() - 0.5) * 10));
        memoryUsage = Math.min(100, Math.max(0, memoryUsage + (random.nextDouble() - 0.5) * 5));
        networkSignal = Math.min(-20, Math.max(-100, networkSignal + random.nextInt(5) - 2));

        // 模拟故障（0.1%概率）
        if (random.nextDouble() < 0.001) {
            status = "FAULT";
            log.warn("模拟器: 设备 {} 模拟故障", deviceCode);
        }

        // 模拟低电量告警
        if (batteryLevel < 20) {
            log.warn("模拟器: 设备 {} 低电量告警: {}%", deviceCode, String.format("%.1f", batteryLevel));
        }

        // 完成任务
        if (taskProgress >= 100) {
            taskProgress = 100;
            status = "ONLINE";
            hasTarget = false;
            log.info("模拟器: 设备 {} 任务完成", deviceCode);
        }
    }

    /**
     * 向目标位置移动
     */
    private void moveTowardsTarget() {
        double dlat = targetLatitude - latitude;
        double dlng = targetLongitude - longitude;
        double dist = Math.sqrt(dlat * dlat + dlng * dlng);

        if (dist < 0.00001) {
            hasTarget = false;
            return;
        }

        double step = 0.00005; // 移动步长
        latitude += dlat / dist * step;
        longitude += dlng / dist * step;
    }

    /**
     * 设置目标位置
     */
    public void setTarget(double lat, double lng) {
        this.targetLatitude = lat;
        this.targetLongitude = lng;
        this.hasTarget = true;
    }

    public void start() {
        running.set(true);
        status = "ONLINE";
        log.info("模拟器: 设备 {} 启动", deviceCode);
    }

    public void stop() {
        running.set(false);
        status = "OFFLINE";
        log.info("模拟器: 设备 {} 停止", deviceCode);
    }

    public void startTask(double targetLat, double targetLng) {
        status = "WORKING";
        taskProgress = 0;
        setTarget(targetLat, targetLng);
        log.info("模拟器: 设备 {} 开始作业", deviceCode);
    }

    public void stopTask() {
        status = "ONLINE";
        taskProgress = 0;
        hasTarget = false;
        log.info("模拟器: 设备 {} 停止作业", deviceCode);
    }

    public void emergencyStop() {
        status = "IDLE";
        speed = 0;
        taskProgress = 0;
        hasTarget = false;
        log.warn("模拟器: 设备 {} 紧急停车", deviceCode);
    }
}

package com.agriculture.drone.task.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.drone.device.entity.DroneDevice;
import com.agriculture.drone.device.mapper.DroneDeviceMapper;
import com.agriculture.drone.route.entity.DroneRoutePlan;
import com.agriculture.drone.route.mapper.DroneRoutePlanMapper;
import com.agriculture.drone.task.dto.TaskCreateRequest;
import com.agriculture.drone.task.entity.DroneInspectionTask;
import com.agriculture.drone.task.mapper.DroneInspectionTaskMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DroneInspectionTaskServiceImpl extends ServiceImpl<DroneInspectionTaskMapper, DroneInspectionTask> {

    private final DroneDeviceMapper deviceMapper;
    private final DroneRoutePlanMapper routeMapper;

    @Transactional
    public DroneInspectionTask createTask(TaskCreateRequest req) {
        DroneDevice drone = deviceMapper.selectById(req.getDroneId());
        if (drone == null) throw new BizException(400, "无人机不存在");
        DroneRoutePlan route = routeMapper.selectById(req.getRouteId());
        if (route == null) throw new BizException(400, "路径不存在");

        DroneInspectionTask task = new DroneInspectionTask();
        task.setTaskCode("T" + System.currentTimeMillis());
        task.setTaskName(req.getTaskName());
        task.setDroneId(req.getDroneId());
        task.setRouteId(req.getRouteId());
        task.setGreenhouseId(req.getGreenhouseId());
        task.setTaskType(req.getTaskType() != null ? req.getTaskType() : "DAILY_INSPECTION");
        task.setTaskStatus("PENDING");
        task.setRemark(req.getRemark());
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        save(task);
        return task;
    }

    @Transactional
    public void startTask(Long taskId) {
        DroneInspectionTask task = getById(taskId);
        if (task == null) throw new BizException(400, "任务不存在");
        if (!"PENDING".equals(task.getTaskStatus())) throw new BizException(400, "任务状态不是待执行，当前为: " + task.getTaskStatus());

        DroneDevice drone = deviceMapper.selectById(task.getDroneId());
        if (drone == null) throw new BizException(400, "无人机不存在");
        if (!"IDLE".equals(drone.getDeviceStatus())) throw new BizException(400, "无人机不在空闲状态，当前为: " + drone.getDeviceStatus());
        if (drone.getBatteryLevel().compareTo(BigDecimal.valueOf(20)) < 0)
            throw new BizException(400, "无人机电量低于20%，不允许开始任务");

        task.setTaskStatus("RUNNING");
        task.setStartTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        updateById(task);

        drone.setDeviceStatus("INSPECTING");
        drone.setCameraStatus("工作中");
        drone.setCurrentTaskId(taskId);
        drone.setUpdateTime(LocalDateTime.now());
        deviceMapper.updateById(drone);
    }

    @Transactional
    public void finishTask(Long taskId, String result) {
        DroneInspectionTask task = getById(taskId);
        if (task == null) throw new BizException(400, "任务不存在");
        if (!"RUNNING".equals(task.getTaskStatus())) throw new BizException(400, "任务状态不是执行中");

        task.setTaskStatus("FINISHED");
        task.setEndTime(LocalDateTime.now());
        task.setResult(result);
        task.setUpdateTime(LocalDateTime.now());
        updateById(task);

        DroneDevice drone = deviceMapper.selectById(task.getDroneId());
        if (drone != null) {
            drone.setDeviceStatus("IDLE");
            drone.setCameraStatus("未启动");
            drone.setCurrentTaskId(null);
            drone.setLastInspectionTime(LocalDateTime.now());
            drone.setTotalInspectionCount((drone.getTotalInspectionCount() != null ? drone.getTotalInspectionCount() : 0) + 1);
            drone.setUpdateTime(LocalDateTime.now());
            deviceMapper.updateById(drone);
        }
    }

    @Transactional
    public void cancelTask(Long taskId) {
        DroneInspectionTask task = getById(taskId);
        if (task == null) throw new BizException(400, "任务不存在");
        String status = task.getTaskStatus();
        if (!"PENDING".equals(status) && !"RUNNING".equals(status))
            throw new BizException(400, "当前状态不可取消: " + status);

        task.setTaskStatus("CANCELED");
        task.setEndTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        updateById(task);

        if ("RUNNING".equals(status)) {
            DroneDevice drone = deviceMapper.selectById(task.getDroneId());
            if (drone != null) {
                drone.setDeviceStatus("IDLE");
                drone.setCameraStatus("未启动");
                drone.setCurrentTaskId(null);
                drone.setUpdateTime(LocalDateTime.now());
                deviceMapper.updateById(drone);
            }
        }
    }
}

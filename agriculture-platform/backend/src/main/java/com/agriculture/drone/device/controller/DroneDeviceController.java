package com.agriculture.drone.device.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.drone.device.entity.DroneDevice;
import com.agriculture.drone.task.entity.DroneInspectionTask;
import com.agriculture.drone.task.mapper.DroneInspectionTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/drone/device")
@RequiredArgsConstructor
@Tag(name = "无人机设备管理")
public class DroneDeviceController {

    private final IService<DroneDevice> service;
    private final DroneInspectionTaskMapper taskMapper;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<PageResult<DroneDevice>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String droneName,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long greenhouseId,
            @RequestParam(required = false) String deviceStatus,
            @RequestParam(required = false) Integer supportDiseaseDetect) {
        LambdaQueryWrapper<DroneDevice> qw = new LambdaQueryWrapper<>();
        if (droneName != null && !droneName.isBlank())
            qw.and(w -> w.like(DroneDevice::getDroneName, droneName).or().like(DroneDevice::getDroneCode, droneName));
        if (baseId != null) qw.eq(DroneDevice::getBaseId, baseId);
        if (greenhouseId != null) qw.eq(DroneDevice::getGreenhouseId, greenhouseId);
        if (deviceStatus != null && !deviceStatus.isBlank()) qw.eq(DroneDevice::getDeviceStatus, deviceStatus);
        if (supportDiseaseDetect != null) qw.eq(DroneDevice::getSupportDiseaseDetect, supportDiseaseDetect);
        qw.orderByAsc(DroneDevice::getDroneCode);
        Page<DroneDevice> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DroneDevice> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    /** 查询可用无人机（空闲&电量充足&温室匹配） */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<List<DroneDevice>> available(
            @RequestParam(required = false) Long greenhouseId,
            @RequestParam(required = false) String taskType) {
        LambdaQueryWrapper<DroneDevice> qw = new LambdaQueryWrapper<>();
        qw.eq(DroneDevice::getDeviceStatus, "IDLE");
        qw.gt(DroneDevice::getBatteryLevel, BigDecimal.valueOf(30));
        if (greenhouseId != null) qw.eq(DroneDevice::getGreenhouseId, greenhouseId);
        if ("DISEASE_REVIEW".equals(taskType) || "ABNORMAL_PRIORITY".equals(taskType))
            qw.eq(DroneDevice::getSupportDiseaseDetect, 1);
        qw.orderByDesc(DroneDevice::getTotalInspectionCount);
        return Result.ok(service.list(qw));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<DroneDevice> add(@RequestBody DroneDevice d) {
        // 新增时自动初始化运行状态，不允许用户手动填写
        d.setBatteryLevel(BigDecimal.valueOf(100));
        d.setLongitude(BigDecimal.valueOf(0));
        d.setLatitude(BigDecimal.valueOf(0));
        d.setAltitude(BigDecimal.valueOf(0));
        d.setCameraStatus("未启动");
        d.setCurrentTaskId(null);
        d.setTotalInspectionCount(0);
        d.setLastInspectionTime(null);
        if (d.getDeviceStatus() == null) d.setDeviceStatus("IDLE");
        service.save(d);
        return Result.ok(d);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody DroneDevice d) {
        d.setId(id);
        // 编辑档案时不允许修改运行状态字段
        d.setBatteryLevel(null);
        d.setLongitude(null);
        d.setLatitude(null);
        d.setAltitude(null);
        d.setCameraStatus(null);
        d.setCurrentTaskId(null);
        d.setTotalInspectionCount(null);
        d.setLastInspectionTime(null);
        service.updateById(d);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

    /** 修改设备状态（启用/停用） */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("deviceStatus");
        DroneDevice d = new DroneDevice();
        d.setId(id);
        d.setDeviceStatus(status);
        service.updateById(d);
        return Result.ok();
    }

    /** 查询无人机巡检历史 */
    @GetMapping("/{id}/inspection-tasks")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<List<DroneInspectionTask>> inspectionTasks(@PathVariable Long id) {
        LambdaQueryWrapper<DroneInspectionTask> qw = new LambdaQueryWrapper<>();
        qw.eq(DroneInspectionTask::getDroneId, id).orderByDesc(DroneInspectionTask::getCreateTime);
        qw.last("LIMIT 20");
        return Result.ok(taskMapper.selectList(qw));
    }

    /** 更新无人机运行状态（供数字孪生巡检调用） */
    @PutMapping("/update-runtime/{id}")
    public Result<Void> updateRuntime(@PathVariable Long id, @RequestBody DroneDevice d) {
        DroneDevice update = new DroneDevice();
        update.setId(id);
        update.setBatteryLevel(d.getBatteryLevel());
        update.setLongitude(d.getLongitude());
        update.setLatitude(d.getLatitude());
        update.setAltitude(d.getAltitude());
        update.setCameraStatus(d.getCameraStatus());
        update.setDeviceStatus(d.getDeviceStatus());
        update.setCurrentTaskId(d.getCurrentTaskId());
        update.setTotalInspectionCount(d.getTotalInspectionCount());
        update.setLastInspectionTime(d.getLastInspectionTime() != null ? d.getLastInspectionTime()
                : (d.getTotalInspectionCount() != null ? java.time.LocalDateTime.now() : null));
        service.updateById(update);
        return Result.ok();
    }
}

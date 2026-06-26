package com.agri.simulator.controller;

import com.agri.common.vo.R;
import com.agri.simulator.vehicle.VehicleSimulatorManager;
import com.agri.simulator.vehicle.VirtualVehicle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * 模拟器控制接口
 *
 * 提供模拟器的完整控制能力：
 * - 启动/停止指定设备
 * - 启动/停止所有设备
 * - 指定设备开始作业
 * - 指定设备停止作业
 * - 指定设备紧急停车
 * - 获取设备状态
 * - 获取运行统计
 */
@Tag(name = "无人车模拟器控制", description = "控制虚拟无人车的启停、作业、状态查询")
@RestController
@RequestMapping("/simulator")
@RequiredArgsConstructor
public class SimulatorController {

    private final VehicleSimulatorManager simulatorManager;

    @Operation(summary = "启动指定设备")
    @PostMapping("/vehicle/{id}/start")
    public R<Void> startVehicle(@PathVariable Long id) {
        simulatorManager.startVehicle(id);
        return R.ok();
    }

    @Operation(summary = "停止指定设备")
    @PostMapping("/vehicle/{id}/stop")
    public R<Void> stopVehicle(@PathVariable Long id) {
        simulatorManager.stopVehicle(id);
        return R.ok();
    }

    @Operation(summary = "指定设备开始作业")
    @PostMapping("/vehicle/{id}/start-task")
    public R<Void> startTask(@PathVariable Long id,
                             @RequestParam double targetLat,
                             @RequestParam double targetLng) {
        simulatorManager.startTask(id, targetLat, targetLng);
        return R.ok();
    }

    @Operation(summary = "指定设备停止作业")
    @PostMapping("/vehicle/{id}/stop-task")
    public R<Void> stopTask(@PathVariable Long id) {
        simulatorManager.stopTask(id);
        return R.ok();
    }

    @Operation(summary = "指定设备紧急停车")
    @PostMapping("/vehicle/{id}/emergency-stop")
    public R<Void> emergencyStop(@PathVariable Long id) {
        simulatorManager.emergencyStop(id);
        return R.ok();
    }

    @Operation(summary = "启动所有设备")
    @PostMapping("/start-all")
    public R<Void> startAll() {
        simulatorManager.startAll();
        return R.ok();
    }

    @Operation(summary = "停止所有设备")
    @PostMapping("/stop-all")
    public R<Void> stopAll() {
        simulatorManager.stopAll();
        return R.ok();
    }

    @Operation(summary = "获取所有虚拟设备状态")
    @GetMapping("/vehicles")
    public R<Collection<VirtualVehicle>> getVehicles() {
        return R.ok(simulatorManager.getAllVehicles());
    }

    @Operation(summary = "获取指定设备状态")
    @GetMapping("/vehicle/{id}")
    public R<VirtualVehicle> getVehicle(@PathVariable Long id) {
        return R.ok(simulatorManager.getVehicle(id));
    }

    @Operation(summary = "获取运行中设备数量")
    @GetMapping("/running-count")
    public R<Integer> getRunningCount() {
        return R.ok(simulatorManager.getRunningCount());
    }
}

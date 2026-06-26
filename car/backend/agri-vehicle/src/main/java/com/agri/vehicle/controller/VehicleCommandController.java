package com.agri.vehicle.controller;

import com.agri.common.vo.R;
import com.agri.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备远程指令控制器
 *
 * 状态模式实现 - 不同状态下对同一指令有不同响应：
 *
 * 状态流转图：
 *   IDLE -> ONLINE -> WORKING -> ONLINE -> OFFLINE
 *   任何状态 -> FAULT -> MAINTENANCE -> IDLE
 *   任何状态（非WORKING） -> SCRAPPED（终态）
 *
 * 紧急停车：WORKING -> IDLE（跳过ONLINE）
 */
@Tag(name = "设备远程指令", description = "设备状态控制：上线/下线/作业/返航/故障/维护/报废")
@RestController
@RequestMapping("/device/command")
@RequiredArgsConstructor
public class VehicleCommandController {

    private final VehicleService vehicleService;

    @Operation(summary = "设备上线", description = "IDLE/OFFLINE -> ONLINE")
    @PostMapping("/{id}/online")
    public R<Void> online(@PathVariable Long id) {
        vehicleService.online(id);
        return R.ok();
    }

    @Operation(summary = "设备离线", description = "ONLINE -> OFFLINE")
    @PostMapping("/{id}/offline")
    public R<Void> offline(@PathVariable Long id) {
        vehicleService.offline(id);
        return R.ok();
    }

    @Operation(summary = "开始作业", description = "ONLINE -> WORKING")
    @PostMapping("/{id}/start-work")
    public R<Void> startWork(@PathVariable Long id) {
        vehicleService.startWork(id);
        return R.ok();
    }

    @Operation(summary = "停止作业", description = "WORKING -> ONLINE")
    @PostMapping("/{id}/stop-work")
    public R<Void> stopWork(@PathVariable Long id) {
        vehicleService.stopWork(id);
        return R.ok();
    }

    @Operation(summary = "返航", description = "WORKING -> ONLINE")
    @PostMapping("/{id}/return")
    public R<Void> returnToBase(@PathVariable Long id) {
        vehicleService.returnToBase(id);
        return R.ok();
    }

    @Operation(summary = "紧急停车", description = "WORKING -> IDLE（跳过ONLINE）")
    @PostMapping("/{id}/emergency-stop")
    public R<Void> emergencyStop(@PathVariable Long id) {
        vehicleService.emergencyStop(id);
        return R.ok();
    }

    @Operation(summary = "报告故障", description = "任意状态 -> FAULT")
    @PostMapping("/{id}/fault")
    public R<Void> reportFault(@PathVariable Long id,
                                @RequestParam String faultCode,
                                @RequestParam String faultType,
                                @RequestParam String description) {
        vehicleService.reportFault(id, faultCode, faultType, description);
        return R.ok();
    }

    @Operation(summary = "进入维护", description = "FAULT/IDLE/OFFLINE -> MAINTENANCE")
    @PostMapping("/{id}/maintenance")
    public R<Void> startMaintenance(@PathVariable Long id) {
        vehicleService.startMaintenance(id);
        return R.ok();
    }

    @Operation(summary = "完成维护", description = "MAINTENANCE -> IDLE")
    @PostMapping("/{id}/complete-maintenance")
    public R<Void> completeMaintenance(@PathVariable Long id) {
        vehicleService.completeMaintenance(id);
        return R.ok();
    }

    @Operation(summary = "报废", description = "任意非WORKING状态 -> SCRAPPED（终态）")
    @PostMapping("/{id}/scrap")
    public R<Void> scrap(@PathVariable Long id) {
        vehicleService.scrap(id);
        return R.ok();
    }
}

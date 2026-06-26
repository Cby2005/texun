package com.agri.vehicle.controller;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.common.vo.R;
import com.agri.vehicle.dto.DeviceRegisterDTO;
import com.agri.vehicle.dto.DeviceUpdateDTO;
import com.agri.vehicle.entity.VehicleDevice;
import com.agri.vehicle.service.VehicleService;
import com.agri.vehicle.vo.DeviceStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 无人车设备控制器
 */
@Tag(name = "无人车设备管理", description = "设备注册、查询、更新、删除、心跳、统计")
@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "设备注册")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody DeviceRegisterDTO dto) {
        vehicleService.register(dto);
        return R.ok();
    }

    @Operation(summary = "分页查询设备")
    @GetMapping("/list")
    public R<PageResult<VehicleDevice>> listDevices(
            @Valid PageQuery query,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long farmId) {
        return R.ok(vehicleService.listDevices(query, keyword, status, farmId));
    }

    @Operation(summary = "获取设备详情")
    @GetMapping("/{id}")
    public R<VehicleDevice> getDevice(@PathVariable Long id) {
        return R.ok(vehicleService.getDevice(id));
    }

    @Operation(summary = "更新设备信息")
    @PutMapping
    public R<Void> updateDevice(@Valid @RequestBody DeviceUpdateDTO dto) {
        vehicleService.updateDevice(dto);
        return R.ok();
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public R<Void> deleteDevice(@PathVariable Long id) {
        vehicleService.deleteDevice(id);
        return R.ok();
    }

    @Operation(summary = "获取在线设备列表")
    @GetMapping("/online")
    public R<List<VehicleDevice>> getOnlineDevices() {
        return R.ok(vehicleService.getOnlineDevices());
    }

    @Operation(summary = "获取指定农场的设备")
    @GetMapping("/farm/{farmId}")
    public R<List<VehicleDevice>> getDevicesByFarm(@PathVariable Long farmId) {
        return R.ok(vehicleService.getDevicesByFarm(farmId));
    }

    @Operation(summary = "设备心跳上报")
    @PostMapping("/{id}/heartbeat")
    public R<Void> heartbeat(@PathVariable Long id) {
        vehicleService.heartbeat(id);
        return R.ok();
    }

    @Operation(summary = "获取设备统计信息")
    @GetMapping("/stats")
    public R<DeviceStatsVO> getDeviceStats() {
        return R.ok(vehicleService.getDeviceStats());
    }
}

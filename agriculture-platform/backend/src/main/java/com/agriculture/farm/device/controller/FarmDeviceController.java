package com.agriculture.farm.device.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.farm.device.entity.FarmDevice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farm/devices")
@RequiredArgsConstructor
@Tag(name = "设备管理")
public class FarmDeviceController {

    private final IService<FarmDevice> service;

    @GetMapping
    public Result<PageResult<FarmDevice>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String state) {
        LambdaQueryWrapper<FarmDevice> qw = new LambdaQueryWrapper<>();
        if (deviceType != null) qw.eq(FarmDevice::getDeviceType, deviceType);
        if (state != null) qw.eq(FarmDevice::getState, state);
        qw.orderByAsc(FarmDevice::getDeviceCode);
        Page<FarmDevice> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<FarmDevice> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    @PostMapping
    public Result<Void> add(@RequestBody FarmDevice d) { service.save(d); return Result.ok(); }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FarmDevice d) { d.setId(id); service.updateById(d); return Result.ok(); }
}

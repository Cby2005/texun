package com.agriculture.farm.env.controller;

import com.agriculture.common.result.Result;
import com.agriculture.farm.env.entity.FarmEnvData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/farm/env")
@RequiredArgsConstructor
@Tag(name = "环境监测")
public class FarmEnvController {

    private final IService<FarmEnvData> service;

    @GetMapping("/data/{landId}")
    public Result<List<FarmEnvData>> getEnvData(
            @PathVariable Long landId,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false, defaultValue = "24") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        LambdaQueryWrapper<FarmEnvData> qw = new LambdaQueryWrapper<FarmEnvData>()
                .eq(FarmEnvData::getLandId, landId)
                .ge(FarmEnvData::getCreateTime, since);
        if (deviceId != null) {
            qw.eq(FarmEnvData::getDeviceId, deviceId);
        }
        qw.orderByAsc(FarmEnvData::getCreateTime);
        return Result.ok(service.list(qw));
    }

    @GetMapping("/latest/{landId}")
    public Result<List<FarmEnvData>> getLatest(@PathVariable Long landId) {
        LambdaQueryWrapper<FarmEnvData> qw = new LambdaQueryWrapper<FarmEnvData>()
                .eq(FarmEnvData::getLandId, landId)
                .orderByDesc(FarmEnvData::getCreateTime);
        Page<FarmEnvData> page = service.page(new Page<>(1, 20), qw);
        return Result.ok(page.getRecords());
    }

    @PostMapping("/data")
    public Result<Void> save(@RequestBody List<FarmEnvData> list) {
        service.saveBatch(list);
        return Result.ok();
    }

    @GetMapping("/latest")
    public Result<List<FarmEnvData>> getLatest() {
        LambdaQueryWrapper<FarmEnvData> qw = new LambdaQueryWrapper<FarmEnvData>()
                .orderByDesc(FarmEnvData::getCreateTime);
        Page<FarmEnvData> page = service.page(new Page<>(1, 12), qw);
        return Result.ok(page.getRecords());
    }
}

package com.agriculture.farm.land.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.farm.land.entity.FarmLand;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farm/lands")
@RequiredArgsConstructor
@Tag(name = "地块管理")
public class FarmLandController {

    private final IService<FarmLand> service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<PageResult<FarmLand>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long enterpriseId,
            @RequestParam(required = false) Integer type) {
        LambdaQueryWrapper<FarmLand> qw = new LambdaQueryWrapper<>();
        if (enterpriseId != null) qw.eq(FarmLand::getEnterpriseId, enterpriseId);
        if (type != null) qw.eq(FarmLand::getType, type);
        qw.orderByAsc(FarmLand::getEnterpriseId, FarmLand::getType, FarmLand::getNumber);
        Page<FarmLand> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<FarmLand> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<Void> add(@RequestBody FarmLand l) { service.save(l); return Result.ok(); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FarmLand l) { l.setId(id); service.updateById(l); return Result.ok(); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }
}

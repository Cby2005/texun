package com.agriculture.farm.crop.controller;

import com.agriculture.common.result.Result;
import com.agriculture.farm.crop.entity.FarmCrop;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/farm/crops")
@RequiredArgsConstructor
@Tag(name = "作物管理")
public class FarmCropController {

    private final IService<FarmCrop> service;

    @GetMapping
    public Result<List<FarmCrop>> list(@RequestParam(required = false) Integer type) {
        LambdaQueryWrapper<FarmCrop> qw = new LambdaQueryWrapper<>();
        if (type != null) qw.eq(FarmCrop::getType, type);
        qw.orderByAsc(FarmCrop::getSortOrder);
        return Result.ok(service.list(qw));
    }

    @GetMapping("/{id}")
    public Result<FarmCrop> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> add(@RequestBody FarmCrop c) { service.save(c); return Result.ok(); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FarmCrop c) { c.setId(id); service.updateById(c); return Result.ok(); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }
}

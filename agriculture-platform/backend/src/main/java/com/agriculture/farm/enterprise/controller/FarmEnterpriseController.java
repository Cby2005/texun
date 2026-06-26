package com.agriculture.farm.enterprise.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.farm.enterprise.entity.FarmEnterprise;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farm/enterprises")
@RequiredArgsConstructor
@Tag(name = "农场管理")
public class FarmEnterpriseController {

    private final IService<FarmEnterprise> service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<PageResult<FarmEnterprise>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<FarmEnterprise> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(FarmEnterprise::getName, keyword);
        }
        qw.orderByDesc(FarmEnterprise::getCreateTime);
        Page<FarmEnterprise> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<FarmEnterprise> getById(@PathVariable Long id) {
        return Result.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> add(@RequestBody FarmEnterprise e) { service.save(e); return Result.ok(); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FarmEnterprise e) {
        e.setId(id); service.updateById(e); return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','TRACE_ADMIN')")
    public Result<java.util.List<FarmEnterprise>> all() {
        return Result.ok(service.list(new LambdaQueryWrapper<FarmEnterprise>().eq(FarmEnterprise::getStatus, 0)));
    }
}

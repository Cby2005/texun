package com.agriculture.trace.batch.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.trace.batch.entity.TraceBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trace/batches")
@RequiredArgsConstructor
@Tag(name = "溯源批次管理")
public class TraceBatchController {

    private final IService<TraceBatch> service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<PageResult<TraceBatch>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String batchNo) {
        LambdaQueryWrapper<TraceBatch> qw = new LambdaQueryWrapper<>();
        if (productCode != null) qw.eq(TraceBatch::getProductCode, productCode);
        if (batchNo != null) qw.like(TraceBatch::getBatchNo, batchNo);
        qw.orderByDesc(TraceBatch::getCreateTime);
        Page<TraceBatch> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<TraceBatch> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<Void> add(@RequestBody TraceBatch b) { service.save(b); return Result.ok(); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody TraceBatch b) { b.setId(id); service.updateById(b); return Result.ok(); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }
}

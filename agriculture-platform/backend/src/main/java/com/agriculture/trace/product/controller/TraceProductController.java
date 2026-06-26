package com.agriculture.trace.product.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.trace.product.entity.TraceProduct;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trace/products")
@RequiredArgsConstructor
@Tag(name = "溯源产品管理")
public class TraceProductController {

    private final IService<TraceProduct> service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<PageResult<TraceProduct>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        LambdaQueryWrapper<TraceProduct> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(TraceProduct::getProductName, keyword)
                    .or().like(TraceProduct::getProductCode, keyword));
        }
        if (category != null) qw.eq(TraceProduct::getCategory, category);
        qw.orderByDesc(TraceProduct::getCreateTime);
        Page<TraceProduct> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<TraceProduct> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<Void> add(@RequestBody TraceProduct p) { service.save(p); return Result.ok(); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody TraceProduct p) { p.setId(id); service.updateById(p); return Result.ok(); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }

    @GetMapping("/public/{qrcode}")
    public Result<TraceProduct> queryByCode(@PathVariable String qrcode) {
        TraceProduct p = service.getOne(new LambdaQueryWrapper<TraceProduct>().eq(TraceProduct::getProductCode, qrcode));
        return p != null ? Result.ok(p) : Result.fail("产品不存在");
    }
}

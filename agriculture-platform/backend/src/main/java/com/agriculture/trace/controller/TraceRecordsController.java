package com.agriculture.trace.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 通用溯源记录控制器，通过 type 路径参数路由到不同记录类型
 * type: production(种植) / processing(加工) / storage(仓储) / logistics(物流) / quality(质检) / sales(销售)
 */
@RestController
@RequestMapping("/api/trace/records")
@RequiredArgsConstructor
public class TraceRecordsController {

    private final java.util.Map<String, IService<?>> recordServices;

    @GetMapping("/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<PageResult<?>> list(@PathVariable String type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String batchNo) {
        IService svc = (IService) recordServices.get(type + "RecordService");
        if (svc == null) return Result.fail("不支持的记录类型: " + type);
        var qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (batchNo != null && !batchNo.isEmpty()) {
            qw.eq("batch_no", batchNo);
        }
        qw.orderByDesc("create_time");
        com.baomidou.mybatisplus.core.metadata.IPage page = svc.page(new Page(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{type}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<?> getById(@PathVariable String type, @PathVariable Long id) {
        IService svc = (IService) recordServices.get(type + "RecordService");
        if (svc == null) return Result.fail("不支持的记录类型: " + type);
        return Result.ok(svc.getById(id));
    }

    @PostMapping("/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Void> add(@PathVariable String type, @RequestBody java.util.Map<String, Object> data) {
        IService svc = (IService) recordServices.get(type + "RecordService");
        if (svc == null) return Result.fail("不支持的记录类型: " + type);
        try {
            Object entity = svc.getEntityClass().getDeclaredConstructor().newInstance();
            for (var field : svc.getEntityClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (data.containsKey(field.getName())) {
                    Object val = data.get(field.getName());
                    if (val != null && field.getType() != val.getClass()) {
                        if (field.getType() == java.time.LocalDateTime.class && val instanceof String s) {
                            java.time.format.DateTimeFormatter fmt = s.contains("T")
                                ? java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                : java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            val = java.time.LocalDateTime.parse(s, fmt);
                        }
                    }
                    field.set(entity, val);
                }
            }
            svc.save(entity);
            return Result.ok();
        } catch (Exception e) { return Result.fail("创建失败: " + e.getMessage()); }
    }

    @PutMapping("/{type}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Void> update(@PathVariable String type, @PathVariable Long id, @RequestBody java.util.Map<String, Object> data) {
        IService svc = (IService) recordServices.get(type + "RecordService");
        if (svc == null) return Result.fail("不支持的记录类型: " + type);
        try {
            Object entity = svc.getById(id);
            if (entity == null) return Result.fail("记录不存在");
            for (var field : svc.getEntityClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (data.containsKey(field.getName())) field.set(entity, data.get(field.getName()));
            }
            svc.updateById(entity);
            return Result.ok();
        } catch (Exception e) { return Result.fail("更新失败: " + e.getMessage()); }
    }

    @DeleteMapping("/{type}/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable String type, @PathVariable Long id) {
        IService<?> svc = recordServices.get(type + "RecordService");
        if (svc == null) return Result.fail("不支持的记录类型: " + type);
        svc.removeById(id);
        return Result.ok();
    }
}

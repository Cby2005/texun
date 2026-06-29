package com.agriculture.greenhouse.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.greenhouse.entity.StrawberryPlantingRecord;
import com.agriculture.greenhouse.service.StrawberryPlantingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/digital-twin/strawberry-planting")
@RequiredArgsConstructor
@Tag(name = "草莓种植管理")
public class StrawberryPlantingController {

    private final StrawberryPlantingService service;

    /** 新增种植记录 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Void> add(@RequestBody StrawberryPlantingRecord r) {
        // 自动计算总成本
        if (r.getPlantCount() != null && r.getUnitCost() != null) {
            r.setTotalCost(r.getUnitCost().multiply(BigDecimal.valueOf(r.getPlantCount()))
                    .setScale(2, RoundingMode.HALF_UP));
        }
        service.save(r);
        return Result.ok();
    }

    /** 查询种植记录列表 */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<PageResult<StrawberryPlantingRecord>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String areaCode) {
        LambdaQueryWrapper<StrawberryPlantingRecord> qw = new LambdaQueryWrapper<>();
        if (areaCode != null && !areaCode.isEmpty()) {
            qw.eq(StrawberryPlantingRecord::getAreaCode, areaCode);
        }
        qw.orderByDesc(StrawberryPlantingRecord::getCreateTime);
        Page<StrawberryPlantingRecord> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /** 查询种植统计 */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Map<String, Object>> statistics() {
        return Result.ok(service.getStatistics());
    }

    /** 查询各区域种植统计 */
    @GetMapping("/area-statistics")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<List<Map<String, Object>>> areaStatistics() {
        return Result.ok(service.getAreaStatistics());
    }

    /** 更新种植记录 */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Void> update(@PathVariable Long id, @RequestBody StrawberryPlantingRecord r) {
        r.setId(id);
        if (r.getPlantCount() != null && r.getUnitCost() != null) {
            r.setTotalCost(r.getUnitCost().multiply(BigDecimal.valueOf(r.getPlantCount()))
                    .setScale(2, RoundingMode.HALF_UP));
        }
        service.updateById(r);
        return Result.ok();
    }

    /** 删除种植记录 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }
}

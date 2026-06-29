package com.agriculture.farm.enterprise.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.farm.enterprise.entity.FarmEnterprise;
import com.agriculture.agri.ai.client.DeepSeekClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/farm/enterprises")
@RequiredArgsConstructor
@Tag(name = "草莓基地管理")
public class FarmEnterpriseController {

    private final IService<FarmEnterprise> service;
    private final DeepSeekClient deepSeekClient;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<PageResult<FarmEnterprise>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String baseType,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<FarmEnterprise> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w
                .like(FarmEnterprise::getBaseName, keyword)
                .or()
                .like(FarmEnterprise::getEnterpriseName, keyword)
                .or()
                .like(FarmEnterprise::getManagerName, keyword));
        }
        if (baseType != null && !baseType.isEmpty()) {
            qw.eq(FarmEnterprise::getBaseType, baseType);
        }
        if (status != null) {
            qw.eq(FarmEnterprise::getStatus, status);
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
    public Result<Void> add(@RequestBody FarmEnterprise e) {
        autoGeocode(e);
        service.save(e);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FarmEnterprise e) {
        e.setId(id);
        autoGeocode(e);
        service.updateById(e);
        return Result.ok();
    }

    /** 如果经纬度为空，通过 DeepSeek 根据地址自动获取 */
    private void autoGeocode(FarmEnterprise e) {
        if (e.getLng() != null && e.getLat() != null) return; // 已有经纬度，不覆盖
        String address = buildAddress(e);
        if (address.isBlank()) return;
        try {
            Map<String, Object> geo = deepSeekClient.geocode(address);
            if (geo != null && geo.get("lng") != null && geo.get("lat") != null) {
                e.setLng(new BigDecimal(geo.get("lng").toString()).setScale(6, RoundingMode.HALF_UP));
                e.setLat(new BigDecimal(geo.get("lat").toString()).setScale(6, RoundingMode.HALF_UP));
                log.info("基地 {} 自动地理编码: lng={}, lat={}", e.getBaseName(), e.getLng(), e.getLat());
            }
        } catch (Exception ex) {
            log.warn("基地 {} 地理编码失败: {}", e.getBaseName(), ex.getMessage());
        }
    }

    private String buildAddress(FarmEnterprise e) {
        StringBuilder sb = new StringBuilder();
        if (e.getRegion() != null && !e.getRegion().isBlank()) sb.append(e.getRegion());
        if (e.getDetailAddress() != null && !e.getDetailAddress().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(e.getDetailAddress());
        }
        return sb.toString().trim();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','TRACE_ADMIN')")
    public Result<java.util.List<FarmEnterprise>> all() {
        return Result.ok(service.list(new LambdaQueryWrapper<FarmEnterprise>().eq(FarmEnterprise::getStatus, 0)));
    }

    /** ponytail: 简单的 id+name 列表，供前端下拉选择 */
    @GetMapping("/simple")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<java.util.List<Map<String, Object>>> simple() {
        java.util.List<FarmEnterprise> list = service.list(
                new LambdaQueryWrapper<FarmEnterprise>().eq(FarmEnterprise::getStatus, 0)
                        .select(FarmEnterprise::getId, FarmEnterprise::getBaseName));
        return Result.ok(list.stream().map(e -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", e.getId()); m.put("name", e.getBaseName());
            return m;
        }).collect(java.util.stream.Collectors.toList()));
    }
}

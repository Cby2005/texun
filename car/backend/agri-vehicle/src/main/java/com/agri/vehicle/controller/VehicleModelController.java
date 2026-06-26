package com.agri.vehicle.controller;

import com.agri.common.vo.R;
import com.agri.vehicle.dto.ModelDTO;
import com.agri.vehicle.entity.VehicleModel;
import com.agri.vehicle.service.VehicleModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 设备型号管理控制器
 */
@Tag(name = "设备型号管理", description = "无人车设备型号的增删改查")
@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class VehicleModelController {

    private final VehicleModelService modelService;

    @Operation(summary = "获取所有型号")
    @GetMapping("/list")
    public R<List<VehicleModel>> listModels() {
        return R.ok(modelService.listAll());
    }

    @Operation(summary = "按类型查询型号")
    @GetMapping("/type/{vehicleType}")
    public R<List<VehicleModel>> listByType(@PathVariable String vehicleType) {
        return R.ok(modelService.listByType(vehicleType));
    }

    @Operation(summary = "获取型号详情")
    @GetMapping("/{id}")
    public R<VehicleModel> getModel(@PathVariable Long id) {
        return R.ok(modelService.getById(id));
    }

    @Operation(summary = "创建型号")
    @PostMapping
    public R<Void> createModel(@Valid @RequestBody ModelDTO dto) {
        modelService.create(dto);
        return R.ok();
    }

    @Operation(summary = "更新型号")
    @PutMapping
    public R<Void> updateModel(@Valid @RequestBody ModelDTO dto) {
        modelService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除型号")
    @DeleteMapping("/{id}")
    public R<Void> deleteModel(@PathVariable Long id) {
        modelService.delete(id);
        return R.ok();
    }
}

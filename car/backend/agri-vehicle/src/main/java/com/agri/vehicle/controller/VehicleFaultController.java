package com.agri.vehicle.controller;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.common.vo.R;
import com.agri.vehicle.entity.VehicleFaultRecord;
import com.agri.vehicle.service.VehicleFaultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 故障记录管理控制器
 */
@Tag(name = "故障记录管理", description = "设备故障记录查询与处理")
@RestController
@RequestMapping("/fault")
@RequiredArgsConstructor
public class VehicleFaultController {

    private final VehicleFaultService faultService;

    @Operation(summary = "分页查询故障记录")
    @GetMapping("/list")
    public R<PageResult<VehicleFaultRecord>> listFaults(
            @Valid PageQuery query,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String faultType,
            @RequestParam(required = false) String status) {
        return R.ok(faultService.listFaults(query, vehicleId, faultType, status));
    }

    @Operation(summary = "获取故障详情")
    @GetMapping("/{id}")
    public R<VehicleFaultRecord> getFault(@PathVariable Long id) {
        return R.ok(faultService.getById(id));
    }

    @Operation(summary = "解决故障")
    @PostMapping("/{id}/resolve")
    public R<Void> resolveFault(@PathVariable Long id,
                                 @RequestParam String resolution) {
        faultService.resolveFault(id, resolution);
        return R.ok();
    }
}

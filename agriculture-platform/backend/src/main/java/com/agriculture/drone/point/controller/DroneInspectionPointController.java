package com.agriculture.drone.point.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.service.impl.DroneInspectionPointServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drone/point")
@RequiredArgsConstructor
@Tag(name = "温室巡检点管理")
public class DroneInspectionPointController {

    private final DroneInspectionPointServiceImpl pointService;

    /** 查询巡检点列表 */
    @GetMapping("/list")
    public Result<PageResult<DroneInspectionPoint>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long greenhouseId,
            @RequestParam(required = false) String areaName,
            @RequestParam(required = false) String pointType) {
        LambdaQueryWrapper<DroneInspectionPoint> qw = new LambdaQueryWrapper<>();
        if (greenhouseId != null) qw.eq(DroneInspectionPoint::getGreenhouseId, greenhouseId);
        if (areaName != null && !areaName.isBlank()) qw.eq(DroneInspectionPoint::getAreaName, areaName);
        if (pointType != null && !pointType.isBlank()) qw.eq(DroneInspectionPoint::getPointType, pointType);
        qw.orderByAsc(DroneInspectionPoint::getId);
        Page<DroneInspectionPoint> page = pointService.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /** 初始化默认巡检点 */
    @PostMapping("/initDefault")
    public Result<String> initDefault(@RequestParam(defaultValue = "1") Long greenhouseId) {
        int count = pointService.initDefault(greenhouseId);
        return Result.ok("成功初始化" + count + "个默认巡检点");
    }

    @GetMapping("/{id}")
    public Result<DroneInspectionPoint> getById(@PathVariable Long id) { return Result.ok(pointService.getById(id)); }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody DroneInspectionPoint p) {
        validateCoord(p.getLongitude(), p.getLatitude());
        pointService.save(p);
        return Result.ok();
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DroneInspectionPoint p) {
        validateCoord(p.getLongitude(), p.getLatitude());
        p.setId(id);
        pointService.updateById(p);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) { pointService.removeById(id); return Result.ok(); }

    /** 查询温室下所有启用巡检点（供数字孪生使用） */
    @GetMapping("/by-greenhouse/{greenhouseId}")
    public Result<List<DroneInspectionPoint>> byGreenhouse(@PathVariable Long greenhouseId) {
        LambdaQueryWrapper<DroneInspectionPoint> qw = new LambdaQueryWrapper<>();
        qw.eq(DroneInspectionPoint::getGreenhouseId, greenhouseId)
          .eq(DroneInspectionPoint::getEnabled, 1)
          .orderByAsc(DroneInspectionPoint::getSortOrder)
          .orderByAsc(DroneInspectionPoint::getAreaName)
          .orderByAsc(DroneInspectionPoint::getRowNo);
        return Result.ok(pointService.list(qw));
    }

    /** 根据区域+种植行+点位位置自动生成坐标 */
    @PostMapping("/generate-coordinate")
    public Result<Map<String, BigDecimal>> generateCoordinate(@RequestBody Map<String, Object> body) {
        String areaName = body.get("areaName") != null ? body.get("areaName").toString() : "A区育苗区";
        Integer rowNo = body.get("rowNo") != null ? Integer.valueOf(body.get("rowNo").toString()) : 1;
        String pos = body.get("pointPosition") != null ? body.get("pointPosition").toString() : "行中";
        // ponytail: 简单区域→坐标映射, 与Three.js场景坐标对齐
        Map<String, BigDecimal> areaBase = Map.of(
            "A区育苗区", BigDecimal.valueOf(-12),
            "B区开花区", BigDecimal.valueOf(-4),
            "C区结果区", BigDecimal.valueOf(4),
            "D区异常修复区", BigDecimal.valueOf(12)
        );
        BigDecimal baseZ = areaBase.getOrDefault(areaName, BigDecimal.ZERO);
        BigDecimal x = BigDecimal.valueOf((rowNo - 3.5) * 1.5); // 行间距1.5, 4行居中
        BigDecimal z;
        switch (pos) {
            case "行首": z = baseZ.subtract(BigDecimal.valueOf(12)); break;
            case "行尾": z = baseZ.add(BigDecimal.valueOf(12)); break;
            case "设备旁": z = baseZ.add(BigDecimal.valueOf(6)); break;
            case "异常复查点": z = baseZ.add(BigDecimal.valueOf(3)); break;
            default: z = baseZ; break; // 行中
        }
        return Result.ok(Map.of("sceneX", x, "sceneY", BigDecimal.valueOf(3.5), "sceneZ", z));
    }

    private void validateCoord(BigDecimal lng, BigDecimal lat) {
        if (lng == null || lat == null) throw new com.agriculture.common.exception.BizException(400, "经纬度不能为空");
        if (lng.doubleValue() < -180 || lng.doubleValue() > 180) throw new com.agriculture.common.exception.BizException(400, "经度范围-180~180");
        if (lat.doubleValue() < -90 || lat.doubleValue() > 90) throw new com.agriculture.common.exception.BizException(400, "纬度范围-90~90");
    }
}

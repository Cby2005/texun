package com.agriculture.drone.point.controller;

import com.agriculture.common.result.Result;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.entity.GreenhouseArea;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.agriculture.drone.point.mapper.GreenhouseAreaMapper;
import com.agriculture.drone.point.service.impl.InspectionPointGenerateServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/greenhouse")
@RequiredArgsConstructor
@Tag(name = "温室分区巡检点管理")
public class GreenhouseAreaController {

    private final GreenhouseAreaMapper areaMapper;
    private final DroneInspectionPointMapper pointMapper;
    private final InspectionPointGenerateServiceImpl generateService;

    // ==================== 分区 CRUD ====================

    @GetMapping("/area/list")
    public Result<List<GreenhouseArea>> listAreas(@RequestParam(required = false) Long greenhouseId) {
        LambdaQueryWrapper<GreenhouseArea> qw = new LambdaQueryWrapper<>();
        if (greenhouseId != null) qw.eq(GreenhouseArea::getGreenhouseId, greenhouseId);
        qw.orderByAsc(GreenhouseArea::getId);
        return Result.ok(areaMapper.selectList(qw));
    }

    @GetMapping("/area/{id}")
    public Result<GreenhouseArea> getArea(@PathVariable Long id) {
        return Result.ok(areaMapper.selectById(id));
    }

    @PostMapping("/area/save")
    public Result<String> saveArea(@RequestBody GreenhouseArea area) {
        if (area.getId() == null) {
            areaMapper.insert(area);
        } else {
            areaMapper.updateById(area);
        }
        return Result.ok("保存成功");
    }

    @DeleteMapping("/area/delete/{id}")
    public Result<String> deleteArea(@PathVariable Long id) {
        areaMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    // ==================== 巡检点生成 ====================

    @PostMapping("/inspection-point/generate/{areaId}")
    public Result<List<DroneInspectionPoint>> generateByArea(@PathVariable Long areaId) {
        return Result.ok(generateService.generateByArea(areaId));
    }

    @PostMapping("/inspection-point/generateAll")
    public Result<String> generateAll() {
        int count = generateService.generateAll();
        return Result.ok("成功生成" + count + "个巡检点");
    }

    @GetMapping("/inspection-point/list")
    public Result<List<DroneInspectionPoint>> listInspectionPoints(
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String pointType,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<DroneInspectionPoint> qw = new LambdaQueryWrapper<>();
        if (areaId != null) qw.eq(DroneInspectionPoint::getAreaId, areaId);
        if (pointType != null) qw.eq(DroneInspectionPoint::getPointType, pointType);
        if (status != null) qw.eq(DroneInspectionPoint::getStatus, status);
        qw.orderByAsc(DroneInspectionPoint::getAreaId, DroneInspectionPoint::getRowIndex, DroneInspectionPoint::getPointIndex);
        return Result.ok(pointMapper.selectList(qw));
    }

    // ==================== 温室场景数据 ====================

    @GetMapping("/scene/data")
    public Result<Map<String, Object>> getSceneData(@RequestParam(defaultValue = "1") Long greenhouseId) {
        Map<String, Object> data = new HashMap<>();
        data.put("greenhouseId", greenhouseId);
        data.put("greenhouseName", greenhouseId + "号温室");

        // 所有分区
        LambdaQueryWrapper<GreenhouseArea> areaQw = new LambdaQueryWrapper<>();
        areaQw.eq(GreenhouseArea::getGreenhouseId, greenhouseId).orderByAsc(GreenhouseArea::getId);
        List<GreenhouseArea> areas = areaMapper.selectList(areaQw);

        // 如果没有分区数据，初始化默认分区
        if (areas.isEmpty()) {
            initDefaultAreas(greenhouseId);
            areas = areaMapper.selectList(areaQw);
        }
        data.put("areas", areas);

        // 所有巡检点
        LambdaQueryWrapper<DroneInspectionPoint> ptQw = new LambdaQueryWrapper<>();
        ptQw.eq(DroneInspectionPoint::getGreenhouseId, greenhouseId)
            .orderByAsc(DroneInspectionPoint::getAreaId, DroneInspectionPoint::getRowIndex, DroneInspectionPoint::getPointIndex);
        List<DroneInspectionPoint> points = pointMapper.selectList(ptQw);

        // 如果没有巡检点，自动生成
        if (points.isEmpty() && !areas.isEmpty()) {
            generateService.generateAll();
            points = pointMapper.selectList(ptQw);
        }
        data.put("inspectionPoints", points);

        return Result.ok(data);
    }

    private void initDefaultAreas(Long greenhouseId) {
        GreenhouseArea[] defaults = {
            buildArea(greenhouseId, "A区育苗区", 4, 12, -5.0, -2.0, 4.0, 6.0, "NORMAL", "LOW"),
            buildArea(greenhouseId, "B区开花区", 4, 12, 0.0, -2.0, 4.0, 6.0, "NORMAL", "LOW"),
            buildArea(greenhouseId, "C区结果区", 4, 12, 5.0, -2.0, 4.0, 6.0, "WARNING", "MEDIUM"),
            buildArea(greenhouseId, "D区异常复核区", 4, 12, -5.0, 3.5, 4.0, 6.0, "ABNORMAL", "HIGH")
        };
        for (GreenhouseArea area : defaults) {
            areaMapper.insert(area);
        }
    }

    private GreenhouseArea buildArea(Long greenhouseId, String name, int rows, int plants,
                                      double sx, double sz, double sw, double sd,
                                      String status, String risk) {
        GreenhouseArea a = new GreenhouseArea();
        a.setGreenhouseId(greenhouseId);
        a.setAreaName(name);
        a.setRowCount(rows);
        a.setPlantCountPerRow(plants);
        a.setSceneX(java.math.BigDecimal.valueOf(sx));
        a.setSceneZ(java.math.BigDecimal.valueOf(sz));
        a.setSceneWidth(java.math.BigDecimal.valueOf(sw));
        a.setSceneDepth(java.math.BigDecimal.valueOf(sd));
        a.setStatus(status);
        a.setDiseaseRisk(risk);
        return a;
    }
}

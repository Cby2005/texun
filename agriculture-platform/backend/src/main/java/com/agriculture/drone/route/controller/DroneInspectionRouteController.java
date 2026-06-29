package com.agriculture.drone.route.controller;

import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import com.agriculture.digitaltwin.entity.StrawberryPlot;
import com.agriculture.digitaltwin.mapper.StrawberryPlotMapper;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.agriculture.drone.route.dto.RouteGenerateReq;
import com.agriculture.drone.route.entity.DroneInspectionRoute;
import com.agriculture.drone.route.entity.DroneInspectionRoutePoint;
import com.agriculture.drone.route.mapper.DroneInspectionRouteMapper;
import com.agriculture.drone.route.service.impl.DroneInspectionRouteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drone/inspection-route")
@RequiredArgsConstructor
@Tag(name = "巡检路线规划(新)")
public class DroneInspectionRouteController {

    private final DroneInspectionRouteService routeService;
    private final DroneInspectionRouteMapper routeMapper;
    private final StrawberryPlotMapper plotMapper;
    private final DroneInspectionPointMapper pointMapper;

    /** 查询温室地块列表(用于右侧网格展示) */
    @GetMapping("/plots")
    public Result<List<Map<String, Object>>> getPlots(@RequestParam Long greenhouseId) {
        List<StrawberryPlot> plots = plotMapper.selectList(
                new LambdaQueryWrapper<StrawberryPlot>()
                        .eq(StrawberryPlot::getGreenhouseId, greenhouseId)
                        .eq(StrawberryPlot::getDeleted, 0)
                        .orderByAsc(StrawberryPlot::getAreaCode, StrawberryPlot::getRowNo, StrawberryPlot::getColNo));

        List<Map<String, Object>> list = plots.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId()); m.put("plotCode", p.getPlotCode());
            m.put("areaCode", p.getAreaCode()); m.put("areaName", p.getAreaName());
            m.put("rowNo", p.getRowNo()); m.put("colNo", p.getColNo());
            m.put("plotStatus", p.getPlotStatus());
            m.put("coordinateX", p.getCoordinateX());
            m.put("coordinateY", p.getCoordinateY());
            m.put("coordinateZ", p.getCoordinateZ());
            return m;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

    /** 查询温室巡检点列表 */
    @GetMapping("/points")
    public Result<List<Map<String, Object>>> getPoints(@RequestParam Long greenhouseId) {
        List<DroneInspectionPoint> pts = pointMapper.selectList(
                new LambdaQueryWrapper<DroneInspectionPoint>()
                        .eq(DroneInspectionPoint::getGreenhouseId, greenhouseId)
                        .eq(DroneInspectionPoint::getEnabled, 1)
                        .orderByAsc(DroneInspectionPoint::getSortOrder));
        List<Map<String, Object>> list = pts.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId()); m.put("pointName", p.getPointName());
            m.put("pointType", p.getPointType()); m.put("areaName", p.getAreaName());
            m.put("rowNo", p.getRowNo()); m.put("pointPosition", p.getPointPosition());
            m.put("riskLevel", p.getRiskLevel()); m.put("status", p.getStatus());
            return m;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

    /** 生成巡检路径 */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(@RequestBody RouteGenerateReq req) {
        DroneInspectionRoute route = routeService.generate(req);
        List<DroneInspectionRoutePoint> points = routeService.getRoutePoints(route.getId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("route", route);
        result.put("points", points);
        return Result.ok(result);
    }

    /** 保存并设为常用 */
    @PutMapping("/{id}/set-common")
    public Result<Void> setCommon(@PathVariable Long id) {
        DroneInspectionRoute r = new DroneInspectionRoute();
        r.setId(id); r.setIsCommon(1);
        routeMapper.updateById(r);
        return Result.ok();
    }

    /** 查询路线列表 */
    @GetMapping("/list")
    public Result<PageResult<DroneInspectionRoute>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long greenhouseId) {
        LambdaQueryWrapper<DroneInspectionRoute> qw = new LambdaQueryWrapper<>();
        qw.eq(DroneInspectionRoute::getDeleted, 0);
        if (greenhouseId != null) qw.eq(DroneInspectionRoute::getGreenhouseId, greenhouseId);
        qw.orderByDesc(DroneInspectionRoute::getCreateTime);
        Page<DroneInspectionRoute> page = routeMapper.selectPage(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /** 查询路线详情(含点) */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        DroneInspectionRoute route = routeMapper.selectById(id);
        if (route == null) return Result.fail(404, "路线不存在");
        List<DroneInspectionRoutePoint> points = routeService.getRoutePoints(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("route", route);
        result.put("points", points);
        return Result.ok(result);
    }

    /** 删除路线 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return Result.ok();
    }
}

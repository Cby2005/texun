package com.agriculture.drone.route.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.agriculture.drone.route.dto.RouteGenerateRequest;
import com.agriculture.drone.route.entity.DroneRoutePlan;
import com.agriculture.drone.route.service.impl.DroneRoutePlanServiceImpl;
import com.agriculture.drone.common.util.PathPlanningUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drone/route")
@RequiredArgsConstructor
@Tag(name = "巡检路径规划")
public class DroneRoutePlanController {

    private final DroneRoutePlanServiceImpl routeService;
    private final DroneInspectionPointMapper pointMapper;

    /** 生成巡检路径（经纬度版） */
    @PostMapping("/generate")
    public Result<DroneRoutePlan> generate(@RequestBody RouteGenerateRequest req) {
        return Result.ok(routeService.generateRoute(req));
    }

    /** 生成场景坐标巡检路径（Three.js Z字形） */
    @PostMapping("/generateSceneRoute")
    public Result<DroneRoutePlan> generateSceneRoute(@RequestBody Map<String, Object> body) {
        Long greenhouseId = body.get("greenhouseId") != null
                ? Long.valueOf(body.get("greenhouseId").toString()) : 1L;
        String routeName = body.get("routeName") != null
                ? body.get("routeName").toString() : "温室巡检路径";

        LambdaQueryWrapper<DroneInspectionPoint> qw = new LambdaQueryWrapper<>();
        qw.eq(DroneInspectionPoint::getGreenhouseId, greenhouseId)
          .eq(DroneInspectionPoint::getEnabled, 1);
        List<DroneInspectionPoint> allPoints = pointMapper.selectList(qw);
        return Result.ok(PathPlanningUtil.generateSceneRoute(allPoints, greenhouseId, routeName));
    }

    @PostMapping("/save")
    public Result<DroneRoutePlan> save(@RequestBody DroneRoutePlan plan) {
        routeService.save(plan);
        return Result.ok(plan);
    }

    @GetMapping("/list")
    public Result<PageResult<DroneRoutePlan>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long greenhouseId,
            @RequestParam(required = false) String routeType) {
        LambdaQueryWrapper<DroneRoutePlan> qw = new LambdaQueryWrapper<>();
        if (greenhouseId != null) qw.eq(DroneRoutePlan::getGreenhouseId, greenhouseId);
        if (routeType != null && !routeType.isBlank()) qw.eq(DroneRoutePlan::getRouteType, routeType);
        qw.orderByDesc(DroneRoutePlan::getCreateTime);
        Page<DroneRoutePlan> page = routeService.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DroneRoutePlan> getById(@PathVariable Long id) { return Result.ok(routeService.getById(id)); }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) { routeService.removeById(id); return Result.ok(); }
}

package com.agri.route.controller;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.common.vo.R;
import com.agri.route.algorithm.AStarPlanner;
import com.agri.route.algorithm.BoustrophedonPlanner;
import com.agri.route.dto.RouteSaveDTO;
import com.agri.route.entity.RoutePlan;
import com.agri.route.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 路线规划控制器
 */
@Tag(name = "路线规划管理", description = "Boustrophedon覆盖路径规划、A*点到点路径规划、路线CRUD")
@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @Operation(summary = "自动生成覆盖式路线", description = "使用Boustrophedon往复式算法生成覆盖路径")
    @PostMapping("/generate/coverage")
    public R<BoustrophedonPlanner.PlanResult> generateCoverageRoute(@RequestBody BoustrophedonPlanner.PlanParams params) {
        return R.ok(routeService.generateCoverageRoute(params));
    }

    @Operation(summary = "A*点到点路径规划", description = "使用A*算法生成避障路径")
    @PostMapping("/generate/point-to-point")
    public R<List<double[]>> generatePointToPointRoute(@RequestBody AStarPlanner.AStarParams params) {
        return R.ok(routeService.generatePointToPointRoute(params));
    }

    @Operation(summary = "保存路线")
    @PostMapping
    public R<Void> saveRoute(@Valid @RequestBody RouteSaveDTO dto) {
        routeService.saveRoute(dto);
        return R.ok();
    }

    @Operation(summary = "分页查询路线")
    @GetMapping("/list")
    public R<PageResult<RoutePlan>> listRoutes(
            @Valid PageQuery query,
            @RequestParam(required = false) Long plotId,
            @RequestParam(required = false) String routeType) {
        return R.ok(routeService.listRoutes(query, plotId, routeType));
    }

    @Operation(summary = "获取路线详情")
    @GetMapping("/{id}")
    public R<RoutePlan> getRoute(@PathVariable Long id) {
        return R.ok(routeService.getRoute(id));
    }

    @Operation(summary = "删除路线")
    @DeleteMapping("/{id}")
    public R<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return R.ok();
    }

    @Operation(summary = "获取地块下的路线列表")
    @GetMapping("/plot/{plotId}")
    public R<List<RoutePlan>> getRoutesByPlot(@PathVariable Long plotId) {
        return R.ok(routeService.getRoutesByPlot(plotId));
    }
}

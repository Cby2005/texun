package com.agri.route.service.impl;

import com.agri.common.dto.PageQuery;
import com.agri.common.enums.ResultCode;
import com.agri.common.exception.BusinessException;
import com.agri.common.utils.GeoUtils;
import com.agri.common.vo.PageResult;
import com.agri.route.algorithm.AStarPlanner;
import com.agri.route.algorithm.BoustrophedonPlanner;
import com.agri.route.dto.RouteSaveDTO;
import com.agri.route.entity.RoutePlan;
import com.agri.route.mapper.RoutePlanMapper;
import com.agri.route.service.RouteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 路线规划服务实现
 *
 * 索引设计：
 *   route_plan.plot_id     ->  INDEX（地块路线查询）
 *   route_plan.farm_id     ->  INDEX（农场路线查询）
 *   route_plan.route_type  ->  INDEX（路线类型筛选）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RoutePlanMapper routePlanMapper;
    private final ObjectMapper objectMapper;

    @Override
    public BoustrophedonPlanner.PlanResult generateCoverageRoute(BoustrophedonPlanner.PlanParams params) {
        BoustrophedonPlanner.PlanResult result = BoustrophedonPlanner.plan(params);
        log.info("覆盖路线规划完成: 航点数={}, 总长度={}m, 覆盖率={}% ",
                result.getWaypoints().size(),
                String.format("%.1f", result.getTotalLength()),
                String.format("%.1f", result.getCoverageRate()));
        return result;
    }

    @Override
    public List<double[]> generatePointToPointRoute(AStarPlanner.AStarParams params) {
        List<double[]> path = AStarPlanner.plan(params);
        log.info("A*路径规划完成: 路径点数={}", path.size());
        return path;
    }

    @Override
    public void saveRoute(RouteSaveDTO dto) {
        RoutePlan route = new RoutePlan();
        route.setRouteName(dto.getRouteName());
        route.setPlotId(dto.getPlotId());
        route.setFarmId(dto.getFarmId());
        route.setRouteType(dto.getRouteType());
        route.setAlgorithm(dto.getAlgorithm());
        route.setWaypoints(dto.getWaypoints());
        route.setWorkWidth(dto.getWorkWidth());
        route.setTurnRadius(dto.getTurnRadius());
        route.setOverlapRate(dto.getOverlapRate());
        route.setAvoidObstacles(dto.getAvoidObstacles());
        route.setStatus("ACTIVE");

        // 自动计算总长度和预估时间
        if (route.getWaypoints() != null) {
            try {
                @SuppressWarnings("unchecked")
                List<double[]> waypoints = objectMapper.readValue(route.getWaypoints(), List.class);
                route.setTotalLength(GeoUtils.routeLength(waypoints));
                // 假设平均速度5km/h
                route.setEstimatedDuration((int) (route.getTotalLength() / 1000 / 5 * 60));
            } catch (JsonProcessingException e) {
                log.warn("解析路线数据失败: {}", e.getMessage());
            }
        }

        routePlanMapper.insert(route);
        log.info("路线保存成功: name={}, plotId={}", dto.getRouteName(), dto.getPlotId());
    }

    @Override
    public PageResult<RoutePlan> listRoutes(PageQuery query, Long plotId, String routeType) {
        LambdaQueryWrapper<RoutePlan> wrapper = new LambdaQueryWrapper<>();
        if (plotId != null) wrapper.eq(RoutePlan::getPlotId, plotId);
        if (StringUtils.hasText(routeType)) wrapper.eq(RoutePlan::getRouteType, routeType);
        wrapper.orderByDesc(RoutePlan::getCreateTime);
        Page<RoutePlan> page = routePlanMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), query.getPageNum(), query.getPageSize(), page.getRecords());
    }

    @Override
    public RoutePlan getRoute(Long id) {
        RoutePlan route = routePlanMapper.selectById(id);
        if (route == null) throw new BusinessException(ResultCode.ROUTE_NOT_FOUND);
        return route;
    }

    @Override
    public void deleteRoute(Long id) {
        routePlanMapper.deleteById(id);
        log.info("路线删除成功: id={}", id);
    }

    @Override
    public List<RoutePlan> getRoutesByPlot(Long plotId) {
        return routePlanMapper.selectList(new LambdaQueryWrapper<RoutePlan>()
                .eq(RoutePlan::getPlotId, plotId)
                .orderByDesc(RoutePlan::getCreateTime));
    }
}

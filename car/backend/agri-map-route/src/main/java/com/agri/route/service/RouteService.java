package com.agri.route.service;

import com.agri.common.dto.PageQuery;
import com.agri.common.vo.PageResult;
import com.agri.route.algorithm.AStarPlanner;
import com.agri.route.algorithm.BoustrophedonPlanner;
import com.agri.route.dto.RouteSaveDTO;
import com.agri.route.entity.RoutePlan;

import java.util.List;

/**
 * 路线规划服务接口
 */
public interface RouteService {

    /**
     * 自动生成覆盖式路线
     */
    BoustrophedonPlanner.PlanResult generateCoverageRoute(BoustrophedonPlanner.PlanParams params);

    /**
     * A*点到点路径规划
     */
    List<double[]> generatePointToPointRoute(AStarPlanner.AStarParams params);

    /**
     * 保存路线
     */
    void saveRoute(RouteSaveDTO dto);

    /**
     * 分页查询路线
     */
    PageResult<RoutePlan> listRoutes(PageQuery query, Long plotId, String routeType);

    /**
     * 获取路线详情
     */
    RoutePlan getRoute(Long id);

    /**
     * 删除路线
     */
    void deleteRoute(Long id);

    /**
     * 获取地块下的路线列表
     */
    List<RoutePlan> getRoutesByPlot(Long plotId);
}

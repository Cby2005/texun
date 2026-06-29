package com.agriculture.drone.route.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.drone.common.util.PathPlanningUtil;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.agriculture.drone.route.dto.RouteGenerateRequest;
import com.agriculture.drone.route.entity.DroneRoutePlan;
import com.agriculture.drone.route.mapper.DroneRoutePlanMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneRoutePlanServiceImpl extends ServiceImpl<DroneRoutePlanMapper, DroneRoutePlan> {

    private final DroneInspectionPointMapper pointMapper;

    public DroneRoutePlan generateRoute(RouteGenerateRequest req) {
        if (req.getPointIds() == null || req.getPointIds().isEmpty()) {
            throw new BizException(400, "请选择至少一个巡检点");
        }
        List<DroneInspectionPoint> points = pointMapper.selectBatchIds(req.getPointIds());
        points = points.stream().filter(p -> p.getEnabled() == null || p.getEnabled() == 1).toList();
        if (points.isEmpty()) throw new BizException(400, "巡检点不存在");

        if ("NEAREST".equalsIgnoreCase(req.getAlgorithmType())) {
            return PathPlanningUtil.generateByNearest(points, req);
        }
        return PathPlanningUtil.generateByOrder(points, req);
    }
}

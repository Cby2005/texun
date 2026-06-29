package com.agriculture.drone.route.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.digitaltwin.entity.StrawberryPlot;
import com.agriculture.digitaltwin.mapper.StrawberryPlotMapper;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.agriculture.drone.route.dto.RouteGenerateReq;
import com.agriculture.drone.route.entity.DroneInspectionRoute;
import com.agriculture.drone.route.entity.DroneInspectionRoutePoint;
import com.agriculture.drone.route.mapper.DroneInspectionRouteMapper;
import com.agriculture.drone.route.mapper.DroneInspectionRoutePointMapper;
import com.agriculture.greenhouse.entity.StrawberryPlantingRecord;
import com.agriculture.greenhouse.mapper.StrawberryPlantingRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DroneInspectionRouteService {

    private final DroneInspectionRouteMapper routeMapper;
    private final DroneInspectionRoutePointMapper pointMapper;
    private final StrawberryPlotMapper plotMapper;
    private final StrawberryPlantingRecordMapper plantingMapper;
    private final DroneInspectionPointMapper inspectionPointMapper;

    /**
     * 生成巡检路线（基于温室地块 + 巡检点）
     */
    @Transactional
    public DroneInspectionRoute generate(RouteGenerateReq req) {
        Long gid = req.getGreenhouseId() != null ? req.getGreenhouseId() : 1L;

        // 1. 读取温室地块
        List<StrawberryPlot> allPlots = plotMapper.selectList(
                new LambdaQueryWrapper<StrawberryPlot>()
                        .eq(StrawberryPlot::getDeleted, 0)
                        .eq(StrawberryPlot::getGreenhouseId, gid)
                        .orderByAsc(StrawberryPlot::getAreaCode, StrawberryPlot::getRowNo, StrawberryPlot::getColNo));

        // 2. 读取种植记录(获取地块状态)
        Map<Long, StrawberryPlantingRecord> plantingMap = new HashMap<>();
        if (!allPlots.isEmpty()) {
            List<Long> plantingIds = allPlots.stream()
                    .map(StrawberryPlot::getCurrentPlantingRecordId).filter(Objects::nonNull).toList();
            if (!plantingIds.isEmpty()) {
                List<StrawberryPlantingRecord> plantings = plantingMapper.selectBatchIds(plantingIds);
                plantingMap = plantings.stream().collect(Collectors.toMap(
                        StrawberryPlantingRecord::getId, p -> p, (a, b) -> a));
            }
        }

        // 3. 读取巡检点
        LambdaQueryWrapper<DroneInspectionPoint> ptQw = new LambdaQueryWrapper<>();
        ptQw.eq(DroneInspectionPoint::getGreenhouseId, gid)
            .eq(DroneInspectionPoint::getEnabled, 1);
        List<DroneInspectionPoint> inspectionPoints = inspectionPointMapper.selectList(ptQw);
        Map<Long, DroneInspectionPoint> pointMap = inspectionPoints.stream()
                .collect(Collectors.toMap(DroneInspectionPoint::getId, p -> p, (a, b) -> a));

        // 4. 按范围过滤
        List<StrawberryPlot> filteredPlots = filterByScope(allPlots, req.getScope());

        // 5. 按模式筛选排序
        List<StrawberryPlot> candidates = filterByMode(filteredPlots, req.getRouteMode(), plantingMap);

        // 6. 按自定义选择
        if ("CUSTOM".equals(req.getRouteMode()) && req.getSelectedPlotIds() != null && !req.getSelectedPlotIds().isEmpty()) {
            Set<Long> selIds = new HashSet<>(req.getSelectedPlotIds());
            candidates = allPlots.stream().filter(p -> selIds.contains(p.getId())).collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            throw new BizException(400, "没有符合条件的巡检点，请调整温室、范围或模式");
        }

        // 7. 排序
        List<StrawberryPlot> ordered = sortPlots(candidates, req.getRouteStrategy());

        // 8. 生成路线点
        List<DroneInspectionRoutePoint> routePoints = new ArrayList<>();
        int seq = 1;
        for (StrawberryPlot plot : ordered) {
            DroneInspectionRoutePoint rp = new DroneInspectionRoutePoint();
            rp.setSequenceNo(seq++);
            rp.setPlotId(plot.getId());
            rp.setPlantingRecordId(plot.getCurrentPlantingRecordId());
            rp.setAreaCode(plot.getAreaCode());
            rp.setAreaName(plot.getAreaName());
            rp.setRowNo(plot.getRowNo());
            rp.setColNo(plot.getColNo());
            // 生成简单坐标：基于区域+行列模拟坐标
            rp.setCoordinateX(BigDecimal.valueOf(areaOffset(plot.getAreaCode()) + (plot.getColNo() != null ? plot.getColNo() * 4 : 4)));
            rp.setCoordinateY(BigDecimal.valueOf(3.5));
            rp.setCoordinateZ(BigDecimal.valueOf((plot.getRowNo() != null ? plot.getRowNo() : 1) * 5.0));

            // 状态映射
            String plotStatus = plot.getPlotStatus();
            if ("ABNORMAL".equals(plotStatus)) {
                rp.setPointType("ABNORMAL");
                rp.setActionType("DETECT");
                rp.setStaySeconds(5);
            } else {
                rp.setPointType("NORMAL");
                rp.setActionType("INSPECT");
                rp.setStaySeconds(3);
            }
            // 匹配种植记录状态
            StrawberryPlantingRecord rec = plot.getCurrentPlantingRecordId() != null
                    ? plantingMap.get(plot.getCurrentPlantingRecordId()) : null;
            if (rec != null && rec.getGrowthStatus() != null && rec.getGrowthStatus().contains("成熟")) {
                rp.setPointType("MATURE");
            }

            routePoints.add(rp);
        }

        // 9. 创建路线
        DroneInspectionRoute route = new DroneInspectionRoute();
        route.setRouteCode("DR" + System.currentTimeMillis() % 100000000);
        route.setRouteName(req.getRouteName() != null && !req.getRouteName().isBlank()
                ? req.getRouteName() : "自动生成巡检路线");
        route.setBaseId(req.getBaseId());
        route.setGreenhouseId(gid);
        route.setRouteMode(req.getRouteMode() != null ? req.getRouteMode() : "FULL");
        route.setScope(req.getScope() != null ? req.getScope() : "ALL");
        route.setRouteStrategy(req.getRouteStrategy() != null ? req.getRouteStrategy() : "AREA_ORDER");
        route.setPointCount(routePoints.size());
        route.setEstimatedDuration(routePoints.stream().mapToInt(DroneInspectionRoutePoint::getStaySeconds).sum() + routePoints.size() * 5);
        route.setStatus("ACTIVE");
        route.setIsCommon(req.getIsCommon() != null && req.getIsCommon() ? 1 : 0);
        route.setCreateTime(LocalDateTime.now());
        route.setUpdateTime(LocalDateTime.now());

        routeMapper.insert(route);

        // 10. 保存路线点
        for (DroneInspectionRoutePoint rp : routePoints) {
            rp.setRouteId(route.getId());
            rp.setCreateTime(LocalDateTime.now());
            pointMapper.insert(rp);
        }

        return route;
    }

    @Transactional
    public void deleteRoute(Long id) {
        pointMapper.delete(new LambdaQueryWrapper<DroneInspectionRoutePoint>()
                .eq(DroneInspectionRoutePoint::getRouteId, id));
        routeMapper.deleteById(id);
    }

    public List<DroneInspectionRoutePoint> getRoutePoints(Long routeId) {
        return pointMapper.selectList(new LambdaQueryWrapper<DroneInspectionRoutePoint>()
                .eq(DroneInspectionRoutePoint::getRouteId, routeId)
                .orderByAsc(DroneInspectionRoutePoint::getSequenceNo));
    }

    // ===== private helpers =====

    private List<StrawberryPlot> filterByScope(List<StrawberryPlot> plots, String scope) {
        if (scope == null || "ALL".equals(scope)) return plots;
        String code = switch (scope) {
            case "A_AREA" -> "A"; case "B_AREA" -> "B";
            case "C_AREA" -> "C"; case "D_AREA" -> "D";
            default -> null;
        };
        if (code == null) return plots;
        return plots.stream().filter(p -> code.equals(p.getAreaCode())).collect(Collectors.toList());
    }

    private List<StrawberryPlot> filterByMode(List<StrawberryPlot> plots, String mode,
                                               Map<Long, StrawberryPlantingRecord> plantingMap) {
        if (mode == null || "FULL".equals(mode)) return new ArrayList<>(plots);

        switch (mode) {
            case "ABNORMAL_PRIORITY":
                // 优先异常状态地块
                List<StrawberryPlot> abnormals = plots.stream()
                        .filter(p -> "ABNORMAL".equals(p.getPlotStatus())).collect(Collectors.toList());
                List<StrawberryPlot> normals = plots.stream()
                        .filter(p -> !"ABNORMAL".equals(p.getPlotStatus())).collect(Collectors.toList());
                abnormals.addAll(normals);
                return abnormals;
            case "MATURE":
                // 选择成熟期地块
                return plots.stream().filter(p -> {
                    StrawberryPlantingRecord rec = p.getCurrentPlantingRecordId() != null
                            ? plantingMap.get(p.getCurrentPlantingRecordId()) : null;
                    return rec != null && rec.getGrowthStatus() != null && rec.getGrowthStatus().contains("成熟");
                }).collect(Collectors.toList());
            case "DISEASE":
                // 有异常记录的地块
                return plots.stream()
                        .filter(p -> "ABNORMAL".equals(p.getPlotStatus())).collect(Collectors.toList());
            default:
                return new ArrayList<>(plots);
        }
    }

    private List<StrawberryPlot> sortPlots(List<StrawberryPlot> plots, String strategy) {
        if (strategy == null || "AREA_ORDER".equals(strategy)) {
            return plots.stream().sorted(
                    Comparator.comparing(StrawberryPlot::getAreaCode, Comparator.nullsLast(String::compareTo))
                            .thenComparing(StrawberryPlot::getRowNo, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(StrawberryPlot::getColNo, Comparator.nullsLast(Integer::compareTo))
            ).collect(Collectors.toList());
        }
        if ("ABNORMAL_FIRST".equals(strategy)) {
            return plots.stream().sorted(
                    Comparator.comparingInt((StrawberryPlot p) -> "ABNORMAL".equals(p.getPlotStatus()) ? 0 : 1)
                            .thenComparing(StrawberryPlot::getAreaCode, Comparator.nullsLast(String::compareTo))
                            .thenComparing(StrawberryPlot::getRowNo, Comparator.nullsLast(Integer::compareTo))
            ).collect(Collectors.toList());
        }
        // NEAREST / MANUAL: 按区域顺序
        return sortPlots(plots, "AREA_ORDER");
    }

    private double areaOffset(String code) {
        if (code == null) return 0;
        return switch (code) { case "A" -> 0; case "B" -> 50; case "C" -> 100; case "D" -> 150; default -> 0; };
    }
}

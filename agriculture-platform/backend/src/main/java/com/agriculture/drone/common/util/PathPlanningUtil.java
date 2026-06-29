package com.agriculture.drone.common.util;

import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.route.dto.RouteGenerateRequest;
import com.agriculture.drone.route.entity.DroneRoutePlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 无人机巡检路径规划 - 基于经纬度Haversine
 */
public class PathPlanningUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * ORDER: 按巡检点顺序生成路径
     */
    public static DroneRoutePlan generateByOrder(List<DroneInspectionPoint> points, RouteGenerateRequest req) {
        List<Map<String, Object>> waypoints = new ArrayList<>();
        double totalDist = 0;
        int order = 0;

        double prevLat = 0, prevLng = 0;
        boolean first = true;

        for (DroneInspectionPoint p : points) {
            double lat = p.getLatitude().doubleValue();
            double lng = p.getLongitude().doubleValue();

            if (!first) {
                totalDist += GeoDistanceUtil.haversine(prevLat, prevLng, lat, lng);
            }
            first = false;

            Map<String, Object> wp = new LinkedHashMap<>();
            wp.put("longitude", p.getLongitude());
            wp.put("latitude", p.getLatitude());
            wp.put("altitude", p.getAltitude());
            wp.put("pointName", p.getPointName());
            wp.put("pointId", p.getId());
            wp.put("pointType", p.getPointType());
            wp.put("orderIndex", order++);
            waypoints.add(wp);

            prevLat = lat;
            prevLng = lng;
        }

        DroneRoutePlan plan = new DroneRoutePlan();
        plan.setRouteCode("DR" + System.currentTimeMillis());
        plan.setRouteName(req.getRouteName());
        plan.setGreenhouseId(req.getGreenhouseId());
        plan.setRouteType(req.getRouteType() != null ? req.getRouteType() : "DAILY_INSPECTION");
        plan.setAlgorithmType("ORDER");
        plan.setTotalDistance(GeoDistanceUtil.toBigDecimal(totalDist));
        plan.setEstimatedTime(GeoDistanceUtil.estimatedTimeSeconds(totalDist));
        plan.setStatus("ACTIVE");
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());

        setStartEndFromWaypoints(plan, waypoints);
        try { plan.setWaypoints(MAPPER.writeValueAsString(waypoints)); } catch (Exception e) { plan.setWaypoints("[]"); }

        return plan;
    }

    /**
     * NEAREST: 最近邻算法生成路径
     */
    public static DroneRoutePlan generateByNearest(List<DroneInspectionPoint> points, RouteGenerateRequest req) {
        if (points.size() <= 2) return generateByOrder(points, req);

        List<DroneInspectionPoint> remaining = new ArrayList<>(points);
        // 找到START类型的点作为起点
        DroneInspectionPoint startPt = remaining.stream()
                .filter(p -> "START".equals(p.getPointType())).findFirst().orElse(remaining.get(0));
        remaining.remove(startPt);

        // 找到END类型的点，留到最后
        DroneInspectionPoint endPt = remaining.stream()
                .filter(p -> "END".equals(p.getPointType())).findFirst().orElse(null);
        if (endPt != null) remaining.remove(endPt);

        List<DroneInspectionPoint> ordered = new ArrayList<>();
        ordered.add(startPt);

        double curLat = startPt.getLatitude().doubleValue();
        double curLng = startPt.getLongitude().doubleValue();

        while (!remaining.isEmpty()) {
            DroneInspectionPoint nearest = null;
            double minDist = Double.MAX_VALUE;
            for (DroneInspectionPoint p : remaining) {
                double d = GeoDistanceUtil.haversine(curLat, curLng,
                        p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
                if (d < minDist) { minDist = d; nearest = p; }
            }
            ordered.add(nearest);
            curLat = nearest.getLatitude().doubleValue();
            curLng = nearest.getLongitude().doubleValue();
            remaining.remove(nearest);
        }

        if (endPt != null) ordered.add(endPt);

        // 用ordering后的结果通过order逻辑生成
        List<Map<String, Object>> waypoints = new ArrayList<>();
        double totalDist = 0;
        int order = 0;
        double prevLat = 0, prevLng = 0;
        boolean first = true;

        for (DroneInspectionPoint p : ordered) {
            double lat = p.getLatitude().doubleValue();
            double lng = p.getLongitude().doubleValue();
            if (!first) totalDist += GeoDistanceUtil.haversine(prevLat, prevLng, lat, lng);
            first = false;

            Map<String, Object> wp = new LinkedHashMap<>();
            wp.put("longitude", p.getLongitude());
            wp.put("latitude", p.getLatitude());
            wp.put("altitude", p.getAltitude());
            wp.put("pointName", p.getPointName());
            wp.put("pointId", p.getId());
            wp.put("pointType", p.getPointType());
            wp.put("orderIndex", order++);
            waypoints.add(wp);

            prevLat = lat; prevLng = lng;
        }

        DroneRoutePlan plan = new DroneRoutePlan();
        plan.setRouteCode("DR" + System.currentTimeMillis());
        plan.setRouteName(req.getRouteName());
        plan.setGreenhouseId(req.getGreenhouseId());
        plan.setRouteType(req.getRouteType() != null ? req.getRouteType() : "DAILY_INSPECTION");
        plan.setAlgorithmType("NEAREST");
        plan.setTotalDistance(GeoDistanceUtil.toBigDecimal(totalDist));
        plan.setEstimatedTime(GeoDistanceUtil.estimatedTimeSeconds(totalDist));
        plan.setStatus("ACTIVE");
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());

        setStartEndFromWaypoints(plan, waypoints);
        try { plan.setWaypoints(MAPPER.writeValueAsString(waypoints)); } catch (Exception e) { plan.setWaypoints("[]"); }

        return plan;
    }

    private static void setStartEndFromWaypoints(DroneRoutePlan plan, List<Map<String, Object>> wps) {
        if (wps.isEmpty()) return;
        Map<String, Object> first = wps.get(0);
        Map<String, Object> last = wps.get(wps.size() - 1);
        try {
            plan.setStartPoint(MAPPER.writeValueAsString(first));
            plan.setEndPoint(MAPPER.writeValueAsString(last));
        } catch (Exception ignored) {}
    }

    /**
     * Z字形场景坐标路径生成 - 用于 Three.js 温室巡检动画
     * 排序规则: 按areaId → 按rowIndex升序 → 奇数行pointIndex升序, 偶数行pointIndex降序
     */
    public static DroneRoutePlan generateSceneRoute(List<DroneInspectionPoint> points, Long greenhouseId, String routeName) {
        // 分离START/END点
        List<DroneInspectionPoint> normalPts = new ArrayList<>();
        DroneInspectionPoint startPt = null;
        DroneInspectionPoint endPt = null;
        for (DroneInspectionPoint p : points) {
            if ("START".equals(p.getPointType())) startPt = p;
            else if ("END".equals(p.getPointType())) endPt = p;
            else normalPts.add(p);
        }

        // Z字形排序
        normalPts.sort((a, b) -> {
            Long aArea = a.getAreaId() != null ? a.getAreaId() : 0L;
            Long bArea = b.getAreaId() != null ? b.getAreaId() : 0L;
            if (!aArea.equals(bArea)) return aArea.compareTo(bArea);
            int aRow = a.getRowIndex() != null ? a.getRowIndex() : 0;
            int bRow = b.getRowIndex() != null ? b.getRowIndex() : 0;
            if (aRow != bRow) return Integer.compare(aRow, bRow);
            int aPi = a.getPointIndex() != null ? a.getPointIndex() : 0;
            int bPi = b.getPointIndex() != null ? b.getPointIndex() : 0;
            // 偶数行降序, 奇数行升序
            if (aRow % 2 == 0) return Integer.compare(-aPi, -bPi);
            else return Integer.compare(aPi, bPi);
        });

        List<Map<String, Object>> waypoints = new ArrayList<>();
        int order = 0;
        double totalDist = 0, prevX = 0, prevZ = 0;
        boolean first = true;

        // 插入START点
        if (startPt != null) {
            Map<String, Object> sp = pointToMap(startPt, order++);
            waypoints.add(sp);
            prevX = startPt.getSceneX() != null ? startPt.getSceneX().doubleValue() : 0;
            prevZ = startPt.getSceneZ() != null ? startPt.getSceneZ().doubleValue() : 0;
            first = false;
        }
        // 遍历排序后的巡检点
        for (DroneInspectionPoint p : normalPts) {
            double sx = p.getSceneX() != null ? p.getSceneX().doubleValue() : 0;
            double sz = p.getSceneZ() != null ? p.getSceneZ().doubleValue() : 0;
            if (!first) totalDist += GeoDistanceUtil.haversine(prevX, 0, sx, 0) + Math.abs(sz - prevZ) * 111; // 近似距离
            Map<String, Object> wp = pointToMap(p, order++);
            waypoints.add(wp);
            prevX = sx; prevZ = sz;
            first = false;
        }
        // 插入END点
        if (endPt != null) {
            double sx = endPt.getSceneX() != null ? endPt.getSceneX().doubleValue() : 0;
            double sz = endPt.getSceneZ() != null ? endPt.getSceneZ().doubleValue() : 0;
            if (!first) totalDist += GeoDistanceUtil.haversine(prevX, 0, sx, 0) + Math.abs(sz - prevZ) * 111;
            Map<String, Object> ep = pointToMap(endPt, order++);
            waypoints.add(ep);
        }

        DroneRoutePlan plan = new DroneRoutePlan();
        plan.setRouteCode("DR" + System.currentTimeMillis());
        plan.setRouteName(routeName != null ? routeName : "温室巡检路径");
        plan.setGreenhouseId(greenhouseId);
        plan.setRouteType("SCENE_INSPECTION");
        plan.setAlgorithmType("ZIGZAG");
        plan.setTotalDistance(GeoDistanceUtil.toBigDecimal(totalDist));
        plan.setEstimatedTime(GeoDistanceUtil.estimatedTimeSeconds(totalDist));
        plan.setStatus("ACTIVE");
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());

        setStartEndFromWaypoints(plan, waypoints);
        try { plan.setWaypoints(MAPPER.writeValueAsString(waypoints)); } catch (Exception e) { plan.setWaypoints("[]"); }
        return plan;
    }

    private static Map<String, Object> pointToMap(DroneInspectionPoint p, int order) {
        Map<String, Object> wp = new LinkedHashMap<>();
        wp.put("pointId", p.getId());
        wp.put("pointName", p.getPointName());
        wp.put("pointType", p.getPointType());
        wp.put("areaId", p.getAreaId());
        wp.put("areaName", p.getAreaName());
        wp.put("sceneX", p.getSceneX() != null ? p.getSceneX() : BigDecimal.ZERO);
        wp.put("sceneY", p.getSceneY() != null ? p.getSceneY() : BigDecimal.valueOf(3.5));
        wp.put("sceneZ", p.getSceneZ() != null ? p.getSceneZ() : BigDecimal.ZERO);
        wp.put("rowIndex", p.getRowIndex());
        wp.put("pointIndex", p.getPointIndex());
        wp.put("status", p.getStatus());
        wp.put("orderIndex", order);
        wp.put("longitude", p.getLongitude());
        wp.put("latitude", p.getLatitude());
        wp.put("altitude", p.getAltitude());
        return wp;
    }
}

package com.agri.common.utils;

import java.util.List;

/**
 * 地理坐标工具类
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS = 6371000; // 地球半径（米）

    private GeoUtils() {}

    /**
     * 计算两点之间的距离（米）- Haversine公式
     */
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(radLat1) * Math.cos(radLat2) *
                Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 计算多边形面积（平方米）- Shoelace公式（近似）
     * 坐标格式: [[lng1,lat1], [lng2,lat2], ...]
     */
    public static double polygonArea(List<double[]> coordinates) {
        if (coordinates == null || coordinates.size() < 3) return 0;

        double area = 0;
        int n = coordinates.size();

        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double[] p1 = coordinates.get(i);
            double[] p2 = coordinates.get(j);
            area += p1[0] * p2[1];
            area -= p2[0] * p1[1];
        }

        area = Math.abs(area) / 2.0;
        // 转换为平方米（粗略近似，适用于小范围）
        double avgLat = coordinates.stream().mapToDouble(c -> c[1]).average().orElse(0);
        double latFactor = Math.cos(Math.toRadians(avgLat));
        return area * 111319.9 * 111319.9 * latFactor;
    }

    /**
     * 计算路线总长度（米）
     */
    public static double routeLength(List<double[]> waypoints) {
        if (waypoints == null || waypoints.size() < 2) return 0;

        double total = 0;
        for (int i = 1; i < waypoints.size(); i++) {
            double[] p1 = waypoints.get(i - 1);
            double[] p2 = waypoints.get(i);
            total += distance(p1[1], p1[0], p2[1], p2[0]);
        }
        return total;
    }

    /**
     * 计算两点之间的方位角（度）
     */
    public static double bearing(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLng = Math.toRadians(lng2 - lng1);

        double y = Math.sin(deltaLng) * Math.cos(radLat2);
        double x = Math.cos(radLat1) * Math.sin(radLat2) -
                Math.sin(radLat1) * Math.cos(radLat2) * Math.cos(deltaLng);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    /**
     * 判断点是否在多边形内 - 射线法
     */
    public static boolean isPointInPolygon(double lat, double lng, List<double[]> polygon) {
        if (polygon == null || polygon.size() < 3) return false;

        boolean inside = false;
        int n = polygon.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double[] pi = polygon.get(i);
            double[] pj = polygon.get(j);

            if ((pi[1] > lng) != (pj[1] > lng) &&
                lat < (pj[0] - pi[0]) * (lng - pi[1]) / (pj[1] - pi[1]) + pi[0]) {
                inside = !inside;
            }
        }
        return inside;
    }

    /**
     * 计算路线覆盖率
     */
    public static double coverageRate(double workedArea, double totalArea) {
        if (totalArea <= 0) return 0;
        return Math.min(workedArea / totalArea * 100, 100);
    }
}

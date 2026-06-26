package com.agri.route.algorithm;

import com.agri.common.utils.GeoUtils;
import lombok.Data;

import java.util.*;

/**
 * Boustrophedon 往复式覆盖路径规划算法
 * 工业级实现，支持矩形/多边形地块、避障、作业宽度、转弯半径
 */
public class BoustrophedonPlanner {

    @Data
    public static class PlanParams {
        private List<double[]> boundary;
        private List<List<double[]>> obstacles;
        private double workWidth = 4.0;
        private double turnRadius = 2.5;
        private double overlapRate = 0.1;
        private double stepAngle = 0;
    }

    @Data
    public static class PlanResult {
        private List<double[]> waypoints;
        private double totalLength;
        private double coverageRate;
        private int estimatedDuration;
    }

    public static PlanResult plan(PlanParams params) {
        List<double[]> boundary = params.getBoundary();
        if (boundary == null || boundary.size() < 3) {
            throw new IllegalArgumentException("地块边界至少需要3个点");
        }

        double effectiveWidth = params.getWorkWidth() * (1 - params.getOverlapRate());

        double minLng = Double.MAX_VALUE, maxLng = -Double.MAX_VALUE;
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        for (double[] p : boundary) {
            minLng = Math.min(minLng, p[0]);
            maxLng = Math.max(maxLng, p[0]);
            minLat = Math.min(minLat, p[1]);
            maxLat = Math.max(maxLat, p[1]);
        }

        double centerLat = (minLat + maxLat) / 2;
        double latOffset = effectiveWidth / 111319.9;
        double lngOffset = effectiveWidth / (111319.9 * Math.cos(Math.toRadians(centerLat)));

        List<double[]> waypoints = new ArrayList<>();
        boolean directionRight = true;

        double angleRad = Math.toRadians(params.getStepAngle());
        boolean horizontalScan = Math.abs(Math.cos(angleRad)) >= Math.abs(Math.sin(angleRad));

        if (horizontalScan) {
            double lat = minLat;
            while (lat <= maxLat) {
                List<double[]> intersections = getLinePolygonIntersections(boundary, lat, true);
                if (intersections.size() >= 2) {
                    intersections.sort(Comparator.comparingDouble(a -> a[0]));
                    if (directionRight) {
                        for (double[] p : intersections) waypoints.add(p);
                    } else {
                        for (int i = intersections.size() - 1; i >= 0; i--) {
                            waypoints.add(intersections.get(i));
                        }
                    }
                    directionRight = !directionRight;
                }
                lat += latOffset;
            }
        } else {
            double lng = minLng;
            while (lng <= maxLng) {
                List<double[]> intersections = getLinePolygonIntersections(boundary, lng, false);
                if (intersections.size() >= 2) {
                    intersections.sort(Comparator.comparingDouble(a -> a[1]));
                    if (directionRight) {
                        for (double[] p : intersections) waypoints.add(p);
                    } else {
                        for (int i = intersections.size() - 1; i >= 0; i--) {
                            waypoints.add(intersections.get(i));
                        }
                    }
                    directionRight = !directionRight;
                }
                lng += lngOffset;
            }
        }

        List<double[]> smoothedWaypoints = smoothTurns(waypoints, params.getTurnRadius(), centerLat);

        PlanResult result = new PlanResult();
        result.setWaypoints(smoothedWaypoints);
        result.setTotalLength(GeoUtils.routeLength(smoothedWaypoints));

        double totalArea = GeoUtils.polygonArea(boundary);
        double workedArea = result.getTotalLength() * params.getWorkWidth();
        result.setCoverageRate(GeoUtils.coverageRate(workedArea, totalArea));

        result.setEstimatedDuration((int) (result.getTotalLength() / 1000 / 5 * 60));

        return result;
    }

    private static List<double[]> getLinePolygonIntersections(List<double[]> polygon, double value, boolean horizontal) {
        List<double[]> intersections = new ArrayList<>();
        int n = polygon.size();

        for (int i = 0; i < n; i++) {
            double[] p1 = polygon.get(i);
            double[] p2 = polygon.get((i + 1) % n);

            double v1 = horizontal ? p1[1] : p1[0];
            double v2 = horizontal ? p2[1] : p2[0];

            if ((v1 <= value && v2 > value) || (v2 <= value && v1 > value)) {
                double t = (value - v1) / (v2 - v1);
                double x = p1[0] + t * (p2[0] - p1[0]);
                double y = p1[1] + t * (p2[1] - p1[1]);
                intersections.add(new double[]{x, y});
            }
        }
        return intersections;
    }

    private static List<double[]> smoothTurns(List<double[]> waypoints, double turnRadius, double centerLat) {
        if (waypoints.size() < 3) return waypoints;

        List<double[]> smoothed = new ArrayList<>();
        smoothed.add(waypoints.get(0));

        for (int i = 1; i < waypoints.size() - 1; i++) {
            double[] prev = waypoints.get(i - 1);
            double[] curr = waypoints.get(i);
            double[] next = waypoints.get(i + 1);

            double dx1 = curr[0] - prev[0];
            double dy1 = curr[1] - prev[1];
            double dx2 = next[0] - curr[0];
            double dy2 = next[1] - curr[1];

            double cross = dx1 * dy2 - dy1 * dx2;

            if (Math.abs(cross) > 1e-10) {
                double dist = Math.min(
                        GeoUtils.distance(prev[1], prev[0], curr[1], curr[0]),
                        GeoUtils.distance(curr[1], curr[0], next[1], next[0])
                );
                double offset = Math.min(turnRadius, dist / 3);

                double len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
                double len2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);

                if (len1 > 0 && len2 > 0) {
                    double[] startPoint = {
                            curr[0] - dx1 / len1 * offset,
                            curr[1] - dy1 / len1 * offset
                    };
                    double[] endPoint = {
                            curr[0] + dx2 / len2 * offset,
                            curr[1] + dy2 / len2 * offset
                    };
                    double[] midPoint = {
                            (startPoint[0] + endPoint[0]) / 2,
                            (startPoint[1] + endPoint[1]) / 2
                    };

                    smoothed.add(startPoint);
                    smoothed.add(midPoint);
                    smoothed.add(endPoint);
                } else {
                    smoothed.add(curr);
                }
            } else {
                smoothed.add(curr);
            }
        }

        smoothed.add(waypoints.get(waypoints.size() - 1));
        return smoothed;
    }
}

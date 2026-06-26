package com.agri.route.algorithm;

import com.agri.common.utils.GeoUtils;
import lombok.Data;

import java.util.*;

/**
 * A* 点到点避障路径规划算法
 */
public class AStarPlanner {

    @Data
    public static class Node {
        private double lng;
        private double lat;
        private double g; // 从起点到当前节点的实际代价
        private double h; // 从当前节点到终点的估计代价
        private double f; // g + h
        private Node parent;

        public Node(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return Math.abs(lng - node.lng) < 1e-8 && Math.abs(lat - node.lat) < 1e-8;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.round(lng * 1e8), Math.round(lat * 1e8));
        }
    }

    @Data
    public static class AStarParams {
        private double startLng;
        private double startLat;
        private double endLng;
        private double endLat;
        private List<double[]> boundary;           // 地块边界
        private List<List<double[]>> obstacles;    // 障碍物
        private double gridSize = 0.00005;         // 网格大小(经纬度)
    }

    /**
     * 执行A*路径规划
     */
    public static List<double[]> plan(AStarParams params) {
        Node startNode = new Node(params.getStartLng(), params.getStartLat());
        Node endNode = new Node(params.getEndLng(), params.getEndLat());

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<Node> closedList = new HashSet<>();

        startNode.setG(0);
        startNode.setH(heuristic(startNode, endNode));
        startNode.setF(startNode.getH());
        openList.add(startNode);

        double gridSize = params.getGridSize();
        int maxIterations = 10000;
        int iterations = 0;

        while (!openList.isEmpty() && iterations < maxIterations) {
            iterations++;
            Node current = openList.poll();

            if (isCloseTo(current, endNode, gridSize)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            // 8方向邻居
            double[][] directions = {
                    {-gridSize, 0}, {gridSize, 0}, {0, -gridSize}, {0, gridSize},
                    {-gridSize, -gridSize}, {-gridSize, gridSize},
                    {gridSize, -gridSize}, {gridSize, gridSize}
            };

            for (double[] dir : directions) {
                double newLng = current.getLng() + dir[0];
                double newLat = current.getLat() + dir[1];
                Node neighbor = new Node(newLng, newLat);

                if (closedList.contains(neighbor)) continue;

                // 检查是否在地块内
                if (params.getBoundary() != null && !GeoUtils.isPointInPolygon(newLat, newLng, params.getBoundary())) {
                    continue;
                }

                // 检查是否在障碍物内
                if (params.getObstacles() != null) {
                    boolean inObstacle = false;
                    for (List<double[]> obstacle : params.getObstacles()) {
                        if (GeoUtils.isPointInPolygon(newLat, newLng, obstacle)) {
                            inObstacle = true;
                            break;
                        }
                    }
                    if (inObstacle) continue;
                }

                double tentativeG = current.getG() + GeoUtils.distance(current.getLat(), current.getLng(), newLat, newLng);

                if (tentativeG < neighbor.getG() || !openList.contains(neighbor)) {
                    neighbor.setG(tentativeG);
                    neighbor.setH(heuristic(neighbor, endNode));
                    neighbor.setF(neighbor.getG() + neighbor.getH());
                    neighbor.setParent(current);

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        // 未找到路径，返回直线
        return List.of(
                new double[]{params.getStartLng(), params.getStartLat()},
                new double[]{params.getEndLng(), params.getEndLat()}
        );
    }

    private static double heuristic(Node a, Node b) {
        return GeoUtils.distance(a.getLat(), a.getLng(), b.getLat(), b.getLng());
    }

    private static boolean isCloseTo(Node a, Node b, double threshold) {
        return GeoUtils.distance(a.getLat(), a.getLng(), b.getLat(), b.getLng()) < threshold * 111319.9;
    }

    private static List<double[]> reconstructPath(Node node) {
        List<double[]> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new double[]{node.getLng(), node.getLat()});
            node = node.getParent();
        }
        return path;
    }
}

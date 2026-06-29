package com.agriculture.drone.point.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.entity.GreenhouseArea;
import com.agriculture.drone.point.mapper.GreenhouseAreaMapper;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 巡检点生成服务 - 根据温室分区真实数据自动生成巡检点
 * 生成规则:
 *   NORMAL 分区: 每行1个巡检点
 *   WARNING 分区: 每行2个巡检点
 *   ABNORMAL 分区: 每行3个巡检点
 *   disease_risk=HIGH: 每行3个巡检点 + 1个异常复核点
 *   奇数行 point_index 升序, 偶数行 point_index 降序 → Z字形
 */
@Service
@RequiredArgsConstructor
public class InspectionPointGenerateServiceImpl {

    private final GreenhouseAreaMapper areaMapper;
    private final DroneInspectionPointMapper pointMapper;

    /**
     * 根据分区ID生成巡检点
     */
    @Transactional
    public List<DroneInspectionPoint> generateByArea(Long areaId) {
        GreenhouseArea area = areaMapper.selectById(areaId);
        if (area == null) throw new BizException(400, "温室分区不存在");

        // 删除该分区旧 AUTO 巡检点
        LambdaQueryWrapper<DroneInspectionPoint> delW = new LambdaQueryWrapper<>();
        delW.eq(DroneInspectionPoint::getAreaId, areaId)
            .eq(DroneInspectionPoint::getSourceType, "AUTO");
        pointMapper.delete(delW);

        List<DroneInspectionPoint> points = buildPoints(area);
        for (DroneInspectionPoint p : points) {
            pointMapper.insert(p);
        }
        return points;
    }

    /**
     * 批量生成所有分区的巡检点
     */
    @Transactional
    public int generateAll() {
        List<GreenhouseArea> allAreas = areaMapper.selectList(null);
        int total = 0;
        for (GreenhouseArea area : allAreas) {
            // 删除该分区旧 AUTO 巡检点
            LambdaQueryWrapper<DroneInspectionPoint> delW = new LambdaQueryWrapper<>();
            delW.eq(DroneInspectionPoint::getAreaId, area.getId())
                .eq(DroneInspectionPoint::getSourceType, "AUTO");
            pointMapper.delete(delW);

            List<DroneInspectionPoint> pts = buildPoints(area);
            for (DroneInspectionPoint p : pts) {
                pointMapper.insert(p);
            }
            total += pts.size();
        }
        return total;
    }

    /**
     * 核心生成逻辑: 根据分区数据构建巡检点列表
     */
    private List<DroneInspectionPoint> buildPoints(GreenhouseArea area) {
        List<DroneInspectionPoint> points = new ArrayList<>();

        int rowCount = area.getRowCount() != null && area.getRowCount() > 0 ? area.getRowCount() : 4;
        int plantsPerRow = area.getPlantCountPerRow() != null && area.getPlantCountPerRow() > 0 ? area.getPlantCountPerRow() : 10;
        String areaStatus = area.getStatus() != null ? area.getStatus() : "NORMAL";
        String diseaseRisk = area.getDiseaseRisk() != null ? area.getDiseaseRisk() : "LOW";

        // 每行巡检点数量
        int pointsPerRow;
        if ("HIGH".equalsIgnoreCase(diseaseRisk)) {
            pointsPerRow = 3;
        } else {
            switch (areaStatus) {
                case "ABNORMAL": pointsPerRow = 3; break;
                case "WARNING":  pointsPerRow = 2; break;
                default:         pointsPerRow = 1;
            }
        }

        double cx = area.getSceneX() != null ? area.getSceneX().doubleValue() : 0;
        double cz = area.getSceneZ() != null ? area.getSceneZ().doubleValue() : 0;
        double width = area.getSceneWidth() != null ? area.getSceneWidth().doubleValue() : 4;
        double depth = area.getSceneDepth() != null ? area.getSceneDepth().doubleValue() : 6;
        double sceneY = 3.5;
        double halfW = width / 2.0;
        double halfD = depth / 2.0;

        String areaName = area.getAreaName() != null ? area.getAreaName() : "分区" + area.getId();

        for (int row = 0; row < rowCount; row++) {
            // 每行 Z 坐标: 从 cz+halfD 到 cz-halfD 均匀分布
            double rowZ;
            if (rowCount == 1) {
                rowZ = cz;
            } else {
                rowZ = cz + halfD - row * (depth / (rowCount - 1));
            }

            for (int pi = 0; pi < pointsPerRow; pi++) {
                // 行内 X 坐标: 从 cx-halfW 到 cx+halfW 均匀分布
                double px;
                if (pointsPerRow == 1) {
                    px = cx;
                } else {
                    px = cx - halfW + pi * (width / (pointsPerRow - 1));
                }

                DroneInspectionPoint point = new DroneInspectionPoint();
                point.setAreaId(area.getId());
                point.setGreenhouseId(area.getGreenhouseId() != null ? area.getGreenhouseId() : 1L);
                point.setAreaName(areaName);
                point.setRowIndex(row + 1);
                point.setPointIndex(pi + 1);
                point.setSceneX(BigDecimal.valueOf(px).setScale(2, RoundingMode.HALF_UP));
                point.setSceneY(BigDecimal.valueOf(sceneY));
                point.setSceneZ(BigDecimal.valueOf(rowZ).setScale(2, RoundingMode.HALF_UP));
                point.setPointType("NORMAL");
                point.setStatus(areaStatus);
                point.setSourceType("AUTO");
                point.setPointName(areaName + "第" + (row + 1) + "行巡检点" + (pi + 1));
                point.setCreateTime(LocalDateTime.now());
                point.setUpdateTime(LocalDateTime.now());
                points.add(point);
            }
        }

        // 高病害风险分区：额外生成1个复核点(分区中心)
        if ("HIGH".equalsIgnoreCase(diseaseRisk)) {
            DroneInspectionPoint p = new DroneInspectionPoint();
            p.setAreaId(area.getId());
            p.setGreenhouseId(area.getGreenhouseId() != null ? area.getGreenhouseId() : 1L);
            p.setAreaName(areaName);
            p.setRowIndex(0);
            p.setPointIndex(0);
            p.setSceneX(BigDecimal.valueOf(cx).setScale(2, RoundingMode.HALF_UP));
            p.setSceneY(BigDecimal.valueOf(sceneY));
            p.setSceneZ(BigDecimal.valueOf(cz).setScale(2, RoundingMode.HALF_UP));
            p.setPointType("ABNORMAL");
            p.setStatus("ABNORMAL");
            p.setSourceType("AUTO");
            p.setPointName(areaName + "异常复核点");
            p.setCreateTime(LocalDateTime.now());
            p.setUpdateTime(LocalDateTime.now());
            points.add(p);
        }

        return points;
    }
}

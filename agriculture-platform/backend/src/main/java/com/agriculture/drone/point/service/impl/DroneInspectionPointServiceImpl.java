package com.agriculture.drone.point.service.impl;

import com.agriculture.common.exception.BizException;
import com.agriculture.drone.point.entity.DroneInspectionPoint;
import com.agriculture.drone.point.mapper.DroneInspectionPointMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DroneInspectionPointServiceImpl extends ServiceImpl<DroneInspectionPointMapper, DroneInspectionPoint> {

    /**
     * 初始化默认巡检点（河南农业大学许昌校区温室草莓基地）
     */
    @Transactional
    public int initDefault(Long greenhouseId) {
        long count = count();
        if (count > 0) throw new BizException(400, "已有巡检点数据，无法重复初始化");

        LocalDateTime now = LocalDateTime.now();
        DroneInspectionPoint[] defaults = {
            point(greenhouseId, "起飞点", "起降区", "113.809055", "34.136151", "2.0", "START", "河南农业大学许昌校区无人机起飞点"),
            point(greenhouseId, "A区巡检点", "A区", "113.809097", "34.136248", "1.5", "NORMAL", "草莓A区日常巡检"),
            point(greenhouseId, "B区巡检点", "B区", "113.809247", "34.136328", "1.5", "NORMAL", "草莓B区日常巡检"),
            point(greenhouseId, "C区异常复核点", "C区", "113.809357", "34.136208", "1.5", "ABNORMAL", "C区发现异常需复核"),
            point(greenhouseId, "返航点", "起降区", "113.809055", "34.136151", "2.0", "END", "河南农业大学许昌校区无人机返航点")
        };
        for (DroneInspectionPoint p : defaults) save(p);
        return defaults.length;
    }

    private DroneInspectionPoint point(Long gid, String name, String area, String lng, String lat, String alt, String type, String remark) {
        DroneInspectionPoint p = new DroneInspectionPoint();
        p.setGreenhouseId(gid);
        p.setPointName(name);
        p.setAreaName(area);
        p.setLongitude(new BigDecimal(lng));
        p.setLatitude(new BigDecimal(lat));
        p.setAltitude(new BigDecimal(alt));
        p.setPointType(type);
        p.setRemark(remark);
        p.setCreateTime(LocalDateTime.now());
        p.setUpdateTime(LocalDateTime.now());
        return p;
    }
}

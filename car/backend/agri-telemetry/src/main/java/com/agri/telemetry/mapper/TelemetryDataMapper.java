package com.agri.telemetry.mapper;

import com.agri.telemetry.entity.TelemetryData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 遥测数据Mapper
 */
@Mapper
public interface TelemetryDataMapper extends BaseMapper<TelemetryData> {

    @Select("SELECT * FROM telemetry_data WHERE vehicle_id = #{vehicleId} " +
            "AND report_time BETWEEN #{startTime} AND #{endTime} ORDER BY report_time ASC")
    List<TelemetryData> selectByVehicleAndTimeRange(
            @Param("vehicleId") Long vehicleId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}

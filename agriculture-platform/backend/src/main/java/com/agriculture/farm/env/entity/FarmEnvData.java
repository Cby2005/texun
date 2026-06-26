package com.agriculture.farm.env.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("farm_env_data")
public class FarmEnvData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long enterpriseId;
    private Long landId;
    private String batchNo;
    private Long deviceId;
    /** 0大棚 1鱼塘 2大田 3仓库 */
    private Integer landType;
    private BigDecimal dataValue;
    private Integer year;
    private Integer yearDate;
    private Integer yearDateHour;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}

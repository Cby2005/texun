package com.agriculture.farm.land.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("farm_land")
public class FarmLand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long enterpriseId;
    private Integer number;
    /** 0大棚 1鱼塘 2大田 3仓库 */
    private Integer type;
    private String displayName;
    private BigDecimal area;
    private String location;
    private String cameraPassNum;
    @TableField(exist = false)
    private BigDecimal longitude;
    @TableField(exist = false)
    private BigDecimal latitude;
    private Integer status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
}

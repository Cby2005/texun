package com.agriculture.digitaltwin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("picking_robot")
public class PickingRobot {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String robotCode;
    private String robotName;
    private Long greenhouseId;
    private Integer battery;
    private String status;
    private BigDecimal maxLoad;
    private Long currentTaskId;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

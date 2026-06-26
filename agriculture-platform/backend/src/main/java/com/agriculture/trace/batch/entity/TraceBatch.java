package com.agriculture.trace.batch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("trace_batch")
public class TraceBatch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String productCode;
    private String productName;
    private BigDecimal quantity;
    private String unit;
    private LocalDate productionDate;
    private Long farmId;
    private Long landId;
    private Long cropId;
    /** 0生产中 1已完成 2已失效 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableField(exist = false)
    private String remark;
    @TableLogic
    private Integer deleted;
}

package com.agriculture.digitaltwin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("strawberry_sale_batch")
public class StrawberrySaleBatch {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String batchCode;
    private Long harvestRecordId;
    private Long plantingRecordId;
    private Long plotId;
    private Long greenhouseId;
    private String variety;
    private BigDecimal saleWeight;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String customerName;
    private String saleChannel;
    private LocalDate saleDate;
    private String traceCode;
    private String saleStatus;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

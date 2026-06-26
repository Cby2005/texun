package com.agriculture.trace.sales.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("trace_sales_record")
public class TraceSalesRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String seller;
    private String salesRegion;
    private LocalDateTime listingTime;
    private BigDecimal salesPrice;
    private Integer stockQuantity;
    private String salesStatus;
    private Long operatorId;
    private String operatorName;
    private String chainHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}

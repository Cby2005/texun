package com.agriculture.trace.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_product")
public class TraceProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productCode;
    private String productName;
    private String category;
    private String specification;
    private String unit;
    private String origin;
    private Long enterpriseId;
    private String producerName;
    private Long farmId;
    private Long cropId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

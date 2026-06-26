package com.agriculture.trace.storage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_storage_record")
public class TraceStorageRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private Integer quantity;
    private String changeType;
    private Integer changeQuantity;
    private Integer afterQuantity;
    private LocalDateTime changeTime;
    private String operatorName;
    private Long operatorId;
    private Long relatedRecordId;
    private String chainHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}

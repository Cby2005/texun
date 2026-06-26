package com.agriculture.trace.quality.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_quality_record")
public class TraceQualityRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private Long processingRecordId;
    private LocalDateTime qualityDate;
    private String qualityResult;
    private String inspector;
    private String reportUrl;
    private String inspectionItems;
    private String inspectionStandard;
    private Integer qualified;
    private Long operatorId;
    private String operatorName;
    private String chainHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}

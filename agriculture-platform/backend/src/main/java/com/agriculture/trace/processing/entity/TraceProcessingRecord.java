package com.agriculture.trace.processing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_processing_record")
public class TraceProcessingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String processingEnterprise;
    private LocalDateTime processingTime;
    private String processingMethod;
    private String processingTemperature;
    private String qualityResult;
    private String inspector;
    private String reportUrl;
    private Long operatorId;
    private String operatorName;
    private String chainHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}

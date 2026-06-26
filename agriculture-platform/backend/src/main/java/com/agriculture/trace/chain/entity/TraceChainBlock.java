package com.agriculture.trace.chain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_chain_block")
public class TraceChainBlock {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String chainId;
    private String blockHash;
    private String previousHash;
    private String dataHash;
    private String dataContent;
    private Long nonce;
    private Long timestamp;
    private String operatorId;
    private String operatorName;
    private String operationType;
    private String productCode;
    private String batchNo;
    private Integer blockHeight;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

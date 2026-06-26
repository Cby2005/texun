package com.agriculture.knowledge.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_search_log")
public class KnowledgeSearchLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String queryText;
    private Integer resultCount;
    private String topChunkIds;
    private Integer spentMs;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}

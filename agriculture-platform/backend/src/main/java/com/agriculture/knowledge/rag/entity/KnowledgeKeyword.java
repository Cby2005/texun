package com.agriculture.knowledge.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_keyword")
public class KnowledgeKeyword {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyword;
    private Long chunkId;
    private Long documentId;
    private Integer frequency;
    private Double tfidf;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}

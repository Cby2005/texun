package com.agriculture.knowledge.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_document")
public class KnowledgeDocument {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private String title;
    private String source;
    private String docType;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

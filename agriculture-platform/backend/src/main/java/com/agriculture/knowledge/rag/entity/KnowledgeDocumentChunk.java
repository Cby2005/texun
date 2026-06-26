package com.agriculture.knowledge.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_document_chunk")
public class KnowledgeDocumentChunk {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long documentId;
    private Integer chunkIndex;
    private String content;
    private Integer chunkSize;
    private String embeddingVector;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}

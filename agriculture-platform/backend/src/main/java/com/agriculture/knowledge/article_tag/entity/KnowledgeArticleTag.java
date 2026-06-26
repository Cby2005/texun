package com.agriculture.knowledge.article_tag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_article_tag")
public class KnowledgeArticleTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long tagId;
    private Double weight;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}

package com.agriculture.knowledge.tag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_tag")
public class KnowledgeTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String tagType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

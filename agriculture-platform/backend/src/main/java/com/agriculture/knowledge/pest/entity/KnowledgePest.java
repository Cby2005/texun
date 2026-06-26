package com.agriculture.knowledge.pest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_pest")
public class KnowledgePest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String affectedCrops;
    private String symptoms;
    private String cause;
    private String prevention;
    private String imgUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

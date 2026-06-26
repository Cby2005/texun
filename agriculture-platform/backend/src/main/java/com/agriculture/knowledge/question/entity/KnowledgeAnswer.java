package com.agriculture.knowledge.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_answer")
public class KnowledgeAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long userId;
    private String content;
    private Integer isAccepted;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

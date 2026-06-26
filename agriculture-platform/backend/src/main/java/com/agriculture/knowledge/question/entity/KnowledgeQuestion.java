package com.agriculture.knowledge.question.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_question")
public class KnowledgeQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private Long cropId;
    @TableField(exist = false)
    private String cropType;
    private String title;
    private String content;
    private String imageUrls;
    /** OPEN/RESOLVED/CLOSED */
    private String status;
    private Integer viewCount;
    private Integer answerCount;
    private Long bestAnswerId;
    /** YOLO诊断状态: 0未检测 1检测中 2已完成 */
    private Integer yoloStatus;
    private String yoloDiseaseName;
    private BigDecimal yoloConfidence;
    private String agentSuggestion;
    private Integer agentScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

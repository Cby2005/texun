package com.agriculture.agri.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_ai_question")
public class AgriAiQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long baseId;
    private Long greenhouseId;
    private Long plotId;
    private Long plantingRecordId;
    private Long abnormalImageId;
    private String questionType;
    private String growthStage;
    private String questionContent;
    private String relatedImage;
    private String abnormalReason;
    private String aiAnswer;
    private String modelName;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private String answerStatus;
    private Integer needExpertReview;
    private String userFeedback;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

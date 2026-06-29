package com.agriculture.agri.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_expert_review_task")
public class AgriExpertReviewTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long expertId;
    private String reviewStatus;
    private String expertAnswer;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

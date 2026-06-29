package com.agriculture.agri.quiz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_quiz_attempt")
public class AgriQuizAttempt {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long quizId;
    private Long userId;
    private String answersJson;
    private Integer score;
    private Integer correctCount;
    private Integer wrongCount;
    /** PENDING/SUBMITTED/SCORED */
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}

package com.agriculture.agri.quiz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_lecture_quiz")
public class AgriLectureQuiz {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long lectureId;
    private Long expertId;
    /** DRAFT/GENERATED/REVIEWING/PUBLISHED/ARCHIVED/DISABLED */
    private String status;
    private LocalDateTime answerStartTime;
    private LocalDateTime answerEndTime;
    private Integer allowRetry;
    private Integer maxAttempts;
    private Integer passScore;
    private Integer rewardThreshold;
    private Integer rewardPoints;
    private Integer notifyRegistered;
    private Integer totalQuestions;
    private Integer totalScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

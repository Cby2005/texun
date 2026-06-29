package com.agriculture.agri.quiz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_quiz_points_reward")
public class AgriQuizPointsReward {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long quizId;
    private Long lectureId;
    private Long userId;
    private Long attemptId;
    private Integer score;
    private Integer rewardPoints;
    /** PENDING/GRANTED/REJECTED/CANCELLED */
    private String rewardStatus;
    private LocalDateTime rewardTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

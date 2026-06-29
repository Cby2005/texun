package com.agriculture.agri.quiz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_quiz_question")
public class AgriQuizQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long quizId;
    private Long lectureId;
    private String questionContent;
    private String questionType;
    private String optionsJson;
    private String correctAnswer;
    private String explanation;
    private Integer score;
    private Integer sortOrder;
    /** DRAFT/PUBLISHED */
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

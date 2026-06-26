package com.agriculture.knowledge.lecture.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_lecture")
public class KnowledgeLecture {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String speakerName;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String joinUrl;
    private Integer capacity;
    private Integer registerCount;
    /** DRAFT/OPEN/CLOSED/FINISHED */
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

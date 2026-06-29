package com.agriculture.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior_log")
public class UserBehaviorLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long contentId;
    private String behaviorType;
    private String keyword;
    private Integer duration;
    private LocalDateTime createTime;
}

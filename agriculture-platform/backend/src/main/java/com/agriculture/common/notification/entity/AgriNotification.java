package com.agriculture.common.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_notification")
public class AgriNotification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    /** LECTURE_QUIZ_PUBLISH/QUIZ_REWARD/MALL_ORDER/SYSTEM */
    private String noticeType;
    /** LECTURE/QUIZ/POINTS/MALL_ORDER */
    private String bizType;
    private Long bizId;
    /** 0未读 1已读 */
    private Integer readStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

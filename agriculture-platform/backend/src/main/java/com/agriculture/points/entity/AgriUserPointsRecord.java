package com.agriculture.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_user_points_record")
public class AgriUserPointsRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer pointsChange;
    /** EARN/SPEND/FREEZE/REFUND/ADJUST */
    private String changeType;
    /** LECTURE_QUIZ_REWARD/MALL_EXCHANGE/ADMIN_ADJUST/REFUND */
    private String sourceType;
    private Long sourceId;
    private Integer beforePoints;
    private Integer afterPoints;
    private String remark;
    private LocalDateTime createTime;
}

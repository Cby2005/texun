package com.agriculture.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_user_points_account")
public class AgriUserPointsAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer totalPoints;
    private Integer availablePoints;
    private Integer frozenPoints;
    private Integer usedPoints;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

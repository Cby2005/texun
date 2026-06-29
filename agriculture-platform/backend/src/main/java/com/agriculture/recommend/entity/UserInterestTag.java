package com.agriculture.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_interest_tag")
public class UserInterestTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String tagName;
    private String tagType;
    private Double weight;
    private LocalDateTime updateTime;
}

package com.agriculture.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String interestTags;
    private String cropPreference;
    private String techPreference;
    private String regionPreference;
    private String profileText;
    private String cropTags;
    private String categoryWeights;
    private LocalDateTime lastActiveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

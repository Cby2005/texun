package com.agriculture.recommend.dto;

import lombok.Data;

@Data
public class BehaviorRecordDTO {
    private Long articleId;
    private Long videoId;
    private String targetType;
    private String behaviorType;
    private Integer staySeconds;
}

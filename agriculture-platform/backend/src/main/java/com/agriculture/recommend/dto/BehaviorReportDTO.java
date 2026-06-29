package com.agriculture.recommend.dto;

import lombok.Data;

@Data
public class BehaviorReportDTO {
    private Long userId;
    private Long contentId;
    private String behaviorType;
    private String keyword;
    private Integer duration;
}

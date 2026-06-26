package com.agriculture.recommend.vo;

import lombok.Data;

@Data
public class RecommendLogVO {
    private Long id;
    private Long userId;
    private Long articleId;
    private String articleTitle;
    private Double score;
    private String reason;
    private String strategy;
    private java.time.LocalDateTime createTime;
}

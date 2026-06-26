package com.agriculture.recommend.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendArticleVO {
    private Long articleId;
    private String title;
    private String summary;
    private Double score;
    private String reason;
    private String trustedLevel;
    private String verifiedBy;
    private String safetyTip;
    private String source;
    private String sourceUrl;
    private String cropType;
    private String region;
    private String season;
    private List<String> tags;
    private String strategy;
    private LocalDateTime publishedAt;
}

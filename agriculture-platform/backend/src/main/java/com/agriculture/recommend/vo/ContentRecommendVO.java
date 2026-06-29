package com.agriculture.recommend.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContentRecommendVO {
    private Long id;
    private String title;
    private String contentType;
    private String category;
    private String coverUrl;
    private String summary;
    private String videoUrl;
    private String videoDuration;
    private String author;
    private String source;
    private Integer viewCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private List<String> tags;
    private LocalDateTime publishTime;
    private Double recommendScore;
}

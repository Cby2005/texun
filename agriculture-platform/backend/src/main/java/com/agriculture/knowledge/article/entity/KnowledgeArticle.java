package com.agriculture.knowledge.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_article")
public class KnowledgeArticle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private Long cropId;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String source;
    private String sourceUrl;
    /** 可信等级: official/expert/normal */
    private String trustedLevel;
    /** DRAFT/PENDING_AUDIT/PUBLISHED/REJECTED/ARCHIVED */
    private String status;
    private Long auditorId;
    private String rejectReason;
    /** 文章作物类型标签 */
    private String cropType;
    private String region;
    private String season;
    private String difficulty;
    private String verifiedBy;
    private String safetyTip;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

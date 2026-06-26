package com.agriculture.knowledge.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_video")
public class KnowledgeVideo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String cropType;
    private String tags;
    private String fileName;
    private String videoUrl;
    private String coverUrl;
    private Integer duration;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer recommendWeight;
    private Integer status;
    private Long createUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

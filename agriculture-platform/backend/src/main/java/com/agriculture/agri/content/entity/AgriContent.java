package com.agriculture.agri.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_content")
public class AgriContent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String contentCode;
    private String title;
    private String contentType;
    private String category;
    private String coverUrl;
    private String mediaSource;
    private String summary;
    private String content;
    private String videoUrl;
    private String videoDuration;
    private String author;
    private String source;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer collectCount;
    private String tags;
    private Integer recommendFlag;
    private Integer hotFlag;
    private String publishStatus;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

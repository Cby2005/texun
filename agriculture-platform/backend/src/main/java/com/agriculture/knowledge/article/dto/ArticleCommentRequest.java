package com.agriculture.knowledge.article.dto;

import lombok.Data;

@Data
public class ArticleCommentRequest {
    private Long parentId;
    private String content;
}

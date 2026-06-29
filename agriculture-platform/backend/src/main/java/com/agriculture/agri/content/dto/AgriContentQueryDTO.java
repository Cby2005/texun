package com.agriculture.agri.content.dto;

import lombok.Data;

@Data
public class AgriContentQueryDTO {
    private String keyword;
    private String contentType;
    private String category;
    private Integer recommendFlag;
    private Integer hotFlag;
    private Integer pageNum = 1;
    private Integer pageSize = 12;
}

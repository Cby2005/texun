package com.agriculture.system.menu.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouterTree {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer orderNum;
    private Meta meta;
    private List<RouterTree> children;

    @Data
    public static class Meta {
        private String title;
        private String icon;
        private boolean hidden;
    }
}

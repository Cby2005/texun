package com.agriculture.knowledge.graph.entity;

import lombok.Data;

@Data
public class KgNode {
    private Long id;
    private String nodeType;
    private String name;
    private Long sourceId;
    private String properties;
}

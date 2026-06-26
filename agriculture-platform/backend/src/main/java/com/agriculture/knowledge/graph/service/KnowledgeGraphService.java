package com.agriculture.knowledge.graph.service;

import java.util.List;
import java.util.Map;

public interface KnowledgeGraphService {
    void syncArticleToGraph(Long articleId);
    void syncTagToGraph(Long tagId);
    void syncCategoryToGraph(Long categoryId);
    List<Map<String, Object>> getGraphData(String nodeType, int limit);
    List<Map<String, Object>> searchGraph(String keyword);
    Map<String, Object> stats();
}

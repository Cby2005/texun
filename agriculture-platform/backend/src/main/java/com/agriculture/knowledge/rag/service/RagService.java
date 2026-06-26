package com.agriculture.knowledge.rag.service;

import java.util.List;
import java.util.Map;

public interface RagService {
    void indexArticle(Long articleId);
    void indexAllArticles();
    Map<String, Object> ask(String query, int topK);
    List<Map<String, Object>> search(String query, int topK);
    Map<String, Object> searchWithChunks(String query, int topK);
    List<Map<String, Object>> searchByKeywords(List<String> keywords);
}

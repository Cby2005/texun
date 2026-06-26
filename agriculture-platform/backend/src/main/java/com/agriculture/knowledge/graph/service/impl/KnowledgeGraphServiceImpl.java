package com.agriculture.knowledge.graph.service.impl;

import cn.hutool.json.JSONUtil;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleMapper;
import com.agriculture.knowledge.article_tag.entity.KnowledgeArticleTag;
import com.agriculture.knowledge.article_tag.mapper.KnowledgeArticleTagMapper;
import com.agriculture.knowledge.category.entity.KnowledgeCategory;
import com.agriculture.knowledge.category.mapper.KnowledgeCategoryMapper;
import com.agriculture.knowledge.graph.service.KnowledgeGraphService;
import com.agriculture.knowledge.tag.entity.KnowledgeTag;
import com.agriculture.knowledge.tag.mapper.KnowledgeTagMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private final KnowledgeArticleMapper articleMapper;
    private final KnowledgeTagMapper tagMapper;
    private final KnowledgeArticleTagMapper articleTagMapper;
    private final KnowledgeCategoryMapper categoryMapper;

    @Override
    public void syncArticleToGraph(Long articleId) {
        log.info("图谱同步(内存模式): 文章{}", articleId);
    }

    @Override
    public void syncTagToGraph(Long tagId) {
        log.info("图谱同步(内存模式): 标签{}", tagId);
    }

    @Override
    public void syncCategoryToGraph(Long categoryId) {
        log.info("图谱同步(内存模式): 分类{}", categoryId);
    }

    @Override
    public List<Map<String, Object>> getGraphData(String nodeType, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        limit = Math.min(limit > 0 ? limit : 30, 200);

        // 文章→标签关系 (构建图谱链路)
        List<KnowledgeArticleTag> rels = articleTagMapper.selectList(
            new LambdaQueryWrapper<KnowledgeArticleTag>().last("LIMIT " + limit));
        if (rels.isEmpty()) return result;

        List<Long> articleIds = rels.stream().map(KnowledgeArticleTag::getArticleId).distinct().collect(java.util.stream.Collectors.toList());
        List<Long> tagIds = rels.stream().map(KnowledgeArticleTag::getTagId).distinct().collect(java.util.stream.Collectors.toList());

        var articleMap = articleMapper.selectBatchIds(articleIds).stream()
            .collect(java.util.stream.Collectors.toMap(a -> a.getId(), a -> a));
        var tagMap = tagMapper.selectBatchIds(tagIds).stream()
            .collect(java.util.stream.Collectors.toMap(t -> t.getId(), t -> t));

        for (KnowledgeArticleTag rel : rels) {
            var article = articleMap.get(rel.getArticleId());
            var tag = tagMap.get(rel.getTagId());
            if (article == null || tag == null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("source", Map.of("id", article.getId(), "type", "ARTICLE", "name", article.getTitle()));
            item.put("target", Map.of("id", tag.getId(), "type", "TAG", "name", tag.getName()));
            item.put("relation", "TAGGED");
            result.add(item);
        }

        // 文章→分类关系
        var cats = categoryMapper.selectList(null);
        var catMap = cats.stream().collect(java.util.stream.Collectors.toMap(c -> c.getId(), c -> c));
        for (var a : articleMap.values()) {
            if (a.getCategoryId() == null) continue;
            var cat = catMap.get(a.getCategoryId());
            if (cat == null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("source", Map.of("id", a.getId(), "type", "ARTICLE", "name", a.getTitle()));
            item.put("target", Map.of("id", cat.getId(), "type", "CATEGORY", "name", cat.getName()));
            item.put("relation", "BELONGS_TO");
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> searchGraph(String keyword) {
        List<Map<String, Object>> result = new ArrayList<>();
        var articles = articleMapper.selectList(
            new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticle>()
                .like(com.agriculture.knowledge.article.entity.KnowledgeArticle::getTitle, keyword).last("LIMIT 20"));
        for (var a : articles) {
            result.add(Map.of("node", Map.of("id", a.getId(), "type", "ARTICLE", "name", a.getTitle())));
        }
        return result;
    }

    @Override
    public Map<String, Object> stats() {
        long nodes = articleMapper.selectCount(null) + tagMapper.selectCount(null) + categoryMapper.selectCount(null);
        long rels = articleTagMapper.selectCount(null);
        return Map.of("nodes", nodes, "relations", rels);
    }
}

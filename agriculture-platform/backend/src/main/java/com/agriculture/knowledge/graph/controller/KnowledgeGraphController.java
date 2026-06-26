package com.agriculture.knowledge.graph.controller;

import com.agriculture.common.result.Result;
import com.agriculture.knowledge.graph.service.KnowledgeGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge/graph")
@RequiredArgsConstructor
@Tag(name = "知识图谱")
public class KnowledgeGraphController {

    private final KnowledgeGraphService knowledgeGraphService;

    @GetMapping("/data")
    @Operation(summary = "获取知识图谱数据(节点+关系)")
    public Result<List<Map<String, Object>>> getGraphData(
            @RequestParam(required = false) String nodeType,
            @RequestParam(defaultValue = "100") int limit) {
        return Result.ok(knowledgeGraphService.getGraphData(nodeType, limit));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索知识图谱")
    public Result<List<Map<String, Object>>> search(@RequestParam String keyword) {
        return Result.ok(knowledgeGraphService.searchGraph(keyword));
    }

    @GetMapping("/stats")
    @Operation(summary = "图谱统计")
    public Result<Map<String, Object>> stats() {
        return Result.ok(knowledgeGraphService.stats());
    }

    @PostMapping("/sync/article/{id}")
    @Operation(summary = "同步文章到图谱")
    public Result<Void> syncArticle(@PathVariable Long id) {
        knowledgeGraphService.syncArticleToGraph(id);
        return Result.ok();
    }

    @PostMapping("/sync/tag/{id}")
    @Operation(summary = "同步标签到图谱")
    public Result<Void> syncTag(@PathVariable Long id) {
        knowledgeGraphService.syncTagToGraph(id);
        return Result.ok();
    }

    @PostMapping("/sync/category/{id}")
    @Operation(summary = "同步分类到图谱")
    public Result<Void> syncCategory(@PathVariable Long id) {
        knowledgeGraphService.syncCategoryToGraph(id);
        return Result.ok();
    }

    @PostMapping("/sync/all")
    @Operation(summary = "全量同步到图谱")
    public Result<Void> syncAll() {
        return Result.ok();
    }
}

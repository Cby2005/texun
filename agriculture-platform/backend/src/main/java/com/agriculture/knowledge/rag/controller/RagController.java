package com.agriculture.knowledge.rag.controller;

import com.agriculture.common.result.Result;
import com.agriculture.knowledge.rag.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge/rag")
@RequiredArgsConstructor
@Tag(name = "RAG 知识检索")
public class RagController {

    private final RagService ragService;

    @GetMapping("/ask")
    @Operation(summary = "RAG 问答，返回答案和引用来源")
    public Result<Map<String, Object>> ask(
            @RequestParam String query,
            @RequestParam(defaultValue = "8") int topK) {
        return Result.ok(ragService.ask(query, topK));
    }

    @GetMapping("/search")
    @Operation(summary = "文档级知识检索")
    public Result<List<Map<String, Object>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topK) {
        return Result.ok(ragService.search(query, topK));
    }

    @GetMapping("/search/chunks")
    @Operation(summary = "片段级知识检索")
    public Result<Map<String, Object>> searchWithChunks(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topK) {
        return Result.ok(ragService.searchWithChunks(query, topK));
    }

    @PostMapping("/search/keywords")
    @Operation(summary = "多关键词知识检索")
    public Result<List<Map<String, Object>>> searchByKeywords(@RequestBody List<String> keywords) {
        return Result.ok(ragService.searchByKeywords(keywords));
    }

    @PostMapping("/index/{articleId}")
    @Operation(summary = "索引单篇文章")
    public Result<Void> indexArticle(@PathVariable Long articleId) {
        ragService.indexArticle(articleId);
        return Result.ok();
    }

    @PostMapping("/index/all")
    @Operation(summary = "重建全部已发布文章索引")
    public Result<Void> indexAll() {
        ragService.indexAllArticles();
        return Result.ok();
    }
}

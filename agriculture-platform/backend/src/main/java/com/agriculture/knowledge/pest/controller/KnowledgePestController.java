package com.agriculture.knowledge.pest.controller;

import com.agriculture.common.result.Result;
import com.agriculture.knowledge.pest.entity.KnowledgePest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge/pests")
@RequiredArgsConstructor
@Tag(name = "病虫害知识")
public class KnowledgePestController {

    private final IService<KnowledgePest> service;

    @GetMapping
    public Result<Object> list(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String category) {
        LambdaQueryWrapper<KnowledgePest> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) qw.like(KnowledgePest::getName, keyword);
        if (category != null && !category.isEmpty()) qw.eq(KnowledgePest::getCategory, category);
        qw.orderByAsc(KnowledgePest::getName);
        return Result.ok(service.list(qw));
    }

    @GetMapping("/{id}")
    public Result<KnowledgePest> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }
}

package com.agriculture.knowledge.category.controller;

import com.agriculture.common.result.Result;
import com.agriculture.knowledge.category.entity.KnowledgeCategory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge/categories")
@RequiredArgsConstructor
@Tag(name = "技术分类")
public class KnowledgeCategoryController {

    private final IService<KnowledgeCategory> service;

    @GetMapping
    public Result<List<KnowledgeCategory>> list() {
        return Result.ok(service.list(new LambdaQueryWrapper<KnowledgeCategory>().orderByAsc(KnowledgeCategory::getSortOrder)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> add(@RequestBody KnowledgeCategory c) { service.save(c); return Result.ok(); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeCategory c) { c.setId(id); service.updateById(c); return Result.ok(); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }
}

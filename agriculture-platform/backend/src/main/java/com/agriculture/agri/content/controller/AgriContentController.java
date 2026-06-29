package com.agriculture.agri.content.controller;

import com.agriculture.agri.content.dto.AgriContentQueryDTO;
import com.agriculture.agri.content.entity.AgriContent;
import com.agriculture.agri.content.service.AgriContentService;
import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agri/content")
@RequiredArgsConstructor
@Tag(name = "农技内容中心")
public class AgriContentController {

    private final AgriContentService service;

    @GetMapping("/list")
    public Result<PageResult<AgriContent>> list(AgriContentQueryDTO query) {
        if (query.getPageNum() == null) query.setPageNum(1);
        if (query.getPageSize() == null) query.setPageSize(12);
        return Result.ok(service.list(query));
    }

    @GetMapping("/recommend")
    public Result<PageResult<AgriContent>> recommend(AgriContentQueryDTO query) {
        if (query.getPageNum() == null) query.setPageNum(1);
        if (query.getPageSize() == null) query.setPageSize(8);
        return Result.ok(service.recommend(query));
    }

    @GetMapping("/hot")
    public Result<PageResult<AgriContent>> hot(AgriContentQueryDTO query) {
        if (query.getPageNum() == null) query.setPageNum(1);
        if (query.getPageSize() == null) query.setPageSize(8);
        return Result.ok(service.hot(query));
    }

    @GetMapping("/{id}")
    public Result<AgriContent> getById(@PathVariable Long id) {
        return Result.ok(service.getById(id));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody AgriContent content) {
        service.add(content);
        return Result.ok("发布成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody AgriContent content) {
        service.update(content);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok("删除成功");
    }

    @PostMapping("/publish")
    public Result<String> publish(@RequestParam Long id) {
        service.publish(id);
        return Result.ok("已发布");
    }

    @PostMapping("/offline/{id}")
    public Result<String> offline(@PathVariable Long id) {
        service.offline(id);
        return Result.ok("已下架");
    }
}

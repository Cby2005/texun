package com.agriculture.knowledge.lecture.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.common.security.UserContext;
import com.agriculture.knowledge.lecture.entity.KnowledgeLecture;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge/lectures")
@RequiredArgsConstructor
@Tag(name = "农技讲座")
public class KnowledgeLectureController {

    private final IService<KnowledgeLecture> service;

    @GetMapping
    public Result<PageResult<KnowledgeLecture>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<KnowledgeLecture> page = service.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<KnowledgeLecture>().orderByDesc(KnowledgeLecture::getCreateTime));
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeLecture> getById(@PathVariable Long id) { return Result.ok(service.getById(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> add(@RequestBody KnowledgeLecture l) {
        l.setUserId(UserContext.getCurrentUserId());
        l.setRegisterCount(0);
        l.setStatus("DRAFT");
        service.save(l);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeLecture l) { l.setId(id); service.updateById(l); return Result.ok(); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) { service.removeById(id); return Result.ok(); }

    @PostMapping("/{id}/register")
    public Result<Void> register(@PathVariable Long id) {
        KnowledgeLecture l = service.getById(id);
        if (l == null) return Result.fail(400, "讲座不存在");
        if (!"OPEN".equals(l.getStatus())) return Result.fail(400, "当前不在报名阶段");
        if (l.getRegisterCount() >= l.getCapacity()) return Result.fail(400, "名额已满");
        l.setRegisterCount(l.getRegisterCount() + 1);
        service.updateById(l);
        return Result.ok();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelRegister(@PathVariable Long id) {
        KnowledgeLecture l = service.getById(id);
        if (l == null) return Result.fail(400, "讲座不存在");
        if (l.getRegisterCount() > 0) {
            l.setRegisterCount(l.getRegisterCount() - 1);
            service.updateById(l);
        }
        return Result.ok();
    }
}

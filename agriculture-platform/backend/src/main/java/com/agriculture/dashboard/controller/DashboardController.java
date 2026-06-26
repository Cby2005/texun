package com.agriculture.dashboard.controller;

import com.agriculture.common.result.Result;
import com.agriculture.farm.enterprise.mapper.FarmEnterpriseMapper;
import com.agriculture.farm.land.entity.FarmLand;
import com.agriculture.farm.env.entity.FarmEnvData;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "首页驾驶舱")
public class DashboardController {

    private final IService<FarmLand> landService;
    private final IService<FarmEnvData> envService;
    private final IService<com.agriculture.knowledge.article.entity.KnowledgeArticle> articleService;
    private final IService<com.agriculture.trace.product.entity.TraceProduct> productService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("landCount", landService.count());
        data.put("articleCount", articleService.count(new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticle>().eq(com.agriculture.knowledge.article.entity.KnowledgeArticle::getStatus, "PUBLISHED")));
        data.put("productCount", productService.count());
        data.put("envDataCount", envService.count());
        return Result.ok(data);
    }

    @GetMapping("/env/summary")
    public Result<Object> envSummary() {
        return Result.ok(envService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FarmEnvData>()
                .orderByDesc(FarmEnvData::getCreateTime)
                .last("LIMIT 12")));
    }
}

package com.agriculture.dashboard.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.farm.enterprise.mapper.FarmEnterpriseMapper;
import com.agriculture.farm.land.entity.FarmLand;
import com.agriculture.farm.env.entity.FarmEnvData;
import com.agriculture.knowledge.question.entity.KnowledgeQuestion;
import com.agriculture.trace.batch.entity.TraceBatch;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
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
    private final IService<TraceBatch> batchService;
    private final IService<KnowledgeQuestion> questionService;

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

    @GetMapping("/role")
    public Result<Map<String, Object>> roleDashboard() {
        List<String> roles = UserContext.getCurrentUser() == null ? List.of() : UserContext.getCurrentUser().getRoles();
        String role = roles.isEmpty() ? "CONSUMER" : roles.get(0);
        Map<String, Object> data = new HashMap<>();
        data.put("role", role);
        data.put("cards", roleCards(role));
        data.put("recentEnv", envService.list(new LambdaQueryWrapper<FarmEnvData>()
                .orderByDesc(FarmEnvData::getCreateTime)
                .last("LIMIT 6")));
        return Result.ok(data);
    }

    private List<Map<String, Object>> roleCards(String role) {
        List<Map<String, Object>> cards = new ArrayList<>();
        if ("EXPERT".equals(role)) {
            cards.add(card("待审核诊断", questionService.count(new LambdaQueryWrapper<KnowledgeQuestion>()
                    .in(KnowledgeQuestion::getStatus, List.of("PENDING", "0"))), "question"));
            cards.add(card("待回答问题", questionService.count(new LambdaQueryWrapper<KnowledgeQuestion>()
                    .eq(KnowledgeQuestion::getStatus, "0")), "answer"));
            cards.add(card("已发布知识", articleService.count(new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticle>()
                    .eq(com.agriculture.knowledge.article.entity.KnowledgeArticle::getStatus, "PUBLISHED")), "article"));
            return cards;
        }
        if ("TRACE_ADMIN".equals(role)) {
            long batchCount = batchService.count();
            long activeBatchCount = batchService.count(new LambdaQueryWrapper<TraceBatch>().eq(TraceBatch::getStatus, 0));
            cards.add(card("生产批次", batchCount, "batch"));
            cards.add(card("生产中批次", activeBatchCount, "active"));
            cards.add(card("溯源产品", productService.count(), "product"));
            return cards;
        }
        if ("CONSUMER".equals(role)) {
            cards.add(card("公开溯源入口", 1, "public-trace"));
            cards.add(card("可查询产品", productService.count(), "product"));
            return cards;
        }
        cards.add(card("待处理问题", questionService.count(new LambdaQueryWrapper<KnowledgeQuestion>()
                .in(KnowledgeQuestion::getStatus, List.of("PENDING", "0"))), "question"));
        cards.add(card("环境异常", envService.count(new LambdaQueryWrapper<FarmEnvData>()
                .gt(FarmEnvData::getDataValue, 80)), "env"));
        cards.add(card("推荐知识", articleService.count(new LambdaQueryWrapper<com.agriculture.knowledge.article.entity.KnowledgeArticle>()
                .eq(com.agriculture.knowledge.article.entity.KnowledgeArticle::getStatus, "PUBLISHED")), "knowledge"));
        return cards;
    }

    private Map<String, Object> card(String title, Object value, String type) {
        Map<String, Object> card = new HashMap<>();
        card.put("title", title);
        card.put("value", value);
        card.put("type", type);
        return card;
    }
}

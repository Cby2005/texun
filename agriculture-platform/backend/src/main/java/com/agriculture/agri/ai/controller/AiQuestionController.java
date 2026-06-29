package com.agriculture.agri.ai.controller;

import com.agriculture.agri.ai.entity.AgriAiQuestion;
import com.agriculture.agri.ai.entity.AgriExpertReviewTask;
import com.agriculture.agri.ai.service.AiQuestionService;
import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agri/ai/questions")
@RequiredArgsConstructor
@Tag(name = "AI 智能问答")
public class AiQuestionController {

    private final AiQuestionService aiQuestionService;

    @PostMapping("/ask")
    @Operation(summary = "提交智能提问并获取 AI 回答")
    public Result<Map<String, Object>> ask(@RequestBody Map<String, Object> params) {
        return Result.ok(aiQuestionService.ask(params));
    }

    @GetMapping("/my")
    @Operation(summary = "查询我的智能问答记录")
    public Result<PageResult<AgriAiQuestion>> listMy(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(aiQuestionService.listMyQuestions(page, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询问答详情")
    public Result<AgriAiQuestion> detail(@PathVariable Long id) {
        return Result.ok(aiQuestionService.getQuestionDetail(id));
    }

    @PostMapping("/{id}/feedback")
    @Operation(summary = "对 AI 回答进行反馈")
    public Result<String> feedback(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String feedback = params.getOrDefault("feedback", "none");
        String reason = params.getOrDefault("reason", "");
        aiQuestionService.feedback(id, feedback, reason);
        return Result.ok("反馈成功");
    }

    @PostMapping("/{id}/expert-review")
    @Operation(summary = "转专家复核")
    public Result<AgriExpertReviewTask> expertReview(@PathVariable Long id) {
        return Result.ok(aiQuestionService.requestExpertReview(id));
    }
}

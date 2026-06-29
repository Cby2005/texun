package com.agriculture.agri.quiz.controller;

import com.agriculture.agri.quiz.entity.*;
import com.agriculture.agri.quiz.service.AgriQuizService;
import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agri/quiz")
@RequiredArgsConstructor
@Tag(name = "讲座测验管理")
public class AgriQuizController {

    private final AgriQuizService quizService;

    // ==================== 专家：测验管理 ====================

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<List<AgriLectureQuiz>> list(@RequestParam(required = false) Long lectureId) {
        return Result.ok(quizService.listQuizzes(lectureId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<AgriLectureQuiz> create(@RequestBody AgriLectureQuiz quiz) {
        return Result.ok(quizService.createQuiz(quiz));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> update(@PathVariable Long id, @RequestBody AgriLectureQuiz quiz) {
        quizService.updateQuiz(id, quiz); return Result.ok();
    }

    // ==================== 专家：题目管理 ====================

    @PostMapping("/{id}/generate")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<List<AgriQuizQuestion>> generateQuestions(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.ok(quizService.generateQuestions(id, body.get("prompt")));
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<List<AgriQuizQuestion>> listQuestions(@PathVariable Long id) {
        return Result.ok(quizService.listQuestions(id));
    }

    @PutMapping("/questions/{qid}")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> updateQuestion(@PathVariable Long qid, @RequestBody AgriQuizQuestion q) {
        quizService.updateQuestion(qid, q); return Result.ok();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<Void> publish(@PathVariable Long id) {
        quizService.publishQuiz(id);
        quizService.notifyUsers(id);
        return Result.ok();
    }

    // ==================== 农户：答题 ====================

    @GetMapping("/published")
    public Result<List<AgriLectureQuiz>> listPublished() {
        return Result.ok(quizService.listPublishedQuizzes());
    }

    @GetMapping("/{id}/detail")
    public Result<AgriLectureQuiz> getDetail(@PathVariable Long id) {
        AgriLectureQuiz q = quizService.getQuiz(id);
        if (q == null || !"PUBLISHED".equals(q.getStatus())) return Result.fail(403, "测验不可用");
        return Result.ok(q);
    }

    @GetMapping("/{id}/farmer-questions")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<List<AgriQuizQuestion>> farmerQuestions(@PathVariable Long id) {
        AgriLectureQuiz q = quizService.getQuiz(id);
        if (q == null || !"PUBLISHED".equals(q.getStatus())) return Result.fail(403, "测验未发布");
        return Result.ok(quizService.listQuestions(id));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<AgriQuizAttempt> submit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        return Result.ok(quizService.submitAttempt(id, answers));
    }

    @GetMapping("/{id}/my-attempts")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<List<AgriQuizAttempt>> myAttempts(@PathVariable Long id) {
        return Result.ok(quizService.listMyAttempts(id));
    }

    // ==================== 专家：查看答题结果 ====================
    @GetMapping("/{id}/attempts")
    @PreAuthorize("hasAnyRole('ADMIN','EXPERT')")
    public Result<List<AgriQuizAttempt>> attempts(@PathVariable Long id) {
        return Result.ok(quizService.listAttemptsByExpert(id));
    }
}

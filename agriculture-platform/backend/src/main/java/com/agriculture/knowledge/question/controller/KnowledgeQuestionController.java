package com.agriculture.knowledge.question.controller;

import com.agriculture.common.result.PageResult;
import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.knowledge.question.dto.AnswerRequest;
import com.agriculture.knowledge.question.entity.KnowledgeAnswer;
import com.agriculture.knowledge.question.entity.KnowledgeQuestion;
import com.agriculture.knowledge.question.mapper.KnowledgeAnswerMapper;
import com.agriculture.system.user.entity.SysUser;
import com.agriculture.system.user.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge/questions")
@RequiredArgsConstructor
@Tag(name = "问答社区")
public class KnowledgeQuestionController {

    private final IService<KnowledgeQuestion> service;
    private final KnowledgeAnswerMapper answerMapper;
    private final SysUserMapper userMapper;

    @GetMapping
    public Result<PageResult<KnowledgeQuestion>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long cropId) {
        LambdaQueryWrapper<KnowledgeQuestion> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) qw.like(KnowledgeQuestion::getTitle, keyword);
        if (cropId != null) qw.eq(KnowledgeQuestion::getCropId, cropId);
        qw.orderByDesc(KnowledgeQuestion::getCreateTime);
        Page<KnowledgeQuestion> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeQuestion> getById(@PathVariable Long id) {
        KnowledgeQuestion question = service.getById(id);
        if (question != null) {
            question.setViewCount(question.getViewCount() == null ? 1 : question.getViewCount() + 1);
            service.updateById(question);
        }
        return Result.ok(question);
    }

    @PostMapping
    public Result<Void> add(@RequestBody KnowledgeQuestion question) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        question.setUserId(userId);
        question.setStatus("0");
        question.setAnswerCount(0);
        question.setViewCount(0);
        service.save(question);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeQuestion question) {
        question.setId(id);
        service.updateById(question);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @GetMapping("/{id}/answers")
    public Result<List<Map<String, Object>>> answers(@PathVariable Long id) {
        List<KnowledgeAnswer> answers = answerMapper.selectList(new LambdaQueryWrapper<KnowledgeAnswer>()
                .eq(KnowledgeAnswer::getQuestionId, id)
                .eq(KnowledgeAnswer::getDeleted, 0)
                .orderByDesc(KnowledgeAnswer::getIsAccepted)
                .orderByAsc(KnowledgeAnswer::getCreateTime));

        List<Map<String, Object>> result = new ArrayList<>();
        for (KnowledgeAnswer answer : answers) {
            SysUser user = userMapper.selectById(answer.getUserId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", answer.getId());
            item.put("questionId", answer.getQuestionId());
            item.put("userId", answer.getUserId());
            item.put("authorName", user == null ? "用户" + answer.getUserId() : resolveNickname(user));
            item.put("content", answer.getContent());
            item.put("isAccepted", answer.getIsAccepted());
            item.put("likeCount", answer.getLikeCount());
            item.put("createTime", answer.getCreateTime());
            item.put("updateTime", answer.getUpdateTime());
            result.add(item);
        }
        return Result.ok(result);
    }

    @PostMapping("/{id}/answers")
    public Result<Void> createAnswer(@PathVariable Long id, @RequestBody AnswerRequest request) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        KnowledgeQuestion question = service.getById(id);
        if (question == null) return Result.fail("问题不存在");
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.fail("回答内容不能为空");
        }
        String content = request.getContent().trim();
        if (content.length() > 5000) {
            return Result.fail("回答内容不能超过 5000 字");
        }

        KnowledgeAnswer answer = new KnowledgeAnswer();
        answer.setQuestionId(id);
        answer.setUserId(userId);
        answer.setContent(content);
        answer.setIsAccepted(0);
        answer.setLikeCount(0);
        answer.setCreateTime(LocalDateTime.now());
        answer.setUpdateTime(LocalDateTime.now());
        answer.setDeleted(0);
        answerMapper.insert(answer);

        question.setAnswerCount(question.getAnswerCount() == null ? 1 : question.getAnswerCount() + 1);
        if (!isResolved(question)) {
            question.setStatus("0");
        }
        service.updateById(question);
        return Result.ok();
    }

    @PostMapping("/{id}/accept/{answerId}")
    public Result<Void> acceptAnswer(@PathVariable Long id, @PathVariable Long answerId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        KnowledgeQuestion question = service.getById(id);
        if (question == null) return Result.fail("问题不存在");

        KnowledgeAnswer answer = answerMapper.selectById(answerId);
        if (answer == null || !id.equals(answer.getQuestionId())) {
            return Result.fail("回答不存在");
        }

        KnowledgeAnswer reset = new KnowledgeAnswer();
        reset.setIsAccepted(0);
        answerMapper.update(reset, new LambdaQueryWrapper<KnowledgeAnswer>()
                .eq(KnowledgeAnswer::getQuestionId, id)
                .eq(KnowledgeAnswer::getDeleted, 0));

        KnowledgeAnswer accepted = new KnowledgeAnswer();
        accepted.setId(answerId);
        accepted.setIsAccepted(1);
        accepted.setUpdateTime(LocalDateTime.now());
        answerMapper.updateById(accepted);

        question.setBestAnswerId(answerId);
        question.setStatus("1");
        service.updateById(question);
        return Result.ok();
    }

    private boolean isResolved(KnowledgeQuestion question) {
        return "1".equals(question.getStatus()) || "RESOLVED".equalsIgnoreCase(question.getStatus());
    }

    private String resolveNickname(SysUser user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername() == null ? "用户" + user.getId() : user.getUsername();
    }
}

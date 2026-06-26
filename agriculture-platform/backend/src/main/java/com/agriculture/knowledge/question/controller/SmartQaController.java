package com.agriculture.knowledge.question.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.security.UserContext;
import com.agriculture.knowledge.question.entity.KnowledgeQuestion;
import com.agriculture.yolo.dto.YoloDetectResponse;
import com.agriculture.yolo.service.YoloService;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@Slf4j
public class SmartQaController {

    private final IService<KnowledgeQuestion> questionService;
    private final RestTemplate restTemplate;
    private final YoloService yoloService;

    @Value("${app.agent.diagnosis-url:http://localhost:8002/diagnose}")
    private String agentServiceUrl;

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> submit(
            @RequestParam("title") String title,
            @RequestParam("cropType") String cropType,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("description") String description,
            @RequestParam(value = "landId", required = false) Long landId,
            @RequestParam(value = "cropId", required = false) Long cropId,
            @RequestParam(value = "batchNo", required = false) String batchNo,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "growthStage", required = false) String growthStage,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }

        KnowledgeQuestion question = new KnowledgeQuestion();
        question.setUserId(userId);
        question.setTitle(title);
        question.setCropType(cropType);
        question.setCropId(cropId);
        question.setLandId(landId);
        question.setBatchNo(batchNo);
        question.setGrowthStage(growthStage);
        question.setRegion(region);
        question.setContent(description);
        question.setCategoryId(categoryId.longValue());
        question.setStatus("PENDING");
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setYoloStatus(0);
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        question.setDeleted(0);
        questionService.save(question);

        Long questionId = question.getId();
        Long yoloResultId = null;
        String yoloDiseaseName = null;
        BigDecimal yoloConfidence = null;
        String agentSuggestion = null;
        Integer agentScore = null;
        Integer finalStatus = 4;

        if (image != null && !image.isEmpty()) {
            try {
                YoloDetectResponse yoloResult = yoloService.detectDisease(image, questionId, null);
                yoloResultId = yoloResult.getResultId();
                if (yoloResult.isSuccess() && yoloResult.hasDetection()) {
                    yoloDiseaseName = yoloResult.getTopDiseaseName();
                    yoloConfidence = yoloResult.getTopConfidence() == null
                            ? BigDecimal.ZERO
                            : BigDecimal.valueOf(yoloResult.getTopConfidence());
                    question.setYoloDiseaseName(yoloDiseaseName);
                    question.setYoloConfidence(yoloConfidence);
                    question.setYoloStatus(1);
                    log.info("YOLO识别成功 questionId={}, disease={}, confidence={}", questionId, yoloDiseaseName, yoloConfidence);
                } else {
                    question.setYoloStatus(2);
                    log.info("YOLO未识别到有效结果 questionId={}, message={}", questionId, yoloResult.getMessage());
                }
            } catch (Exception e) {
                log.error("YOLO服务调用失败 questionId={}", questionId, e);
                question.setYoloStatus(2);
            }

            if (yoloDiseaseName != null
                    && yoloConfidence != null
                    && yoloConfidence.compareTo(new BigDecimal("0.5")) >= 0) {
                try {
                    Map<String, Object> agentResult = callAgentService(yoloDiseaseName, cropType, description);
                    if (agentResult != null && Boolean.TRUE.equals(agentResult.get("success"))) {
                        agentSuggestion = (String) agentResult.get("suggestion");
                        Object scoreObj = agentResult.get("score");
                        agentScore = scoreObj != null ? ((Number) scoreObj).intValue() : null;
                        question.setAgentSuggestion(agentSuggestion);
                        question.setAgentScore(agentScore);

                        if (agentScore != null && agentScore >= 90) {
                            finalStatus = 3;
                            question.setStatus("RESOLVED");
                        } else {
                            finalStatus = 4;
                            question.setStatus("PENDING");
                        }
                        log.info("Agent诊断完成 questionId={}, score={}", questionId, agentScore);
                    }
                } catch (Exception e) {
                    log.error("Agent服务调用失败 questionId={}", questionId, e);
                }
            }
        } else {
            question.setYoloStatus(0);
            finalStatus = 0;
        }

        question.setUpdateTime(LocalDateTime.now());
        questionService.updateById(question);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("questionId", questionId);
        result.put("status", finalStatus);
        result.put("statusText", statusText(finalStatus));
        result.put("yoloResultId", yoloResultId);
        result.put("yoloDiseaseName", yoloDiseaseName);
        result.put("yoloConfidence", yoloConfidence);
        result.put("agentSuggestion", agentSuggestion);
        result.put("agentScore", agentScore);
        return Result.ok(result);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callAgentService(String diseaseName, String cropType, String description) {
        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("diseaseName", diseaseName);
            request.put("cropType", cropType);
            request.put("description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(agentServiceUrl, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Agent调用异常", e);
            return null;
        }
    }

    private String statusText(Integer finalStatus) {
        if (finalStatus == 3) {
            return "智能识别并自动回复成功";
        }
        if (finalStatus == 4) {
            return "已转交农业专家审核";
        }
        if (finalStatus == 0) {
            return "问题已提交";
        }
        return "等待处理";
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        var page = questionService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize));
        return Result.ok(Map.of("records", page.getRecords(), "total", page.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeQuestion> getDetail(@PathVariable Long id) {
        return Result.ok(questionService.getById(id));
    }
}

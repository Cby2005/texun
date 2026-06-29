package com.agriculture.agri.ai.service.impl;

import com.agriculture.agri.ai.client.DeepSeekClient;
import com.agriculture.agri.ai.config.DeepSeekConfig;
import com.agriculture.agri.ai.entity.AgriAiQuestion;
import com.agriculture.agri.ai.entity.AgriExpertReviewTask;
import com.agriculture.agri.ai.mapper.AgriAiQuestionMapper;
import com.agriculture.agri.ai.mapper.AgriExpertReviewTaskMapper;
import com.agriculture.agri.ai.service.AiQuestionService;
import com.agriculture.common.exception.BizException;
import com.agriculture.common.result.PageResult;
import com.agriculture.common.security.UserContext;
import com.agriculture.recommend.entity.UserBehavior;
import com.agriculture.recommend.mapper.UserBehaviorMapper;
import com.agriculture.recommend.mapper.UserInterestTagMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiQuestionServiceImpl implements AiQuestionService {

    private final AgriAiQuestionMapper questionMapper;
    private final AgriExpertReviewTaskMapper expertReviewTaskMapper;
    private final DeepSeekClient deepSeekClient;
    private final DeepSeekConfig deepSeekConfig;
    private final UserBehaviorMapper userBehaviorMapper;
    private final UserInterestTagMapper userInterestTagMapper;

    // ─── 问题类型 → 标签映射 ───
    private static final Map<String, String> TYPE_TAG_MAP = Map.of(
        "草莓种植管理", "草莓种植管理",
        "病虫害防治", "病虫害防治",
        "温室环境调控", "温室环境调控",
        "水肥一体化", "水肥一体化",
        "采摘与销售", "采摘与销售",
        "设备巡检", "设备巡检"
    );

    // ─── 生长阶段 → 标签映射 ───
    private static final Map<String, String> STAGE_TAG_MAP = Map.of(
        "育苗期", "育苗期管理",
        "生长期", "生长期管理",
        "开花期", "开花期管理",
        "结果期", "结果期管理",
        "成熟期", "成熟期管理",
        "采摘期", "采摘期管理",
        "温室日常管理", "温室日常管理"
    );

    // ─── 系统提示词 ───
    private static final String SYSTEM_PROMPT = """
        你是温室草莓智慧农业平台中的农技问答助手，负责回答草莓种植、温室管理、病虫害防治、水肥一体化、采摘销售和设备巡检相关问题。

        回答要求：
        1. 专业、简洁、可执行；
        2. 不要编造不存在的数据；
        3. 如果问题信息不足，要提示用户补充温度、湿度、光照、病害图片、生长阶段等信息；
        4. 涉及农药、肥料和病害处理时，应给出安全提醒，并建议结合当地农技规范或专业人员确认。

        请按以下固定结构输出回答：
        【问题判断】
        （对问题所属类别和严重程度的判断）
        【可能原因】
        （列出1-3条可能性最高的原因）
        【处理建议】
        （给出具体可执行的处理步骤）
        【预防措施】
        （给出长期预防建议）
        【是否需要人工复核】
        是 / 否
        """;

    @Override
    @Transactional
    public Map<String, Object> ask(Map<String, Object> params) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(401, "请先登录后再使用智能提问");
        }

        String questionContent = (String) params.get("questionContent");
        if (questionContent == null || questionContent.trim().isEmpty()) {
            throw new BizException(400, "问题内容不能为空");
        }
        questionContent = questionContent.trim();
        if (questionContent.length() > 2000) {
            throw new BizException(400, "问题内容不能超过2000字");
        }

        // 敏感词过滤
        questionContent = filterSensitiveWords(questionContent);

        String questionType = (String) params.getOrDefault("questionType", "其他问题");
        String growthStage = (String) params.getOrDefault("growthStage", "");
        Long greenhouseId = params.get("greenhouseId") != null ? Long.valueOf(params.get("greenhouseId").toString()) : null;
        Long baseId = params.get("baseId") != null ? Long.valueOf(params.get("baseId").toString()) : null;
        Long plotId = params.get("plotId") != null ? Long.valueOf(params.get("plotId").toString()) : null;
        Long plantingRecordId = params.get("plantingRecordId") != null ? Long.valueOf(params.get("plantingRecordId").toString()) : null;
        Long abnormalImageId = params.get("abnormalImageId") != null ? Long.valueOf(params.get("abnormalImageId").toString()) : null;
        String relatedImage = (String) params.getOrDefault("imageUrl", "");
        String abnormalReason = (String) params.getOrDefault("abnormalReason", "");

        // 1. 保存问题记录（pending 状态）
        AgriAiQuestion question = new AgriAiQuestion();
        question.setUserId(userId);
        question.setQuestionContent(questionContent);
        question.setQuestionType(questionType);
        question.setGrowthStage(growthStage);
        question.setGreenhouseId(greenhouseId);
        question.setBaseId(baseId);
        question.setPlotId(plotId);
        question.setPlantingRecordId(plantingRecordId);
        question.setAbnormalImageId(abnormalImageId);
        question.setRelatedImage(relatedImage);
        question.setAbnormalReason(abnormalReason);
        question.setAnswerStatus("pending");
        question.setUserFeedback("none");
        question.setNeedExpertReview(0);
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.insert(question);

        // 2. 记录用户行为：ai_question
        recordBehavior(userId, question.getId(), "question", "ai_question");

        // 3. 更新用户画像（独立于 DeepSeek 调用结果）
        updateUserProfile(userId, questionType, growthStage);

        // 4. 构建上下文消息
        String userMessage = buildUserMessage(questionContent, questionType, growthStage, abnormalReason, params);
        List<Map<String, String>> messages = deepSeekClient.buildMessages(SYSTEM_PROMPT, userMessage);

        // 4. 调用 DeepSeek API
        try {
            Map<String, Object> aiResult = deepSeekClient.chat(messages);
            String aiAnswer = (String) aiResult.get("content");
            int promptTokens = (int) aiResult.getOrDefault("promptTokens", 0);
            int completionTokens = (int) aiResult.getOrDefault("completionTokens", 0);
            int totalTokens = (int) aiResult.getOrDefault("totalTokens", 0);

            // 是否需要人工复核（判断 AI 回答中是否包含"是"或问题类型为病虫害）
            int needExpertReview = (questionType.contains("病虫害") || questionType.contains("用药")) ? 1 : 0;
            if (aiAnswer.contains("【是否需要人工复核】") && aiAnswer.contains("是")) {
                needExpertReview = 1;
            }

            // 5. 更新问题记录
            question.setAiAnswer(aiAnswer);
            question.setModelName(deepSeekConfig.getModel());
            question.setPromptTokens(promptTokens);
            question.setCompletionTokens(completionTokens);
            question.setTotalTokens(totalTokens);
            question.setAnswerStatus("answered");
            question.setNeedExpertReview(needExpertReview);
            question.setUpdateTime(LocalDateTime.now());
            questionMapper.updateById(question);

            // 6. 如果 AI 判断需要专家复核，自动生成复核任务
            if (needExpertReview == 1) {
                AgriExpertReviewTask task = new AgriExpertReviewTask();
                task.setQuestionId(question.getId());
                task.setReviewStatus("pending");
                task.setCreateTime(LocalDateTime.now());
                task.setUpdateTime(LocalDateTime.now());
                expertReviewTaskMapper.insert(task);
                recordBehavior(userId, question.getId(), "question", "expert_review_request");
            }

            // 7. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("questionId", question.getId());
            result.put("aiAnswer", aiAnswer);
            result.put("answerStatus", question.getAnswerStatus());
            result.put("needExpertReview", needExpertReview);
            result.put("createTime", question.getCreateTime());
            return result;

        } catch (BizException e) {
            // 调用失败，记录失败状态 + 生成专家复核任务
            question.setAnswerStatus("failed");
            question.setNeedExpertReview(1);
            question.setUpdateTime(LocalDateTime.now());
            questionMapper.updateById(question);

            AgriExpertReviewTask task = new AgriExpertReviewTask();
            task.setQuestionId(question.getId());
            task.setReviewStatus("pending");
            task.setCreateTime(LocalDateTime.now());
            task.setUpdateTime(LocalDateTime.now());
            expertReviewTaskMapper.insert(task);

            Map<String, Object> result = new HashMap<>();
            result.put("questionId", question.getId());
            result.put("aiAnswer", e.getMessage());
            result.put("answerStatus", "failed");
            result.put("needExpertReview", 1);
            result.put("createTime", question.getCreateTime());
            return result;
        }
    }

    @Override
    public PageResult<AgriAiQuestion> listMyQuestions(int page, int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(401, "请先登录");
        }
        LambdaQueryWrapper<AgriAiQuestion> qw = new LambdaQueryWrapper<>();
        qw.eq(AgriAiQuestion::getUserId, userId)
          .orderByDesc(AgriAiQuestion::getCreateTime);
        Page<AgriAiQuestion> p = new Page<>(page, pageSize);
        IPage<AgriAiQuestion> result = questionMapper.selectPage(p, qw);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public AgriAiQuestion getQuestionDetail(Long id) {
        Long userId = UserContext.getCurrentUserId();
        AgriAiQuestion q = questionMapper.selectById(id);
        if (q == null) {
            throw new BizException(404, "问答记录不存在");
        }
        // 普通用户只能看自己的记录
        if (!Objects.equals(q.getUserId(), userId)) {
            throw new BizException(403, "无权查看该问答记录");
        }
        // 记录行为：ai_answer_view
        recordBehavior(userId, id, "question", "ai_answer_view");
        return q;
    }

    @Override
    @Transactional
    public void feedback(Long id, String feedback, String reason) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(401, "请先登录");
        }
        AgriAiQuestion q = questionMapper.selectById(id);
        if (q == null) {
            throw new BizException(404, "问答记录不存在");
        }
        if (!Objects.equals(q.getUserId(), userId)) {
            throw new BizException(403, "无权操作");
        }
        q.setUserFeedback(feedback);
        q.setUpdateTime(LocalDateTime.now());
        questionMapper.updateById(q);

        // 记录行为
        String behaviorType = "unhelpful".equals(feedback) ? "ai_feedback_unhelpful" : "ai_feedback_helpful";
        recordBehavior(userId, id, "question", behaviorType);
    }

    @Override
    @Transactional
    public AgriExpertReviewTask requestExpertReview(Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(401, "请先登录");
        }
        AgriAiQuestion q = questionMapper.selectById(id);
        if (q == null) {
            throw new BizException(404, "问答记录不存在");
        }

        // 更新问题状态
        q.setAnswerStatus("expert_review");
        q.setNeedExpertReview(1);
        q.setUpdateTime(LocalDateTime.now());
        questionMapper.updateById(q);

        // 创建专家复核任务
        AgriExpertReviewTask task = new AgriExpertReviewTask();
        task.setQuestionId(id);
        task.setReviewStatus("pending");
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        expertReviewTaskMapper.insert(task);

        // 记录行为
        recordBehavior(userId, id, "question", "expert_review_request");

        log.info("问题 {} 已转专家复核", id);
        return task;
    }

    // ─── 私有方法 ───

    private String buildUserMessage(String questionContent, String questionType, String growthStage, String abnormalReason, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户问题：").append(questionContent).append("\n");
        sb.append("问题类型：").append(questionType).append("\n");
        if (growthStage != null && !growthStage.isEmpty()) {
            sb.append("草莓生长阶段：").append(growthStage).append("\n");
        }
        if (abnormalReason != null && !abnormalReason.isEmpty()) {
            sb.append("异常原因说明：").append(abnormalReason).append("\n");
        }
        // 地块上下文
        String plotInfo = (String) params.getOrDefault("plotInfo", "");
        if (!plotInfo.isEmpty()) sb.append("地块信息：").append(plotInfo).append("\n");
        // 检测结果
        String detectResult = (String) params.getOrDefault("detectResult", "");
        if (!detectResult.isEmpty()) sb.append("巡检识别结果：").append(detectResult).append("\n");
        String confidence = params.get("confidence") != null ? params.get("confidence").toString() : "";
        if (!confidence.isEmpty()) sb.append("置信度：").append(confidence).append("\n");
        // 环境数据
        String envData = (String) params.getOrDefault("envData", "");
        if (!envData.isEmpty()) sb.append("温室环境数据：").append(envData).append("\n");
        sb.append("\n请根据以上信息给出专业农技建议。");
        return sb.toString();
    }

    private void recordBehavior(Long userId, Long targetId, String targetType, String behaviorType) {
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            if ("question".equals(targetType)) {
                // 对于 ai_question 行为，targetId 可以使用 questionId 但不能直接赋给 articleId/videoId
                // 用 targetType 来区分
            }
            behavior.setTargetType(targetType);
            behavior.setBehaviorType(behaviorType);
            behavior.setWeight(getBehaviorWeight(behaviorType));
            behavior.setCreateTime(LocalDateTime.now());
            behavior.setUpdateTime(LocalDateTime.now());
            userBehaviorMapper.insert(behavior);
        } catch (Exception e) {
            log.warn("记录用户行为失败: {}", e.getMessage());
        }
    }

    private double getBehaviorWeight(String behaviorType) {
        return switch (behaviorType) {
            case "ai_question" -> 2.0;
            case "ai_answer_view" -> 1.0;
            case "ai_feedback_helpful" -> 1.5;
            case "ai_feedback_unhelpful" -> 0.5;
            case "expert_review_request" -> 3.0;
            default -> 1.0;
        };
    }

    private void updateUserProfile(Long userId, String questionType, String growthStage) {
        try {
            // 根据问题类型更新兴趣标签
            String typeTag = TYPE_TAG_MAP.getOrDefault(questionType, questionType);
            if (!typeTag.isEmpty()) {
                userInterestTagMapper.increaseWeight(userId, typeTag, "question_type", 1.0);
            }
            // 根据生长阶段更新兴趣标签
            String stageTag = STAGE_TAG_MAP.getOrDefault(growthStage, null);
            if (stageTag != null && !stageTag.isEmpty()) {
                userInterestTagMapper.increaseWeight(userId, stageTag, "growth_stage", 0.5);
            }
        } catch (Exception e) {
            log.warn("更新用户画像失败: {}", e.getMessage());
        }
    }

    private String filterSensitiveWords(String content) {
        // 基础敏感词过滤
        if (content == null) return null;
        String[] sensitiveWords = {"fuck", "shit", "damn", "fuck you"};
        String result = content;
        for (String word : sensitiveWords) {
            result = result.replaceAll("(?i)" + word, "***");
        }
        return result;
    }
}

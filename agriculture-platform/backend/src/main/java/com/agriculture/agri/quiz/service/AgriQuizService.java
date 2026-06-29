package com.agriculture.agri.quiz.service;

import com.agriculture.agri.quiz.entity.AgriQuizAttempt;
import com.agriculture.agri.quiz.entity.AgriQuizPointsReward;
import com.agriculture.agri.quiz.entity.AgriLectureQuiz;
import com.agriculture.agri.quiz.entity.AgriQuizQuestion;
import com.agriculture.agri.quiz.mapper.*;
import com.agriculture.common.notification.mapper.AgriNotificationMapper;
import com.agriculture.common.notification.entity.AgriNotification;
import com.agriculture.common.security.UserContext;
import com.agriculture.knowledge.rag.service.RagService;
import com.agriculture.points.service.PointsAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgriQuizService {
    private final AgriLectureQuizMapper quizMapper;
    private final AgriQuizQuestionMapper questionMapper;
    private final AgriQuizAttemptMapper attemptMapper;
    private final AgriQuizPointsRewardMapper rewardMapper;
    private final AgriNotificationMapper notificationMapper;
    private final PointsAccountService pointsAccountService;
    private final RagService ragService;
    private final ObjectMapper objectMapper;

    // ==================== Quiz CRUD ====================

    public List<AgriLectureQuiz> listQuizzes(Long lectureId) {
        LambdaQueryWrapper<AgriLectureQuiz> qw = new LambdaQueryWrapper<>();
        qw.eq(AgriLectureQuiz::getExpertId, UserContext.getCurrentUserId());
        if (lectureId != null) qw.eq(AgriLectureQuiz::getLectureId, lectureId);
        qw.orderByDesc(AgriLectureQuiz::getCreateTime);
        return quizMapper.selectList(qw);
    }

    public List<AgriLectureQuiz> listPublishedQuizzes() {
        return quizMapper.selectList(new LambdaQueryWrapper<AgriLectureQuiz>()
                .eq(AgriLectureQuiz::getStatus, "PUBLISHED")
                .orderByDesc(AgriLectureQuiz::getCreateTime));
    }

    public AgriLectureQuiz getQuiz(Long id) {
        return quizMapper.selectById(id);
    }

    public AgriLectureQuiz createQuiz(AgriLectureQuiz quiz) {
        quiz.setExpertId(UserContext.getCurrentUserId());
        quiz.setStatus("DRAFT");
        quiz.setCreateTime(LocalDateTime.now());
        quiz.setUpdateTime(LocalDateTime.now());
        quizMapper.insert(quiz);
        return quiz;
    }

    public void updateQuiz(Long id, AgriLectureQuiz quiz) {
        quiz.setId(id);
        quiz.setUpdateTime(LocalDateTime.now());
        quizMapper.updateById(quiz);
    }

    /** RAG 生成题目草稿 */
    public List<AgriQuizQuestion> generateQuestions(Long quizId, String prompt) {
        AgriLectureQuiz quiz = quizMapper.selectById(quizId);
        if (quiz == null) throw new RuntimeException("测验不存在");

        String genPrompt = (prompt != null && !prompt.isEmpty()) ? prompt :
            "请基于讲座知识库内容，生成5道选择题用于农户知识测验。每题4个选项，返回JSON数组格式：[{\"questionContent\":\"...\",\"options\":[\"A.xxx\",\"B.xxx\",\"C.xxx\",\"D.xxx\"],\"correctAnswer\":\"A\",\"explanation\":\"...\"}]";

        Map<String, Object> ragResult = ragService.ask(genPrompt, 8);
        String aiAnswer = (String) ragResult.get("answer");
        if (aiAnswer == null) throw new RuntimeException("RAG 生成题目失败");

        // 尝试从 AI 回答中提取 JSON 数组
        List<Map<String, Object>> questions;
        try {
            String json = aiAnswer.trim();
            int arrStart = json.indexOf('[');
            int arrEnd = json.lastIndexOf(']');
            if (arrStart >= 0 && arrEnd > arrStart) json = json.substring(arrStart, arrEnd + 1);
            questions = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("RAG 返回格式无法解析: " + e.getMessage());
        }

        List<AgriQuizQuestion> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);
            AgriQuizQuestion qq = new AgriQuizQuestion();
            qq.setQuizId(quizId);
            qq.setLectureId(quiz.getLectureId());
            qq.setQuestionContent((String) q.get("questionContent"));
            qq.setQuestionType("SINGLE_CHOICE");
            try { qq.setOptionsJson(objectMapper.writeValueAsString(q.get("options"))); } catch (Exception e) {}
            qq.setCorrectAnswer((String) q.get("correctAnswer"));
            qq.setExplanation((String) q.getOrDefault("explanation", ""));
            qq.setScore(20);
            qq.setSortOrder(i + 1);
            qq.setStatus("DRAFT");
            qq.setCreateTime(LocalDateTime.now());
            qq.setUpdateTime(LocalDateTime.now());
            questionMapper.insert(qq);
            result.add(qq);
        }

        quiz.setStatus("GENERATED");
        quiz.setUpdateTime(LocalDateTime.now());
        quizMapper.updateById(quiz);

        return result;
    }

    public List<AgriQuizQuestion> listQuestions(Long quizId) {
        return questionMapper.selectList(new LambdaQueryWrapper<AgriQuizQuestion>()
                .eq(AgriQuizQuestion::getQuizId, quizId).orderByAsc(AgriQuizQuestion::getSortOrder));
    }

    public void updateQuestion(Long id, AgriQuizQuestion q) {
        q.setId(id); q.setUpdateTime(LocalDateTime.now()); questionMapper.updateById(q);
    }

    /** 发布测验 */
    public void publishQuiz(Long quizId) {
        AgriLectureQuiz quiz = quizMapper.selectById(quizId);
        if (quiz == null) throw new RuntimeException("测验不存在");
        List<AgriQuizQuestion> questions = listQuestions(quizId);
        if (questions.isEmpty()) throw new RuntimeException("请先生成题目");

        int total = questions.stream().mapToInt(q -> q.getScore() != null ? q.getScore() : 0).sum();
        quiz.setTotalQuestions(questions.size());
        quiz.setTotalScore(total);
        quiz.setStatus("PUBLISHED");
        quiz.setUpdateTime(LocalDateTime.now());
        quizMapper.updateById(quiz);

        // 把题目也标记为Published
        for (AgriQuizQuestion q : questions) {
            if (!"PUBLISHED".equals(q.getStatus())) {
                q.setStatus("PUBLISHED");
                questionMapper.updateById(q);
            }
        }
    }

    // ==================== 农户答题 ====================

    @Transactional
    public AgriQuizAttempt submitAttempt(Long quizId, List<Map<String, Object>> answers) {
        Long userId = UserContext.getCurrentUserId();
        AgriLectureQuiz quiz = quizMapper.selectById(quizId);
        if (quiz == null) throw new RuntimeException("测验不存在");
        if (!"PUBLISHED".equals(quiz.getStatus())) throw new RuntimeException("测验未发布");
        LocalDateTime now = LocalDateTime.now();
        if (quiz.getAnswerStartTime() != null && now.isBefore(quiz.getAnswerStartTime())) throw new RuntimeException("答题未开始");
        if (quiz.getAnswerEndTime() != null && now.isAfter(quiz.getAnswerEndTime())) throw new RuntimeException("答题已结束");

        // 检查答题次数
        if (quiz.getMaxAttempts() != null) {
            Long count = attemptMapper.selectCount(new LambdaQueryWrapper<AgriQuizAttempt>()
                    .eq(AgriQuizAttempt::getQuizId, quizId).eq(AgriQuizAttempt::getUserId, userId));
            if (count >= quiz.getMaxAttempts()) throw new RuntimeException("已达最大答题次数");
        }

        // 评分
        List<AgriQuizQuestion> questions = listQuestions(quizId);
        int score = 0, correctCount = 0, wrongCount = 0;
        for (AgriQuizQuestion q : questions) {
            Map<String, Object> userAnswer = answers.stream()
                    .filter(a -> String.valueOf(q.getId()).equals(String.valueOf(a.get("questionId"))))
                    .findFirst().orElse(null);
            if (userAnswer != null && q.getCorrectAnswer() != null && q.getCorrectAnswer().equals(userAnswer.get("answer"))) {
                score += q.getScore() != null ? q.getScore() : 0;
                correctCount++;
            } else {
                wrongCount++;
            }
        }

        // 保存答题记录
        AgriQuizAttempt attempt = new AgriQuizAttempt();
        attempt.setQuizId(quizId);
        attempt.setUserId(userId);
        try { attempt.setAnswersJson(objectMapper.writeValueAsString(answers)); } catch (Exception e) {}
        attempt.setScore(score);
        attempt.setCorrectCount(correctCount);
        attempt.setWrongCount(wrongCount);
        attempt.setStatus("SCORED");
        attempt.setStartTime(now);
        attempt.setSubmitTime(now);
        attempt.setCreateTime(now);
        attemptMapper.insert(attempt);

        // 积分奖励
        grantRewardIfEligible(quiz, userId, attempt.getId(), score);

        return attempt;
    }

    private void grantRewardIfEligible(AgriLectureQuiz quiz, Long userId, Long attemptId, int score) {
        int threshold = quiz.getRewardThreshold() != null ? quiz.getRewardThreshold() : 90;
        if (score < threshold) return;

        // 检查是否已奖励
        Long granted = rewardMapper.selectCount(new LambdaQueryWrapper<AgriQuizPointsReward>()
                .eq(AgriQuizPointsReward::getQuizId, quiz.getId())
                .eq(AgriQuizPointsReward::getUserId, userId)
                .eq(AgriQuizPointsReward::getRewardStatus, "GRANTED"));
        if (granted > 0) return;

        int rewardPoints = quiz.getRewardPoints() != null ? quiz.getRewardPoints() : 20;

        AgriQuizPointsReward reward = new AgriQuizPointsReward();
        reward.setQuizId(quiz.getId());
        reward.setLectureId(quiz.getLectureId());
        reward.setUserId(userId);
        reward.setAttemptId(attemptId);
        reward.setScore(score);
        reward.setRewardPoints(rewardPoints);
        reward.setRewardStatus("GRANTED");
        reward.setRewardTime(LocalDateTime.now());
        reward.setCreateTime(LocalDateTime.now());
        reward.setUpdateTime(LocalDateTime.now());
        rewardMapper.insert(reward);

        // 发放积分
        pointsAccountService.earnPoints(userId, rewardPoints, "LECTURE_QUIZ_REWARD", reward.getId(), "答题奖励");

        // 通知
        sendNotification(userId, "QUIZ_REWARD", "QUIZ", quiz.getId(),
                "答题积分奖励", "恭喜！您在测验中获得了" + score + "分，奖励" + rewardPoints + "积分。");
    }

    // ==================== 答题记录查询 ====================

    public List<AgriQuizAttempt> listMyAttempts(Long quizId) {
        LambdaQueryWrapper<AgriQuizAttempt> qw = new LambdaQueryWrapper<>();
        qw.eq(AgriQuizAttempt::getUserId, UserContext.getCurrentUserId());
        if (quizId != null) qw.eq(AgriQuizAttempt::getQuizId, quizId);
        qw.orderByDesc(AgriQuizAttempt::getCreateTime);
        return attemptMapper.selectList(qw);
    }

    public List<AgriQuizAttempt> listAttemptsByExpert(Long quizId) {
        return attemptMapper.selectList(new LambdaQueryWrapper<AgriQuizAttempt>()
                .eq(AgriQuizAttempt::getQuizId, quizId).orderByDesc(AgriQuizAttempt::getCreateTime));
    }

    /** 发布后通知用户 */
    public void notifyUsers(Long quizId) {
        AgriLectureQuiz quiz = quizMapper.selectById(quizId);
        if (quiz == null) return;
        // ponytail: 简单通知所有用户（实际应通知已注册/关注的用户）
        sendNotification(0L, "LECTURE_QUIZ_PUBLISH", "LECTURE", quiz.getLectureId(),
                "新题目发布", "您关注的讲座已发布知识测验，请及时完成答题。");
    }

    private void sendNotification(Long userId, String noticeType, String bizType, Long bizId, String title, String content) {
        AgriNotification n = new AgriNotification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        n.setNoticeType(noticeType);
        n.setBizType(bizType);
        n.setBizId(bizId);
        n.setReadStatus(0);
        n.setCreateTime(LocalDateTime.now());
        n.setUpdateTime(LocalDateTime.now());
        notificationMapper.insert(n);
    }
}

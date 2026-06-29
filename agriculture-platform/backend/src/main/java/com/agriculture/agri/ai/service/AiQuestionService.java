package com.agriculture.agri.ai.service;

import com.agriculture.agri.ai.entity.AgriAiQuestion;
import com.agriculture.agri.ai.entity.AgriExpertReviewTask;
import com.agriculture.common.result.PageResult;

import java.util.Map;

public interface AiQuestionService {

    /** 提交智能提问并获取 AI 回答 */
    Map<String, Object> ask(Map<String, Object> params);

    /** 查询我的智能问答记录 */
    PageResult<AgriAiQuestion> listMyQuestions(int page, int pageSize);

    /** 查询问答详情 */
    AgriAiQuestion getQuestionDetail(Long id);

    /** 对 AI 回答进行反馈 */
    void feedback(Long id, String feedback, String reason);

    /** 转专家复核 */
    AgriExpertReviewTask requestExpertReview(Long id);
}

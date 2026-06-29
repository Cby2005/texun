import request from '@/utils/request'

// 提交智能提问并获取 AI 回答
export function askAiQuestion(data) {
  return request.post('/agri/ai/questions/ask', data, { timeout: 120000 })
}

// 查询我的智能问答记录
export function listMyAiQuestions(params) {
  return request.get('/agri/ai/questions/my', { params })
}

// 查询问答详情
export function getAiQuestionDetail(id) {
  return request.get('/agri/ai/questions/' + id)
}

// 对 AI 回答进行反馈 (feedback: helpful | unhelpful)
export function feedbackAiQuestion(id, feedback, reason) {
  return request.post('/agri/ai/questions/' + id + '/feedback', { feedback, reason })
}

// 转专家复核
export function requestExpertReview(id) {
  return request.post('/agri/ai/questions/' + id + '/expert-review')
}

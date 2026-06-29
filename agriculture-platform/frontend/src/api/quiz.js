import request from '@/utils/request'

// 测验管理
export function listQuizzes(params) { return request.get('/agri/quiz/list', { params }) }
export function createQuiz(data) { return request.post('/agri/quiz', data) }
export function updateQuiz(id, data) { return request.put('/agri/quiz/' + id, data) }
export function generateQuestions(id, prompt) { return request.post('/agri/quiz/' + id + '/generate', { prompt }) }
export function listQuestions(id) { return request.get('/agri/quiz/' + id + '/questions') }
export function updateQuestion(id, data) { return request.put('/agri/quiz/questions/' + id, data) }
export function publishQuiz(id) { return request.post('/agri/quiz/' + id + '/publish') }

// 农户答题
export function listPublishedQuizzes() { return request.get('/agri/quiz/published') }
export function getQuizDetail(id) { return request.get('/agri/quiz/' + id + '/detail') }
export function getFarmerQuestions(id) { return request.get('/agri/quiz/' + id + '/farmer-questions') }
export function submitAttempt(id, answers) { return request.post('/agri/quiz/' + id + '/submit', { answers }) }
export function myAttempts(id) { return request.get('/agri/quiz/' + id + '/my-attempts') }

// 专家查看统计
export function listAttempts(id) { return request.get('/agri/quiz/' + id + '/attempts') }

// 积分
export function myPointsAccount() { return request.get('/points/my') }
export function myPointsRecords() { return request.get('/points/my/records') }

// 积分商城
export function listMallGoods(category) { return request.get('/points/mall/goods', { params: { category } }) }
export function getMallGoods(id) { return request.get('/points/mall/goods/' + id) }
export function exchangeGoods(goodsId, data) { return request.post('/points/mall/exchange/' + goodsId, data) }
export function myExchangeOrders() { return request.get('/points/mall/my-orders') }
export function cancelExchangeOrder(id) { return request.post('/points/mall/orders/' + id + '/cancel') }
export function allExchangeOrders() { return request.get('/points/mall/all-orders') }
export function updateOrderStatus(id, status) { return request.put('/points/mall/orders/' + id + '/status', null, { params: { status } }) }

// 商品管理(管理员)
export function createMallGoods(data) { return request.post('/points/mall/goods', data) }
export function updateMallGoods(id, data) { return request.put('/points/mall/goods/' + id, data) }
export function updateGoodsStatus(id, status) { return request.put('/points/mall/goods/' + id + '/status', null, { params: { status } }) }
export function allMallGoods() { return request.get('/points/mall/all-goods') }

// 通知
export function myNotifications() { return request.get('/notification/my') }
export function unreadNotificationCount() { return request.get('/notification/unread-count') }
export function markNotificationRead(id) { return request.put('/notification/' + id + '/read') }
export function markAllNotificationsRead() { return request.put('/notification/read-all') }

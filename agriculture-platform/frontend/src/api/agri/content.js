import request from '@/utils/request'

// 农技内容中心 API
export function listContent(params) { return request.get('/agri/content/list', { params }) }
export function getContent(id) { return request.get('/agri/content/' + id) }
export function addContent(data) { return request.post('/agri/content/add', data) }
export function updateContent(data) { return request.put('/agri/content/update', data) }
export function deleteContent(id) { return request.delete('/agri/content/delete/' + id) }
export function publishContent(id) { return request.post('/agri/content/publish', null, { params: { id } }) }
export function offlineContent(id) { return request.post('/agri/content/offline/' + id) }

// 推荐接口
export function getRecommendContent(params) { return request.get('/recommend/content', { params }) }
export function getSimilarContent(params) { return request.get('/recommend/similar', { params }) }
export function reportBehavior(data) { return request.post('/recommend/behavior/report', data) }
export function getUserProfile(userId) { return request.get('/recommend/profile', { params: { userId } }) }

// 兼容旧接口
export function getRecommendContentLegacy(params) { return request.get('/agri/content/recommend', { params }) }
export function getHotContent(params) { return request.get('/agri/content/hot', { params }) }

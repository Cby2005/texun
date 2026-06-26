import request from '@/utils/request'

export function getRecommendArticles(params) { return request.get('/recommend/articles', { params }) }
export function recordBehavior(data) { return request.post('/behavior/record', data) }
export function getUserProfile() { return request.get('/behavior/profile') }
export function refreshProfile() { return request.post('/behavior/profile/refresh') }
export function extractTags(articleId) { return request.post('/behavior/tags/extract/' + articleId) }

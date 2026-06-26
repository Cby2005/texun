import request from '@/utils/request'

export function getGraphData(params) { return request.get('/knowledge/graph/data', { params }) }
export function searchGraph(keyword) { return request.get('/knowledge/graph/search', { params: { keyword } }) }
export function getGraphStats() { return request.get('/knowledge/graph/stats') }
export function syncArticleGraph(id) { return request.post('/knowledge/graph/sync/article/' + id) }

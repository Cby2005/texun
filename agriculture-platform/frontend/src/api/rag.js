import request from '@/utils/request'

export function askKnowledge(query, topK) { return request.get('/knowledge/rag/ask', { params: { query, topK } }) }
export function searchKnowledge(query, topK) { return request.get('/knowledge/rag/search', { params: { query, topK } }) }
export function searchWithChunks(query, topK) { return request.get('/knowledge/rag/search/chunks', { params: { query, topK } }) }
export function indexArticle(articleId) { return request.post('/knowledge/rag/index/' + articleId) }
export function indexAllArticles() { return request.post('/knowledge/rag/index/all') }

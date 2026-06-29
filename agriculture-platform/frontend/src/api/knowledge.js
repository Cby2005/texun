import request from '@/utils/request'

export function getArticles(params) { return request.get('/knowledge/articles', { params }) }
export function getArticle(id) { return request.get('/knowledge/articles/' + id) }
export function addArticle(data) { return request.post('/knowledge/articles', data) }
export function updateArticle(id, data) { return request.put('/knowledge/articles/' + id, data) }
export function deleteArticle(id) { return request.delete('/knowledge/articles/' + id) }
export function auditArticle(id, status, remark) { return request.put('/knowledge/articles/' + id + '/audit', null, { params: { status, remark } }) }
export function getPendingArticles(params) { return request.get('/knowledge/articles/pending', { params }) }
export function getMyLikedArticles(params) { return request.get('/knowledge/articles/my/likes', { params }) }
export function getMyFavoriteArticles(params) { return request.get('/knowledge/articles/my/favorites', { params }) }
export function getArticleInteraction(id) { return request.get('/knowledge/articles/' + id + '/interaction', { silent: true }) }
export function likeArticle(id) { return request.post('/knowledge/articles/' + id + '/like') }
export function unlikeArticle(id) { return request.delete('/knowledge/articles/' + id + '/like') }
export function favoriteArticle(id) { return request.post('/knowledge/articles/' + id + '/favorite') }
export function cancelFavoriteArticle(id) { return request.post('/knowledge/articles/' + id + '/cancel-favorite') }
export function getArticleComments(id) { return request.get('/knowledge/articles/' + id + '/comments', { silent: true }) }
export function createArticleComment(id, data) { return request.post('/knowledge/articles/' + id + '/comments', data) }

export function getCategories() { return request.get('/knowledge/categories') }
export function addCategory(data) { return request.post('/knowledge/categories', data) }
export function updateCategory(id, data) { return request.put('/knowledge/categories/' + id, data) }
export function deleteCategory(id) { return request.delete('/knowledge/categories/' + id) }

export function getLectures(params) { return request.get('/knowledge/lectures', { params }) }
export function getLecture(id) { return request.get('/knowledge/lectures/' + id) }
export function addLecture(data) { return request.post('/knowledge/lectures', data) }
export function updateLecture(id, data) { return request.put('/knowledge/lectures/' + id, data) }
export function deleteLecture(id) { return request.delete('/knowledge/lectures/' + id) }
export function registerLecture(id) { return request.post('/knowledge/lectures/' + id + '/register') }
export function cancelLecture(id) { return request.post('/knowledge/lectures/' + id + '/cancel') }

export function getVideos(params) { return request.get('/knowledge/videos', { params }) }
export function getVideo(id) { return request.get('/knowledge/videos/' + id) }
export function getRecommendedVideos() { return request.get('/knowledge/videos/recommend') }
export function recordVideoView(id) { return request.post('/knowledge/videos/' + id + '/view') }
export function likeVideo(id) { return request.post('/knowledge/videos/' + id + '/like') }
export function unlikeVideo(id) { return request.delete('/knowledge/videos/' + id + '/like') }
export function favoriteVideo(id) { return request.post('/knowledge/videos/' + id + '/favorite') }
export function cancelFavoriteVideo(id) { return request.post('/knowledge/videos/' + id + '/cancel-favorite') }
export function uploadVideo(formData) { return request.post('/knowledge/videos/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' }, timeout: 120000 }) }
export function deleteVideo(id) { return request.delete('/knowledge/videos/' + id) }

// 智能问答
export function submitSmartQuestion(formData) { return request.post('/question/submit', formData, { headers: { 'Content-Type': 'multipart/form-data' }, timeout: 60000 }) }
export function getQuestionDetail(id) { return request.get('/question/' + id) }
export function listSmartQuestions(params) { return request.get('/question/list', { params }) }

// 知识图谱
export function getGraphData(params) { return request.get('/knowledge/graph', { params }) }

export function getPests(params) { return request.get('/knowledge/pests', { params }) }
export function getPest(id) { return request.get('/knowledge/pests/' + id) }

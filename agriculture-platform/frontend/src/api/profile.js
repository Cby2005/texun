import request from '@/utils/request'

// 获取当前用户完整信息
export function getProfile() { return request.get('/profile') }

// 修改基本资料
export function updateProfile(data) { return request.put('/profile', data) }

// 上传头像
export function uploadAvatar(formData) {
  return request.post('/profile/avatar/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 修改密码
export function updatePassword(data) { return request.put('/profile/password', data) }

// 发送手机验证码
export function sendPhoneCode(phone) { return request.post('/profile/phone/code', { phone }) }

// 绑定手机号
export function bindPhone(data) { return request.post('/profile/phone/bind', data) }

// 换绑手机号
export function changePhone(data) { return request.post('/profile/phone/change', data) }

// 解绑手机号
export function unbindPhone(data) { return request.post('/profile/phone/unbind', data) }

// 获取账号安全信息
export function getSecurityInfo() { return request.get('/profile/security') }

// 获取登录日志
export function getLoginLogs(params) { return request.get('/profile/login-log', { params }) }

// 我的收藏
export function getMyFavorites(params) { return request.get('/profile/favorites', { params }) }
export function removeFavorite(articleId) { return request.delete('/profile/favorites/' + articleId) }

// 我的评论
export function getMyComments(params) { return request.get('/profile/comments', { params }) }
export function deleteMyComment(commentId) { return request.delete('/profile/comments/' + commentId) }

// 行为统计
export function getBehaviorStats() { return request.get('/profile/behavior-stats') }

// 兴趣画像
export function getInterestTags() { return request.get('/profile/interest-tags') }

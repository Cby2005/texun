import request from '@/utils/request'

// 上传图片
export function uploadImage(file, bizType = 'article_cover') {
  const fd = new FormData()
  fd.append('file', file)
  fd.append('bizType', bizType)
  return request.post('/common/upload/image', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 上传视频
export function uploadVideo(file, bizType = 'video_file') {
  const fd = new FormData()
  fd.append('file', file)
  fd.append('bizType', bizType)
  return request.post('/common/upload/video', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

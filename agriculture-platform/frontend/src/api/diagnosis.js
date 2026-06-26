import request from '@/utils/request'

export function detectDisease(file) {
  const form = new FormData()
  form.append('file', file)
  return request.post('/yolo/detect', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}

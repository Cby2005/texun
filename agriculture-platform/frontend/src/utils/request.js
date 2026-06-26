import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => Promise.reject(error))

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res
    if (res.code === 401) {
      localStorage.clear()
      window.location.href = '/login'
      ElMessage.error('登录已过期')
      return Promise.reject(new Error(res.message))
    }
    if (!response.config?.silent) {
      ElMessage.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message))
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    const msg = error.response?.data?.message || error.message || '网络错误'
    if (!error.config?.silent) {
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default service

import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface UserInfo {
  userId: number
  username: string
  nickname: string
  avatar: string
  role: string
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(null)

  async function login(username: string, password: string) {
    const res: any = await request.post('/auth/login', { username, password })
    token.value = res.data.accessToken
    refreshToken.value = res.data.refreshToken
    localStorage.setItem('token', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    userInfo.value = {
      userId: res.data.userId,
      username: res.data.username,
      nickname: res.data.nickname,
      avatar: res.data.avatar,
      role: res.data.role,
      permissions: res.data.permissions || [],
    }
    return res.data
  }

  async function getUserInfo() {
    const res: any = await request.get('/auth/userinfo')
    userInfo.value = res.data
    return res.data
  }

  function logout() {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  function hasPermission(perm: string): boolean {
    if (userInfo.value?.role === 'ADMIN') return true
    return userInfo.value?.permissions?.includes(perm) || false
  }

  return { token, refreshToken, userInfo, login, getUserInfo, logout, hasPermission }
})

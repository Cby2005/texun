import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || null)
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))
  const permissions = ref(JSON.parse(localStorage.getItem('permissions') || '[]'))

  const isLoggedIn = computed(() => !!token.value)
  const primaryRole = computed(() => roles.value[0] || '')
  const isAdmin = computed(() => primaryRole.value === 'ADMIN')
  const isFarmAdmin = computed(() => primaryRole.value === 'FARM_ADMIN')
  const isTraceAdmin = computed(() => primaryRole.value === 'TRACE_ADMIN')
  const isExpert = computed(() => primaryRole.value === 'EXPERT')
  const isConsumer = computed(() => primaryRole.value === 'CONSUMER')

  const roleLabel = computed(() => {
    const map = { ADMIN: '管理员', FARM_ADMIN: '农场管理员', TRACE_ADMIN: '溯源企业',
      EXPERT: '专家', FARMER: '农户', CONSUMER: '消费者' }
    return map[primaryRole.value] || primaryRole.value
  })

  function login(data) {
    token.value = data.token
    refreshToken.value = data.refreshToken
    userId.value = data.userId || null
    username.value = data.username
    nickname.value = data.nickname || data.username
    roles.value = data.roles || []
    permissions.value = data.permissions || []
    localStorage.setItem('token', data.token)
    localStorage.setItem('refreshToken', data.refreshToken)
    if (data.userId) localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname || data.username)
    localStorage.setItem('roles', JSON.stringify(data.roles || []))
    localStorage.setItem('permissions', JSON.stringify(data.permissions || []))
  }

  function logout() {
    token.value = ''
    refreshToken.value = ''
    userId.value = null
    username.value = ''
    nickname.value = ''
    roles.value = []
    permissions.value = []
    localStorage.clear()
  }

  return { token, refreshToken, userId, username, nickname, roles, permissions,
    isLoggedIn, primaryRole, isAdmin, isFarmAdmin, isTraceAdmin, isExpert, isConsumer,
    roleLabel, login, logout }
})

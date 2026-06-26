import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
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
    username.value = data.username
    nickname.value = data.nickname || data.username
    roles.value = data.roles || []
    permissions.value = data.permissions || []
    localStorage.setItem('token', data.token)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname || data.username)
    localStorage.setItem('roles', JSON.stringify(data.roles || []))
    localStorage.setItem('permissions', JSON.stringify(data.permissions || []))
  }

  function logout() {
    token.value = ''
    refreshToken.value = ''
    username.value = ''
    nickname.value = ''
    roles.value = []
    permissions.value = []
    localStorage.clear()
  }

  return { token, refreshToken, username, nickname, roles, permissions,
    isLoggedIn, primaryRole, isAdmin, isFarmAdmin, isTraceAdmin, isExpert, isConsumer,
    roleLabel, login, logout }
})

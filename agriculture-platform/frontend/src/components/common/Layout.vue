<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <h2>智慧农业平台</h2>
      </div>
      <el-menu :default-active="route.path" router background-color="#001529" text-color="#ffffff80" active-text-color="#fff" class="side-menu">
        <template v-for="item in filteredMenu" :key="item.id || item.title">
          <el-sub-menu v-if="item.children && item.children.length" :index="item.path || item.title">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="c in item.children" :key="c.path" :index="c.path">
              <el-icon><component :is="c.icon" /></el-icon>
              <span>{{ c.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="page-title">{{ route.meta.title }}</span>
        <div class="header-right">
          <el-tag size="small" :type="roleTagType">{{ store.roleLabel }}</el-tag>
          <span class="username">{{ store.nickname || store.username }}</span>
          <el-dropdown>
            <el-icon :size="20"><UserFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">个人信息</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../store'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const roleTagType = computed(() => {
  const m = { ADMIN: 'danger', FARM_ADMIN: 'warning', TRACE_ADMIN: 'primary', EXPERT: 'success', FARMER: '', AUDITOR: 'info' }
  return m[store.primaryRole] || ''
})

const menuData = [
  { title: '首页驾驶舱', path: '/dashboard', icon: 'Odometer' },
  { title: '农场管理', icon: 'Grid', children: [
    { title: '农场管理', path: '/farm/enterprises', icon: 'OfficeBuilding', roles: ['ADMIN','FARM_ADMIN'] },
    { title: '地块管理', path: '/farm/lands', icon: 'Grid', roles: ['ADMIN','FARM_ADMIN'] },
    { title: '作物管理', path: '/farm/crops', icon: 'Apple' },
    { title: '设备管理', path: '/farm/devices', icon: 'Cpu', roles: ['ADMIN','FARM_ADMIN'] },
    { title: '环境监测', path: '/farm/env', icon: 'DataLine' },
    { title: '天气预测', path: '/farm/weather', icon: 'PartlyCloudy' },
    { title: '产量预测', path: '/farm/yield', icon: 'TrendCharts' }
  ]},
  { title: '溯源管理', icon: 'Goods', children: [
    { title: '产品管理', path: '/trace/products', icon: 'ShoppingBag', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '批次管理', path: '/trace/batches', icon: 'Box', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '种植记录', path: '/trace/production', icon: 'Sunny', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '加工记录', path: '/trace/processing', icon: 'SetUp', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '仓储记录', path: '/trace/storage', icon: 'Histogram', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '物流记录', path: '/trace/logistics', icon: 'Van', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '质检记录', path: '/trace/quality', icon: 'Finished', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '销售记录', path: '/trace/sales', icon: 'Sell', roles: ['ADMIN','TRACE_ADMIN'] },
    { title: '全链追溯', path: '/trace/chain', icon: 'Link', roles: ['ADMIN','TRACE_ADMIN'] }
  ]},
  { title: '技术推广', icon: 'Reading', children: [
    { title: '农技文章', path: '/knowledge/articles', icon: 'Document' },
    { title: '问答社区', path: '/knowledge/questions', icon: 'ChatDotRound' },
    { title: '智能提问', path: '/knowledge/smart-question', icon: 'MagicStick' },
    { title: '农技讲座', path: '/knowledge/lectures', icon: 'VideoCamera' },
    { title: '农技视频', path: '/knowledge/videos', icon: 'Film' },
    { title: '病虫害知识', path: '/knowledge/pests', icon: 'Warning' },
    { title: '知识图谱', path: '/knowledge/graph', icon: 'Share' },
    { title: '技术分类', path: '/knowledge/categories', icon: 'Folder', roles: ['ADMIN'] }
  ]},
  { title: '系统管理', icon: 'Setting', children: [
    { title: '用户管理', path: '/system/users', icon: 'User', roles: ['ADMIN'] },
    { title: '角色管理', path: '/system/roles', icon: 'Avatar', roles: ['ADMIN'] },
    { title: '菜单管理', path: '/system/menus', icon: 'Menu', roles: ['ADMIN'] }
  ]}
]

const filteredMenu = computed(() => {
  return menuData.filter(item => {
    if (item.roles && !item.roles.some(r => store.roles.includes(r))) return false
    if (item.children) {
      item.children = item.children.filter(c => !c.roles || c.roles.some(r => store.roles.includes(r)))
    }
    return true
  })
})

function handleLogout() {
  store.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.layout { height: 100vh; }
.aside {
  background: #001529; overflow-y: auto;
  .logo { height: 64px; display: flex; align-items: center; justify-content: center; border-bottom: 1px solid #ffffff10;
    h2 { color: #fff; font-size: 16px; }
  }
}
.side-menu { border-right: none; }
.header {
  background: #fff; display: flex; align-items: center; justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06); padding: 0 24px;
  .page-title { font-size: 16px; font-weight: 600; }
  .header-right { display: flex; align-items: center; gap: 12px; }
  .username { font-size: 14px; }
}
.main { background: #F5F7FA; padding: 20px; overflow-y: auto; }
</style>

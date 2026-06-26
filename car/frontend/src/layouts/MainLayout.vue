<template>
  <el-container class="layout-shell">
    <el-aside :width="isCollapse ? '76px' : '268px'" class="layout-aside">
      <div class="brand-lockup" :class="{ compact: isCollapse }">
        <div class="brand-mark">
          <el-icon><Van /></el-icon>
        </div>
        <div v-show="!isCollapse" class="brand-copy">
          <strong>无人车管理系统</strong>
          <span>Vehicle Command</span>
        </div>
      </div>

      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapse"
        router
        class="layout-menu"
      >
        <el-menu-item index="/vehicle">
          <el-icon><Van /></el-icon>
          <span>无人车管理</span>
        </el-menu-item>
        <el-menu-item index="/route">
          <el-icon><MapLocation /></el-icon>
          <span>路线规划</span>
        </el-menu-item>
        <el-menu-item index="/simulator">
          <el-icon><VideoPlay /></el-icon>
          <span>模拟器控制</span>
        </el-menu-item>
        <el-menu-item index="/field">
          <el-icon><Location /></el-icon>
          <span>农田管理</span>
        </el-menu-item>
        <el-menu-item index="/monitor">
          <el-icon><Monitor /></el-icon>
          <span>作物监测</span>
        </el-menu-item>
      </el-menu>

      <div v-show="!isCollapse" class="aside-status">
        <span class="pulse-dot"></span>
        <div>
          <strong>车端管理系统</strong>
          <span>独立部署的无人车管理终端</span>
        </div>
      </div>
    </el-aside>

    <el-container class="layout-stage">
      <el-header class="layout-header">
        <div class="header-left">
          <button class="icon-button" type="button" aria-label="切换侧栏" @click="isCollapse = !isCollapse">
            <el-icon>
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
          </button>
          <div class="route-context">
            <h1>{{ currentTitle || '无人车管理' }}</h1>
          </div>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" :src="userStore.userInfo?.avatar">
                {{ avatarText }}
              </el-avatar>
              <span class="username">{{ displayName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowDown,
  Expand,
  Fold,
  Location,
  MapLocation,
  Monitor,
  Van,
  VideoPlay,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const currentRoute = computed(() => {
  const path = route.path
  if (path.startsWith('/vehicle')) return '/vehicle'
  if (path.startsWith('/route')) return '/route'
  if (path.startsWith('/simulator')) return '/simulator'
  if (path.startsWith('/field')) return '/field'
  if (path.startsWith('/monitor')) return '/monitor'
  return path
})

const currentTitle = computed(() => route.meta.title as string)

const displayName = computed(() => {
  return userStore.userInfo?.nickname || userStore.userInfo?.username || '未登录'
})

const avatarText = computed(() => {
  const name = userStore.userInfo?.nickname || userStore.userInfo?.username || 'U'
  return name.charAt(0).toUpperCase()
})

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped lang="scss">
.layout-shell {
  height: 100vh;
}

.layout-aside {
  background: var(--agri-panel);
  box-shadow: var(--agri-shadow);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  overflow: hidden;
}

.brand-lockup {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--agri-line);

  &.compact { justify-content: center; padding: 16px; }
}

.brand-mark {
  width: 40px; height: 40px;
  border-radius: 12px;
  background: var(--agri-green);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.brand-copy {
  strong { display: block; font-size: 15px; color: var(--agri-ink); }
  span { font-size: 11px; color: var(--agri-muted); }
}

.layout-menu {
  flex: 1;
  overflow-y: auto;
  border-right: none;
}

.aside-status {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid var(--agri-line);
  background: var(--agri-panel-soft);
  font-size: 12px;

  strong { display: block; color: var(--agri-ink); }
  span { color: var(--agri-muted); }
}

.pulse-dot {
  width: 8px; height: 8px;
  margin-top: 4px;
  border-radius: 50%;
  background: var(--agri-green);
  flex-shrink: 0;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.layout-stage {
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  height: 64px !important;
  background: var(--agri-panel);
  border-bottom: 1px solid var(--agri-line);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;

  .header-left { display: flex; align-items: center; gap: 16px; }
  .header-right { display: flex; align-items: center; gap: 16px; }
}

.icon-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  color: var(--agri-muted);
  padding: 4px;
  display: flex;
  align-items: center;
}

.route-context h1 {
  font-size: 18px;
  font-weight: 600;
  color: var(--agri-ink);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  .username { font-size: 14px; color: var(--agri-ink); }
}

.layout-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #eef5eb;
}
</style>

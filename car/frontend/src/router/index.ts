import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册', public: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/vehicle',
    children: [
      {
        path: 'vehicle',
        name: 'Vehicle',
        component: () => import('@/views/vehicle/VehicleList.vue'),
        meta: { title: '无人车管理' },
      },
      {
        path: 'vehicle/:id',
        name: 'VehicleDetail',
        component: () => import('@/views/vehicle/VehicleDetail.vue'),
        meta: { title: '设备详情' },
      },
      {
        path: 'simulator',
        name: 'Simulator',
        component: () => import('@/views/simulator/SimulatorView.vue'),
        meta: { title: '模拟器控制' },
      },
      {
        path: 'field',
        name: 'Field',
        component: () => import('@/views/field/FieldView.vue'),
        meta: { title: '农田管理' },
      },
      {
        path: 'route',
        name: 'Route',
        component: () => import('@/views/field/RouteView.vue'),
        meta: { title: '路线规划' },
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/MonitorView.vue'),
        meta: { title: '作物监测' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string) || '智慧农场无人驾驶车端管理系统'
  const userStore = useUserStore()
  if (to.meta.public) {
    next()
    return
  }
  if (!userStore.token) {
    next('/login')
    return
  }
  next()
})

export default router

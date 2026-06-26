import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store'

const farmerRoles = ['ADMIN', 'FARM_ADMIN', 'FARMER']
const knowledgeRoles = ['ADMIN', 'FARM_ADMIN', 'EXPERT', 'FARMER']

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/auth/login.vue'), meta: { public: true, title: '登录' } },
  { path: '/register', name: 'Register', component: () => import('../views/auth/register.vue'), meta: { public: true, title: '注册' } },
  {
    path: '/',
    component: () => import('../components/common/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', meta: { title: '首页驾驶舱' }, component: () => import('../views/dashboard/index.vue') },
      { path: 'profile', name: 'Profile', meta: { title: '个人信息' }, component: () => import('../views/auth/profile.vue') },

      { path: 'system/users', name: 'SysUsers', meta: { title: '用户管理', roles: ['ADMIN'] }, component: () => import('../views/system/user/list.vue') },
      { path: 'system/roles', name: 'SysRoles', meta: { title: '角色管理', roles: ['ADMIN'] }, component: () => import('../views/system/role/list.vue') },
      { path: 'system/menus', name: 'SysMenus', meta: { title: '菜单管理', roles: ['ADMIN'] }, component: () => import('../views/system/menu/list.vue') },

      { path: 'farm/enterprises', name: 'FarmEnterprises', meta: { title: '农场管理', roles: ['ADMIN', 'FARM_ADMIN'] }, component: () => import('../views/farm/enterprise/list.vue') },
      { path: 'farm/lands', name: 'FarmLands', meta: { title: '地块管理', roles: ['ADMIN', 'FARM_ADMIN'] }, component: () => import('../views/farm/land/list.vue') },
      { path: 'farm/crops', name: 'FarmCrops', meta: { title: '作物管理', roles: farmerRoles }, component: () => import('../views/farm/crop/list.vue') },
      { path: 'farm/devices', name: 'FarmDevices', meta: { title: '设备管理', roles: ['ADMIN', 'FARM_ADMIN'] }, component: () => import('../views/farm/device/list.vue') },
      { path: 'farm/env', name: 'FarmEnv', meta: { title: '环境监测', roles: farmerRoles }, component: () => import('../views/farm/env/index.vue') },
      { path: 'farm/weather', name: 'Weather', meta: { title: '天气预测', roles: farmerRoles }, component: () => import('../views/farm/Weather.vue') },
      { path: 'farm/yield', name: 'YieldPredict', meta: { title: '产量预测', roles: farmerRoles }, component: () => import('../views/farm/YieldPredict.vue') },

      { path: 'trace/products', name: 'TraceProducts', meta: { title: '产品管理', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/product/list.vue') },
      { path: 'trace/batches', name: 'TraceBatches', meta: { title: '批次管理', roles: ['ADMIN', 'FARM_ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/batch/list.vue') },
      { path: 'trace/records', name: 'TraceRecords', meta: { title: '溯源记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/record/index.vue') },
      { path: 'trace/production', name: 'TraceProduction', meta: { title: '种植记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/production/list.vue') },
      { path: 'trace/processing', name: 'TraceProcessing', meta: { title: '加工记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/processing/list.vue') },
      { path: 'trace/storage', name: 'TraceStorage', meta: { title: '仓储记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/storage/list.vue') },
      { path: 'trace/logistics', name: 'TraceLogistics', meta: { title: '物流记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/logistics/list.vue') },
      { path: 'trace/quality', name: 'TraceQuality', meta: { title: '质检记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/quality/list.vue') },
      { path: 'trace/sales', name: 'TraceSales', meta: { title: '销售记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/sales/list.vue') },
      { path: 'trace/chain', name: 'TraceChain', meta: { title: '全链追溯', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/chain/index.vue') },
      { path: 'trace/public', name: 'PublicTrace', meta: { title: '公开溯源', roles: ['CONSUMER', 'ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/public.vue') },

      { path: 'knowledge/articles', name: 'Articles', meta: { title: '农技文章', roles: knowledgeRoles }, component: () => import('../views/knowledge/article/list.vue') },
      { path: 'knowledge/articles/create', name: 'ArticleCreate', meta: { title: '发布文章', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/knowledge/article/form.vue') },
      { path: 'knowledge/articles/:id', name: 'ArticleDetail', meta: { title: '文章详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/article/detail.vue') },
      { path: 'knowledge/articles/:id/edit', name: 'ArticleEdit', meta: { title: '编辑文章', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/knowledge/article/form.vue') },
      { path: 'knowledge/questions', name: 'Questions', meta: { title: '问答社区', roles: knowledgeRoles }, component: () => import('../views/knowledge/question/list.vue') },
      { path: 'knowledge/questions/create', name: 'QuestionCreate', meta: { title: '提问', roles: farmerRoles }, component: () => import('../views/diagnosis/SmartSubmit.vue') },
      { path: 'knowledge/questions/:id', name: 'QuestionDetail', meta: { title: '问题详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/question/detail.vue') },
      { path: 'knowledge/lectures', name: 'Lectures', meta: { title: '农技讲座', roles: knowledgeRoles }, component: () => import('../views/knowledge/lecture/list.vue') },
      { path: 'knowledge/lectures/create', name: 'LectureCreate', meta: { title: '发布讲座', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/knowledge/lecture/form.vue') },
      { path: 'knowledge/lectures/:id', name: 'LectureDetail', meta: { title: '讲座详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/lecture/detail.vue') },
      { path: 'knowledge/videos', name: 'Videos', meta: { title: '农技视频', roles: knowledgeRoles }, component: () => import('../views/knowledge/video/list.vue') },
      { path: 'knowledge/videos/create', name: 'VideoCreate', meta: { title: '发布视频', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/knowledge/video/form.vue') },
      { path: 'knowledge/videos/:id', name: 'VideoDetail', meta: { title: '视频详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/video/detail.vue') },
      { path: 'knowledge/pests', name: 'Pests', meta: { title: '病虫害知识', roles: knowledgeRoles }, component: () => import('../views/knowledge/pest/list.vue') },
      { path: 'knowledge/categories', name: 'Categories', meta: { title: '技术分类', roles: ['ADMIN'] }, component: () => import('../views/knowledge/category/list.vue') },
      { path: 'knowledge/graph', name: 'KnowledgeGraph', meta: { title: '知识图谱', roles: knowledgeRoles }, component: () => import('../views/knowledge/Graph.vue') },
      { path: 'knowledge/search', name: 'KnowledgeSearch', meta: { title: '知识检索', roles: knowledgeRoles }, component: () => import('../views/knowledge/search.vue') },
      { path: 'knowledge/smart-question', name: 'SmartQuestionSubmit', meta: { title: '智能提问', roles: farmerRoles }, component: () => import('../views/diagnosis/SmartSubmit.vue') },
      { path: 'knowledge/questions/:id/smart', name: 'SmartQuestionDetail', meta: { title: '智能诊断详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/question/smartDetail.vue') },

      { path: 'recommend', name: 'Recommend', meta: { title: '个性化推荐', roles: knowledgeRoles }, component: () => import('../views/recommend/list.vue') },
      { path: 'user-profile', name: 'UserProfile', meta: { title: '用户画像', roles: knowledgeRoles }, component: () => import('../views/user/profile.vue') },
      { path: 'diagnosis', name: 'Diagnosis', meta: { title: '病虫害诊断', roles: farmerRoles }, component: () => import('../views/diagnosis/index.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 智慧农业平台` : '智慧农业综合服务平台'
  if (to.meta.public) { next(); return }
  const store = useUserStore()
  if (!store.isLoggedIn) { next('/login'); return }
  if (to.meta.roles && !to.meta.roles.some(r => store.roles.includes(r))) {
    next('/dashboard')
    return
  }
  next()
})

export default router

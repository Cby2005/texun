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
      { path: 'dashboard', name: 'Dashboard', meta: { title: '草莓驾驶舱' }, component: () => import('../views/dashboard/index.vue') },
      { path: 'profile', name: 'Profile', meta: { title: '个人信息' }, component: () => import('../views/auth/profile.vue') },

      { path: 'system/users', name: 'SysUsers', meta: { title: '用户管理', roles: ['ADMIN'] }, component: () => import('../views/system/user/list.vue') },
      { path: 'system/roles', name: 'SysRoles', meta: { title: '角色管理', roles: ['ADMIN'] }, component: () => import('../views/system/role/list.vue') },
      { path: 'system/menus', name: 'SysMenus', meta: { title: '菜单管理', roles: ['ADMIN'] }, component: () => import('../views/system/menu/list.vue') },
      { path: 'system/profile-analysis', name: 'ProfileAnalysis', meta: { title: '用户画像分析', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/system/profile-analysis.vue') },

      { path: 'farm/enterprises', name: 'FarmEnterprises', meta: { title: '草莓基地管理', roles: ['ADMIN', 'FARM_ADMIN'] }, component: () => import('../views/farm/enterprise/list.vue') },
      { path: 'farm/lands', name: 'FarmLands', meta: { title: '温室地块管理', roles: ['ADMIN', 'FARM_ADMIN'] }, component: () => import('../views/farm/land/list.vue') },
      { path: 'farm/devices', name: 'FarmDevices', meta: { title: '设备管理', roles: ['ADMIN', 'FARM_ADMIN'] }, component: () => import('../views/farm/device/list.vue') },
      { path: 'farm/env', name: 'FarmEnv', meta: { title: '环境监测', roles: farmerRoles }, component: () => import('../views/farm/env/index.vue') },
      { path: 'farm/weather', name: 'Weather', meta: { title: '天气预测', roles: farmerRoles }, component: () => import('../views/farm/Weather.vue') },
      { path: 'farm/yield', name: 'YieldPredict', meta: { title: '草莓产量预测', roles: farmerRoles }, component: () => import('../views/farm/YieldPredict.vue') },

      { path: 'trace/products', name: 'TraceProducts', meta: { title: '草莓产品管理', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/product/list.vue') },
      { path: 'trace/batches', name: 'TraceBatches', meta: { title: '草莓批次管理', roles: ['ADMIN', 'FARM_ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/batch/list.vue') },
      { path: 'trace/records', name: 'TraceRecords', meta: { title: '溯源记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/record/index.vue') },
      { path: 'trace/production', name: 'TraceProduction', meta: { title: '种植记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/production/list.vue') },
      { path: 'trace/processing', name: 'TraceProcessing', meta: { title: '加工记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/processing/list.vue') },
      { path: 'trace/storage', name: 'TraceStorage', meta: { title: '仓储记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/storage/list.vue') },
      { path: 'trace/logistics', name: 'TraceLogistics', meta: { title: '物流记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/logistics/list.vue') },
      { path: 'trace/quality', name: 'TraceQuality', meta: { title: '质检记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/quality/list.vue') },
      { path: 'trace/sales', name: 'TraceSales', meta: { title: '销售记录', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/sales/list.vue') },
      { path: 'trace/chain', name: 'TraceChain', meta: { title: '草莓全链追溯', roles: ['ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/chain/index.vue') },
      { path: 'trace/public', name: 'PublicTrace', meta: { title: '公开溯源', roles: ['CONSUMER', 'ADMIN', 'TRACE_ADMIN'] }, component: () => import('../views/trace/public.vue') },

      { path: 'agri/content', name: 'AgriContent', meta: { title: '农技内容中心', roles: knowledgeRoles }, component: () => import('../views/agri/content/index.vue') },
      { path: 'agri/content/:id', name: 'AgriContentDetail', meta: { title: '内容详情', roles: knowledgeRoles }, component: () => import('../views/agri/content/index.vue') },
      { path: 'knowledge/articles', redirect: '/agri/content?type=ARTICLE' },
      { path: 'knowledge/articles/:pathMatch(.*)', redirect: to => '/agri/content?type=ARTICLE' },
      { path: 'knowledge/videos', redirect: '/agri/content' },
      { path: 'knowledge/videos/:pathMatch(.*)', redirect: '/agri/content' },
      { path: 'knowledge/lectures', name: 'Lectures', meta: { title: '农技讲座', roles: knowledgeRoles }, component: () => import('../views/knowledge/lecture/list.vue') },
      { path: 'knowledge/lectures/create', name: 'LectureCreate', meta: { title: '发布讲座', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/knowledge/lecture/form.vue') },
      { path: 'knowledge/lectures/:id', name: 'LectureDetail', meta: { title: '讲座详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/lecture/detail.vue') },
      { path: 'knowledge/videos', name: 'Videos', meta: { title: '农技视频', roles: knowledgeRoles }, component: () => import('../views/knowledge/video/list.vue') },
      { path: 'knowledge/videos/create', name: 'VideoCreate', meta: { title: '发布视频', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/knowledge/video/form.vue') },
      { path: 'knowledge/videos/:id', name: 'VideoDetail', meta: { title: '视频详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/video/detail.vue') },
      { path: 'knowledge/pests', name: 'Pests', meta: { title: '草莓病虫害知识', roles: knowledgeRoles }, component: () => import('../views/knowledge/pest/list.vue') },
      { path: 'knowledge/categories', name: 'Categories', meta: { title: '技术分类', roles: ['ADMIN'] }, component: () => import('../views/knowledge/category/list.vue') },
      { path: 'knowledge/graph', name: 'KnowledgeGraph', meta: { title: '知识图谱', roles: knowledgeRoles }, component: () => import('../views/knowledge/Graph.vue') },
      { path: 'knowledge/search', name: 'KnowledgeSearch', meta: { title: '知识检索', roles: knowledgeRoles }, component: () => import('../views/knowledge/search.vue') },
      { path: 'knowledge/smart-question', name: 'SmartQuestionSubmit', meta: { title: '智能提问', roles: farmerRoles }, component: () => import('../views/diagnosis/SmartSubmit.vue') },
      { path: 'knowledge/questions/:id/smart', name: 'SmartQuestionDetail', meta: { title: '智能诊断详情', roles: knowledgeRoles }, component: () => import('../views/knowledge/question/smartDetail.vue') },

      { path: 'drone/device', name: 'DroneDevice', meta: { title: '无人机设备管理', roles: farmerRoles }, component: () => import('../views/drone/device/index.vue') },
      { path: 'drone/point', name: 'DronePoint', meta: { title: '温室巡检点管理', roles: farmerRoles }, component: () => import('../views/drone/point/index.vue') },
      { path: 'drone/route', name: 'DroneRoute', meta: { title: '巡检路径规划', roles: farmerRoles }, component: () => import('../views/drone/route/index.vue') },
      { path: 'drone/task', name: 'DroneTask', meta: { title: '巡检任务管理', roles: farmerRoles }, component: () => import('../views/drone/task/index.vue') },
      { path: 'drone/image', name: 'DroneImage', meta: { title: '巡检图像记录', roles: farmerRoles }, component: () => import('../views/drone/image/index.vue') },
      { path: 'drone/report', name: 'DroneReport', meta: { title: '巡检报告管理', roles: farmerRoles }, component: () => import('../views/drone/report/index.vue') },
      { path: 'drone/routeMap', name: 'DroneRouteMap', meta: { title: '路径可视化', roles: farmerRoles }, component: () => import('../views/drone/routeMap/index.vue') },
      { path: 'drone/greenhouse-simulation', name: 'GreenhouseSimulation', meta: { title: '数字孪生巡检', roles: farmerRoles }, component: () => import('../views/drone/greenhouseSimulation/index.vue') },

      { path: 'recommend', name: 'Recommend', meta: { title: '个性化推荐', roles: knowledgeRoles }, component: () => import('../views/recommend/list.vue') },
      { path: 'user-profile', name: 'UserProfile', meta: { title: '用户画像', roles: knowledgeRoles }, component: () => import('../views/user/profile.vue') },
      { path: 'diagnosis', name: 'Diagnosis', meta: { title: '病虫害诊断', roles: farmerRoles }, component: () => import('../views/diagnosis/index.vue') },

      // 讲座测验(专家)
      { path: 'quiz/manage', name: 'QuizManage', meta: { title: '讲座题目管理', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/quiz/QuizManage.vue') },
      { path: 'quiz/manage/:id', name: 'QuizEdit', meta: { title: '编辑题目', roles: ['ADMIN', 'EXPERT'] }, component: () => import('../views/quiz/QuizEdit.vue') },
      // 农户答题
      { path: 'quiz/list', name: 'QuizList', meta: { title: '知识测验', roles: ['ADMIN', 'FARMER'] }, component: () => import('../views/quiz/QuizList.vue') },
      { path: 'quiz/:id', name: 'QuizTake', meta: { title: '答题', roles: ['ADMIN', 'FARMER'] }, component: () => import('../views/quiz/QuizTake.vue') },
      // 积分
      { path: 'points/my', name: 'MyPoints', meta: { title: '我的积分', roles: ['ADMIN', 'FARMER'] }, component: () => import('../views/points/MyPoints.vue') },
      // 积分商城
      { path: 'points/mall', name: 'PointsMall', meta: { title: '积分商城', roles: ['ADMIN', 'FARMER'] }, component: () => import('../views/points/PointsMall.vue') },
      { path: 'points/mall/admin', name: 'MallAdmin', meta: { title: '商城管理', roles: ['ADMIN'] }, component: () => import('../views/points/MallAdmin.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 温室草莓平台` : '温室草莓智慧农业综合服务平台'
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

<template>
  <div class="dashboard">
    <section class="dashboard-hero" aria-labelledby="dashboard-title">
      <video
        class="hero-video"
        src="/media/strawberry-greenhouse.mp4"
        autoplay
        muted
        loop
        playsinline
        preload="metadata"
      >
        您的浏览器暂不支持视频播放。
      </video>
      <div class="hero-shade" aria-hidden="true"></div>
      <div class="hero-content">
        <h2 id="dashboard-title">{{ pageTitle }}</h2>
        <p>{{ pageSubtitle }}</p>
      </div>
    </section>

    <div class="role-cards">
      <div v-for="card in roleCards" :key="card.title" class="stat-card">
        <el-icon class="icon" :color="iconColor(card.type)">
          <component :is="iconName(card.type)" />
        </el-icon>
        <div>
          <div class="value">{{ card.value }}</div>
          <div class="label">{{ card.title }}</div>
        </div>
      </div>
    </div>

    <!-- 无人机数字孪生入口 -->
    <div class="sim-entry" @click="$router.push('/drone/greenhouse-simulation')">
      <div class="sim-entry-icon">
        <el-icon :size="32"><Monitor /></el-icon>
      </div>
      <div class="sim-entry-text">
        <div class="sim-entry-title">温室模拟巡检</div>
        <div class="sim-entry-desc">点击进入温室草莓数字孪生巡检场景</div>
      </div>
      <el-button type="success" round>进入模拟</el-button>
    </div>

    <el-card v-if="store.primaryRole === 'CONSUMER'" class="trace-query">
      <template #header><span>有机草莓公开溯源查询</span></template>
      <div class="query-line">
        <el-input v-model="batchNo" placeholder="输入生产批次号，如 B202406001" clearable @keyup.enter="loadPublicTrace" />
        <el-button type="primary" @click="loadPublicTrace">查询</el-button>
      </div>
      <el-descriptions v-if="publicTrace" :column="3" border size="small" class="trace-summary">
        <el-descriptions-item label="批次号">{{ publicTrace.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="完整度">{{ publicTrace.traceCompleteness || 0 }}%</el-descriptions-item>
        <el-descriptions-item label="生产记录">{{ publicTrace.production?.length || 0 }}</el-descriptions-item>
        <el-descriptions-item label="诊断记录">{{ publicTrace.diagnosis?.length || 0 }}</el-descriptions-item>
        <el-descriptions-item label="质检记录">{{ publicTrace.quality?.length || 0 }}</el-descriptions-item>
        <el-descriptions-item label="物流记录">{{ publicTrace.logistics?.length || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-else>
      <template #header><span>最近环境监测数据</span></template>
      <el-table :data="envData" stripe>
        <el-table-column prop="batchNo" label="批次号" width="150" />
        <el-table-column prop="landId" label="地块ID" width="90" />
        <el-table-column prop="deviceId" label="设备ID" width="90" />
        <el-table-column prop="dataValue" label="数值" />
        <el-table-column prop="landType" label="类型" width="90">
          <template #default="{row}">{{ landTypeLabel(row.landType) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </el-card>

    <!-- 草莓市场行情卡片（非消费者角色可见） -->
    <MarketPriceCard v-if="store.primaryRole !== 'CONSUMER'" />

  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store'
import { getDashboardStats, getEnvSummary, getRoleDashboard } from '../../api/farm'
import { getPublicChainTrace } from '../../api/trace'
import MarketPriceCard from './components/MarketPriceCard.vue'

const store = useUserStore()
const stats = ref({})
const roleData = ref({})
const envData = ref([])
const batchNo = ref('B202406001')
const publicTrace = ref(null)

const roleCards = computed(() => roleData.value.cards || [
  { title: '总地块数', value: stats.value.landCount || 0, type: 'land' },
  { title: '已发布知识', value: stats.value.articleCount || 0, type: 'knowledge' },
  { title: '溯源产品', value: stats.value.productCount || 0, type: 'product' },
  { title: '环境数据', value: stats.value.envDataCount || 0, type: 'env' }
])

const pageTitle = computed(() => {
  const map = {
    FARMER: '草莓种植户首页',
    FARM_ADMIN: '草莓基地管理首页',
    EXPERT: '专家工作台',
    TRACE_ADMIN: '溯源企业首页',
    CONSUMER: '消费者溯源页',
    ADMIN: '温室草莓驾驶舱'
  }
  return map[store.primaryRole] || '温室草莓驾驶舱'
})

const pageSubtitle = computed(() => {
  const map = {
    FARMER: '学习草莓种植知识，提交病虫害图片和生产问题',
    FARM_ADMIN: '掌握温室环境、设备状态、农事记录和产量信息',
    EXPERT: '处理待审核诊断和待回答问题',
    TRACE_ADMIN: '维护有机草莓批次、质检、仓储、物流和销售链路',
    CONSUMER: '查询有机草莓批次来源、质检和物流信息',
    ADMIN: '查看温室草莓生产、溯源、农技和诊断概览'
  }
  return map[store.primaryRole] || '温室草莓生产与溯源闭环概览'
})

onMounted(async () => {
  try { const r = await getDashboardStats(); stats.value = r.data || {} } catch (e) {}
  try { const r = await getRoleDashboard(); roleData.value = r.data || {} } catch (e) {}
  try { const r = await getEnvSummary(); envData.value = r.data || [] } catch (e) {}
})

async function loadPublicTrace() {
  if (!batchNo.value) {
    ElMessage.warning('请输入生产批次号')
    return
  }
  try {
    const r = await getPublicChainTrace(batchNo.value)
    publicTrace.value = r.data
  } catch (e) {
    ElMessage.error('未查询到溯源信息')
  }
}

function iconName(type) {
  const map = {
    question: 'ChatDotRound',
    answer: 'EditPen',
    article: 'Document',
    batch: 'Box',
    active: 'Operation',
    product: 'Goods',
    env: 'DataLine',
    knowledge: 'Reading',
    land: 'Grid',
    'public-trace': 'Search'
  }
  return map[type] || 'Odometer'
}

function iconColor(type) {
  const map = {
    question: '#D97706',
    answer: '#2563EB',
    article: '#1F7A4D',
    batch: '#7C3AED',
    active: '#0F766E',
    product: '#C2410C',
    env: '#B91C1C',
    knowledge: '#15803D',
    land: '#2E7D32',
    'public-trace': '#0F766E'
  }
  return map[type] || '#475467'
}

function landTypeLabel(type) {
  return { 0: '大棚', 1: '鱼塘', 2: '大田', 3: '仓库' }[type] || type
}
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: 16px; }
.dashboard-hero {
  position: relative;
  min-height: 280px;
  max-height: 360px;
  aspect-ratio: 16 / 5;
  overflow: hidden;
  border-radius: 8px;
  background: #17251d;
}
.hero-video { width: 100%; height: 100%; display: block; object-fit: cover; }
.hero-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(0deg, rgba(9, 25, 15, 0.78) 0%, rgba(9, 25, 15, 0.08) 68%);
  pointer-events: none;
}
.hero-content { position: absolute; right: 24px; bottom: 22px; left: 24px; color: #fff; }
.hero-content h2 { margin: 0; font-size: 26px; line-height: 1.3; }
.hero-content p { margin: 6px 0 0; line-height: 1.6; }
.role-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; }
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 1px 3px rgba(16,24,40,0.08);
}
.icon { font-size: 30px; flex: 0 0 auto; }
.value { font-size: 26px; font-weight: 700; color: #101828; }
.label { margin-top: 4px; color: #667085; font-size: 13px; }
.trace-query { max-width: 980px; }
.query-line { display: grid; grid-template-columns: minmax(0, 1fr) auto; gap: 12px; }
.trace-summary { margin-top: 16px; }

.sim-entry {
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  border: 2px dashed #66bb6a;
  border-radius: 12px;
  padding: 18px 22px;
  cursor: pointer;
  transition: all 0.25s;
  max-width: 700px;
}
.sim-entry:hover {
  background: linear-gradient(135deg, #c8e6c9 0%, #a5d6a7 100%);
  border-color: #43a047;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.2);
}
.sim-entry-icon {
  background: #fff;
  border-radius: 12px;
  padding: 10px;
  color: #2e7d32;
}
.sim-entry-text {
  flex: 1;
}
.sim-entry-title {
  font-size: 17px;
  font-weight: 700;
  color: #2e7d32;
}
.sim-entry-desc {
  font-size: 13px;
  color: #558b2f;
  margin-top: 2px;
}

@media (max-width: 768px) {
  .dashboard-hero { min-height: 220px; aspect-ratio: 4 / 3; }
  .hero-content { right: 16px; bottom: 16px; left: 16px; }
  .hero-content h2 { font-size: 22px; }
}
</style>

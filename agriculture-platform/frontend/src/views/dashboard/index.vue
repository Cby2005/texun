<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>{{ pageTitle }}</h2>
      <p>{{ pageSubtitle }}</p>
    </div>

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

    <el-card v-if="store.primaryRole === 'CONSUMER'" class="trace-query">
      <template #header><span>公开溯源查询</span></template>
      <div class="query-line">
        <el-input v-model="batchNo" placeholder="输入生产批次号" clearable @keyup.enter="loadPublicTrace" />
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
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store'
import { getDashboardStats, getEnvSummary, getRoleDashboard } from '../../api/farm'
import { getPublicChainTrace } from '../../api/trace'

const store = useUserStore()
const stats = ref({})
const roleData = ref({})
const envData = ref([])
const batchNo = ref('')
const publicTrace = ref(null)

const roleCards = computed(() => roleData.value.cards || [
  { title: '总地块数', value: stats.value.landCount || 0, type: 'land' },
  { title: '已发布知识', value: stats.value.articleCount || 0, type: 'knowledge' },
  { title: '溯源产品', value: stats.value.productCount || 0, type: 'product' },
  { title: '环境数据', value: stats.value.envDataCount || 0, type: 'env' }
])

const pageTitle = computed(() => {
  const map = {
    FARMER: '农户首页',
    FARM_ADMIN: '农场管理首页',
    EXPERT: '专家工作台',
    TRACE_ADMIN: '溯源企业首页',
    CONSUMER: '消费者溯源页',
    ADMIN: '管理员驾驶舱'
  }
  return map[store.primaryRole] || '首页驾驶舱'
})

const pageSubtitle = computed(() => {
  const map = {
    FARMER: '关注待处理问题、环境异常和推荐知识',
    EXPERT: '处理待审核诊断和待回答问题',
    TRACE_ADMIN: '查看批次、溯源完整度和产品状态',
    CONSUMER: '查询公开生产批次溯源信息'
  }
  return map[store.primaryRole] || '智慧农业综合服务平台概览'
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
</style>

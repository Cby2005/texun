<template>
  <div>
    <div class="page-header"><h2>首页驾驶舱</h2><p>智慧农业综合服务平台概览</p></div>
    <div class="stat-cards">
      <div class="stat-card"><el-icon class="icon" color="#2E7D32"><Grid /></el-icon><div><div class="value">{{ stats.landCount || 0 }}</div><div class="label">总地块数</div></div></div>
      <div class="stat-card"><el-icon class="icon" color="#1565C0"><Document /></el-icon><div><div class="value">{{ stats.articleCount || 0 }}</div><div class="label">已发布文章</div></div></div>
      <div class="stat-card"><el-icon class="icon" color="#E65100"><Goods /></el-icon><div><div class="value">{{ stats.productCount || 0 }}</div><div class="label">溯源产品</div></div></div>
      <div class="stat-card"><el-icon class="icon" color="#6A1B9A"><DataLine /></el-icon><div><div class="value">{{ stats.envDataCount || 0 }}</div><div class="label">环境数据</div></div></div>
    </div>
    <el-card>
      <template #header><span>最近环境监测数据</span></template>
      <el-table :data="envData" stripe>
        <el-table-column prop="landId" label="地块ID" width="80" />
        <el-table-column prop="deviceId" label="设备ID" width="80" />
        <el-table-column prop="dataValue" label="数值" />
        <el-table-column prop="landType" label="类型" width="80"><template #default="{row}">{{ {0:'大棚',1:'鱼塘',2:'大田',3:'仓库'}[row.landType] || row.landType }}</template></el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats, getEnvSummary } from '../../api/farm'

const stats = ref({})
const envData = ref([])

onMounted(async () => {
  try { const r = await getDashboardStats(); stats.value = r.data } catch (e) {}
  try { const r = await getEnvSummary(); envData.value = r.data } catch (e) {}
})
</script>

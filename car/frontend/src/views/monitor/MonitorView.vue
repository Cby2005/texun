<template>
  <div class="monitor-view">
    <el-card shadow="hover">
      <template #header>作物监测数据</template>
      <el-form :inline="true" class="search-form">
        <el-form-item label="地块ID"><el-input v-model.number="query.plotId" placeholder="地块ID" /></el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="query.dataType" clearable placeholder="全部">
            <el-option label="土壤湿度" value="SOIL_MOISTURE" /><el-option label="土壤温度" value="SOIL_TEMP" />
            <el-option label="光照强度" value="LIGHT" /><el-option label="空气温度" value="AIR_TEMP" />
            <el-option label="空气湿度" value="AIR_HUMIDITY" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="sensorId" label="传感器ID" width="100" />
        <el-table-column prop="plotId" label="地块ID" width="80" />
        <el-table-column prop="dataType" label="数据类型" width="120" />
        <el-table-column prop="value" label="数值" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="reportTime" label="采集时间" />
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
        layout="total, sizes, prev, pager, next" @change="loadData" style="margin-top: 16px; justify-content: flex-end" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { listMonitorData } from '@/api/monitor'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, plotId: '', dataType: '' })

async function loadData() {
  loading.value = true
  try { const res: any = await listMonitorData(query); tableData.value = res.data.records; total.value = res.data.total } finally { loading.value = false }
}
onMounted(loadData)
</script>

<style scoped>
.search-form { margin-bottom: 16px; }
</style>

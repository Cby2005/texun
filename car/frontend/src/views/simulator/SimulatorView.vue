<template>
  <div class="simulator-view">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>无人车模拟器控制</span>
          <div>
            <el-button type="success" @click="handleStartAll" :loading="batchLoading">启动全部</el-button>
            <el-button type="danger" @click="handleStopAll" :loading="batchLoading">停止全部</el-button>
            <el-button @click="loadData"><el-icon><Refresh /></el-icon> 刷新</el-button>
          </div>
        </div>
      </template>

      <el-alert :title="`运行中: ${runningCount} / ${vehicles.length} 台`" type="info" show-icon :closable="false" style="margin-bottom: 16px" />

      <el-table :data="vehicles" v-loading="loading" stripe>
        <el-table-column prop="vehicleId" label="ID" width="60" />
        <el-table-column prop="deviceCode" label="设备编码" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="位置" width="200">
          <template #default="{ row }">
            {{ row.latitude?.toFixed(6) }}, {{ row.longitude?.toFixed(6) }}
          </template>
        </el-table-column>
        <el-table-column prop="speed" label="速度(km/h)" width="100">
          <template #default="{ row }">{{ row.speed?.toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="batteryLevel" label="电量" width="150">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.batteryLevel || 0)" :status="row.batteryLevel < 20 ? 'exception' : row.batteryLevel < 50 ? 'warning' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="taskProgress" label="任务进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.taskProgress || 0)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleStart(row)" v-if="!row.running">启动</el-button>
            <el-button size="small" type="warning" @click="handleStop(row)" v-else>停止</el-button>
            <el-button size="small" type="primary" @click="handleStartTask(row)" v-if="row.running && row.status !== 'WORKING'">开始作业</el-button>
            <el-button size="small" type="danger" @click="handleEmergencyStop(row)" v-if="row.running">急停</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSimulatorVehicles, startSimulatorVehicle, stopSimulatorVehicle,
  startSimulatorTask, emergencyStopSimulator, startAllSimulators, stopAllSimulators
} from '@/api/simulator'

const loading = ref(false)
const batchLoading = ref(false)
const vehicles = ref<any[]>([])

const runningCount = computed(() => vehicles.value.filter(v => v.running).length)

function statusType(status: string) {
  const map: Record<string, string> = { IDLE: 'info', ONLINE: 'success', WORKING: '', OFFLINE: 'warning', FAULT: 'danger' }
  return (map[status] || 'info') as any
}

function statusLabel(status: string) {
  const map: Record<string, string> = { IDLE: '空闲', ONLINE: '在线', WORKING: '作业中', OFFLINE: '离线', FAULT: '故障' }
  return map[status] || status
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getSimulatorVehicles()
    vehicles.value = res.data || []
  } finally { loading.value = false }
}

async function handleStart(row: any) {
  await startSimulatorVehicle(row.vehicleId)
  ElMessage.success(`${row.deviceCode} 已启动`)
  loadData()
}

async function handleStop(row: any) {
  await stopSimulatorVehicle(row.vehicleId)
  ElMessage.success(`${row.deviceCode} 已停止`)
  loadData()
}

async function handleStartTask(row: any) {
  await startSimulatorTask(row.vehicleId, 39.91 + Math.random() * 0.01, 116.40 + Math.random() * 0.01)
  ElMessage.success(`${row.deviceCode} 开始作业`)
  loadData()
}

async function handleEmergencyStop(row: any) {
  await ElMessageBox.confirm(`确认对 ${row.deviceCode} 紧急停车？`, '警告', { type: 'warning' })
  await emergencyStopSimulator(row.vehicleId)
  ElMessage.success(`${row.deviceCode} 已紧急停车`)
  loadData()
}

async function handleStartAll() {
  batchLoading.value = true
  try { await startAllSimulators(); ElMessage.success('全部启动成功'); loadData() } finally { batchLoading.value = false }
}

async function handleStopAll() {
  await ElMessageBox.confirm('确认停止所有模拟器？', '警告', { type: 'warning' })
  batchLoading.value = true
  try { await stopAllSimulators(); ElMessage.success('全部停止成功'); loadData() } finally { batchLoading.value = false }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>

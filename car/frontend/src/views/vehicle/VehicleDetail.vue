<template>
  <div class="vehicle-detail" v-loading="loading">
    <el-page-header @back="$router.back()" :content="device?.deviceName || '设备详情'" />

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>设备信息</template>
          <el-descriptions :column="1" border v-if="device">
            <el-descriptions-item label="设备编码">{{ device.deviceCode }}</el-descriptions-item>
            <el-descriptions-item label="设备名称">{{ device.deviceName }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="statusType(device.status)">{{ statusLabel(device.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="固件版本">{{ device.firmwareVersion || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最后上线">{{ device.lastOnlineTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" style="margin-top: 16px">
          <template #header>远程控制</template>
          <el-space wrap>
            <el-button type="success" @click="handleCommand('online')">上线</el-button>
            <el-button type="warning" @click="handleCommand('offline')">离线</el-button>
            <el-button type="primary" @click="handleCommand('start-work')">开始作业</el-button>
            <el-button @click="handleCommand('stop-work')">停止作业</el-button>
            <el-button @click="handleCommand('return')">返航</el-button>
            <el-button type="danger" @click="handleCommand('emergency-stop')">紧急停车</el-button>
          </el-space>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between">
              <span>实时遥测数据</span>
              <el-tag :type="wsConnected ? 'success' : 'danger'" size="small">
                {{ wsConnected ? '已连接' : '未连接' }}
              </el-tag>
            </div>
          </template>
          <el-descriptions :column="3" border v-if="telemetry">
            <el-descriptions-item label="纬度">{{ telemetry.latitude?.toFixed(6) }}</el-descriptions-item>
            <el-descriptions-item label="经度">{{ telemetry.longitude?.toFixed(6) }}</el-descriptions-item>
            <el-descriptions-item label="海拔">{{ telemetry.altitude?.toFixed(1) }}m</el-descriptions-item>
            <el-descriptions-item label="速度">{{ telemetry.speed?.toFixed(1) }}km/h</el-descriptions-item>
            <el-descriptions-item label="方向">{{ telemetry.direction?.toFixed(1) }}°</el-descriptions-item>
            <el-descriptions-item label="电量">{{ telemetry.batteryLevel?.toFixed(1) }}%</el-descriptions-item>
            <el-descriptions-item label="CPU">{{ telemetry.cpuUsage?.toFixed(1) }}%</el-descriptions-item>
            <el-descriptions-item label="内存">{{ telemetry.memoryUsage?.toFixed(1) }}%</el-descriptions-item>
            <el-descriptions-item label="任务进度">{{ telemetry.taskProgress?.toFixed(1) }}%</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="暂无遥测数据" />
        </el-card>

        <el-card shadow="hover" style="margin-top: 16px">
          <template #header>位置地图</template>
          <div ref="mapRef" style="height: 400px; background: #f0f0f0; border-radius: 4px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDevice, onlineDevice, offlineDevice, startWork, stopWork, emergencyStop, returnToBase } from '@/api/vehicle'
import { WebSocketClient } from '@/utils/websocket'

const route = useRoute()
const deviceId = Number(route.params.id)
const loading = ref(false)
const device = ref<any>(null)
const telemetry = ref<any>(null)
const wsConnected = ref(false)
const mapRef = ref<HTMLElement>()
let wsClient: WebSocketClient | null = null

function statusType(status: string) {
  const map: Record<string, string> = { IDLE: 'info', ONLINE: 'success', WORKING: '', OFFLINE: 'warning', FAULT: 'danger', MAINTENANCE: 'warning' }
  return (map[status] || 'info') as any
}

function statusLabel(status: string) {
  const map: Record<string, string> = { IDLE: '空闲', ONLINE: '在线', WORKING: '作业中', OFFLINE: '离线', FAULT: '故障', MAINTENANCE: '维护', SCRAPPED: '报废' }
  return map[status] || status
}

async function loadDevice() {
  loading.value = true
  try {
    const res: any = await getDevice(deviceId)
    device.value = res.data
  } finally {
    loading.value = false
  }
}

async function handleCommand(cmd: string) {
  try {
    switch (cmd) {
      case 'online': await onlineDevice(deviceId); break
      case 'offline': await offlineDevice(deviceId); break
      case 'start-work': await startWork(deviceId); break
      case 'stop-work': await stopWork(deviceId); break
      case 'return': await returnToBase(deviceId); break
      case 'emergency-stop': await emergencyStop(deviceId); break
    }
    ElMessage.success('指令发送成功')
    loadDevice()
  } catch (e: any) {
    ElMessage.error(e.message || '指令发送失败')
  }
}

function connectWebSocket() {
  const wsUrl = `ws://${window.location.hostname}:8300/ws/telemetry`
  wsClient = new WebSocketClient(wsUrl)
  wsClient.on('_connected', () => {
    wsConnected.value = true
    wsClient?.subscribe(deviceId)
  })
  wsClient.on('_disconnected', () => { wsConnected.value = false })
  wsClient.on('telemetry', (data: any) => {
    if (data.vehicleId === deviceId) {
      telemetry.value = data.data
    }
  })
  wsClient.connect()
}

onMounted(() => {
  loadDevice()
  connectWebSocket()
})

onUnmounted(() => {
  wsClient?.disconnect()
})
</script>

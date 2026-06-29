<template>
  <div class="map-page">
    <el-card class="control-card" shadow="hover">
      <el-row :gutter="12" align="middle">
        <el-col :xs="24" :lg="18">
          <span class="label">巡检操作</span>
          <div class="btn-group">
            <el-button type="success" icon="MapLocation" :disabled="!mapReady || enterprisesLoaded" @click="loadEnterprises">
              加载基地
            </el-button>
            <el-button type="primary" icon="LocationFilled" :disabled="!mapReady || pointsLoaded" @click="loadPoints">
              加载巡检点
            </el-button>
            <el-button type="warning" icon="Share" :disabled="!mapReady || !pointsLoaded || routeDrawn" @click="genRoute">
              生成路径
            </el-button>
            <el-button
              type="success"
              icon="Promotion"
              :disabled="!mapReady || !routeDrawn || (inspecting && !paused)"
              :loading="inspecting && !paused"
              @click="startInspection"
            >
              {{ paused ? '继续巡检' : '开始巡检' }}
            </el-button>
            <el-button type="warning" icon="VideoPause" :disabled="!inspecting || paused" @click="pauseInspection">
              暂停巡检
            </el-button>
            <el-button icon="RefreshLeft" :disabled="!mapReady || !hasLayers" @click="resetAll">重置巡检</el-button>
            <el-button type="danger" icon="Delete" :disabled="!mapReady || !hasLayers" @click="clearLayers">清除图层</el-button>
          </div>
        </el-col>
        <el-col :xs="24" :lg="6">
          <div class="status-bar">
            <el-tag :type="statusTagType" size="large" effect="dark">{{ statusText }}</el-tag>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="12" class="main-row">
      <el-col :xs="24" :lg="17">
        <el-card shadow="hover" class="map-card">
          <el-alert v-if="mapError" :title="mapError" type="error" show-icon :closable="false" class="map-error" />
          <div id="route-map" class="map-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="7">
        <el-card shadow="hover" class="info-card">
          <template #header><span class="card-title">巡检状态监控</span></template>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="任务状态">
              <el-tag :type="statusTagType" size="small" effect="dark">{{ statusText }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前位置">
              <span v-if="currentPoint" class="active-point">
                <span class="dot" :style="{ background: pointColor(currentPoint.pointType) }"></span>
                {{ currentPoint.pointName }}
              </span>
              <span v-else-if="routeDrawn">等待起飞...</span>
              <span v-else class="text-muted">未开始</span>
            </el-descriptions-item>
            <el-descriptions-item label="点位类型">
              <el-tag v-if="currentPoint" :type="pointTagType(currentPoint.pointType)" size="small">
                {{ pointTypeLabel(currentPoint.pointType) }}
              </el-tag>
              <span v-else class="text-muted">-</span>
            </el-descriptions-item>
            <el-descriptions-item label="巡检区域">{{ currentPoint?.areaName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前坐标">
              <span v-if="currentPoint" class="coord-text">{{ currentPoint.longitude }}, {{ currentPoint.latitude }}</span>
              <span v-else class="text-muted">-</span>
            </el-descriptions-item>
            <el-descriptions-item label="巡检进度">
              <el-progress
                v-if="routeDrawn"
                :percentage="inspectionProgress"
                :color="progressColor"
                :stroke-width="16"
                :text-inside="true"
              />
              <span v-else class="text-muted">-</span>
            </el-descriptions-item>
            <el-descriptions-item label="航点总数">{{ inspectionPoints.length }}</el-descriptions-item>
            <el-descriptions-item label="总距离">{{ routeDrawn ? `${totalDistance}m` : '-' }}</el-descriptions-item>
            <el-descriptions-item label="预计耗时">{{ routeDrawn ? `${estimatedTime}秒` : '-' }}</el-descriptions-item>
            <el-descriptions-item label="已用时间">{{ inspecting || paused || finished ? `${elapsedSeconds}秒` : '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card v-if="inspectionPoints.length" shadow="hover" class="info-card point-card">
          <template #header><span class="card-title">航点顺序</span></template>
          <el-table
            :data="inspectionPoints"
            size="small"
            max-height="360"
            stripe
            highlight-current-row
            :row-class-name="tableRowClass"
          >
            <el-table-column type="index" label="序号" width="54" align="center" />
            <el-table-column prop="pointName" label="名称" min-width="100" />
            <el-table-column label="类型" width="82" align="center">
              <template #default="{ row }">
                <el-tag :type="pointTagType(row.pointType)" size="small">{{ pointTypeLabel(row.pointType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="areaName" label="区域" min-width="110" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'

const MAP_CENTER = [113.809058, 34.136323]
const DEFAULT_POINTS = [
  { id: 1, pointName: '起飞点', pointType: 'START', areaName: '起飞区', longitude: 113.809058, latitude: 34.136323, altitude: 1.5, remark: '无人机起飞与返航位置' },
  { id: 2, pointName: 'A区巡检点', pointType: 'NORMAL', areaName: 'A区草莓种植区', longitude: 113.8091, latitude: 34.13642, altitude: 1.5, remark: '日常生长巡检点' },
  { id: 3, pointName: 'B区巡检点', pointType: 'NORMAL', areaName: 'B区草莓种植区', longitude: 113.80925, latitude: 34.1365, altitude: 1.5, remark: '草莓长势监测点' },
  { id: 4, pointName: 'C区异常复核点', pointType: 'ABNORMAL', areaName: 'C区异常复核区', longitude: 113.80936, latitude: 34.13638, altitude: 1.5, remark: '环境异常或病害复核点' },
  { id: 5, pointName: '返航点', pointType: 'END', areaName: '起飞区', longitude: 113.809058, latitude: 34.136323, altitude: 1.5, remark: '无人机返航位置' }
]

const mapReady = ref(false)
const mapError = ref('')
const enterprisesLoaded = ref(false)
const pointsLoaded = ref(false)
const routeDrawn = ref(false)
const inspecting = ref(false)
const paused = ref(false)
const finished = ref(false)
const inspectionPoints = ref([])
const currentIndex = ref(-1)
const elapsedSeconds = ref(0)
const totalDistance = ref(0)
const estimatedTime = ref(0)

let map
let AMap
let enterpriseMarkers = []
let pointMarkers = []
let routeLine
let droneMarker
let currentInfoWindow
let inspectionTimer
let elapsedTimer

const hasLayers = computed(() => enterprisesLoaded.value || pointsLoaded.value || routeDrawn.value)
const currentPoint = computed(() => inspectionPoints.value[currentIndex.value] || null)
const statusText = computed(() => {
  if (finished.value) return '巡检完成'
  if (paused.value) return '已暂停'
  if (inspecting.value) return '巡检中...'
  if (routeDrawn.value) return '路径已就绪'
  if (pointsLoaded.value) return '点位已加载'
  if (enterprisesLoaded.value) return '基地已加载'
  return mapReady.value ? '就绪' : '地图加载中'
})
const statusTagType = computed(() => {
  if (finished.value) return 'success'
  if (paused.value || inspecting.value) return 'warning'
  if (routeDrawn.value) return 'primary'
  if (pointsLoaded.value || enterprisesLoaded.value) return ''
  return 'info'
})
const inspectionProgress = computed(() => {
  if (!inspectionPoints.value.length) return 0
  if (finished.value) return 100
  if (currentIndex.value < 0) return 0
  return Math.round((currentIndex.value / (inspectionPoints.value.length - 1)) * 100)
})
const progressColor = computed(() => finished.value ? '#67c23a' : inspecting.value || paused.value ? '#e6a23c' : '#409eff')

function pointColor(type) {
  return { START: '#67c23a', NORMAL: '#409eff', ABNORMAL: '#f56c6c', END: '#909399' }[type] || '#409eff'
}

function pointTagType(type) {
  return { START: 'success', NORMAL: '', ABNORMAL: 'danger', END: 'info' }[type] || ''
}

function pointTypeLabel(type) {
  return { START: '起飞点', NORMAL: '巡检点', ABNORMAL: '异常复核点', END: '返航点' }[type] || type
}

function latLng(point) {
  return [point.longitude, point.latitude]
}

function createInfoContent(point) {
  return `<div class="point-popup">
    <strong>${point.pointName}</strong><br>
    类型：${pointTypeLabel(point.pointType)}<br>
    区域：${point.areaName}<br>
    经度：${point.longitude}<br>
    纬度：${point.latitude}<br>
    高度：${point.altitude}m<br>
    备注：${point.remark}
  </div>`
}

function haversineDistance(lng1, lat1, lng2, lat2) {
  const radius = 6371000
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const value = Math.sin(dLat / 2) ** 2
    + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLng / 2) ** 2
  return radius * 2 * Math.atan2(Math.sqrt(value), Math.sqrt(1 - value))
}

async function initMap() {
  try {
    const key = import.meta.env.VITE_AMAP_KEY
    const securityJsCode = import.meta.env.VITE_AMAP_SECURITY_CODE
    if (!key || !securityJsCode) throw new Error('未配置高德地图 Key 或安全密钥')

    window._AMapSecurityConfig = { securityJsCode }
    AMap = await AMapLoader.load({
      key,
      version: '2.0',
      plugins: ['AMap.Scale', 'AMap.ToolBar']
    })

    map = new AMap.Map('route-map', {
      center: MAP_CENTER,
      zoom: 18,
      viewMode: '2D',
      resizeEnable: true,
      zooms: [14, 20]
    })
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar({ position: { top: '10px', right: '10px' } }))
    mapReady.value = true
  } catch (error) {
    mapError.value = `地图初始化失败：${error.message || error}`
  }
}

function loadEnterprises() {
  if (!map) return
  clearEnterpriseMarkers()
  import('../../../api/farm').then(({ getAllEnterprises }) => {
    getAllEnterprises().then(r => {
      const enterprises = (r.data || []).filter(e => e.lng && e.lat)
      if (enterprises.length) {
        renderEnterprises(enterprises)
      } else {
        ElMessage.warning('暂无基地数据，请先在基地管理中新建基地并填写经纬度')
      }
    }).catch(() => {
      ElMessage.warning('暂无基地数据，请先在基地管理中新建基地并填写经纬度')
    })
  })
}

function renderEnterprises(enterprises) {
  if (!enterprises.length) {
    ElMessage.warning('暂无基地数据')
    return
  }
  enterprises.forEach(ent => {
    const lng = Number(ent.lng)
    const lat = Number(ent.lat)
    if (!lng || !lat) return
    // 基地区域圆（模拟基地范围）
    const circle = new AMap.Circle({
      center: [lng, lat],
      radius: 80,
      strokeColor: '#2e7d32',
      strokeWeight: 2,
      strokeStyle: 'dashed',
      fillColor: '#a5d6a7',
      fillOpacity: 0.25,
      zIndex: 10
    })
    map.add(circle)
    enterpriseMarkers.push(circle)
    // 基地名称标记
    const label = new AMap.Marker({
      position: [lng, lat],
      content: `<div style="background:#2e7d32;color:#fff;padding:2px 8px;border-radius:4px;font-size:12px;white-space:nowrap;box-shadow:0 2px 8px rgba(0,0,0,0.3)">${ent.baseName||ent.name}</div>`,
      offset: new AMap.Pixel(-40, -30),
      zIndex: 90
    })
    map.add(label)
    enterpriseMarkers.push(label)
    // 基地中心点
    const marker = new AMap.Marker({
      position: [lng, lat],
      content: '<div style="width:16px;height:16px;border-radius:50%;background:#2e7d32;border:3px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3)"></div>',
      offset: new AMap.Pixel(-8, -8),
      zIndex: 100
    })
    marker.on('click', () => {
      const info = `<strong>${ent.baseName||ent.name}</strong><br>${ent.detailAddress||ent.address||''}<br>经度：${lng}<br>纬度：${lat}`
      openInfo(info, [lng, lat], -28)
    })
    map.add(marker)
    enterpriseMarkers.push(marker)
  })
  map.setFitView(enterpriseMarkers.filter(m => m.CLASS_NAME === 'Overlay.Circle'), false, [50, 50, 50, 50])
  enterprisesLoaded.value = true
  ElMessage.success(`已加载 ${enterprises.length} 个基地`)
}

function clearEnterpriseMarkers() {
  enterpriseMarkers.forEach(m => map?.remove(m))
  enterpriseMarkers = []
}

function loadPoints() {
  if (!map) return
  clearPointMarkers()
  inspectionPoints.value = DEFAULT_POINTS.map(point => ({ ...point }))
  pointMarkers = inspectionPoints.value.map(point => {
    const marker = new AMap.Marker({
      position: latLng(point),
      content: `<div class="point-marker" style="background:${pointColor(point.pointType)}" title="${point.pointName}"></div>`,
      offset: new AMap.Pixel(-11, -11),
      zIndex: 100
    })
    marker.on('click', () => openInfo(createInfoContent(point), latLng(point), -28))
    map.add(marker)
    return marker
  })
  map.setFitView(pointMarkers, false, [60, 60, 60, 60], 19)
  pointsLoaded.value = true
  routeDrawn.value = false
  ElMessage.success(`成功加载 ${inspectionPoints.value.length} 个巡检点`)
}

function genRoute() {
  if (!map || !inspectionPoints.value.length) return
  removeLayer(routeLine)
  routeLine = new AMap.Polyline({
    path: inspectionPoints.value.map(latLng),
    strokeColor: '#1976d2',
    strokeWeight: 5,
    strokeOpacity: 0.85,
    showDir: true,
    dirColor: '#fff',
    zIndex: 50
  })
  map.add(routeLine)
  map.setFitView([routeLine], false, [70, 70, 70, 70], 19)

  let distance = 0
  for (let index = 1; index < inspectionPoints.value.length; index++) {
    const previous = inspectionPoints.value[index - 1]
    const current = inspectionPoints.value[index]
    distance += haversineDistance(previous.longitude, previous.latitude, current.longitude, current.latitude)
  }
  totalDistance.value = Math.round(distance)
  estimatedTime.value = Math.round(distance / 1.5)
  routeDrawn.value = true
  finished.value = false
  ElMessage.success(`路径生成成功，总距离 ${totalDistance.value}m`)
}

function startInspection() {
  if (!map || !inspectionPoints.value.length) return
  if (paused.value) {
    paused.value = false
    inspecting.value = true
    startElapsedTimer()
    scheduleNext(currentIndex.value + 1)
    ElMessage.success('巡检已继续')
    return
  }

  stopTimers()
  removeLayer(droneMarker)
  inspecting.value = true
  paused.value = false
  finished.value = false
  elapsedSeconds.value = 0
  currentIndex.value = 0

  const first = inspectionPoints.value[0]
  droneMarker = new AMap.Marker({
    position: latLng(first),
    content: '<div class="drone-marker"><span>✈</span></div>',
    offset: new AMap.Pixel(-18, -18),
    zIndex: 200
  })
  map.add(droneMarker)
  openInfo(`<strong>${first.pointName}</strong><br>${pointTypeLabel(first.pointType)}<br>正在巡检...`, latLng(first), -38)
  startElapsedTimer()
  scheduleNext(1)
}

function scheduleNext(index) {
  inspectionTimer = setTimeout(() => {
    if (paused.value || !inspecting.value) return
    if (index >= inspectionPoints.value.length) {
      finishInspection()
      return
    }
    const point = inspectionPoints.value[index]
    currentIndex.value = index
    droneMarker.setPosition(latLng(point))
    openInfo(`<strong>${point.pointName}</strong><br>${pointTypeLabel(point.pointType)}<br>巡检进行中...`, latLng(point), -38)
    scheduleNext(index + 1)
  }, 1000)
}

function finishInspection() {
  stopTimers()
  const last = inspectionPoints.value.at(-1)
  droneMarker.setPosition(latLng(last))
  openInfo('<strong>巡检完成</strong><br>所有航点已巡检完毕', latLng(last), -38)
  currentIndex.value = inspectionPoints.value.length - 1
  inspecting.value = false
  paused.value = false
  finished.value = true
  ElMessage.success('巡检任务模拟完成')
}

function pauseInspection() {
  paused.value = true
  clearTimeout(inspectionTimer)
  clearInterval(elapsedTimer)
  inspectionTimer = undefined
  elapsedTimer = undefined
  ElMessage.warning('巡检已暂停')
}

function startElapsedTimer() {
  clearInterval(elapsedTimer)
  elapsedTimer = setInterval(() => { elapsedSeconds.value += 1 }, 1000)
}

function stopTimers() {
  clearTimeout(inspectionTimer)
  clearInterval(elapsedTimer)
  inspectionTimer = undefined
  elapsedTimer = undefined
}

function removeLayer(layer) {
  if (map && layer) map.remove(layer)
}

function openInfo(content, position, offsetY = -20) {
  currentInfoWindow?.close()
  currentInfoWindow = new AMap.InfoWindow({
    content,
    offset: new AMap.Pixel(0, offsetY)
  })
  currentInfoWindow.open(map, position)
}

function clearPointMarkers() {
  pointMarkers.forEach(removeLayer)
  pointMarkers = []
}

function resetState() {
  enterprisesLoaded.value = false
  pointsLoaded.value = false
  routeDrawn.value = false
  inspecting.value = false
  paused.value = false
  finished.value = false
  inspectionPoints.value = []
  currentIndex.value = -1
  elapsedSeconds.value = 0
  totalDistance.value = 0
  estimatedTime.value = 0
}

function clearLayers(showMessage = true) {
  stopTimers()
  removeLayer(droneMarker)
  removeLayer(routeLine)
  clearEnterpriseMarkers()
  clearPointMarkers()
  droneMarker = undefined
  routeLine = undefined
  currentInfoWindow?.close()
  currentInfoWindow = undefined
  resetState()
  if (showMessage) ElMessage.info('所有图层已清除')
}

function resetAll() {
  clearLayers(false)
  map.setZoomAndCenter(18, MAP_CENTER)
  ElMessage.info('巡检已重置')
}

function tableRowClass({ rowIndex }) {
  if (currentIndex.value === rowIndex) return 'current-row'
  if (currentIndex.value > rowIndex) return 'done-row'
  return ''
}

onMounted(async () => {
  await nextTick()
  initMap()
})

onUnmounted(() => {
  stopTimers()
  currentInfoWindow?.close()
  map?.destroy()
  map = undefined
  AMap = undefined
})
</script>

<style scoped>
.map-page {
  min-height: calc(100vh - 100px);
  padding: 4px;
  background: #f0f7f0;
}

.control-card {
  margin-bottom: 10px;
  border: 1px solid #c8e6c9;
}

.label {
  display: block;
  margin-bottom: 6px;
  color: #2d8c2d;
  font-size: 12px;
  font-weight: 700;
}

.btn-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 40px;
}

.main-row {
  margin-top: 8px;
}

.map-card,
.info-card {
  border: 1px solid #c8e6c9;
}

.map-container {
  width: 100%;
  height: 650px;
  border-radius: 4px;
  background: #e8eee8;
  z-index: 0;
}

.map-error {
  margin-bottom: 10px;
}

.point-card {
  margin-top: 12px;
}

.card-title {
  color: #2d8c2d;
  font-weight: 700;
}

.active-point {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #e6a23c;
  font-weight: 700;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  animation: pulse 1s infinite;
}

.coord-text {
  color: #666;
  font-family: monospace;
  font-size: 12px;
}

.text-muted {
  color: #bbb;
}

:deep(.el-card__header) {
  padding: 10px 16px;
  border-bottom: 1px solid #c8e6c9;
  background: #f6fdf6;
}

:deep(.point-marker) {
  width: 22px;
  height: 22px;
  box-sizing: border-box;
  border: 3px solid #fff;
  border-radius: 50%;
  box-shadow: 0 2px 7px rgb(0 0 0 / 35%);
}

:deep(.drone-marker) {
  display: grid;
  place-items: center;
  width: 36px !important;
  height: 36px !important;
  border: 2px solid #fff;
  border-radius: 50%;
  background: #e6a23c;
  color: #fff;
  box-shadow: 0 2px 8px rgb(0 0 0 / 35%);
  font-size: 23px;
  line-height: 1;
}

:deep(.point-popup) {
  min-width: 180px;
  font-size: 13px;
  line-height: 1.7;
}

:deep(.current-row) {
  background: #fef3e6 !important;
}

:deep(.done-row) {
  background: #f0fdf0 !important;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.3); }
}

@media (max-width: 1199px) {
  .status-bar {
    justify-content: flex-start;
    margin-top: 10px;
  }

  .info-card {
    margin-top: 12px;
  }

  .map-container {
    height: 520px;
  }
}
</style>

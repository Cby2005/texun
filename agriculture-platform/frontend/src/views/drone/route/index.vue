<template>
  <div class="route-page">
    <div class="page-header">
      <h2>巡检路径规划</h2>
      <p class="page-desc">基于温室地块、巡检点和异常植株状态生成无人机巡检路线，并同步到数字孪生巡检页面执行。</p>
    </div>

    <div class="route-layout">
      <!-- ===== 左侧：参数面板 ===== -->
      <div class="left-panel">
        <el-card shadow="never" class="param-card">
          <template #header><span class="card-title">路径生成参数</span></template>

          <div class="param-item">
            <label>所属基地 <span class="required">*</span></label>
            <el-select v-model="form.baseId" placeholder="选择基地" class="full-w" clearable @change="onBaseChange">
              <el-option v-for="b in bases" :key="b.id" :label="b.baseName" :value="b.id" />
            </el-select>
          </div>

          <div class="param-item">
            <label>所属温室 <span class="required">*</span></label>
            <el-select v-model="form.greenhouseId" placeholder="选择温室" class="full-w" clearable @change="onGreenhouseChange">
              <el-option v-for="g in greenhouses" :key="g.id" :label="g.displayName || g.landName || g.name || ('温室 #' + g.id)" :value="g.id" />
            </el-select>
            <span v-if="greenhouses.length === 0" class="hint-text">暂无温室数据</span>
          </div>

          <div class="param-item">
            <label>路径名称</label>
            <el-input v-model="form.routeName" placeholder="如：A区异常植株复查路线" />
          </div>

          <div class="param-item">
            <label>巡检模式</label>
            <el-select v-model="form.routeMode" class="full-w">
              <el-option v-for="m in routeModes" :key="m.value" :label="m.label" :value="m.value" />
            </el-select>
          </div>

          <div class="param-item">
            <label>巡检范围</label>
            <el-select v-model="form.scope" class="full-w">
              <el-option v-for="s in scopes" :key="s.value" :label="s.label" :value="s.value" />
            </el-select>
          </div>

          <div class="param-item">
            <label>生成策略</label>
            <el-radio-group v-model="form.routeStrategy">
              <el-radio v-for="st in strategies" :key="st.value" :value="st.value">{{ st.label }}</el-radio>
            </el-radio-group>
          </div>

          <div class="param-item row">
            <label>设为常用路线</label>
            <el-switch v-model="form.isCommon" />
          </div>

          <div class="btn-row">
            <el-button type="primary" :loading="generating" class="full-w" @click="doGenerate">
              {{ generating ? '正在生成…' : '生成路径' }}
            </el-button>
            <el-button v-if="generatedRoute" type="success" class="full-w" @click="doSave">
              保存路径到数据库
            </el-button>
          </div>
        </el-card>

        <el-card shadow="never" class="param-card" style="margin-top:12px" v-if="generatedRoute">
          <template #header><span class="card-title">路径摘要</span></template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="编号">{{ generatedRoute.routeCode }}</el-descriptions-item>
            <el-descriptions-item label="模式">{{ modeLabel(generatedRoute.routeMode) }}</el-descriptions-item>
            <el-descriptions-item label="策略">{{ strategyLabel(generatedRoute.routeStrategy) }}</el-descriptions-item>
            <el-descriptions-item label="巡检点">{{ generatedRoute.pointCount }} 个</el-descriptions-item>
            <el-descriptions-item label="预计耗时">{{ Math.round(generatedRoute.estimatedDuration / 60 * 10) / 10 }} 分钟</el-descriptions-item>
            <el-descriptions-item label="范围">{{ scopeLabel(generatedRoute.scope) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="param-card" style="margin-top:12px" v-if="generatedRoute">
          <template #header><span class="card-title">选择执行无人机</span></template>
          <el-select v-model="selectedDroneId" placeholder="请选择可用无人机" class="full-w" @change="onDroneChange">
            <el-option v-for="d in availableDrones" :key="d.id" :label="`${d.droneName} (${d.droneCode}) - 电量${d.batteryLevel}%`" :value="d.id">
              <span>{{ d.droneName }} ({{ d.droneCode }})</span>
              <span style="float:right;color:#909399;font-size:12px">电量{{ d.batteryLevel }}% | {{ statusLabelDrone(d.deviceStatus) }}</span>
            </el-option>
          </el-select>
          <div v-if="selectedDroneId && selectedDrone" class="drone-info-card">
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="名称">{{ selectedDrone.droneName }}</el-descriptions-item>
              <el-descriptions-item label="编号">{{ selectedDrone.droneCode }}</el-descriptions-item>
              <el-descriptions-item label="电量">
                <el-progress :percentage="Number(selectedDrone.batteryLevel || 0)" :stroke-width="8" :color="batteryColor(selectedDrone.batteryLevel)" />
              </el-descriptions-item>
              <el-descriptions-item label="病害识别">{{ selectedDrone.supportDiseaseDetect ? '支持' : '不支持' }}</el-descriptions-item>
              <el-descriptions-item label="累计巡检">{{ selectedDrone.totalInspectionCount ?? 0 }} 次</el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-if="availableDrones.length === 0" description="暂无可用的空闲无人机（需要电量>30%且对照温室的无人机）" :image-size="60" />
        </el-card>
      </div>

      <!-- ===== 右侧：地块可视化 + 路径 ===== -->
      <div class="right-panel">
        <el-card shadow="never" class="viz-card" v-loading="plotLoading">
          <template #header>
            <span class="card-title">温室地块网格</span>
            <div class="legend">
              <span class="leg-item" v-for="lg in legend" :key="lg.color">
                <i :style="{ background: lg.color }"></i>{{ lg.label }}
              </span>
            </div>
          </template>

          <!-- 地块网格 -->
          <div v-if="plotAreas.length > 0" class="plot-grid-container">
            <div v-for="area in plotAreas" :key="area.code" class="area-block">
              <div class="area-header">{{ area.name }}
                <el-tag v-if="area.abnormalCount > 0" size="small" type="danger">异常 {{ area.abnormalCount }}</el-tag>
              </div>
              <div class="plot-row" v-for="r in area.rows" :key="r">
                <div
                  v-for="plot in getPlots(area.code, r)"
                  :key="plot.code"
                  class="plot-cell"
                  :class="[plot.statusClass, { selected: isSelected(plot.id), 'route-point': isInRoute(plot.id) }]"
                  :title="plot.code + ' (' + plot.status + ')'"
                  @click="togglePlot(plot)"
                >
                  <span class="plot-label">{{ plot.colNo }}</span>
                  <span v-if="getRouteSeq(plot.id)" class="seq-badge">{{ getRouteSeq(plot.id) }}</span>
                </div>
              </div>
              <!-- 连接线 SVG -->
              <svg v-if="routeLines.length > 0" class="route-lines-svg" :viewBox="area.viewBox">
                <line v-for="(l,i) in area.routeSegments" :key="'l'+i"
                  :x1="l.x1" :y1="l.y1" :x2="l.x2" :y2="l.y2"
                  stroke="#22c55e" stroke-width="2.5" stroke-linecap="round"
                  :stroke-dasharray="l.dash ? '6,4' : 'none'" />
              </svg>
            </div>
          </div>
          <el-empty v-else description="请先选择基地和温室以加载地块" :image-size="80" />
        </el-card>
      </div>
    </div>

    <!-- ===== 路线列表 ===== -->
    <el-card shadow="never" header="已有路线列表" style="margin-top:16px">
      <el-table :data="routeList" stripe v-loading="listLoading" size="small">
        <el-table-column prop="routeCode" label="编号" width="110" />
        <el-table-column prop="routeName" label="名称" min-width="180" />
        <el-table-column label="巡检模式" width="120">
          <template #default="{row}">{{ modeLabel(row.routeMode) }}</template>
        </el-table-column>
        <el-table-column label="策略" width="100">
          <template #default="{row}">{{ strategyLabel(row.routeStrategy) }}</template>
        </el-table-column>
        <el-table-column prop="pointCount" label="点数" width="70" align="center" />
        <el-table-column label="预计" width="90">
          <template #default="{row}">{{ Math.round((row.estimatedDuration||0)/60*10)/10 }}min</template>
        </el-table-column>
        <el-table-column label="常用" width="70" align="center">
          <template #default="{row}">
            <el-tag v-if="row.isCommon===1" size="small" type="warning">常用</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{row}">{{ row.status === 'ACTIVE' ? '启用' : '停用' }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="150">
          <template #default="{row}">{{ row.createTime?.substring(0,16) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button type="primary" size="small" link @click="viewRoute(row.id)">查看</el-button>
            <el-button size="small" link @click="setCommon(row.id)" v-if="row.isCommon!==1">常用</el-button>
            <el-button size="small" type="warning" link @click="syncToTwin(row.id)">同步</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="routeTotal>0" :current-page="pageNum" :page-size="pageSize" :total="routeTotal"
        layout="total,prev,pager,next" @current-change="p=>{pageNum=p;loadRoutes()}" style="margin-top:12px;justify-content:flex-end" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLands, getAllEnterprises } from '@/api/farm'
import { getRoutePlots, generateInspectionRoute, getInspectionRouteList, getInspectionRoute, setRouteCommon, deleteInspectionRoute } from '@/api/drone'
import { getAvailableDevices } from '@/api/drone'

// ── 常量 ──
const routeModes = [
  { value: 'FULL', label: '全量巡检' },
  { value: 'ABNORMAL_PRIORITY', label: '异常优先巡检' },
  { value: 'MATURE', label: '成熟检测巡检' },
  { value: 'DISEASE', label: '病虫害复查' },
  { value: 'DEVICE', label: '设备点位巡检' },
  { value: 'CUSTOM', label: '自定义巡检' }
]
const scopes = [
  { value: 'ALL', label: '全部区域' },
  { value: 'A_AREA', label: 'A区育苗区' },
  { value: 'B_AREA', label: 'B区开花区' },
  { value: 'C_AREA', label: 'C区结果区' },
  { value: 'D_AREA', label: 'D区采收区/异常修复区' }
]
const strategies = [
  { value: 'AREA_ORDER', label: '按区域顺序' },
  { value: 'ABNORMAL_FIRST', label: '异常优先' },
  { value: 'NEAREST', label: '最近邻规划' }
]
const legend = [
  { color: '#e8f5e9', label: '生长中' },
  { color: '#fff3e0', label: '成熟期' },
  { color: '#ffebee', label: '异常' },
  { color: '#f5f5f5', label: '空地' }
]
const statusClassMap = { NORMAL: '', RESULT: 'mature', MATURE: 'mature', ABNORMAL: 'abnormal', IDLE: 'idle' }

// ── 表单 ──
const form = reactive({
  baseId: null, greenhouseId: null, routeName: '',
  routeMode: 'ABNORMAL_PRIORITY', scope: 'ALL', routeStrategy: 'AREA_ORDER', isCommon: false
})

// ── 数据 ──
const bases = ref([])
const greenhouses = ref([])
const plots = ref([])            // 所有地块
const generating = ref(false)
const plotLoading = ref(false)
const generatedRoute = ref(null)
const generatedPoints = ref([])   // 路线点
const selectedPlotIds = ref(new Set())

// ── 无人机选择 ──
const selectedDroneId = ref(null)
const availableDrones = ref([])
const selectedDrone = ref(null)

// ── 路线列表 ──
const routeList = ref([])
const routeTotal = ref(0)
const listLoading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// ── 加载基地 ──
async function loadBases() {
  try { const r = await getAllEnterprises(); bases.value = r.data || [] } catch {}
}
// ── 加载温室列表(与巡检点管理页同源: farm_land) ──
async function loadGreenhouses() {
  try { const r = await getLands({ pageNum: 1, pageSize: 100 }); greenhouses.value = r.data?.records || [] } catch {}
}

// ── 基地变更：联动筛选温室 ──
async function onBaseChange() {
  form.greenhouseId = null
  plots.value = []
  generatedRoute.value = null
  generatedPoints.value = []
  selectedPlotIds.value = new Set()
  // 重新加载温室，跟巡检点管理页面用同一数据源
  await loadGreenhouses()
}

// ── 温室变更 ──
function onGreenhouseChange() {
  plots.value = []
  generatedRoute.value = null
  generatedPoints.value = []
  selectedPlotIds.value = new Set()
  if (form.greenhouseId) loadPlots(form.greenhouseId)
}

// ── 加载地块 ──
async function loadPlots(greenhouseId) {
  if (!greenhouseId) return
  plotLoading.value = true
  try {
    const r = await getRoutePlots(greenhouseId)
    plots.value = (r.data || []).map(p => ({
      ...p,
      statusClass: statusClassMap[p.plotStatus] || '',
      status: p.plotStatus || 'NORMAL'
    }))
  } catch (e) {
    ElMessage.warning('加载地块失败：' + (e.message || '暂无地块数据，请先初始化地块'))
  }
  plotLoading.value = false
}

// ── 地块网格分组 ──
const areaCodes = ['A', 'B', 'C', 'D']
const areaNames = { A: 'A区育苗区', B: 'B区开花区', C: 'C区结果区', D: 'D区采收区' }

const plotAreas = computed(() => {
  return areaCodes.map(code => {
    const areaPlots = plots.value.filter(p => p.areaCode === code)
    const rows = [...new Set(areaPlots.map(p => p.rowNo).filter(r => r != null))].sort((a, b) => a - b)
    const abnormalCount = areaPlots.filter(p => p.plotStatus === 'ABNORMAL').length
    const maxCol = Math.max(areaPlots.map(p => p.colNo||0).reduce((a,b)=>Math.max(a,b),0), 4)
    const rowCount = rows.length || 1
    const canvasH = rowCount * 48

    // 计算路由线段（按 sequence_no）
    const areaPointsInRoute = generatedPoints.value
      .filter(rp => rp.areaCode === code)
      .sort((a, b) => a.sequenceNo - b.sequenceNo)
    const routeSegments = []
    for (let i = 1; i < areaPointsInRoute.length; i++) {
      const prev = areaPointsInRoute[i-1]
      const curr = areaPointsInRoute[i]
      const rowIdx = rows.indexOf(prev.rowNo)
      const nextRowIdx = rows.indexOf(curr.rowNo)
      routeSegments.push({
        x1: (prev.colNo||1) * 48 - 24,
        y1: (rowIdx >= 0 ? rowIdx : 0) * 48 + 24,
        x2: (curr.colNo||1) * 48 - 24,
        y2: (nextRowIdx >= 0 ? nextRowIdx : 0) * 48 + 24,
        dash: curr.areaCode !== prev.areaCode
      })
    }

    return {
      code, name: areaNames[code],
      rows, abnormalCount, maxCol,
      viewBox: `0 0 ${maxCol * 48 + 20} ${canvasH + 10}`,
      routeSegments
    }
  }).filter(a => a.rows.length > 0)
})

function getPlots(areaCode, row) {
  return plots.value.filter(p => p.areaCode === areaCode && p.rowNo === row)
    .sort((a, b) => (a.colNo || 0) - (b.colNo || 0))
}

const routeLines = computed(() => {
  return generatedPoints.value.length > 1
})

// ── 地块选择（自定义模式） ──
function togglePlot(plot) {
  if (form.routeMode !== 'CUSTOM') return
  const s = new Set(selectedPlotIds.value)
  if (s.has(plot.id)) s.delete(plot.id); else s.add(plot.id)
  selectedPlotIds.value = s
}
function isSelected(plotId) { return form.routeMode === 'CUSTOM' && selectedPlotIds.value.has(plotId) }
function isInRoute(plotId) { return generatedPoints.value.some(rp => rp.plotId === plotId) }
function getRouteSeq(plotId) {
  const rp = generatedPoints.value.find(rp => rp.plotId === plotId)
  return rp ? rp.sequenceNo : null
}

// ── 生成路径 ──
async function doGenerate() {
  if (!form.greenhouseId) { ElMessage.warning('请先选择温室'); return }
  if (form.routeMode === 'CUSTOM' && selectedPlotIds.value.size === 0) {
    ElMessage.warning('自定义模式请先选择地块'); return
  }
  generating.value = true; generatedRoute.value = null; generatedPoints.value = []
  selectedDroneId.value = null; selectedDrone.value = null; availableDrones.value = []
  try {
    const params = {
      baseId: form.baseId, greenhouseId: form.greenhouseId,
      routeName: form.routeName || '自动生成巡检路线',
      routeMode: form.routeMode, scope: form.scope,
      routeStrategy: form.routeStrategy, isCommon: form.isCommon,
      selectedPlotIds: form.routeMode === 'CUSTOM' ? [...selectedPlotIds.value] : null
    }
    const res = await generateInspectionRoute(params)
    generatedRoute.value = res.data.route
    generatedPoints.value = res.data.points || []
    ElMessage.success(`路径生成成功，共 ${generatedRoute.value.pointCount} 个巡检点`)
    // 加载可用无人机
    loadAvailableDrones()
  } catch (e) {
    ElMessage.error('生成失败: ' + (e.message || '请先初始化温室地块'))
  }
  generating.value = false
}

// ── 加载可用无人机 ──
async function loadAvailableDrones() {
  try {
    const r = await getAvailableDevices({ greenhouseId: form.greenhouseId, taskType: form.routeMode })
    availableDrones.value = r.data || []
  } catch { availableDrones.value = [] }
}
function onDroneChange() {
  selectedDrone.value = availableDrones.value.find(d => d.id === selectedDroneId.value) || null
}
function statusLabelDrone(s) {
  return { IDLE: '空闲', INSPECTING: '巡检中', CHARGING: '充电中', MAINTENANCE: '维修中', OFFLINE: '离线', disabled: '停用' }[s] || s
}
function batteryColor(level) {
  const v = Number(level || 0); if (v > 60) return '#67c23a'; if (v > 30) return '#e6a23c'; return '#f56c6c'
}

// ── 保存路径 ──
async function doSave() {
  if (!generatedRoute.value) return
  if (!selectedDroneId.value) { ElMessage.warning('请选择一台执行无人机'); return }
  try { ElMessage.success('路径已保存到数据库，编号: ' + generatedRoute.value.routeCode + '，执行无人机: ' + (selectedDrone.value?.droneName || '未选')); loadRoutes() }
  catch (e) { ElMessage.error('保存失败') }
}

// ── 列表 ──
async function loadRoutes() {
  listLoading.value = true
  try {
    const r = await getInspectionRouteList({ pageNum: pageNum.value, pageSize: pageSize.value, greenhouseId: form.greenhouseId })
    routeList.value = r.data?.records || []
    routeTotal.value = r.data?.total || 0
  } catch {}
  listLoading.value = false
}

async function viewRoute(id) {
  try {
    const r = await getInspectionRoute(id)
    generatedRoute.value = r.data.route
    generatedPoints.value = r.data.points || []
    ElMessage.success('已加载路线')
  } catch {}
}

async function setCommon(id) {
  try { await setRouteCommon(id); ElMessage.success('已设为常用路线'); loadRoutes() } catch {}
}
function syncToTwin(id) {
  ElMessage.success('路线已同步到数字孪生巡检页面，切换至数字孪生巡检页面即可选择该路线')
}
async function handleDelete(id) {
  try { await ElMessageBox.confirm('确认删除此路线？', '提示', { type: 'warning' }); await deleteInspectionRoute(id); ElMessage.success('已删除'); loadRoutes() } catch {}
}

function modeLabel(v) {
  const m = routeModes.find(x => x.value === v); return m ? m.label : v || '-'
}
function scopeLabel(v) {
  const s = scopes.find(x => x.value === v); return s ? s.label : v || '全部'
}
function strategyLabel(v) {
  const st = strategies.find(x => x.value === v); return st ? st.label : v || '-'
}

onMounted(() => { loadBases(); loadGreenhouses(); loadRoutes() })
</script>

<style scoped>
.route-page { padding: 16px; min-height: 100vh; }
.page-header h2 { margin: 0 0 6px; font-size: 22px; }
.page-desc { margin: 0; color: #888; font-size: 13px; }
.route-layout { display: flex; gap: 16px; margin-top: 16px; }
.left-panel { width: 320px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.param-card { }
.card-title { font-weight: 600; font-size: 14px; }
.full-w { width: 100%; }
.param-item { margin-bottom: 14px; }
.param-item label { display: block; font-size: 13px; color: #555; margin-bottom: 4px; }
.param-item.row { display: flex; align-items: center; justify-content: space-between; }
.param-item.row label { margin-bottom: 0; }
.required { color: #f44336; }
.hint-text { color: #aaa; font-size: 12px; }
.btn-row { display: flex; flex-direction: column; gap: 8px; margin-top: 8px; }

/* 可视化 */
.viz-card { }
.legend { display: flex; gap: 12px; margin-left: 12px; }
.leg-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #888; }
.leg-item i { width: 14px; height: 14px; border-radius: 3px; display: inline-block; }

.plot-grid-container { }
.area-block { margin-bottom: 16px; position: relative; border: 1px solid #eee; border-radius: 10px; padding: 10px; }
.area-header { font-size: 13px; font-weight: 600; color: #333; margin-bottom: 6px; display: flex; align-items: center; gap: 8px; }
.plot-row { display: flex; gap: 6px; margin-bottom: 6px; }
.plot-cell {
  width: 42px; height: 42px; border-radius: 6px; border: 1px solid #e0e0e0;
  display: flex; align-items: center; justify-content: center; position: relative;
  background: #e8f5e9; font-size: 12px; color: #555; cursor: default;
  transition: all .15s;
}
.plot-cell.mature { background: #fff3e0; border-color: #ffcc02; }
.plot-cell.abnormal { background: #ffebee; border-color: #f44336; animation: pulse 2s infinite; }
.plot-cell.idle { background: #f5f5f5; color: #ccc; }
.plot-cell.selected { border-color: #1976d2; box-shadow: 0 0 0 2px rgba(25,118,210,0.3); }
.plot-cell.route-point { border-color: #22c55e; box-shadow: 0 0 0 2px rgba(34,197,94,0.25); }
.plot-cell:hover { transform: scale(1.05); }
.plot-label { font-weight: 500; }
.seq-badge {
  position: absolute; top: -8px; right: -8px;
  width: 18px; height: 18px; border-radius: 50%; background: #22c55e;
  color: #fff; font-size: 10px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
@keyframes pulse { 0%,100% { opacity: 1; } 50% { opacity: 0.7; } }

/* 路线SVG */
.route-lines-svg { position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; }

@media (max-width: 900px) {
  .route-layout { flex-direction: column; }
  .left-panel { width: 100%; }
}
</style>

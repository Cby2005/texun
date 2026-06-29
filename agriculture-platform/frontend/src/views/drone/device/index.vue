<template>
  <div class="page">
    <div class="page-header"><h2>无人机设备管理</h2><p>维护无人机设备档案，数据供巡检路径规划与数字孪生巡检使用</p></div>

    <!-- 搜索栏 -->
    <div class="filter-bar">
      <el-input v-model="searchName" placeholder="名称 / 编号" clearable style="width:180px" @keyup.enter="load" />
      <el-select v-model="searchBaseId" placeholder="所属基地" clearable style="width:160px" @change="load">
        <el-option v-for="b in bases" :key="b.id" :label="b.baseName || b.name" :value="b.id" />
      </el-select>
      <el-select v-model="searchGreenhouseId" placeholder="所属温室" clearable style="width:160px" @change="load">
        <el-option v-for="g in greenhouses" :key="g.id" :label="g.displayName || g.display_name || '温室#' + g.id" :value="g.id" />
      </el-select>
      <el-select v-model="searchStatus" placeholder="设备状态" clearable style="width:120px" @change="load">
        <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
      </el-select>
      <el-select v-model="searchDisease" placeholder="病害识别" clearable style="width:120px" @change="load">
        <el-option label="支持" :value="1" />
        <el-option label="不支持" :value="0" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openAdd">新增无人机</el-button>
    </div>

    <!-- 列表 -->
    <el-card>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="droneCode" label="编号" width="100" />
        <el-table-column prop="droneName" label="名称" min-width="110" />
        <el-table-column prop="model" label="型号" width="110" />
        <el-table-column label="所属基地" width="110">
          <template #default="{ row }">{{ baseName(row.baseId) }}</template>
        </el-table-column>
        <el-table-column label="所属温室" width="110">
          <template #default="{ row }">{{ greenhouseName(row.greenhouseId) }}</template>
        </el-table-column>
        <el-table-column prop="defaultArea" label="默认区域" width="110" />
        <el-table-column label="续航" width="70">
          <template #default="{ row }">{{ row.maxEndurance || '-' }}分钟</template>
        </el-table-column>
        <el-table-column label="电量" width="70">
          <template #default="{ row }">
            <el-progress :percentage="Number(row.batteryLevel || 0)" :stroke-width="6" :show-text="true" :color="batteryColor(row.batteryLevel)" />
          </template>
        </el-table-column>
        <el-table-column label="病害识别" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.supportDiseaseDetect ? 'success' : 'info'">{{ row.supportDiseaseDetect ? '支持' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="设备状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTag(row.deviceStatus)">{{ statusLabel(row.deviceStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最近巡检" width="110">
          <template #default="{ row }">{{ row.lastInspectionTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="累计巡检" width="75">
          <template #default="{ row }">{{ row.totalInspectionCount ?? 0 }}次</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" :type="row.deviceStatus === 'disabled' ? 'success' : 'warning'" @click="toggleStatus(row)">
              {{ row.deviceStatus === 'disabled' ? '启用' : '停用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total>0" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="p=>{pageNum=p;load()}" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="无人机编号" prop="droneCode">
              <el-input v-model="form.droneCode" placeholder="如 DJ-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="无人机名称" prop="droneName">
              <el-input v-model="form.droneName" placeholder="如 巡检一号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="无人机型号" prop="model">
              <el-input v-model="form.model" placeholder="如 DJI Mavic 3M" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备厂商">
              <el-input v-model="form.manufacturer" placeholder="如 大疆创新" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="序列号">
          <el-input v-model="form.serialNumber" placeholder="设备出厂序列号" style="width:50%" />
        </el-form-item>

        <el-divider content-position="left">归属信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属基地">
              <el-select v-model="form.baseId" style="width:100%" placeholder="请选择" clearable>
                <el-option v-for="b in bases" :key="b.id" :label="b.baseName || b.name" :value="b.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属温室" prop="greenhouseId">
              <el-select v-model="form.greenhouseId" style="width:100%" placeholder="请选择">
                <el-option v-for="g in greenhouses" :key="g.id" :label="g.displayName || g.display_name || '温室#' + g.id" :value="g.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="默认巡检区域">
              <el-select v-model="form.defaultArea" style="width:100%" placeholder="选填">
                <el-option v-for="a in areaOptions" :key="a" :label="a" :value="a" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备状态" prop="deviceStatus">
              <el-select v-model="form.deviceStatus" style="width:100%">
                <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">能力配置</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="最大续航(分钟)" prop="maxEndurance">
              <el-input-number v-model="form.maxEndurance" :min="0" :precision="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大飞行高度(米)" prop="maxFlightHeight">
              <el-input-number v-model="form.maxFlightHeight" :min="0" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大载重(kg)">
              <el-input-number v-model="form.maxLoad" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="搭载摄像头">
              <el-switch v-model="form.hasCamera" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="摄像头类型">
              <el-select v-model="form.cameraType" style="width:100%" :disabled="!form.hasCamera">
                <el-option label="4K高清" value="4K高清" />
                <el-option label="1080P" value="1080P" />
                <el-option label="红外热成像" value="红外热成像" />
                <el-option label="多光谱" value="多光谱" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="病害识别">
              <el-switch v-model="form.supportDiseaseDetect" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-alert v-if="!isEdit" type="info" show-icon :closable="false" style="margin-top:8px">
          <template #title>新增后系统将自动初始化运行状态：电量100%、坐标(0,0,0)、摄像头未启动、累计巡检0次</template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="无人机详情" v-model="detailVisible" width="750px" destroy-on-close>
      <template v-if="detailDrone">
        <el-divider content-position="left">基础档案</el-divider>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="编号">{{ detailDrone.droneCode }}</el-descriptions-item>
          <el-descriptions-item label="名称">{{ detailDrone.droneName }}</el-descriptions-item>
          <el-descriptions-item label="型号">{{ detailDrone.model }}</el-descriptions-item>
          <el-descriptions-item label="厂商">{{ detailDrone.manufacturer || '-' }}</el-descriptions-item>
          <el-descriptions-item label="序列号">{{ detailDrone.serialNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属基地">{{ baseName(detailDrone.baseId) }}</el-descriptions-item>
          <el-descriptions-item label="所属温室">{{ greenhouseName(detailDrone.greenhouseId) }}</el-descriptions-item>
          <el-descriptions-item label="默认巡检区">{{ detailDrone.defaultArea || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailDrone.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">能力配置</el-divider>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="最大续航">{{ detailDrone.maxEndurance || 0 }} 分钟</el-descriptions-item>
          <el-descriptions-item label="最大高度">{{ detailDrone.maxFlightHeight || 0 }} 米</el-descriptions-item>
          <el-descriptions-item label="最大载重">{{ detailDrone.maxLoad ?? '-' }} kg</el-descriptions-item>
          <el-descriptions-item label="摄像头">{{ detailDrone.hasCamera ? '有' : '无' }}</el-descriptions-item>
          <el-descriptions-item label="摄像头类型">{{ detailDrone.cameraType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="病害识别">{{ detailDrone.supportDiseaseDetect ? '支持' : '不支持' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">当前运行状态</el-divider>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="设备状态">
            <el-tag size="small" :type="statusTag(detailDrone.deviceStatus)">{{ statusLabel(detailDrone.deviceStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="电量">{{ detailDrone.batteryLevel ?? 0 }}%</el-descriptions-item>
          <el-descriptions-item label="摄像头状态">{{ detailDrone.cameraStatus || '未启动' }}</el-descriptions-item>
          <el-descriptions-item label="当前坐标">
            ({{ detailDrone.longitude ?? 0 }}, {{ detailDrone.latitude ?? 0 }}, {{ detailDrone.altitude ?? 0 }})
          </el-descriptions-item>
          <el-descriptions-item label="当前任务ID">{{ detailDrone.currentTaskId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="累计巡检">{{ detailDrone.totalInspectionCount ?? 0 }} 次</el-descriptions-item>
          <el-descriptions-item label="最近巡检">{{ detailDrone.lastInspectionTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">最近巡检任务（最近20条）</el-divider>
        <el-table :data="detailTasks" stripe size="small" v-loading="detailTasksLoading">
          <el-table-column prop="taskCode" label="任务编号" width="140" />
          <el-table-column prop="taskName" label="名称" min-width="120" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">{{ taskTypeLabel(row.taskType) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag size="small" :type="taskStatusTag(row.taskStatus)">{{ taskStatusLabel(row.taskStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="150" />
          <el-table-column prop="endTime" label="结束时间" width="150" />
          <el-table-column prop="result" label="结果" min-width="100" show-overflow-tooltip />
        </el-table>
        <p v-if="detailTasks.length === 0 && !detailTasksLoading" style="color:#909399;text-align:center;margin-top:12px">暂无巡检记录</p>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeviceList, addDevice, updateDevice, deleteDevice, getAvailableDevices, updateDeviceStatus, getDeviceTasks } from '@/api/drone'
import { getLands, getAllEnterprises } from '@/api/farm'

// ================== 列表状态 ==================
const records = ref([]); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10); const loading = ref(false)
const searchName = ref(''); const searchBaseId = ref(null); const searchGreenhouseId = ref(null)
const searchStatus = ref(''); const searchDisease = ref(null)

const bases = ref([]); const greenhouses = ref([])

const areaOptions = ['A区育苗区', 'B区开花区', 'C区结果区', 'D区异常修复区']
const statusOptions = [
  { label: '空闲', value: 'IDLE' }, { label: '巡检中', value: 'INSPECTING' },
  { label: '充电中', value: 'CHARGING' }, { label: '维修中', value: 'MAINTENANCE' },
  { label: '离线', value: 'OFFLINE' }, { label: '停用', value: 'disabled' }
]

function baseName(id) { const b = bases.value.find(x => x.id === id); return b ? (b.baseName || b.name) : (id || '-') }
function greenhouseName(id) { const g = greenhouses.value.find(x => x.id === id); return g ? (g.displayName || g.display_name || '温室#' + g.id) : (id || '-') }

function statusTag(s) {
  return { IDLE: 'success', INSPECTING: '', CHARGING: 'warning', MAINTENANCE: 'danger', OFFLINE: 'info', disabled: 'info' }[s] || 'info'
}
function statusLabel(s) {
  return { IDLE: '空闲', INSPECTING: '巡检中', CHARGING: '充电中', MAINTENANCE: '维修中', OFFLINE: '离线', disabled: '停用' }[s] || s
}
function taskTypeLabel(t) { return { DAILY_INSPECTION: '日常巡检', ABNORMAL_PRIORITY: '异常优先', DISEASE_REVIEW: '病害复查' }[t] || (t || '-') }
function taskStatusTag(s) {
  return { PENDING: 'info', RUNNING: 'warning', FINISHED: 'success', CANCELED: 'info' }[s] || 'info'
}
function taskStatusLabel(s) {
  return { PENDING: '待执行', RUNNING: '执行中', FINISHED: '已完成', CANCELED: '已取消' }[s] || s
}
function batteryColor(level) {
  const v = Number(level || 0); if (v > 60) return '#67c23a'; if (v > 30) return '#e6a23c'; return '#f56c6c'
}

// ================== 弹窗状态 ==================
const dialogVisible = ref(false); const dialogTitle = ref('新增无人机'); const isEdit = ref(false); const formRef = ref(null)

const defaultForm = () => ({
  droneCode: '', droneName: '', model: '', manufacturer: '', serialNumber: '',
  baseId: null, greenhouseId: null, defaultArea: '',
  maxEndurance: 30, maxFlightHeight: 100, maxLoad: null,
  hasCamera: 1, cameraType: '4K高清', supportDiseaseDetect: 0,
  deviceStatus: 'IDLE', remark: ''
})

const form = ref(defaultForm())

const rules = {
  droneCode: [{ required: true, message: '请输入无人机编号', trigger: 'blur' }],
  droneName: [{ required: true, message: '请输入无人机名称', trigger: 'blur' }],
  model: [{ required: true, message: '请输入无人机型号', trigger: 'blur' }],
  greenhouseId: [{ required: true, message: '请选择所属温室', trigger: 'change' }],
  deviceStatus: [{ required: true, message: '请选择设备状态', trigger: 'change' }]
}

// ================== 详情弹窗 ==================
const detailVisible = ref(false); const detailDrone = ref(null)
const detailTasks = ref([]); const detailTasksLoading = ref(false)

async function openDetail(row) {
  detailDrone.value = row
  detailVisible.value = true
  detailTasksLoading.value = true
  try {
    const r = await getDeviceTasks(row.id)
    detailTasks.value = r.data || []
  } catch { detailTasks.value = [] }
  detailTasksLoading.value = false
}

// ================== 数据加载 ==================
async function load() {
  loading.value = true
  try {
    const r = await getDeviceList({
      pageNum: pageNum.value, pageSize: pageSize.value,
      droneName: searchName.value || undefined,
      baseId: searchBaseId.value || undefined,
      greenhouseId: searchGreenhouseId.value || undefined,
      deviceStatus: searchStatus.value || undefined,
      supportDiseaseDetect: searchDisease.value
    })
    records.value = r.data.records; total.value = r.data.total
  } catch { /* ignore */ } finally { loading.value = false }
}

async function loadBases() { try { const r = await getAllEnterprises(); bases.value = r.data || [] } catch {} }
async function loadGreenhouses() { try { const r = await getLands({ pageNum: 1, pageSize: 100 }); greenhouses.value = r.data?.records || [] } catch {} }

// ================== 新增/编辑 ==================
function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增无人机'; form.value = defaultForm()
  dialogVisible.value = true; nextTick(() => formRef.value?.clearValidate())
}
function openEdit(row) {
  isEdit.value = true; dialogTitle.value = '编辑无人机'; form.value = { ...row }
  dialogVisible.value = true; nextTick(() => formRef.value?.clearValidate())
}
async function submit() {
  try { await formRef.value.validate() } catch { return }
  try {
    if (isEdit.value) {
      await updateDevice(form.value.id, form.value); ElMessage.success('更新成功')
    } else {
      await addDevice(form.value); ElMessage.success('新增成功')
    }
    dialogVisible.value = false; load()
  } catch { /* interceptor handles */ }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该无人机吗？', '提示', { type: 'warning' })
  await deleteDevice(id); ElMessage.success('删除成功'); load()
}

async function toggleStatus(row) {
  const newStatus = row.deviceStatus === 'disabled' ? 'IDLE' : 'disabled'
  try {
    await updateDeviceStatus(row.id, { deviceStatus: newStatus })
    ElMessage.success(newStatus === 'disabled' ? '已停用' : '已启用')
    load()
  } catch { /* ignore */ }
}

onMounted(() => { load(); loadBases(); loadGreenhouses() })
</script>

<style scoped>
.page { padding: 16px; }
.page-header h2 { margin: 0 0 4px; }
.page-header p { color: #909399; font-size: 13px; margin: 0; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.el-divider { margin: 12px 0 16px; }
:deep(.el-divider__text) { font-weight: 600; color: #606266; }
</style>

<template>
  <div class="vehicle-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>无人车设备列表</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon> 注册设备
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="设备编码/名称" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="空闲" value="IDLE" />
            <el-option label="在线" value="ONLINE" />
            <el-option label="作业中" value="WORKING" />
            <el-option label="离线" value="OFFLINE" />
            <el-option label="故障" value="FAULT" />
            <el-option label="维护" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="deviceCode" label="设备编码" width="150" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="firmwareVersion" label="固件版本" width="120" />
        <el-table-column prop="lastOnlineTime" label="最后上线" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/vehicle/${row.id}`)">详情</el-button>
            <el-button size="small" type="success" @click="handleOnline(row)" v-if="row.status === 'IDLE' || row.status === 'OFFLINE'">上线</el-button>
            <el-button size="small" type="warning" @click="handleOffline(row)" v-if="row.status === 'ONLINE'">离线</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @change="loadData"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 注册设备对话框 -->
    <el-dialog v-model="showAddDialog" title="注册设备" width="500">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="设备编码" prop="deviceCode">
          <el-input v-model="addForm.deviceCode" placeholder="如 AGRI-0001" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="addForm.deviceName" placeholder="如 智农T200-01" />
        </el-form-item>
        <el-form-item label="所属农场">
          <el-input v-model="addForm.farmId" placeholder="农场ID" />
        </el-form-item>
        <el-form-item label="固件版本">
          <el-input v-model="addForm.firmwareVersion" placeholder="v1.0.0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd" :loading="addLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listDevices, registerDevice, deleteDevice, onlineDevice, offlineDevice } from '@/api/vehicle'

const loading = ref(false)
const addLoading = ref(false)
const showAddDialog = ref(false)
const addFormRef = ref<FormInstance>()
const tableData = ref<any[]>([])
const total = ref(0)

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', status: '' })

const addForm = reactive({ deviceCode: '', deviceName: '', farmId: '', firmwareVersion: '' })
const addRules: FormRules = {
  deviceCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
}

function statusType(status: string) {
  const map: Record<string, string> = { IDLE: 'info', ONLINE: 'success', WORKING: '', OFFLINE: 'warning', FAULT: 'danger', MAINTENANCE: 'warning', SCRAPPED: 'info' }
  return (map[status] || 'info') as any
}

function statusLabel(status: string) {
  const map: Record<string, string> = { IDLE: '空闲', ONLINE: '在线', WORKING: '作业中', OFFLINE: '离线', FAULT: '故障', MAINTENANCE: '维护', SCRAPPED: '报废' }
  return map[status] || status
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await listDevices(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function handleAdd() {
  const valid = await addFormRef.value?.validate().catch(() => false)
  if (!valid) return
  addLoading.value = true
  try {
    await registerDevice(addForm)
    ElMessage.success('注册成功')
    showAddDialog.value = false
    loadData()
  } finally {
    addLoading.value = false
  }
}

async function handleOnline(row: any) {
  await onlineDevice(row.id)
  ElMessage.success('上线成功')
  loadData()
}

async function handleOffline(row: any) {
  await offlineDevice(row.id)
  ElMessage.success('离线成功')
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该设备？', '提示', { type: 'warning' })
  await deleteDevice(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.search-form {
  margin-bottom: 16px;
}
</style>

<template>
  <div class="route-view">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>路线规划</span>
          <el-button type="primary" @click="showGenerateDialog = true">生成路线</el-button>
        </div>
      </template>
      <el-table :data="routes" v-loading="loading">
        <el-table-column prop="routeName" label="路线名称" />
        <el-table-column prop="algorithm" label="算法" width="140" />
        <el-table-column prop="totalLength" label="总长度(m)" width="120">
          <template #default="{ row }">{{ row.totalLength?.toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="coverageRate" label="覆盖率(%)" width="120">
          <template #default="{ row }">{{ row.coverageRate?.toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="estimatedDuration" label="预估耗时(分)" width="120" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="previewRoute(row)">预览</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>路线预览地图</template>
      <div ref="mapRef" style="height: 500px; background: #f0f0f0; border-radius: 4px"></div>
    </el-card>

    <!-- 生成路线对话框 -->
    <el-dialog v-model="showGenerateDialog" title="生成覆盖式路线" width="500">
      <el-form :model="genForm" label-width="100px">
        <el-form-item label="地块ID"><el-input v-model.number="genForm.plotId" /></el-form-item>
        <el-form-item label="作业宽度(m)"><el-input-number v-model="genForm.workWidth" :min="1" :max="20" /></el-form-item>
        <el-form-item label="转弯半径(m)"><el-input-number v-model="genForm.turnRadius" :min="0.5" :max="10" :step="0.5" /></el-form-item>
        <el-form-item label="重叠率(%)"><el-input-number v-model="genForm.overlapRate" :min="0" :max="50" /></el-form-item>
        <el-form-item label="边界坐标">
          <el-input v-model="genForm.boundaryStr" type="textarea" :rows="4" placeholder='[[lng1,lat1],[lng2,lat2],...]' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGenerateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const routes = ref<any[]>([])
const showGenerateDialog = ref(false)
const mapRef = ref<HTMLElement>()

const genForm = reactive({
  plotId: '',
  workWidth: 4,
  turnRadius: 2.5,
  overlapRate: 10,
  boundaryStr: '',
})

async function loadData() {
  loading.value = true
  try {
    const res = await fetch('/api/route/route/list?pageNum=1&pageSize=50')
    const data = await res.json()
    routes.value = data.data?.records || []
  } finally {
    loading.value = false
  }
}

async function handleGenerate() {
  try {
    const boundary = JSON.parse(genForm.boundaryStr)
    const res = await fetch('/api/route/route/generate/coverage', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        boundary,
        workWidth: genForm.workWidth,
        turnRadius: genForm.turnRadius,
        overlapRate: genForm.overlapRate / 100,
      }),
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success(`路线生成成功: ${data.data.waypoints?.length} 个航点`)
      showGenerateDialog.value = false
    }
  } catch (e: any) {
    ElMessage.error(e.message || '生成失败')
  }
}

function previewRoute(route: any) {
  ElMessage.info('路线预览: ' + route.routeName)
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除？')
  await fetch(`/api/route/route/${row.id}`, { method: 'DELETE' })
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>

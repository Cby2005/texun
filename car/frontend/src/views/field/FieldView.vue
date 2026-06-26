<template>
  <div class="field-view">
    <el-row :gutter="16">
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>农场列表</span>
              <el-button type="primary" size="small" @click="showFarmDialog = true">新增农场</el-button>
            </div>
          </template>
          <el-table :data="farms" highlight-current-row @current-change="selectFarm" style="cursor: pointer">
            <el-table-column prop="farmName" label="农场名称" />
            <el-table-column prop="totalArea" label="面积(亩)" width="100" />
            <el-table-column prop="province" label="省" width="80" />
            <el-table-column prop="city" label="市" width="80" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>地块列表 {{ selectedFarm ? '- ' + selectedFarm.farmName : '' }}</span>
              <el-button type="primary" size="small" @click="showPlotDialog = true" :disabled="!selectedFarm">新增地块</el-button>
            </div>
          </template>
          <el-table :data="plots" v-loading="plotLoading">
            <el-table-column prop="plotName" label="地块名称" />
            <el-table-column prop="area" label="面积(亩)" width="100" />
            <el-table-column prop="cropType" label="作物" width="100" />
            <el-table-column prop="soilType" label="土壤" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag>{{ row.status || '-' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="hover" style="margin-top: 16px">
          <template #header>地块地图</template>
          <div ref="mapRef" style="height: 400px; background: #f0f0f0; border-radius: 4px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增农场对话框 -->
    <el-dialog v-model="showFarmDialog" title="新增农场" width="500">
      <el-form :model="farmForm" label-width="80px">
        <el-form-item label="农场名称"><el-input v-model="farmForm.farmName" /></el-form-item>
        <el-form-item label="农场编码"><el-input v-model="farmForm.farmCode" /></el-form-item>
        <el-form-item label="省"><el-input v-model="farmForm.province" /></el-form-item>
        <el-form-item label="市"><el-input v-model="farmForm.city" /></el-form-item>
        <el-form-item label="区"><el-input v-model="farmForm.district" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="farmForm.address" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFarmDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddFarm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增地块对话框 -->
    <el-dialog v-model="showPlotDialog" title="新增地块" width="600">
      <el-form :model="plotForm" label-width="80px">
        <el-form-item label="地块名称"><el-input v-model="plotForm.plotName" /></el-form-item>
        <el-form-item label="地块编码"><el-input v-model="plotForm.plotCode" /></el-form-item>
        <el-form-item label="作物类型"><el-input v-model="plotForm.cropType" /></el-form-item>
        <el-form-item label="土壤类型">
          <el-select v-model="plotForm.soilType" placeholder="选择土壤类型">
            <el-option label="壤土" value="LOAM" />
            <el-option label="黏土" value="CLAY" />
            <el-option label="沙土" value="SAND" />
            <el-option label="粉土" value="SILT" />
          </el-select>
        </el-form-item>
        <el-form-item label="边界坐标">
          <el-input v-model="plotForm.boundary" type="textarea" :rows="4" placeholder='[[lng1,lat1],[lng2,lat2],...]' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPlotDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddPlot">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listFarms, createFarm, listPlots, createPlot } from '@/api/field'

const farms = ref<any[]>([])
const plots = ref<any[]>([])
const selectedFarm = ref<any>(null)
const plotLoading = ref(false)
const showFarmDialog = ref(false)
const showPlotDialog = ref(false)
const mapRef = ref<HTMLElement>()

const farmForm = reactive({ farmName: '', farmCode: '', province: '', city: '', district: '', address: '' })
const plotForm = reactive({ plotName: '', plotCode: '', cropType: '', soilType: '', boundary: '', farmId: '' })

async function loadFarms() {
  const res: any = await listFarms({ pageNum: 1, pageSize: 100 })
  farms.value = res.data.records
}

function selectFarm(row: any) {
  selectedFarm.value = row
  if (row) loadPlots(row.id)
}

async function loadPlots(farmId: number) {
  plotLoading.value = true
  try {
    const res: any = await listPlots({ pageNum: 1, pageSize: 100, farmId })
    plots.value = res.data.records
  } finally {
    plotLoading.value = false
  }
}

async function handleAddFarm() {
  await createFarm(farmForm)
  ElMessage.success('创建成功')
  showFarmDialog.value = false
  loadFarms()
}

async function handleAddPlot() {
  plotForm.farmId = String(selectedFarm.value?.id || '')
  await createPlot(plotForm)
  ElMessage.success('创建成功')
  showPlotDialog.value = false
  loadPlots(selectedFarm.value.id)
}

onMounted(loadFarms)
</script>

<style scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>

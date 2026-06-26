<template>
  <div class="page">
    <div class="page-header"><h2>环境监测</h2><p>实时查看各地块环境传感器数据</p></div>
    <div class="filter-bar">
      <el-select v-model="landId" placeholder="选择地块" clearable style="width:240px" @change="load">
        <el-option v-for="l in lands" :key="l.id" :value="l.id" :label="l.displayName" />
      </el-select>
      <el-button type="primary" @click="load"><el-icon><Refresh /></el-icon>刷新</el-button>
    </div>
    <el-row :gutter="16">
      <el-col :span="12" v-for="d in envData" :key="d.id" style="margin-bottom:16px">
        <el-card>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <div>
              <div style="font-size:13px;color:#999">设备 #{{ d.deviceId }}</div>
              <div style="font-size:24px;font-weight:700;margin-top:4px">{{ d.dataValue }}</div>
            </div>
            <el-tag>{{ {0:'大棚',1:'鱼塘',2:'大田',3:'仓库'}[d.landType] || '未知' }}</el-tag>
          </div>
          <div style="margin-top:8px;font-size:12px;color:#999">{{ d.createTime }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!envData.length" description="请选择地块查看环境数据" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLands, getLatestEnv } from '../../../api/farm'

const lands = ref([]), landId = ref(null), envData = ref([])

async function load() {
  if (!landId.value) return
  try { const r = await getLatestEnv(landId.value); envData.value = r.data } catch(e) {}
}

onMounted(async () => {
  try { const r = await getLands({ pageSize: 100 }); lands.value = r.data.records } catch(e) {}
})
</script>

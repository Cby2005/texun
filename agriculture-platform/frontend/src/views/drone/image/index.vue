<template>
  <div class="page">
    <div class="page-header"><h2>巡检图像记录</h2></div>
    <el-card shadow="never">
      <div class="search-bar">
        <el-input-number v-model="searchTaskId" placeholder="任务ID" style="width:160px" />
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="success" @click="openAdd">新增图像记录</el-button>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="taskId" label="任务ID" width="100" />
        <el-table-column prop="imageUrl" label="图片地址" min-width="200" />
        <el-table-column prop="capturePoint" label="拍摄点" width="120" />
        <el-table-column prop="detectResult" label="识别结果" width="100">
          <template #default="{row}"><el-tag :type="detectTag(row.detectResult)">{{ detectLabel(row.detectResult) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="diseaseType" label="病害类型" width="120" />
        <el-table-column prop="confidence" label="置信度" width="90" />
        <el-table-column prop="suggestion" label="建议" min-width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button size="small" type="warning" :disabled="row.detectResult!=='PENDING'" @click="handleDetect(row.id)">识别</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total>0" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="p=>{pageNum=p;load()}" />
    </el-card>
    <el-dialog title="新增图像记录" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="任务ID"><el-input-number v-model="form.taskId" /></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="form.imageUrl" placeholder="输入图片地址" /></el-form-item>
        <el-form-item label="拍摄点坐标"><el-input v-model="form.capturePoint" placeholder="如 (10,20,2)" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getImageList, addImage, detectImage } from '@/api/drone'
const records = ref([]); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10)
const loading = ref(false); const searchTaskId = ref(null)
const dialogVisible = ref(false); const form = ref({})
function detectTag(r) { const m={PENDING:'info',HEALTHY:'success',DISEASE:'danger'}; return m[r]||'info' }
function detectLabel(r) { const m={PENDING:'待识别',HEALTHY:'健康',DISEASE:'病害'}; return m[r]||r }
async function load() { loading.value=true; try{const r=await getImageList({pageNum:pageNum.value,pageSize:pageSize.value,taskId:searchTaskId.value});records.value=r.data.records;total.value=r.data.total}finally{loading.value=false} }
function openAdd(){ form.value={}; dialogVisible.value=true }
async function submit(){ await addImage(form.value); ElMessage.success('新增成功'); dialogVisible.value=false; load() }
async function handleDetect(id){ await detectImage(id); ElMessage.success('识别完成'); load() }
onMounted(load)
</script>
<style scoped>.page{padding:16px}.page-header h2{margin:0 0 16px}.search-bar{display:flex;gap:12px;margin-bottom:16px;flex-wrap:wrap}</style>

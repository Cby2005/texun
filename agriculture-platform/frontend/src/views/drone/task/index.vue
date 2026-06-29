<template>
  <div class="page">
    <div class="page-header"><h2>巡检任务管理</h2></div>
    <el-card shadow="never">
      <div class="search-bar">
        <el-select v-model="searchType" placeholder="任务类型" clearable style="width:160px"><el-option label="日常巡检" value="DAILY_INSPECTION" /><el-option label="病害巡检" value="DISEASE_INSPECTION" /><el-option label="异常复核" value="ABNORMAL_RECHECK" /></el-select>
        <el-select v-model="searchStatus" placeholder="任务状态" clearable style="width:150px"><el-option label="待执行" value="PENDING" /><el-option label="执行中" value="RUNNING" /><el-option label="已完成" value="FINISHED" /><el-option label="已取消" value="CANCELED" /></el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="success" @click="openCreate">创建任务</el-button>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="任务编号" width="130" />
        <el-table-column prop="taskName" label="任务名称" width="180" />
        <el-table-column prop="taskType" label="类型" width="110"><template #default="{row}">{{ typeLabel(row.taskType) }}</template></el-table-column>
        <el-table-column prop="taskStatus" label="状态" width="100"><template #default="{row}"><el-tag :type="statusTag(row.taskStatus)">{{ statusLabel(row.taskStatus) }}</el-tag></template></el-table-column>
        <el-table-column prop="droneId" label="无人机ID" width="100" />
        <el-table-column prop="routeId" label="路径ID" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column label="操作" min-width="280" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" :disabled="row.taskStatus!=='PENDING'" @click="handleStart(row.id)">开始</el-button>
            <el-button size="small" type="success" :disabled="row.taskStatus!=='RUNNING'" @click="handleFinish(row.id)">完成</el-button>
            <el-button size="small" type="warning" :disabled="row.taskStatus!=='PENDING'&&row.taskStatus!=='RUNNING'" @click="handleCancel(row.id)">取消</el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total>0" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="p=>{pageNum=p;load()}" />
    </el-card>
    <el-dialog title="创建巡检任务" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="任务名称"><el-input v-model="form.taskName" /></el-form-item>
        <el-form-item label="无人机ID"><el-input-number v-model="form.droneId" style="width:100%" /></el-form-item>
        <el-form-item label="路径ID"><el-input-number v-model="form.routeId" style="width:100%" /></el-form-item>
        <el-form-item label="温室ID"><el-input-number v-model="form.greenhouseId" style="width:100%" /></el-form-item>
        <el-form-item label="任务类型"><el-select v-model="form.taskType" style="width:100%"><el-option label="日常巡检" value="DAILY_INSPECTION" /><el-option label="病害巡检" value="DISEASE_INSPECTION" /><el-option label="异常复核" value="ABNORMAL_RECHECK" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="doCreate">确定</el-button></template>
    </el-dialog>
    <el-dialog title="任务详情" v-model="detailVisible" width="600px">
      <el-descriptions v-if="detail" :column="2" border><el-descriptions-item label="任务编号">{{ detail.taskCode }}</el-descriptions-item><el-descriptions-item label="任务名称">{{ detail.taskName }}</el-descriptions-item><el-descriptions-item label="任务类型">{{ typeLabel(detail.taskType) }}</el-descriptions-item><el-descriptions-item label="状态"><el-tag :type="statusTag(detail.taskStatus)">{{ statusLabel(detail.taskStatus) }}</el-tag></el-descriptions-item><el-descriptions-item label="无人机ID">{{ detail.droneId }}</el-descriptions-item><el-descriptions-item label="路径ID">{{ detail.routeId }}</el-descriptions-item><el-descriptions-item label="温室ID">{{ detail.greenhouseId }}</el-descriptions-item><el-descriptions-item label="开始时间">{{ detail.startTime }}</el-descriptions-item><el-descriptions-item label="结束时间">{{ detail.endTime }}</el-descriptions-item><el-descriptions-item label="结果" :span="2">{{ detail.result }}</el-descriptions-item></el-descriptions>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, createTask, startTask, finishTask, cancelTask } from '@/api/drone'
const records = ref([]); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10); const loading = ref(false)
const searchType = ref(''); const searchStatus = ref('')
const dialogVisible = ref(false); const form = ref({ taskType: 'DAILY_INSPECTION' })
const detailVisible = ref(false); const detail = ref(null)
function statusTag(s) { const m={PENDING:'info',RUNNING:'',FINISHED:'success',CANCELED:'warning',ERROR:'danger'}; return m[s]||'info' }
function statusLabel(s) { const m={PENDING:'待执行',RUNNING:'执行中',FINISHED:'已完成',CANCELED:'已取消',ERROR:'异常'}; return m[s]||s }
function typeLabel(t) { const m={DAILY_INSPECTION:'日常巡检',DISEASE_INSPECTION:'病害巡检',ABNORMAL_RECHECK:'异常复核'}; return m[t]||t }
async function load(){ loading.value=true; try{const r=await getTaskList({pageNum:pageNum.value,pageSize:pageSize.value,taskType:searchType.value,taskStatus:searchStatus.value});records.value=r.data.records;total.value=r.data.total}finally{loading.value=false} }
function openCreate(){ form.value={taskType:'DAILY_INSPECTION'}; dialogVisible.value=true }
async function doCreate(){ await createTask(form.value); ElMessage.success('任务创建成功'); dialogVisible.value=false; load() }
async function handleStart(id){ await startTask(id); ElMessage.success('任务已开始'); load() }
async function handleFinish(id){ try{await ElMessageBox.prompt('请输入巡检结果','完成任务',{inputValue:'巡检完成，草莓生长状况良好'})}catch{return}; await finishTask(id); ElMessage.success('任务已完成'); load() }
async function handleCancel(id){ await ElMessageBox.confirm('确认取消该任务?','提示',{type:'warning'}); await cancelTask(id); ElMessage.success('任务已取消'); load() }
function viewDetail(row){ detail.value=row; detailVisible.value=true }
onMounted(load)
</script>
<style scoped>.page{padding:16px}.page-header h2{margin:0 0 16px}.search-bar{display:flex;gap:12px;margin-bottom:16px;flex-wrap:wrap}</style>

<template>
  <div class="page">
    <div class="page-header"><h2>巡检报告管理</h2></div>
    <el-card shadow="never">
      <div class="search-bar">
        <el-input-number v-model="searchTaskId" placeholder="按任务ID筛选" style="width:160px" />
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="reportCode" label="报告编号" width="140" />
        <el-table-column prop="taskName" label="任务名称" width="180" />
        <el-table-column prop="droneName" label="无人机" width="120" />
        <el-table-column prop="routeName" label="路径" width="120" />
        <el-table-column prop="totalImages" label="总图片数" width="90" />
        <el-table-column prop="abnormalImages" label="异常数" width="80"><template #default="{row}"><el-tag :type="row.abnormalImages>0?'danger':'success'">{{ row.abnormalImages }}</el-tag></template></el-table-column>
        <el-table-column prop="diseaseTypes" label="病害类型" width="150" />
        <el-table-column prop="createTime" label="生成时间" width="170" />
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="viewDetail(row)">查看详情</el-button>
            <el-button size="small" type="success" :disabled="!row.taskId" @click="doGenerate(row.taskId)">生成报告</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total>0" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="p=>{pageNum=p;load()}" />
    </el-card>
    <el-card shadow="never" style="margin-top:16px" header="根据任务生成新报告">
      <el-row :gutter="16">
        <el-col :span="8"><el-input-number v-model="genTaskId" placeholder="输入任务ID" style="width:100%" /></el-col>
        <el-col :span="4"><el-button type="primary" :loading="generating" @click="doGenerate(genTaskId)">生成报告</el-button></el-col>
      </el-row>
    </el-card>
    <el-dialog title="报告详情" v-model="detailVisible" width="650px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="报告编号">{{ detail.reportCode }}</el-descriptions-item>
        <el-descriptions-item label="生成时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ detail.taskName }}</el-descriptions-item>
        <el-descriptions-item label="巡检无人机">{{ detail.droneName }}</el-descriptions-item>
        <el-descriptions-item label="巡检路径">{{ detail.routeName }}</el-descriptions-item>
        <el-descriptions-item label="巡检区域">{{ detail.greenhouseName }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="总图片数">{{ detail.totalImages }}</el-descriptions-item>
        <el-descriptions-item label="异常图片数"><el-tag :type="detail.abnormalImages>0?'danger':'success'">{{ detail.abnormalImages }}</el-tag></el-descriptions-item>
        <el-descriptions-item v-if="detail.diseaseTypes" label="病害类型">{{ detail.diseaseTypes }}</el-descriptions-item>
        <el-descriptions-item label="处理建议" :span="2">{{ detail.suggestion }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportList, generateReport } from '@/api/drone'
const records = ref([]); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10); const loading = ref(false)
const searchTaskId = ref(null); const genTaskId = ref(1); const generating = ref(false)
const detailVisible = ref(false); const detail = ref(null)
async function load(){ loading.value=true; try{const r=await getReportList({pageNum:pageNum.value,pageSize:pageSize.value,taskId:searchTaskId.value});records.value=r.data.records;total.value=r.data.total}finally{loading.value=false} }
async function doGenerate(taskId){ if(!taskId){ElMessage.warning('请输入任务ID');return}; generating.value=true; try{const r=await generateReport(taskId); ElMessage.success('报告生成成功'); load() }catch(e){ElMessage.error('生成失败')}finally{generating.value=false} }
function viewDetail(row){ detail.value=row; detailVisible.value=true }
onMounted(load)
</script>
<style scoped>.page{padding:16px}.page-header h2{margin:0 0 16px}.search-bar{display:flex;gap:12px;margin-bottom:16px;flex-wrap:wrap}</style>

<template>
  <div class="page public-trace">
    <div class="page-header">
      <h2>公开溯源</h2>
      <p>消费者可通过生产批次号查看公开的生产、诊断、质检和物流链路。</p>
    </div>
    <el-card>
      <div class="query-line">
        <el-input v-model="batchNo" placeholder="输入生产批次号" clearable @keyup.enter="loadTrace" />
        <el-button type="primary" @click="loadTrace">查询</el-button>
      </div>
    </el-card>

    <el-card v-if="trace" class="trace-result">
      <template #header>
        <div class="result-header">
          <span>批次 {{ trace.batchNo }}</span>
          <el-tag type="success">完整度 {{ trace.traceCompleteness || 0 }}%</el-tag>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item timestamp="生产记录" placement="top">
          <span>{{ trace.production?.length || 0 }} 条</span>
        </el-timeline-item>
        <el-timeline-item timestamp="诊断记录" placement="top">
          <span>{{ trace.diagnosis?.length || 0 }} 条</span>
        </el-timeline-item>
        <el-timeline-item timestamp="质检记录" placement="top">
          <span>{{ trace.quality?.length || 0 }} 条</span>
        </el-timeline-item>
        <el-timeline-item timestamp="物流记录" placement="top">
          <span>{{ trace.logistics?.length || 0 }} 条</span>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPublicChainTrace } from '../../api/trace'

const batchNo = ref('')
const trace = ref(null)

async function loadTrace() {
  if (!batchNo.value) {
    ElMessage.warning('请输入生产批次号')
    return
  }
  try {
    const r = await getPublicChainTrace(batchNo.value)
    trace.value = r.data
  } catch (e) {
    ElMessage.error('未查询到溯源信息')
  }
}
</script>

<style scoped>
.public-trace { display: flex; flex-direction: column; gap: 16px; }
.query-line { display: grid; grid-template-columns: minmax(0, 1fr) auto; gap: 12px; }
.trace-result { max-width: 900px; }
.result-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
</style>

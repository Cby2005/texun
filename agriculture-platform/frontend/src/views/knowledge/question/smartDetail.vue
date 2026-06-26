<template>
  <div class="page" v-loading="pageLoading">
    <div class="page-header"><h2>问题详情</h2></div>
    <el-card v-if="question">
      <div class="title-row">
        <h2>{{ question.title || '未命名问题' }}</h2>
        <el-tag :type="statusTagType(question.status)">{{ question.statusText || '未知' }}</el-tag>
      </div>
      <div class="meta">
        <span>作物：{{ question.cropType || '-' }}</span>
        <span>{{ formatTime(question.createTime) }}</span>
      </div>
      <el-divider />
      <section><h4>问题描述</h4><div class="content">{{ question.content || question.description || '-' }}</div></section>

      <!-- 图片 -->
      <section v-if="question.imageUrls && question.imageUrls.length" class="section-block">
        <h4>上传图片</h4>
        <div class="image-list">
          <el-image v-for="(url, idx) in question.imageUrls" :key="idx" :src="url" :preview-src-list="question.imageUrls" fit="cover" class="question-image" />
        </div>
      </section>

      <!-- YOLO识别结果 -->
      <section v-if="question.yoloStatus && question.yoloStatus > 0" class="section-block yolo-panel">
        <h4><el-icon><Camera /></el-icon> 智能识别结果</h4>
        <el-descriptions v-if="question.yoloStatus === 1" :column="2" border size="small">
          <el-descriptions-item label="识别病虫害">{{ question.yoloDiseaseName || '未识别到明确病虫害' }}</el-descriptions-item>
          <el-descriptions-item label="识别置信度">
            <el-progress :percentage="Math.round(Number(question.yoloConfidence || 0) * 100)" :color="Number(question.yoloConfidence || 0) >= 0.8 ? '#67c23a' : '#e6a23c'" :stroke-width="16" />
          </el-descriptions-item>
        </el-descriptions>
        <el-alert v-else-if="question.yoloStatus === 2" title="图片识别结果不够明确" type="warning" description="系统已转交农业专家进行人工处理。" show-icon :closable="false" />
      </section>

      <!-- Agent诊断建议 -->
      <section v-if="question.agentSuggestion" class="section-block agent-panel">
        <h4><el-icon><MagicStick /></el-icon> AI 诊断建议</h4>
        <div class="pre-wrap">{{ question.agentSuggestion }}</div>
        <div v-if="question.agentScore !== null && question.agentScore !== undefined" class="score-line">
          <el-tag :type="question.agentScore >= 90 ? 'success' : 'warning'">AI评分：{{ question.agentScore }}分</el-tag>
        </div>
      </section>

      <!-- 专家回复 -->
      <section v-if="question.expertReply" class="section-block expert-panel">
        <h4><el-icon><User /></el-icon> 专家审核回复</h4>
        <div class="pre-wrap">{{ question.expertReply }}</div>
      </section>

      <el-alert v-if="!question.expertReply && question.status === '4'" class="section-block" title="问题正在等待专家审核" type="info" show-icon :closable="false" />
    </el-card>
    <el-button style="margin-top:16px" @click="$router.back()">返回</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getQuestionDetail } from '../../../api/knowledge'

const route = useRoute()
const question = ref(null), pageLoading = ref(false)

const formatTime = (t) => t ? new Date(t).toLocaleString('zh-CN') : '-'
const statusTagType = (s) => {
  const v = Number(s)
  if (v === 5 || v === 6 || v === 3) return 'success'
  if (v === 4) return 'warning'
  return 'info'
}

async function fetchData() {
  pageLoading.value = true
  try {
    const r = await getQuestionDetail(route.params.id)
    question.value = r.data
  } catch(e) { ElMessage.error('获取详情失败') }
  finally { pageLoading.value = false }
}
onMounted(fetchData)
</script>

<style scoped>
.title-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.title-row h2 { margin: 0; font-size: 22px; color: #303133; word-break: break-word; }
.meta { display: flex; gap: 20px; color: #909399; font-size: 14px; margin-top: 10px; }
.content, .pre-wrap { white-space: pre-wrap; word-break: break-word; line-height: 1.8; }
section h4 { margin: 0 0 10px; color: #303133; }
.section-block { margin-top: 18px; }
.image-list { display: flex; flex-wrap: wrap; gap: 10px; }
.question-image { width: 180px; height: 128px; border-radius: 6px; border: 1px solid #ebeef5; }
.yolo-panel { padding: 16px; background: #f5f7fa; border-radius: 6px; }
.agent-panel { padding: 16px; background: #ecf5ff; border-left: 4px solid #409eff; border-radius: 6px; }
.expert-panel { padding: 16px; background: #f0f9eb; border-left: 4px solid #67c23a; border-radius: 6px; }
.score-line { margin-top: 12px; }
</style>

<template>
  <div class="page">
    <el-button @click="$router.back()" style="margin-bottom:16px"><el-icon><ArrowLeft /></el-icon>返回</el-button>

    <el-card v-loading="loading" v-if="question">
      <div class="title-row">
        <h2>{{ question.title }}</h2>
        <el-tag v-if="question.status==='RESOLVED'||question.status===1" type="success">已解决</el-tag>
        <el-tag v-else-if="question.status==='PENDING'" type="warning">待回答</el-tag>
      </div>
      <div class="meta">
        <span>提问者：{{ question.authorName || question.username || '匿名' }}</span>
        <span v-if="question.category">分类：{{ question.category }}</span>
        <span>浏览：{{ question.viewCount || 0 }}</span>
        <span>{{ formatTime(question.createTime) }}</span>
      </div>
      <el-divider />
      <div class="question-content">{{ question.content || question.description }}</div>

      <!-- 图片 -->
      <div v-if="question.imageUrls && question.imageUrls.length" class="image-panel">
        <h4>上传图片</h4>
        <el-image v-for="(url, idx) in question.imageUrls" :key="idx" :src="url" :preview-src-list="question.imageUrls" style="width:200px;margin-right:8px;margin-bottom:8px;border-radius:8px" />
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <el-button :type="question.liked ? 'primary' : 'default'" size="small" @click="toggleLike">
          <el-icon><CaretTop /></el-icon> {{ question.liked ? '已点赞' : '点赞' }} {{ question.likeCount || 0 }}
        </el-button>
        <el-button :type="question.favorited ? 'warning' : 'default'" size="small" @click="toggleFavorite">
          <el-icon><Star /></el-icon> {{ question.favorited ? '已收藏' : '收藏' }} {{ question.favoriteCount || 0 }}
        </el-button>
      </div>
    </el-card>

    <!-- 回答区 -->
    <el-card style="margin-top:16px">
      <template #header><span>回答 ({{ answers.length }})</span></template>
      <div v-if="answers.length===0" style="color:#909399;text-align:center;padding:20px">暂无回答</div>
      <div v-for="item in answers" :key="item.id" class="answer-item" :class="{ 'accepted': item.isAccepted===1 }">
        <div class="answer-header">
          <span class="author">{{ item.authorName || '匿名' }}</span>
          <div>
            <el-tag v-if="item.isAccepted===1" type="success" size="small" style="margin-right:8px">最佳答案</el-tag>
            <span class="time">{{ formatTime(item.createTime || item.createdAt) }}</span>
          </div>
        </div>
        <div class="answer-content">{{ item.content }}</div>
        <div v-if="isOwner && question.status!=='RESOLVED' && question.status!==1 && item.isAccepted!==1" class="answer-actions">
          <el-button type="success" size="small" @click="handleAdopt(item.id)">采纳为最佳答案</el-button>
        </div>
        <el-divider />
      </div>

      <h4 style="margin-top:16px">发表回答</h4>
      <el-input v-model="newAnswer" type="textarea" :rows="4" placeholder="请输入你的回答" style="margin-top:10px" />
      <el-button type="primary" :loading="submitting" style="margin-top:10px" @click="handleSubmitAnswer">提交回答</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getQuestion, getAnswers, createAnswer, adoptAnswer, likeQuestion, unlikeQuestion, favoriteQuestion, cancelFavoriteQuestion } from '../../../api/knowledge'

const route = useRoute()
const question = ref(null), answers = ref([]), loading = ref(false), submitting = ref(false), newAnswer = ref('')

const isOwner = computed(() => question.value && question.value.userId === 1) // TODO: use actual user context

function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '-' }

async function fetchData() {
  loading.value = true
  try {
    const [qR, aR] = await Promise.all([getQuestion(route.params.id), getAnswers(route.params.id)])
    question.value = qR.data || qR; answers.value = aR.data || aR || []
  } finally { loading.value = false }
}

async function toggleLike() {
  try {
    if (question.value.liked) {
      await unlikeQuestion(question.value.id); question.value.liked = false
      question.value.likeCount = Math.max((question.value.likeCount||1)-1, 0)
    } else {
      await likeQuestion(question.value.id); question.value.liked = true
      question.value.likeCount = (question.value.likeCount||0) + 1
    }
  } catch(e) { ElMessage.error('操作失败') }
}

async function toggleFavorite() {
  try {
    if (question.value.favorited) {
      await cancelFavoriteQuestion(question.value.id); question.value.favorited = false
      question.value.favoriteCount = Math.max((question.value.favoriteCount||1)-1, 0)
      ElMessage.success('已取消收藏')
    } else {
      await favoriteQuestion(question.value.id); question.value.favorited = true
      question.value.favoriteCount = (question.value.favoriteCount||0) + 1
      ElMessage.success('收藏成功')
    }
  } catch(e) { ElMessage.error('操作失败') }
}

async function handleSubmitAnswer() {
  if (!newAnswer.value.trim()) { ElMessage.warning('请输入回答内容'); return }
  submitting.value = true
  try {
    await createAnswer(route.params.id, { content: newAnswer.value })
    ElMessage.success('回答成功'); newAnswer.value = ''; fetchData()
  } finally { submitting.value = false }
}

async function handleAdopt(answerId) {
  try {
    await adoptAnswer(route.params.id, answerId)
    ElMessage.success('已采纳为最佳答案'); fetchData()
  } catch(e) {}
}

onMounted(fetchData)
</script>

<style scoped>
.title-row { display: flex; align-items: center; gap: 12px; }
.title-row h2 { margin: 0; font-size: 22px; color: #303133; word-break: break-word; }
.meta { display: flex; gap: 20px; color: #909399; font-size: 14px; margin-top: 10px; }
.question-content { line-height: 2; white-space: pre-wrap; word-break: break-word; }
.action-bar { margin-top: 20px; display: flex; gap: 10px; }
.image-panel { margin-top: 16px; }
.image-panel h4 { color: #606266; margin-bottom: 8px; }
.answer-item { padding: 12px; border-radius: 8px; }
.answer-item.accepted { background: #f0f9eb; border: 1px solid #e1f3d8; }
.answer-header { display: flex; justify-content: space-between; align-items: center; }
.answer-header .author { font-weight: 600; color: #303133; }
.answer-header .time { color: #909399; font-size: 13px; }
.answer-content { margin-top: 8px; line-height: 1.6; color: #606266; }
.answer-actions { margin-top: 8px; }
</style>

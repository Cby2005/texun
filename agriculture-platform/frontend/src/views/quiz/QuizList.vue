<template>
  <div class="quiz-list-page">
    <h2>知识测验</h2>
    <p class="sub">完成讲座知识测验，通过后可获得积分奖励</p>

    <el-row :gutter="16">
      <el-col v-for="q in quizzes" :key="q.id" :span="8">
        <el-card shadow="hover" class="quiz-card" @click="goTake(q)">
          <div class="quiz-card-header">
            <el-tag :type="q.passScore >= 90 ? 'warning' : 'success'">{{ q.passScore }}分通过</el-tag>
            <span class="reward">{{ q.rewardPoints }}积分奖励</span>
          </div>
          <div class="quiz-info">
            <p>讲座ID: {{ q.lectureId }} | {{ q.totalQuestions }}题 | 总分{{ q.totalScore }}</p>
            <p v-if="q.answerStartTime" class="time">
              答题时间: {{ q.answerStartTime?.substring(0,16) }} ~ {{ q.answerEndTime?.substring(0,16) }}
            </p>
          </div>
          <el-button type="primary" size="small" style="margin-top:8px">开始答题</el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!quizzes.length && !loading" description="暂无可用测验" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listPublishedQuizzes } from '@/api/quiz'

const router = useRouter()
const quizzes = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try { const r = await listPublishedQuizzes(); quizzes.value = r.data || [] } catch {} finally { loading.value = false }
}
function goTake(q) { router.push('/quiz/' + q.id) }

onMounted(load)
</script>

<style scoped>
.quiz-list-page { padding: 20px; max-width: 1200px; margin: 0 auto; }
.quiz-list-page h2 { margin: 0 0 6px; font-size: 22px; }
.sub { color: #888; margin: 0 0 20px; }
.quiz-card { cursor: pointer; transition: transform .2s; }
.quiz-card:hover { transform: translateY(-2px); }
.quiz-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.reward { font-size: 13px; color: #e6a23c; font-weight: 600; }
.quiz-info p { margin: 4px 0; font-size: 13px; color: #666; }
.time { color: #999 !important; font-size: 12px !important; }
</style>

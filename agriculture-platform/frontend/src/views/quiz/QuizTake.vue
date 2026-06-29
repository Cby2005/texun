<template>
  <div class="quiz-take">
    <div v-if="submitted" class="result-page">
      <el-result :icon="resultData.icon" :title="resultData.title" :sub-title="resultData.sub">
        <template #extra>
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </template>
      </el-result>
    </div>

    <div v-else class="quiz-form">
      <h2>知识测验</h2>
      <div v-for="(q, i) in questions" :key="q.id" class="question-block">
        <div class="q-title">{{ i+1 }}. {{ q.questionContent }} <el-tag size="small">({{ q.score }}分)</el-tag></div>
        <el-radio-group v-model="answers[i]" class="options-group">
          <el-radio v-for="(opt, j) in parseOptions(q.optionsJson)" :key="j" :label="String.fromCharCode(65+j)" :value="String.fromCharCode(65+j)">
            {{ opt }}
          </el-radio>
        </el-radio-group>
      </div>
      <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit" style="margin-top:20px;width:200px">提交答卷</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFarmerQuestions, submitAttempt } from '@/api/quiz'

const route = useRoute(); const router = useRouter()
const quizId = Number(route.params.id)
const questions = ref([])
const answers = ref([])
const submitting = ref(false)
const submitted = ref(false)
const resultData = ref({})

function parseOptions(json) {
  try { return JSON.parse(json) } catch { return [] }
}

async function loadQuestions() {
  try {
    const r = await getFarmerQuestions(quizId)
    questions.value = r.data || []
    answers.value = questions.value.map(() => '')
  } catch (e) { ElMessage.error('测验不可用'); router.back() }
}

async function handleSubmit() {
  const unanswered = answers.value.filter(a => !a).length
  if (unanswered > 0) { ElMessage.warning('还有' + unanswered + '题未作答'); return }
  submitting.value = true
  try {
    const answerList = questions.value.map((q, i) => ({ questionId: q.id, answer: answers.value[i] }))
    const r = await submitAttempt(quizId, answerList)
    const d = r.data
    const pass = d.score >= 90
    resultData.value = {
      icon: pass ? 'success' : 'warning',
      title: pass ? '恭喜通过！' : '未通过',
      sub: `得分: ${d.score}分 | 正确: ${d.correctCount}题 | 错误: ${d.wrongCount}题` + (pass ? ' (已获积分奖励)' : ' (达到90分方可获得积分)')
    }
    submitted.value = true
  } catch (e) { ElMessage.error(e.message) }
  submitting.value = false
}

function goBack() { router.push('/quiz/list') }

onMounted(loadQuestions)
</script>

<style scoped>
.quiz-take { padding: 24px; max-width: 800px; margin: 0 auto; }
.quiz-take h2 { margin: 0 0 20px; }
.question-block { margin-bottom: 20px; padding: 14px; border: 1px solid #eee; border-radius: 8px; background: #fafafa; }
.q-title { margin-bottom: 10px; font-weight: 600; font-size: 15px; display: flex; gap: 8px; align-items: center; }
.options-group { display: flex; flex-direction: column; gap: 8px; }
.result-page { padding: 40px; }
</style>

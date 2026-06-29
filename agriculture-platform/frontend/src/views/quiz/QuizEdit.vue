<template>
  <div class="quiz-edit">
    <div class="page-header">
      <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h2>题目管理 - 测验 #{{ quizId }}</h2>
      <div>
        <el-button type="warning" :loading="generating" @click="handleGenerate">RAG 生成题目草稿</el-button>
        <el-button type="success" @click="handlePublish">发布题目</el-button>
      </div>
    </div>

    <el-card v-if="questions.length" class="question-list">
      <div v-for="(q, i) in questions" :key="q.id" class="question-item">
        <div class="q-header">
          <span>第{{ i+1 }}题 ({{ q.score }}分)</span>
          <el-tag size="small" :type="q.status==='PUBLISHED'?'success':'info'">{{ q.status }}</el-tag>
        </div>
        <el-input v-model="q.questionContent" placeholder="题目内容" class="q-input" />
        <div class="options-row">
          <el-input v-for="(opt, j) in parseOptions(q.optionsJson)" :key="j"
            v-model="parseOptions(q.optionsJson)[j]"
            :placeholder="String.fromCharCode(65+j)+'选项'"
            size="small" class="opt-input"
            @change="syncOptions(q, parseOptions(q.optionsJson))" />
        </div>
        <div class="q-footer">
          <el-select v-model="q.correctAnswer" size="small" style="width:120px">
            <el-option v-for="l in 'ABCD'.split('')" :key="l" :label="l" :value="l" />
          </el-select>
          <el-input v-model="q.explanation" placeholder="答案解析" size="small" style="flex:1" />
          <el-input-number v-model="q.score" :min="5" :max="50" size="small" style="width:80px" />
          <el-button size="small" type="primary" @click="handleSaveQuestion(q)">保存</el-button>
        </div>
      </div>
    </el-card>
    <el-empty v-else description="暂无题目，请点击「RAG生成题目草稿」生成" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listQuestions, generateQuestions, updateQuestion, publishQuiz } from '@/api/quiz'

const route = useRoute(); const router = useRouter()
const quizId = Number(route.params.id)
const questions = ref([])
const generating = ref(false)

function parseOptions(json) {
  try { return JSON.parse(json) } catch { return ['','','',''] }
}
function syncOptions(q, opts) { q.optionsJson = JSON.stringify(opts) }

async function loadQuestions() {
  try { const r = await listQuestions(quizId); questions.value = r.data || [] } catch {}
}
async function handleGenerate() {
  generating.value = true
  try { await generateQuestions(quizId, ''); ElMessage.success('题目已生成，请审核修改后发布'); await loadQuestions() }
  catch (e) { ElMessage.error(e.message) }
  generating.value = false
}
async function handleSaveQuestion(q) {
  try { await updateQuestion(q.id, q); ElMessage.success('已保存') } catch {}
}
async function handlePublish() {
  try { await ElMessageBox.confirm('确认发布？', '发布', { type: 'warning' }); await publishQuiz(quizId); ElMessage.success('已发布'); router.push('/quiz/manage') } catch {}
}

onMounted(loadQuestions)
</script>

<style scoped>
.quiz-edit { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 18px; }
.question-item { border: 1px solid #eee; border-radius: 8px; padding: 14px; margin-bottom: 12px; }
.q-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: 600; }
.q-input { margin-bottom: 8px; }
.options-row { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-bottom: 8px; }
.opt-input { flex: 1; }
.q-footer { display: flex; gap: 8px; align-items: center; }
</style>

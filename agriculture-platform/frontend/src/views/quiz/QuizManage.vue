<template>
  <div class="quiz-manage">
    <div class="page-header">
      <h2>讲座题目管理</h2>
      <el-button type="primary" @click="showCreateDialog">+ 创建新测验</el-button>
    </div>

    <el-table :data="quizList" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="lectureId" label="讲座ID" width="80" />
      <el-table-column label="状态" width="110">
        <template #default="{row}">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalQuestions" label="题目数" width="80" />
      <el-table-column prop="totalScore" label="总分" width="70" />
      <el-table-column prop="passScore" label="通过分" width="80" />
      <el-table-column label="奖励" width="150">
        <template #default="{row}">
          <span v-if="row.rewardThreshold">≥{{row.rewardThreshold}}分 → {{row.rewardPoints}}积分</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="答题时间" width="180">
        <template #default="{row}">
          <span v-if="row.answerStartTime">{{row.answerStartTime?.substring(0,16)}} ~ {{row.answerEndTime?.substring(0,16)}}</span>
          <span v-else>不限</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="editQuiz(row)">编辑设置</el-button>
          <el-button v-if="row.status!=='PUBLISHED'" size="small" type="warning" @click="openQuestionEditor(row)">管理题目</el-button>
          <el-button v-if="row.status==='GENERATED'" size="small" type="success" @click="publishQuiz(row.id)">发布</el-button>
          <el-button size="small" @click="viewAttempts(row.id)">答题统计</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑测验 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑测验设置' : '创建新测验'" width="550px" destroy-on-close>
      <el-form :model="dialog.form" label-width="110px">
        <el-form-item label="所属讲座ID"><el-input-number v-model="dialog.form.lectureId" :min="1" /></el-form-item>
        <el-form-item label="答题开始时间"><el-date-picker v-model="dialog.form.answerStartTime" type="datetime" /></el-form-item>
        <el-form-item label="答题截止时间"><el-date-picker v-model="dialog.form.answerEndTime" type="datetime" /></el-form-item>
        <el-form-item label="是否允许重答"><el-switch v-model="dialog.form.allowRetry" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="最大答题次数"><el-input-number v-model="dialog.form.maxAttempts" :min="1" :max="10" /></el-form-item>
        <el-form-item label="通过分数"><el-input-number v-model="dialog.form.passScore" :min="1" :max="100" /></el-form-item>
        <el-form-item label="奖励阈值(分)"><el-input-number v-model="dialog.form.rewardThreshold" :min="1" :max="100" /></el-form-item>
        <el-form-item label="奖励积分"><el-input-number v-model="dialog.form.rewardPoints" :min="1" :max="1000" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" @click="handleSaveQuiz">保存</el-button>
      </template>
    </el-dialog>

    <!-- 答题统计 -->
    <el-dialog v-model="statsDialog.visible" title="答题统计" width="700px">
      <el-table :data="statsDialog.attempts" size="small" max-height="400">
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="score" label="得分" width="80" />
        <el-table-column prop="correctCount" label="正确" width="70" />
        <el-table-column prop="wrongCount" label="错误" width="70" />
        <el-table-column prop="submitTime" label="提交时间" width="170" />
        <el-table-column label="状态" width="90">
          <template #default="{row}"><el-tag size="small" :type="row.status==='SCORED'?'success':'info'">{{row.status}}</el-tag></template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listQuizzes, createQuiz, updateQuiz, publishQuiz as pubQuiz, listAttempts } from '@/api/quiz'

const router = useRouter()
const quizList = ref([])
const loading = ref(false)

const dialog = reactive({ visible: false, isEdit: false, form: {} })
const statsDialog = reactive({ visible: false, attempts: [] })

function statusTagType(s) {
  return { DRAFT: 'info', GENERATED: 'warning', REVIEWING: 'warning', PUBLISHED: 'success', ARCHIVED: '' }[s] || 'info'
}
function statusLabel(s) {
  return { DRAFT: '草稿', GENERATED: '已生成', REVIEWING: '审核中', PUBLISHED: '已发布', ARCHIVED: '已归档' }[s] || s
}

async function loadList() {
  loading.value = true
  try { const r = await listQuizzes(); quizList.value = r.data || [] } catch {} finally { loading.value = false }
}

function showCreateDialog() {
  dialog.isEdit = false
  dialog.form = { lectureId: null, passScore: 60, rewardThreshold: 90, rewardPoints: 20, allowRetry: 0, maxAttempts: 1 }
  dialog.visible = true
}
function editQuiz(row) {
  dialog.isEdit = true
  dialog.form = { ...row }
  dialog.visible = true
}
async function handleSaveQuiz() {
  try {
    if (dialog.isEdit) { await updateQuiz(dialog.form.id, dialog.form); ElMessage.success('已更新') }
    else { await createQuiz(dialog.form); ElMessage.success('已创建') }
    dialog.visible = false; loadList()
  } catch (e) { ElMessage.error(e.message) }
}
async function publishQuiz(id) {
  try { await ElMessageBox.confirm('确认发布？发布后农户即可答题', '发布确认', { type: 'warning' }); await pubQuiz(id); ElMessage.success('已发布'); loadList() } catch {}
}
function openQuestionEditor(row) { router.push('/quiz/manage/' + row.id) }
async function viewAttempts(id) {
  try { const r = await listAttempts(id); statsDialog.attempts = r.data || []; statsDialog.visible = true } catch {}
}

onMounted(loadList)
</script>

<style scoped>
.quiz-manage { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>

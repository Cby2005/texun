<template>
  <div class="ai-page">
    <!-- ===== 1. 顶部 Hero ===== -->
    <section class="hero">
      <div class="hero-glow"></div>
      <div class="hero-content">
        <div class="hero-badge">
          <svg class="badge-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
          <span>AI 农技助手</span>
        </div>
        <h1 class="hero-title">温室草莓智能问答</h1>
        <p class="hero-desc">基于 DeepSeek 大模型，结合异常植株图片、地块信息与生长阶段，提供草莓种植、病虫害、温室调控等智能诊断建议。</p>
      </div>
    </section>

    <!-- ===== 2. AI 问答输入卡片 ===== -->
    <section class="input-section">
      <div class="input-card">
        <!-- 异常植株联动提示 -->
        <div v-if="abnormalReason" class="twin-banner">
          <el-icon :size="18"><WarningFilled /></el-icon>
          <span>来自数字孪生巡检 — 异常原因：{{ abnormalReason }}</span>
          <el-button text type="primary" size="small" @click="abnormalReason='';detectResult='';imageUrl=''">清除</el-button>
        </div>

        <!-- 图片预览 -->
        <div v-if="imageUrl" class="img-preview-bar">
          <el-image :src="imageUrl" fit="cover" class="preview-thumb" />
          <span>已上传异常植株图片</span>
          <el-button text type="danger" size="small" @click="imageUrl=''">移除</el-button>
        </div>

        <!-- 文本输入 -->
        <div class="input-row">
          <el-input
            v-model="questionContent"
            type="textarea"
            :rows="3"
            :placeholder="abnormalReason ? '已自动填充问题，可补充描述后点击诊断…' : '请输入草莓种植问题，或上传异常植株图片进行智能诊断…'"
            maxlength="2000"
            show-word-limit
            :disabled="loading"
            class="main-input"
          />
        </div>

        <!-- 选项行 -->
        <div class="options-row">
          <div class="opt">
            <el-select v-model="questionType" :disabled="loading" size="default" class="opt-select">
              <el-option v-for="t in questionTypes" :key="t" :label="t" :value="t" />
            </el-select>
          </div>
          <div class="opt">
            <el-select v-model="growthStage" :disabled="loading" size="default" class="opt-select">
              <el-option v-for="s in growthStages" :key="s" :label="s" :value="s" />
            </el-select>
          </div>
          <div class="opt">
            <el-select v-model="baseId" clearable placeholder="所属基地" :disabled="loading" size="default" class="opt-select" @change="onBaseChange">
              <el-option v-for="f in bases" :key="f.id" :label="f.name" :value="f.id" />
            </el-select>
          </div>
          <div class="opt">
            <el-select v-model="greenhouseId" clearable placeholder="所属温室(可选)" :disabled="loading || !baseId" size="default" class="opt-select">
              <el-option v-for="g in greenhouses" :key="g.id" :label="g.name" :value="g.id" />
            </el-select>
          </div>
          <div class="opt opt-upload">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="handleImageUpload"
              accept="image/jpeg,image/png,image/webp"
            >
              <el-button :icon="uploadLoading ? Loading : Camera" :loading="uploadLoading" :disabled="loading" circle class="upload-btn" />
            </el-upload>
          </div>
          <div class="opt-submit">
            <el-button type="primary" size="default" :loading="loading" :disabled="!questionContent.trim()" class="diagnose-btn" @click="handleAsk">
              <template v-if="!loading">AI 诊断</template>
              <template v-else>AI 正在分析…</template>
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== 3. 快捷操作按钮 ===== -->
    <section class="quick-actions">
      <el-button round class="quick-btn" @click="$router.push('/drone/greenhouse-simulation')">
        <el-icon><Search /></el-icon> 异常植株诊断
      </el-button>
      <el-button round class="quick-btn" @click="loadMyBaseData">
        <el-icon><DataBoard /></el-icon> 使用我的基地数据
      </el-button>
    </section>

    <!-- ===== AI 回答区 ===== -->
    <section v-if="currentAnswer || loading" class="answer-section">
      <div v-if="loading" class="answer-loading">
        <div class="loading-spin"><el-icon :size="36" class="is-loading"><Loading /></el-icon></div>
        <p>AI 正在分析，正在调用 DeepSeek 大模型为您生成农技建议…</p>
        <div class="loading-dots"><span></span><span></span><span></span></div>
      </div>
      <div v-else class="answer-card">
        <div class="answer-card-top">
          <div class="answer-badge"><el-icon><CircleCheck /></el-icon> 智能农技建议</div>
          <el-tag size="small" effect="plain" type="info">{{ currentAnswer.questionType }}</el-tag>
        </div>
        <div class="answer-body">
          <div v-for="sec in parsedSectionsArr" :key="sec.label" :class="['answer-block', sec.highlight ? 'highlight' : '']">
            <div class="block-label">{{ sec.label }}</div>
            <div class="block-text">{{ sec.text }}</div>
          </div>
        </div>
        <el-alert v-if="hasSafetyWarning" type="warning" :closable="false" show-icon
          description="涉及用药、施肥或病害处理时，请结合当地农技规范或在专业人员指导下操作。" class="safety-tip" />
        <div class="answer-footer">
          <div class="answer-meta">
            <span>模型：{{ currentAnswer.modelName || 'DeepSeek' }}</span>
            <span v-if="currentAnswer.totalTokens">{{ currentAnswer.totalTokens }} tokens</span>
            <span>{{ formatTime(currentAnswer.createTime) }}</span>
          </div>
          <div class="answer-feedback">
            <el-button size="small" :type="feedbackStatus === 'helpful' ? 'success' : ''" :disabled="!!feedbackStatus" @click="handleFeedback('helpful')">有帮助</el-button>
            <el-button size="small" :type="feedbackStatus === 'unhelpful' ? 'danger' : ''" :disabled="!!feedbackStatus" @click="handleFeedback('unhelpful')">不满意</el-button>
            <el-button v-if="currentAnswer.needExpertReview !== 1" size="small" type="warning" plain @click="handleExpertReview">转专家复核</el-button>
            <el-button size="small" plain @click="resetQuestion">继续提问</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== 4. 示例问题 / 最近问答 ===== -->
    <section class="bottom-section">
      <el-tabs v-model="bottomTab" class="bottom-tabs">
        <el-tab-pane label="示例问题" name="examples">
          <div class="example-cards">
            <div v-for="ex in exampleList" :key="ex.title" class="example-card" @click="fillExample(ex)">
              <div class="ex-img" :style="{ background: ex.bg }">
                <el-icon :size="32" color="#fff"><component :is="ex.icon" /></el-icon>
              </div>
              <div class="ex-body">
                <h4>{{ ex.title }}</h4>
                <p>{{ ex.desc }}</p>
              </div>
              <el-icon class="ex-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="最近问答" name="history">
          <div v-if="historyList.length === 0" class="history-empty">
            <el-empty description="暂无问答记录" :image-size="80">
              <template #description><span style="color:#999;font-size:13px">暂无问答记录，试着向 AI 农技助手提一个问题吧。</span></template>
            </el-empty>
          </div>
          <div v-else class="history-list">
            <div v-for="h in historyList" :key="h.id" class="history-row" @click="viewHistory(h)">
              <div class="h-main">
                <div class="h-question">{{ h.questionContent }}</div>
                <div class="h-tags">
                  <el-tag size="small" type="info">{{ h.questionType }}</el-tag>
                  <el-tag v-if="h.relatedImage" size="small" type="warning">含图片</el-tag>
                  <el-tag size="small" :type="h.answerStatus === 'answered' ? 'success' : 'danger'">
                    {{ h.answerStatus === 'answered' ? '已回答' : '失败' }}
                  </el-tag>
                </div>
              </div>
              <div class="h-time">{{ formatTime(h.createTime) }}</div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </section>

    <!-- 回到顶部 -->
    <div v-if="showBackTop" class="back-top" @click="scrollToTop">
      <el-icon :size="20"><Top /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { askAiQuestion, listMyAiQuestions, feedbackAiQuestion, requestExpertReview } from '@/api/ai'
import { uploadImage } from '@/api/common/upload'
import { getAllEnterprises } from '@/api/farm'

const route = useRoute()
const router = useRouter()

// ── 常量 ──
const questionTypes = ['草莓种植管理', '病虫害防治', '温室环境调控', '水肥一体化', '采摘与销售', '设备巡检', '其他问题']
const growthStages = ['育苗期', '生长期', '开花期', '结果期', '成熟期', '采摘期']

const exampleList = [
  { title: '草莓叶片病斑诊断', desc: '识别叶片病斑类型，分析病害原因并提供防治建议。',
    bg: 'linear-gradient(135deg,#43a047,#81c784)', icon: 'Apple', question: '草莓叶片出现褐色病斑，边缘发黄，请问是什么病害？如何防治？', type: '病虫害防治' },
  { title: '果实畸形原因分析', desc: '分析授粉、温度、养分等因素对果实畸形的影响并提出改善方案。',
    bg: 'linear-gradient(135deg,#e65100,#ff9800)', icon: 'Orange', question: '草莓果实出现明显畸形，大小不均匀，请问可能的原因有哪些？如何改善？', type: '草莓种植管理' },
  { title: '温室湿度调控建议', desc: '根据当前湿度与病害风险，提供降低灰霉病风险的调控建议。',
    bg: 'linear-gradient(135deg,#0277bd,#29b6f6)', icon: 'Cloudy', question: '温室草莓结果期湿度持续偏高，如何降低灰霉病风险？', type: '温室环境调控' }
]

// ── 表单 ──
const questionContent = ref('')
const questionType = ref('草莓种植管理')
const growthStage = ref('结果期')
const baseId = ref(null)
const greenhouseId = ref(null)
const imageUrl = ref('')
const uploadLoading = ref(false)
const loading = ref(false)

// ── 数字孪生联动 ──
const abnormalReason = ref('')
const detectResult = ref('')
const confidence = ref('')
const plotId = ref(null)
const plantingRecordId = ref(null)

// ── 下拉数据 ──
const bases = ref([])
const greenhouses = ref([])

// ── 回答 / 历史 ──
const currentAnswer = ref(null)
const feedbackStatus = ref('')
const historyList = ref([])
const bottomTab = ref('examples')
const showBackTop = ref(false)

// ── computed ──
const parsedSectionsArr = computed(() => {
  const text = currentAnswer.value?.aiAnswer || ''
  const sections = []
  const labels = ['问题判断','可能原因','处理建议','预防措施','是否建议人工复核','是否需要人工复核']
  for (const l of labels) {
    const m = text.match(new RegExp(`【${l}】\\s*\\n?([\\s\\S]*?)(?=【|$)`,'m'))
    if (m) sections.push({ label: l === '是否需要人工复核' ? '是否建议人工复核' : l, text: m[1].trim(), highlight: l === '处理建议' })
  }
  return sections
})
const hasSafetyWarning = computed(() => /农药|肥料|施肥|用药|病害|药剂|杀菌|杀虫/.test(currentAnswer.value?.aiAnswer || ''))

// ── 上传 ──
function beforeImageUpload(file) {
  if (!['image/jpeg','image/png','image/webp'].includes(file.type)) { ElMessage.error('只支持 jpg/png/webp'); return false }
  if (file.size > 5*1024*1024) { ElMessage.error('图片大小不能超过5MB'); return false }
  return true
}
async function handleImageUpload({ file }) {
  uploadLoading.value = true
  try { const r = await uploadImage(file, 'disease_image'); imageUrl.value = r.data.fileUrl; ElMessage.success('图片上传成功') }
  catch (e) { ElMessage.error('上传失败') }
  uploadLoading.value = false
}

// ── 基地 / 温室 ──
async function loadBases() {
  try { const r = await getAllEnterprises(); bases.value = r.data || [] } catch {}
}
function onBaseChange() {
  greenhouseId.value = null
  greenhouses.value = []
  if (!baseId.value) return
  // ponytail: 项目无独立温室表，使用地块作为温室近似
  // 预留：可对接 GET /api/farm/lands?baseId=
}

// ── 快捷操作 ──
function loadMyBaseData() {
  if (bases.value.length > 0) {
    baseId.value = bases.value[0].id
    ElMessage.success('已自动选择基地：' + bases.value[0].name)
  } else {
    ElMessage.warning('暂无可用的基地数据')
  }
}

function fillExample(ex) {
  questionContent.value = ex.question
  questionType.value = ex.type
  bottomTab.value = 'examples'
  window.scrollTo({ top: 200, behavior: 'smooth' })
  ElMessage.success('已填充问题，请点击 AI 诊断')
}

// ── 提交 ──
async function handleAsk() {
  if (!questionContent.value.trim()) return ElMessage.warning('问题内容不能为空')
  loading.value = true; currentAnswer.value = null
  try {
    const params = {
      questionContent: questionContent.value.trim(),
      questionType: questionType.value, growthStage: growthStage.value,
      baseId: baseId.value, greenhouseId: greenhouseId.value,
      plotId: plotId.value, plantingRecordId: plantingRecordId.value,
      imageUrl: imageUrl.value, abnormalReason: abnormalReason.value,
      detectResult: detectResult.value, confidence: confidence.value,
      plotInfo: abnormalReason.value ? '数字孪生巡检异常植株' : ''
    }
    const res = await askAiQuestion(params)
    currentAnswer.value = res.data
    feedbackStatus.value = ''
    ElMessage.success('AI 回答已生成')
    loadHistory()
    // 滚动到回答区
    setTimeout(() => { window.scrollTo({ top: 360, behavior: 'smooth' }) }, 100)
  } catch (e) { ElMessage.error(e.message || 'AI 服务暂时不可用') }
  loading.value = false
}

async function handleFeedback(type) {
  if (!currentAnswer.value) return
  try { await feedbackAiQuestion(currentAnswer.value.questionId, type, ''); feedbackStatus.value = type; ElMessage.success(type === 'helpful' ? '感谢反馈！' : '已记录') } catch {}
}
async function handleExpertReview() {
  if (!currentAnswer.value) return
  try { await ElMessageBox.confirm('确认将该问题转给专家复核？', '转专家复核', { type: 'warning' }); await requestExpertReview(currentAnswer.value.questionId); ElMessage.success('已转专家复核') } catch {}
}

function viewHistory(item) {
  if (item.aiAnswer) {
    currentAnswer.value = { questionId: item.id, aiAnswer: item.aiAnswer, questionType: item.questionType,
      relatedImage: item.relatedImage, modelName: item.modelName, totalTokens: item.totalTokens,
      needExpertReview: item.needExpertReview, createTime: item.createTime }
    feedbackStatus.value = item.userFeedback === 'helpful' ? 'helpful' : item.userFeedback === 'unhelpful' ? 'unhelpful' : ''
    setTimeout(() => { window.scrollTo({ top: 360, behavior: 'smooth' }) }, 100)
  } else {
    ElMessage.info('该问题暂无 AI 回答')
  }
}

function resetQuestion() {
  questionContent.value = ''; currentAnswer.value = null; feedbackStatus.value = ''
  imageUrl.value = ''; abnormalReason.value = ''; detectResult.value = ''
}

async function loadHistory() {
  try { const r = await listMyAiQuestions({ page: 1, pageSize: 20 }); historyList.value = r.data?.records || [] } catch {}
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

// ── 滚动控制 ──
function onScroll() { showBackTop.value = window.scrollY > 400 }
function scrollToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }

onMounted(() => {
  loadBases(); loadHistory(); window.addEventListener('scroll', onScroll)

  // 数字孪生联动
  const q = route.query
  if (q.imageUrl) imageUrl.value = q.imageUrl
  if (q.abnormalReason) abnormalReason.value = q.abnormalReason
  if (q.detectResult) detectResult.value = q.detectResult
  if (q.confidence) confidence.value = q.confidence
  if (q.plotId) plotId.value = Number(q.plotId)
  if (q.plantingRecordId) plantingRecordId.value = Number(q.plantingRecordId)
  if (q.baseId) { baseId.value = Number(q.baseId) }
  if (q.growthStage) growthStage.value = q.growthStage
  if (abnormalReason.value && !questionContent.value) {
    questionContent.value = '请根据该异常植株图片和巡检结果，分析草莓植株异常原因，并给出处理建议。'
  }
})

onUnmounted(() => { window.removeEventListener('scroll', onScroll) })
</script>

<style scoped>
/* ===== 全局 ===== */
.ai-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #f0f9f0 0%, #f5f3ff 40%, #fff 100%);
  padding: 0 24px 60px;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== Hero ===== */
.hero {
  position: relative;
  text-align: center;
  padding: 56px 20px 44px;
  overflow: hidden;
}
.hero-glow {
  position: absolute; top: -80px; left: 50%; transform: translateX(-50%);
  width: 600px; height: 300px;
  background: radial-gradient(ellipse, rgba(76,175,80,0.12) 0%, transparent 70%);
  pointer-events: none;
}
.hero-content { position: relative; z-index: 1; max-width: 720px; margin: 0 auto; }
.hero-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 6px 16px; border-radius: 999px;
  background: rgba(76,175,80,0.08); border: 1px solid rgba(76,175,80,0.15);
  color: #2e7d32; font-size: 13px; font-weight: 600; margin-bottom: 16px;
  backdrop-filter: blur(8px);
}
.badge-icon { width: 18px; height: 18px; flex-shrink: 0; }
.hero-title {
  font-size: 38px; font-weight: 800; color: #1a1a2e; margin: 0 0 12px;
  letter-spacing: -0.5px; line-height: 1.2;
}
.hero-desc {
  font-size: 15px; color: #777; line-height: 1.7; margin: 0 auto; max-width: 560px;
}

/* ===== 输入卡片 ===== */
.input-section { max-width: 820px; margin: 0 auto 20px; }
.input-card {
  background: #fff; border-radius: 20px; padding: 24px;
  box-shadow: 0 4px 32px rgba(0,0,0,0.04), 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
}
/* 数字孪生提示 */
.twin-banner {
  display: flex; align-items: center; gap: 8px;
  background: #fff3e0; border: 1px solid #ffe0b2; border-radius: 10px;
  padding: 10px 14px; margin-bottom: 14px; font-size: 13px; color: #e65100;
}
.twin-banner .el-button { margin-left: auto; }
/* 图片预览 */
.img-preview-bar {
  display: flex; align-items: center; gap: 10px;
  background: #f5f5f5; border-radius: 10px; padding: 8px 14px; margin-bottom: 14px; font-size: 13px; color: #555;
}
.preview-thumb { width: 52px; height: 52px; border-radius: 8px; flex-shrink: 0; }
.img-preview-bar .el-button { margin-left: auto; }

.main-input { margin-bottom: 14px; }
.main-input :deep(textarea) { font-size: 15px; line-height: 1.6; padding: 14px 16px; border-radius: 12px; }

/* 选项行 */
.options-row { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.opt { flex: 1 1 140px; min-width: 120px; }
.opt-select { width: 100%; }
.opt-upload { flex: 0 0 auto; }
.upload-btn { width: 40px; height: 40px; color: #888; border: 1px dashed #d9d9d9; }
.upload-btn:hover { border-color: #43a047; color: #43a047; }
.opt-submit { flex: 0 0 auto; margin-left: auto; }
.diagnose-btn {
  height: 40px; padding: 0 28px; font-size: 15px; font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #43a047, #66bb6a);
  border: none;
  box-shadow: 0 2px 12px rgba(76,175,80,0.3);
  transition: all .25s;
}
.diagnose-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 18px rgba(76,175,80,0.4); }

/* ===== 快捷操作 ===== */
.quick-actions { display: flex; justify-content: center; gap: 14px; margin-bottom: 28px; }
.quick-btn {
  height: 42px; padding: 0 22px; border-radius: 999px;
  border: 1px solid #e0e0e0; background: #fff; color: #555; font-size: 14px;
  transition: all .2s;
}
.quick-btn:hover { border-color: #43a047; color: #43a047; background: #f6fdf6; }

/* ===== 回答区 ===== */
.answer-section { max-width: 820px; margin: 0 auto 28px; }
.answer-card {
  background: #fff; border-radius: 20px; padding: 24px 28px;
  box-shadow: 0 4px 32px rgba(0,0,0,0.04), 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
}
.answer-card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; padding-bottom: 14px; border-bottom: 1px solid #f5f5f5; }
.answer-badge { display: flex; align-items: center; gap: 6px; font-weight: 700; font-size: 16px; color: #2e7d32; }
.answer-block { margin-bottom: 16px; }
.answer-block.highlight { background: #f1f8e9; border-left: 3px solid #67c23a; border-radius: 0 8px 8px 0; padding: 12px 16px; margin-left: -4px; }
.block-label { font-size: 14px; font-weight: 700; color: #333; margin-bottom: 6px; }
.block-text { font-size: 14px; color: #555; line-height: 1.8; white-space: pre-wrap; }
.safety-tip { margin: 14px 0; border-radius: 10px; }
.answer-footer { border-top: 1px solid #f0f0f0; padding-top: 14px; margin-top: 8px; }
.answer-meta { font-size: 12px; color: #bbb; display: flex; gap: 16px; margin-bottom: 10px; }
.answer-feedback { display: flex; gap: 8px; flex-wrap: wrap; }

/* 加载 */
.answer-loading { text-align: center; padding: 48px 20px; background: #fff; border-radius: 20px; box-shadow: 0 4px 32px rgba(0,0,0,0.04); }
.loading-spin { color: #43a047; margin-bottom: 14px; }
.answer-loading p { color: #888; font-size: 14px; margin: 0 0 14px; }
.loading-dots { display: flex; justify-content: center; gap: 6px; }
.loading-dots span { width: 8px; height: 8px; border-radius: 50%; background: #43a047; animation: bounce 1.2s infinite; }
.loading-dots span:nth-child(2) { animation-delay: .2s; }
.loading-dots span:nth-child(3) { animation-delay: .4s; }
@keyframes bounce { 0%,80%,100% { opacity: 0.3; transform: scale(0.8); } 40% { opacity: 1; transform: scale(1); } }

/* ===== 底部区域 ===== */
.bottom-section { max-width: 820px; margin: 0 auto; }
.bottom-tabs { background: #fff; border-radius: 18px; padding: 8px 24px 24px; box-shadow: 0 2px 20px rgba(0,0,0,0.03); }

/* 示例卡片 */
.example-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
.example-card {
  display: flex; align-items: center; gap: 12px;
  border: 1px solid #f0f0f0; border-radius: 14px; padding: 14px;
  cursor: pointer; transition: all .25s; background: #fff;
}
.example-card:hover { border-color: #c8e6c9; box-shadow: 0 4px 16px rgba(76,175,80,0.08); transform: translateY(-2px); }
.ex-img { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.ex-body { flex: 1; min-width: 0; }
.ex-body h4 { margin: 0 0 4px; font-size: 14px; color: #222; }
.ex-body p { margin: 0; font-size: 12px; color: #999; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.ex-arrow { color: #ccc; flex-shrink: 0; font-size: 18px; transition: transform .2s; }
.example-card:hover .ex-arrow { transform: translateX(4px); color: #43a047; }

/* 历史 */
.history-list { }
.history-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; border-radius: 12px; cursor: pointer; transition: background .15s;
  border: 1px solid transparent; margin-bottom: 4px;
}
.history-row:hover { background: #f8fdf8; border-color: #e8f5e9; }
.h-main { flex: 1; min-width: 0; }
.h-question { font-size: 14px; color: #333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: 6px; }
.h-tags { display: flex; gap: 4px; }
.h-time { font-size: 12px; color: #bbb; white-space: nowrap; margin-left: 16px; }
.history-empty { padding: 20px 0; }

/* 回到顶部 */
.back-top {
  position: fixed; bottom: 40px; right: 30px;
  width: 44px; height: 44px; border-radius: 50%;
  background: #fff; box-shadow: 0 2px 12px rgba(0,0,0,0.1); border: 1px solid #eee;
  display: flex; align-items: center; justify-content: center; cursor: pointer; z-index: 99;
  transition: all .2s;
}
.back-top:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.15); color: #43a047; }

@media (max-width: 768px) {
  .hero-title { font-size: 26px; }
  .options-row { flex-direction: column; }
  .opt-submit { width: 100%; }
  .diagnose-btn { width: 100%; }
  .example-cards { grid-template-columns: 1fr; }
}
</style>

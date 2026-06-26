<template>
  <div class="page smart-question-page">
    <div class="page-header"><h2>智能提问</h2><p>上传病虫害图片，系统会先进行AI识别（Roboflow YOLO），必要时转交专家审核</p></div>

    <!-- 提交结果 -->
    <el-card v-if="submitResult" shadow="hover">
      <template #header>
        <div class="result-header">
          <el-icon :size="24" :color="submitResult.status === 3 ? '#67C23A' : '#E6A23C'">
            <CircleCheckFilled v-if="submitResult.status === 3" /><InfoFilled v-else />
          </el-icon>
          <span>{{ submitResult.statusText }}</span>
        </div>
      </template>

      <div v-if="submitResult.status === 3" class="auto-reply">
        <el-alert type="success" :closable="false" show-icon class="result-alert">
          <template #title>YOLO识别：<b>{{ submitResult.yoloDiseaseName }}</b>，置信度：<b>{{ toPercent(submitResult.yoloConfidence) }}</b></template>
        </el-alert>
        <div v-if="submitResult.agentSuggestion" class="agent-suggestion">
          <h4>AI诊断建议</h4>
          <div class="suggestion-content">{{ submitResult.agentSuggestion }}</div>
        </div>
        <div v-if="submitResult.agentScore" class="score-info">
          <el-tag type="success">回答评分：{{ submitResult.agentScore }}分</el-tag>
        </div>
      </div>
      <div v-else class="expert-review">
        <el-alert type="warning" :closable="false" show-icon class="result-alert">
          <template #title>系统已转交农业专家审核，请耐心等待</template>
        </el-alert>
        <div v-if="submitResult.yoloDiseaseName" class="yolo-info">
          <p>YOLO识别结果：<b>{{ submitResult.yoloDiseaseName }}</b>（置信度：{{ toPercent(submitResult.yoloConfidence) }}）</p>
          <p class="muted">识别置信度不足或AI评分未达标，已转交农业专家审核。</p>
        </div>
        <div v-else class="yolo-info">
          <p class="muted">图片识别结果不够明确，系统已转交农业专家处理。</p>
        </div>
      </div>
      <div class="result-actions">
        <el-button type="primary" @click="goHome">返回列表</el-button>
        <el-button @click="resetForm">继续提问</el-button>
      </div>
    </el-card>

    <!-- 表单 -->
    <el-card v-else shadow="hover">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="问题标题" prop="title">
          <el-input v-model="form.title" placeholder="例如：番茄叶片出现黄色斑点" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="作物类型" prop="cropType">
          <el-select v-model="form.cropType" placeholder="请选择作物类型" filterable allow-create>
            <el-option label="水稻" value="水稻" /><el-option label="小麦" value="小麦" />
            <el-option label="玉米" value="玉米" /><el-option label="大豆" value="大豆" />
            <el-option label="番茄" value="番茄" /><el-option label="黄瓜" value="黄瓜" />
            <el-option label="苹果" value="苹果" /><el-option label="柑橘" value="柑橘" />
            <el-option label="葡萄" value="葡萄" /><el-option label="辣椒" value="辣椒" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option label="病虫害防治" :value="1" /><el-option label="种植技术" :value="2" />
            <el-option label="土壤肥料" :value="3" /><el-option label="养殖技术" :value="4" />
            <el-option label="农机设备" :value="5" /><el-option label="其他问题" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请详细描述症状、发现时间、种植环境和已经采取的处理措施" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="所在地区"><el-input v-model="form.region" placeholder="例如：山东省寿光市" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生长阶段">
            <el-select v-model="form.growthStage" placeholder="选择生长阶段" clearable>
              <el-option label="苗期" value="苗期" /><el-option label="生长期" value="生长期" />
              <el-option label="开花期" value="开花期" /><el-option label="结果期" value="结果期" />
              <el-option label="成熟期" value="成熟期" />
            </el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="病虫害图片">
          <el-upload ref="uploadRef" class="upload-area" drag :auto-upload="false" :limit="1" accept=".jpg,.jpeg,.png,.webp" :before-upload="beforeUpload" :on-change="handleFileChange" :on-exceed="handleExceed" :on-remove="handleRemove">
            <el-icon :size="40" class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">拖拽图片到此处，或<em>点击上传</em></div>
            <template #tip><div class="el-upload__tip">支持 jpg/jpeg/png/webp，不超过5MB。上传后会自动进行AI识别。</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="imagePreview" label="图片预览">
          <el-image :src="imagePreview" :preview-src-list="[imagePreview]" class="preview-image" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" size="large">{{ submitting ? '正在智能识别中...' : '提交问题' }}</el-button>
          <el-button @click="resetForm" size="large">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 加载弹窗 -->
    <el-dialog v-model="submitting" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="400px" center>
      <div class="loading-dialog">
        <el-icon :size="48" color="#409EFF" class="rotating"><Loading /></el-icon>
        <h3>正在进行智能识别，请稍候</h3>
        <p>系统正在通过Roboflow YOLO模型识别病虫害...</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitSmartQuestion } from '../../../api/knowledge'

const router = useRouter()
const formRef = ref(null), uploadRef = ref(null), submitting = ref(false)
const imageFile = ref(null), imagePreview = ref(''), submitResult = ref(null)

const form = reactive({ title: '', cropType: '', categoryId: null, description: '', region: '', growthStage: '' })
const rules = {
  title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
  cropType: [{ required: true, message: '请选择作物类型', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择问题分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
}

const toPercent = (v) => v !== null && v !== undefined ? `${(Number(v) * 100).toFixed(1)}%` : '-'

function beforeUpload(file) {
  const ok = ['image/jpeg','image/png','image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持 jpg/jpeg/png/webp 格式'); return false }
  if (file.size > 5*1024*1024) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}
function handleFileChange(file) {
  if (!beforeUpload(file.raw)) { uploadRef.value?.clearFiles(); return }
  imageFile.value = file.raw; imagePreview.value = URL.createObjectURL(file.raw)
}
function handleExceed() { ElMessage.warning('只能上传一张图片') }
function handleRemove() { imageFile.value = null; imagePreview.value = '' }

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    fd.append('cropType', form.cropType)
    fd.append('categoryId', form.categoryId)
    fd.append('description', form.description)
    if (form.region) fd.append('region', form.region)
    if (form.growthStage) fd.append('growthStage', form.growthStage)
    if (imageFile.value) fd.append('image', imageFile.value)
    const r = await submitSmartQuestion(fd)
    submitResult.value = r.data
    if (r.data.status === 3) ElMessage.success('智能识别完成！已自动生成诊断建议')
    else ElMessage.info('问题已提交，已转交专家审核')
  } catch(e) { ElMessage.error('提交失败：' + (e.message || '系统异常')) }
  finally { submitting.value = false }
}

function resetForm() {
  submitResult.value = null; imageFile.value = null; imagePreview.value = ''
  form.title = ''; form.cropType = ''; form.categoryId = null
  form.description = ''; form.region = ''; form.growthStage = ''
  formRef.value?.clearValidate(); uploadRef.value?.clearFiles()
}
function goHome() { router.push('/knowledge/questions') }
</script>

<style scoped>
.smart-question-page { max-width: 840px; margin: 0 auto; }
.result-header { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; }
.result-alert { margin-bottom: 16px; }
.suggestion-content { background: #f0f9eb; border: 1px solid #e1f3d8; border-radius: 6px; padding: 16px; line-height: 1.8; white-space: pre-wrap; word-break: break-word; }
.agent-suggestion h4 { margin: 0 0 12px; color: #67c23a; }
.score-info { margin-top: 12px; }
.yolo-info { padding: 12px; background: #fdf6ec; border-radius: 6px; }
.muted { font-size: 13px; color: #909399; }
.result-actions { margin-top: 20px; text-align: center; }
.upload-area { width: 100%; }
.upload-icon { color: #c0c4cc; }
.upload-text { margin-top: 8px; }
.upload-text em { color: #409eff; }
.preview-image { max-width: 300px; max-height: 200px; border-radius: 6px; }
.loading-dialog { text-align: center; padding: 20px 0; }
.loading-dialog h3 { margin-top: 16px; color: #303133; }
.loading-dialog p { color: #909399; font-size: 14px; }
.rotating { animation: spin 1.5s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
</style>

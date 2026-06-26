<template>
  <div class="page">
    <el-card v-if="submitResult" shadow="hover">
      <template #header>
        <div class="result-header">
          <el-icon :size="24" :color="submitResult.status === 3 ? '#67C23A' : '#E6A23C'">
            <CircleCheckFilled v-if="submitResult.status === 3" /><InfoFilled v-else />
          </el-icon>
          <span class="result-title">{{ submitResult.statusText }}</span>
        </div>
      </template>
      <div v-if="submitResult.status === 3" class="auto-reply">
        <el-alert type="success" :closable="false" show-icon>
          <template #title>YOLO识别：<b>{{ submitResult.yoloDiseaseName }}</b>，置信度：<b>{{ toPercent(submitResult.yoloConfidence) }}</b></template>
        </el-alert>
        <div class="agent-suggestion" v-if="submitResult.agentSuggestion">
          <h4>AI诊断建议</h4>
          <div class="suggestion-content">{{ submitResult.agentSuggestion }}</div>
        </div>
        <div v-if="submitResult.agentScore" class="score-info">
          <el-tag type="success">回答评分：{{ submitResult.agentScore }}分</el-tag>
        </div>
      </div>
      <div v-else class="expert-review">
        <el-alert type="warning" :closable="false" show-icon><template #title>系统已转交农业专家审核，请耐心等待</template></el-alert>
        <div v-if="submitResult.yoloDiseaseName" class="yolo-info">
          <p>YOLO识别结果：<b>{{ submitResult.yoloDiseaseName }}</b>（置信度：{{ toPercent(submitResult.yoloConfidence) }}）</p>
          <p class="muted">识别置信度不足或AI评分未达标，已转交农业专家审核。</p>
        </div>
      </div>
      <div class="result-actions">
        <el-button type="primary" @click="$router.push('/diagnosis')">继续提问</el-button>
      </div>
    </el-card>

    <el-card v-else shadow="hover">
      <template #header>
        <div><h2>智能提问</h2><span class="sub-title">上传病虫害图片，系统会先进行AI识别，必要时转交专家审核</span></div>
      </template>
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitSmartQuestion } from '../../api/knowledge'

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
    submitResult.value = r.data; ElMessage.success('提交成功')
  } catch(e) { ElMessage.error('提交失败') }
  finally { submitting.value = false }
}

function resetForm() {
  formRef.value?.resetFields(); imageFile.value = null; imagePreview.value = ''; submitResult.value = null
  uploadRef.value?.clearFiles()
}
</script>

<style scoped>
.sub-title { color: #909399; font-size: 13px; margin-left: 12px; }
.result-header { display: flex; align-items: center; gap: 10px; }
.result-title { font-size: 16px; font-weight: 600; }
.auto-reply { margin-top: 12px; }
.agent-suggestion { margin-top: 16px; padding: 16px; background: #f5f7fa; border-radius: 6px; }
.agent-suggestion h4 { margin: 0 0 10px; color: #409eff; }
.suggestion-content { line-height: 1.8; white-space: pre-wrap; }
.score-info { margin-top: 12px; }
.yolo-info { margin-top: 12px; }
.yolo-info p { margin: 4px 0; }
.muted { color: #909399; font-size: 13px; }
.result-actions { margin-top: 18px; }
.expert-review { margin-top: 12px; }
.upload-area { width: 100%; }
.upload-icon { color: #409eff; }
.upload-text { margin-top: 8px; color: #606266; }
.upload-text em { color: #409eff; }
.preview-image { width: 200px; border-radius: 8px; }
</style>

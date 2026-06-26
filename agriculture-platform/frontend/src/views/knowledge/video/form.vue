<template>
  <div class="page video-form-page">
    <div class="page-header">
      <div>
        <h2>发布视频</h2>
        <p>上传农技教学视频，补充作物类型、标签和简介，方便用户检索学习</p>
      </div>
      <el-button @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <div class="form-layout">
      <el-card shadow="never" class="form-card">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
          <el-form-item label="视频标题" prop="title">
            <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="例如：番茄晚疫病识别与防治" />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :xs="24" :md="12">
              <el-form-item label="作物类型" prop="cropType">
                <el-select v-model="form.cropType" placeholder="选择作物" clearable filterable allow-create>
                  <el-option v-for="item in cropOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="12">
              <el-form-item label="标签">
                <el-input v-model="form.tags" placeholder="用逗号分隔，如 病虫害,施肥,棚室管理" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="视频简介">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="5"
              maxlength="1000"
              show-word-limit
              placeholder="说明视频适用场景、核心技术点和注意事项"
            />
          </el-form-item>

          <el-form-item label="视频文件" prop="videoFile">
            <el-upload
              ref="videoUploadRef"
              class="upload-box"
              drag
              :auto-upload="false"
              :limit="1"
              accept=".mp4,.avi,.mov,.webm"
              :on-change="handleVideoChange"
              :on-remove="handleVideoRemove"
              :on-exceed="() => ElMessage.warning('只能上传一个视频文件')"
            >
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="el-upload__text">拖拽视频到此处，或 <em>点击选择</em></div>
              <template #tip>
                <div class="el-upload__tip">支持 mp4、avi、mov、webm，单个文件不超过 200MB。</div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item label="封面图片">
            <el-upload
              ref="coverUploadRef"
              class="upload-box"
              :auto-upload="false"
              :limit="1"
              accept=".jpg,.jpeg,.png,.webp"
              :on-change="handleCoverChange"
              :on-remove="handleCoverRemove"
              :on-exceed="() => ElMessage.warning('只能上传一张封面')"
            >
              <el-button>
                <el-icon><Picture /></el-icon>
                选择封面
              </el-button>
              <template #tip>
                <div class="el-upload__tip">可选，支持 jpg、jpeg、png、webp，建议 16:9 横图。</div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submit">
              <el-icon><Upload /></el-icon>
              发布视频
            </el-button>
            <el-button :disabled="submitting" @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <aside class="preview-panel">
        <el-card shadow="never">
          <template #header>
            <div class="preview-title">
              <el-icon><VideoCamera /></el-icon>
              发布预览
            </div>
          </template>

          <div class="preview-cover">
            <img v-if="coverPreview" :src="coverPreview" alt="视频封面预览" />
            <video v-else-if="videoPreview" :src="videoPreview" controls />
            <div v-else class="preview-placeholder">
              <el-icon :size="42"><VideoCamera /></el-icon>
            </div>
          </div>

          <h3>{{ form.title || '未填写标题' }}</h3>
          <div class="preview-meta">
            <el-tag v-if="form.cropType" type="success" effect="light">{{ form.cropType }}</el-tag>
            <span v-if="videoFileName">{{ videoFileName }}</span>
          </div>
          <p>{{ form.description || '填写简介后会在这里预览。' }}</p>
        </el-card>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { uploadVideo } from '../../../api/knowledge'

const MAX_VIDEO_SIZE = 200 * 1024 * 1024
const MAX_COVER_SIZE = 5 * 1024 * 1024
const videoExts = ['mp4', 'avi', 'mov', 'webm']
const coverExts = ['jpg', 'jpeg', 'png', 'webp']
const cropOptions = ['水稻', '小麦', '玉米', '大豆', '番茄', '黄瓜', '果树']

const router = useRouter()
const formRef = ref(null)
const videoUploadRef = ref(null)
const coverUploadRef = ref(null)
const submitting = ref(false)
const videoPreview = ref('')
const coverPreview = ref('')

const form = reactive({
  title: '',
  cropType: '',
  tags: '',
  description: '',
  videoFile: null,
  coverFile: null
})

const rules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  videoFile: [{ required: true, message: '请选择视频文件', trigger: 'change' }]
}

const videoFileName = computed(() => form.videoFile?.name || '')

function handleVideoChange(file) {
  const raw = file.raw
  if (!validateFile(raw, videoExts, MAX_VIDEO_SIZE, '视频')) {
    videoUploadRef.value?.clearFiles()
    form.videoFile = null
    clearObjectUrl(videoPreview)
    return
  }
  form.videoFile = raw
  setObjectUrl(videoPreview, raw)
  formRef.value?.validateField('videoFile')
}

function handleVideoRemove() {
  form.videoFile = null
  clearObjectUrl(videoPreview)
}

function handleCoverChange(file) {
  const raw = file.raw
  if (!validateFile(raw, coverExts, MAX_COVER_SIZE, '封面')) {
    coverUploadRef.value?.clearFiles()
    form.coverFile = null
    clearObjectUrl(coverPreview)
    return
  }
  form.coverFile = raw
  setObjectUrl(coverPreview, raw)
}

function handleCoverRemove() {
  form.coverFile = null
  clearObjectUrl(coverPreview)
}

function validateFile(file, allowedExts, maxSize, label) {
  if (!file) return false
  const ext = file.name.split('.').pop()?.toLowerCase()
  if (!allowedExts.includes(ext)) {
    ElMessage.error(`${label}格式不支持`)
    return false
  }
  if (file.size > maxSize) {
    ElMessage.error(`${label}文件过大`)
    return false
  }
  return true
}

function setObjectUrl(targetRef, file) {
  clearObjectUrl(targetRef)
  targetRef.value = URL.createObjectURL(file)
}

function clearObjectUrl(targetRef) {
  if (targetRef.value) {
    URL.revokeObjectURL(targetRef.value)
    targetRef.value = ''
  }
}

function normalizeTags(tags) {
  return tags
    .split(/[,，、\s]+/)
    .map(item => item.trim())
    .filter(Boolean)
    .join(',')
}

async function submit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('title', form.title.trim())
    if (form.description.trim()) formData.append('description', form.description.trim())
    if (form.cropType) formData.append('cropType', form.cropType)
    if (form.tags.trim()) formData.append('tags', normalizeTags(form.tags))
    formData.append('video', form.videoFile)
    if (form.coverFile) formData.append('cover', form.coverFile)

    const response = await uploadVideo(formData)
    ElMessage.success('视频发布成功')
    const id = response.data?.id
    router.push(id ? `/knowledge/videos/${id}` : '/knowledge/videos')
  } catch {
    ElMessage.error('视频发布失败')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  form.title = ''
  form.cropType = ''
  form.tags = ''
  form.description = ''
  form.videoFile = null
  form.coverFile = null
  clearObjectUrl(videoPreview)
  clearObjectUrl(coverPreview)
  formRef.value?.clearValidate()
  videoUploadRef.value?.clearFiles()
  coverUploadRef.value?.clearFiles()
}

onUnmounted(() => {
  clearObjectUrl(videoPreview)
  clearObjectUrl(coverPreview)
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
}

.form-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 18px;
  align-items: start;
}

.form-card :deep(.el-select) {
  width: 100%;
}

.upload-box {
  width: 100%;
}

.upload-box :deep(.el-upload),
.upload-box :deep(.el-upload-dragger) {
  width: 100%;
}

.upload-icon {
  color: var(--color-primary);
}

.preview-panel {
  position: sticky;
  top: 16px;
}

.preview-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-cover {
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-cover img,
.preview-cover video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  color: #c0c4cc;
}

.preview-panel h3 {
  margin: 14px 0 8px;
  font-size: 16px;
  color: #303133;
  word-break: break-word;
}

.preview-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
  color: #909399;
  font-size: 12px;
}

.preview-panel p {
  margin: 0;
  line-height: 1.7;
  color: #606266;
}

@media (max-width: 960px) {
  .form-layout {
    grid-template-columns: 1fr;
  }

  .preview-panel {
    position: static;
  }
}

@media (max-width: 768px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>

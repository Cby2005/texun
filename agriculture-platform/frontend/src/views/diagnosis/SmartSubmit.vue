<template>
  <div class="page smart-submit">
    <el-card v-if="submitResult" shadow="hover">
      <template #header>
        <div class="result-header">
          <el-icon :size="24" :color="submitResult.status === 3 ? '#67C23A' : '#E6A23C'">
            <CircleCheckFilled v-if="submitResult.status === 3" />
            <InfoFilled v-else />
          </el-icon>
          <span class="result-title">{{ submitResult.statusText }}</span>
        </div>
      </template>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="批次号">{{ form.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="作物">{{ form.cropType }}</el-descriptions-item>
        <el-descriptions-item label="地块">{{ selectedLandName }}</el-descriptions-item>
        <el-descriptions-item label="生长期">{{ form.growthStage }}</el-descriptions-item>
      </el-descriptions>
      <div class="result-body" v-if="submitResult.agentSuggestion">
        <h4>AI诊断建议</h4>
        <div class="suggestion-content">{{ submitResult.agentSuggestion }}</div>
      </div>
      <el-alert v-else type="warning" :closable="false" show-icon title="问题已进入专家处理队列" />
      <div class="result-actions">
        <el-button type="primary" @click="resetForm">继续提问</el-button>
        <el-button @click="$router.push('/knowledge/questions')">查看问答</el-button>
      </div>
    </el-card>

    <el-card v-else shadow="hover">
      <template #header>
        <div>
          <h2>生产诊断提问</h2>
          <span class="sub-title">把问题挂到地块、作物和生产批次，诊断结果会自动进入溯源链。</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="104px">
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="地块" prop="landId">
              <el-select v-model="form.landId" placeholder="选择地块" filterable style="width:100%">
                <el-option v-for="land in lands" :key="land.id" :label="land.displayName || `地块 ${land.number || land.id}`" :value="land.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="作物" prop="cropId">
              <el-select v-model="form.cropId" placeholder="选择作物" filterable style="width:100%" @change="handleCropChange">
                <el-option v-for="crop in crops" :key="crop.id" :label="crop.name" :value="crop.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="生产批次" prop="batchNo">
              <el-select v-model="form.batchNo" placeholder="选择生产批次" filterable style="width:100%">
                <el-option v-for="batch in filteredBatches" :key="batch.batchNo" :label="`${batch.batchNo} ${batch.productName || ''}`" :value="batch.batchNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="生长期" prop="growthStage">
              <el-select v-model="form.growthStage" placeholder="选择生长期" style="width:100%">
                <el-option label="苗期" value="苗期" />
                <el-option label="生长期" value="生长期" />
                <el-option label="开花期" value="开花期" />
                <el-option label="结果期" value="结果期" />
                <el-option label="成熟期" value="成熟期" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="问题标题" prop="title">
          <el-input v-model="form.title" placeholder="例如：番茄叶片出现黄斑" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-input v-model="form.region" placeholder="例如：山东省寿光市" />
        </el-form-item>
        <el-form-item label="症状描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="6" placeholder="描述症状、发现时间、环境变化和已采取的处理措施" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="症状图片" required>
          <el-upload ref="uploadRef" class="upload-area" drag :auto-upload="false" :limit="1" accept=".jpg,.jpeg,.png,.webp" :before-upload="beforeUpload" :on-change="handleFileChange" :on-exceed="handleExceed" :on-remove="handleRemove">
            <el-icon :size="40" class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">拖拽图片到此处，或 <em>点击上传</em></div>
            <template #tip><div class="el-upload__tip">支持 jpg/jpeg/png/webp，不超过 5MB。</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="imagePreview" label="图片预览">
          <el-image :src="imagePreview" :preview-src-list="[imagePreview]" class="preview-image" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" size="large">
            {{ submitting ? '正在诊断...' : '提交诊断' }}
          </el-button>
          <el-button @click="resetForm" size="large">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { submitSmartQuestion } from '../../api/knowledge'
import { getCrops, getLands } from '../../api/farm'
import { getBatches } from '../../api/trace'

const formRef = ref(null)
const uploadRef = ref(null)
const submitting = ref(false)
const imageFile = ref(null)
const imagePreview = ref('')
const submitResult = ref(null)
const lands = ref([])
const crops = ref([])
const batches = ref([])

const form = reactive({
  title: '',
  landId: null,
  cropId: null,
  cropType: '',
  batchNo: '',
  categoryId: 1,
  description: '',
  region: '',
  growthStage: ''
})

const rules = {
  landId: [{ required: true, message: '请选择地块', trigger: 'change' }],
  cropId: [{ required: true, message: '请选择作物', trigger: 'change' }],
  batchNo: [{ required: true, message: '请选择生产批次', trigger: 'change' }],
  growthStage: [{ required: true, message: '请选择生长期', trigger: 'change' }],
  title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入症状描述', trigger: 'blur' }]
}

const filteredBatches = computed(() => batches.value.filter(batch => {
  if (form.landId && batch.landId && batch.landId !== form.landId) return false
  if (form.cropId && batch.cropId && batch.cropId !== form.cropId) return false
  return true
}))

const selectedLandName = computed(() => {
  const land = lands.value.find(item => item.id === form.landId)
  return land ? (land.displayName || `地块 ${land.number || land.id}`) : '-'
})

onMounted(async () => {
  try {
    const [landRes, cropRes, batchRes] = await Promise.all([
      getLands({ pageNum: 1, pageSize: 100 }),
      getCrops(),
      getBatches({ pageNum: 1, pageSize: 100 })
    ])
    lands.value = landRes.data?.records || []
    crops.value = cropRes.data || []
    batches.value = batchRes.data?.records || []
  } catch (e) {
    ElMessage.warning('基础数据加载失败，请确认账号权限')
  }
})

function handleCropChange(cropId) {
  const crop = crops.value.find(item => item.id === cropId)
  form.cropType = crop?.name || ''
}

function beforeUpload(file) {
  const ok = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持 jpg/jpeg/png/webp 格式'); return false }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}

function handleFileChange(file) {
  if (!beforeUpload(file.raw)) { uploadRef.value?.clearFiles(); return }
  imageFile.value = file.raw
  imagePreview.value = URL.createObjectURL(file.raw)
}

function handleExceed() { ElMessage.warning('只能上传一张图片') }
function handleRemove() { imageFile.value = null; imagePreview.value = '' }

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  if (!imageFile.value) {
    ElMessage.warning('请上传症状图片')
    return
  }
  submitting.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    fd.append('cropType', form.cropType)
    fd.append('cropId', form.cropId)
    fd.append('landId', form.landId)
    fd.append('batchNo', form.batchNo)
    fd.append('categoryId', form.categoryId)
    fd.append('description', form.description)
    if (form.region) fd.append('region', form.region)
    fd.append('growthStage', form.growthStage)
    fd.append('image', imageFile.value)
    const r = await submitSmartQuestion(fd)
    submitResult.value = r.data
    ElMessage.success('提交成功')
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { title: '', landId: null, cropId: null, cropType: '', batchNo: '', categoryId: 1, description: '', region: '', growthStage: '' })
  imageFile.value = null
  imagePreview.value = ''
  submitResult.value = null
  uploadRef.value?.clearFiles()
}
</script>

<style scoped>
.smart-submit { max-width: 960px; margin: 0 auto; }
.sub-title { color: #667085; font-size: 13px; }
.result-header { display: flex; align-items: center; gap: 10px; }
.result-title { font-size: 16px; font-weight: 600; }
.result-body { margin-top: 16px; padding: 16px; background: #f5f7fa; border-radius: 6px; }
.result-body h4 { margin: 0 0 10px; color: #1f7a4d; }
.suggestion-content { line-height: 1.8; white-space: pre-wrap; }
.result-actions { margin-top: 18px; display: flex; gap: 10px; }
.upload-area { width: 100%; }
.upload-icon { color: #409eff; }
.upload-text { margin-top: 8px; color: #606266; }
.upload-text em { color: #409eff; }
.preview-image { width: 220px; border-radius: 8px; }
</style>

<template>
  <div class="page">
    <div class="page-header"><h2>病虫害智能诊断</h2></div>
    <el-card>
      <el-alert title="上传草莓叶片、果实或植株照片，系统将使用本地 YOLO11 分割模型识别病虫害" type="info" :closable="false" style="margin-bottom:16px" />
      <div style="display:flex;gap:16px">
        <div style="flex:1">
          <el-upload
            drag
            :auto-upload="false"
            :on-change="handleFile"
            :limit="1"
            accept=".jpg,.jpeg,.png,.webp"
            :show-file-list="false"
          >
            <el-icon :size="48"><UploadFilled /></el-icon>
            <div style="margin-top:8px">拖拽或点击上传病虫害图片</div>
            <div style="font-size:12px;color:#999">支持 jpg/png/webp，不超过5MB</div>
          </el-upload>
          <el-button type="primary" :loading="loading" @click="handleDetect" style="width:100%;margin-top:12px" :disabled="!file">
            {{ loading ? '识别中...' : '开始识别' }}
          </el-button>
        </div>
        <div style="flex:1">
          <div v-if="result" style="border:1px solid #ebeef5;border-radius:8px;padding:16px">
            <h3 style="margin:0 0 12px">识别结果</h3>
            <div v-if="result.success">
              <div style="font-size:24px;font-weight:bold;color:#409eff">{{ result.topDiseaseName || '未识别到病虫害' }}</div>
              <div style="color:#999;margin:8px 0">置信度: {{ (result.topConfidence * 100).toFixed(1) }}%</div>
              <div v-if="result.predictions && result.predictions.length">
                <div v-for="(p, i) in result.predictions" :key="i" style="font-size:13px;margin:4px 0">
                  {{ p.className }}: {{ (p.confidence * 100).toFixed(1) }}%
                  <span style="color:#999"> 区域({{ p.x }},{{ p.y }},{{ p.width }}x{{ p.height }})</span>
                </div>
              </div>
            </div>
            <div v-else style="color:#e6a23c">{{ result.message }}</div>
          </div>
          <el-empty v-else description="等待上传图片" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { detectDisease } from '../../api/diagnosis'
import { UploadFilled } from '@element-plus/icons-vue'

const file = ref(null)
const loading = ref(false)
const result = ref(null)

function handleFile(f) { file.value = f.raw }

async function handleDetect() {
  if (!file.value) return
  loading.value = true
  try {
    const r = await detectDisease(file.value)
    result.value = r.data
  } catch (e) {} finally { loading.value = false }
}
</script>

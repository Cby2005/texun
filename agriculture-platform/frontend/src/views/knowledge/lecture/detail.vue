<template>
  <div class="page">
    <div class="page-header">
      <el-button @click="$router.back()"><el-icon><ArrowLeft /></el-icon>返回</el-button>
    </div>
    <el-card v-if="lecture">
      <h1>{{ lecture.title }}</h1>
      <div style="display:flex;gap:16px;margin:12px 0;color:#999;font-size:13px">
        <span>主讲人：{{ lecture.speakerName || '--' }}</span>
        <span>时间：{{ lecture.startsAt }} ~ {{ lecture.endsAt }}</span>
        <span>已报名：{{ lecture.registerCount || 0 }}/{{ lecture.capacity || 0 }}</span>
      </div>
      <el-divider />
      <div style="line-height:2;white-space:pre-wrap">{{ lecture.content }}</div>
      <div v-if="lecture.summary" style="margin-top:12px;color:#666;font-size:14px">{{ lecture.summary }}</div>
      <el-divider />
      <p v-if="lecture.joinUrl" style="color:#999;font-size:13px">参会链接：{{ lecture.joinUrl }}</p>
      <div style="margin-top:16px">
        <el-button v-if="lecture.status==='OPEN'&&(lecture.registerCount||0)<(lecture.capacity||0)" type="primary" @click="handleRegister">立即报名</el-button>
        <el-tag v-else type="info">{ {OPEN:'报名中',DRAFT:'草稿',CLOSED:'已截止',FINISHED:'已结束'}[lecture.status]||lecture.status }</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getLecture, registerLecture, cancelLecture } from '../../../api/knowledge'
import { ElMessage } from 'element-plus'

const route = useRoute()
const lecture = ref(null)

async function load() { try { const r = await getLecture(route.params.id); lecture.value = r.data } catch(e){} }

async function handleRegister() {
  try { await registerLecture(route.params.id); ElMessage.success('报名成功'); load() } catch(e){}
}

onMounted(load)
</script>

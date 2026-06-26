<template>
  <div class="page">
    <div class="page-header"><h2>{{ isEdit ? '编辑讲座' : '发布讲座' }}</h2></div>
    <el-card>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="主讲人"><el-input v-model="form.speakerName" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="详情"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="参会链接"><el-input v-model="form.joinUrl" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startsAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endsAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="人数上限"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSave">保存</el-button><el-button @click="$router.back()">返回</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addLecture, getLecture, updateLecture } from '../../../api/knowledge'
import { ElMessage } from 'element-plus'

const route = useRoute(); const router = useRouter()
const id = route.params.id; const isEdit = !!id
const form = reactive({ title:'',speakerName:'',summary:'',content:'',joinUrl:'',startsAt:'',endsAt:'',capacity:100 })

onMounted(async () => { if (isEdit) { try { const r = await getLecture(id); Object.assign(form, r.data) } catch(e){} } })

async function handleSave() {
  try {
    if (isEdit) await updateLecture(id, form); else await addLecture(form)
    ElMessage.success('保存成功'); router.push('/knowledge/lectures')
  } catch(e){}
}
</script>

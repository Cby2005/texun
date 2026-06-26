<template>
  <div class="page">
    <div class="page-header"><h2>{{ isEdit ? '编辑文章' : '发布文章' }}</h2></div>
    <el-card>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId"><el-option v-for="c in categories" :key="c.id" :value="c.id" :label="c.name" /></el-select>
        </el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="12" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSave">保存</el-button><el-button @click="$router.back()">返回</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, addArticle, getArticle, updateArticle } from '../../../api/knowledge'
import { ElMessage } from 'element-plus'

const route = useRoute(); const router = useRouter()
const id = route.params.id; const isEdit = !!id
const form = reactive({ title:'',categoryId:null,summary:'',content:'' })
const categories = ref([])

onMounted(async () => {
  try { const r = await getCategories(); categories.value = r.data } catch(e){}
  if (isEdit) { try { const r = await getArticle(id); Object.assign(form, r.data) } catch(e){} }
})

async function handleSave() {
  try {
    if (isEdit) await updateArticle(id, form); else await addArticle(form)
    ElMessage.success('保存成功'); router.push('/knowledge/articles')
  } catch(e){}
}
</script>

<template>
  <div class="page">
    <div class="page-header"><h2>病虫害知识</h2><p>常见农作物病虫害识别与防治</p></div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索病虫害" clearable style="width:240px" />
      <el-select v-model="category" placeholder="分类" clearable style="width:140px"><el-option label="病害" value="病害"/><el-option label="虫害" value="虫害"/></el-select>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
    </div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="p in list" :key="p.id" style="margin-bottom:16px">
        <el-card>
          <template #header><span style="font-weight:600">{{ p.name }}</span><el-tag size="small" style="float:right">{{ p.category }}</el-tag></template>
          <p style="font-size:13px;color:#666;margin-bottom:8px"><strong>危害作物：</strong>{{ p.affectedCrops }}</p>
          <p style="font-size:13px;color:#666;margin-bottom:8px"><strong>症状：</strong>{{ p.symptoms }}</p>
          <p style="font-size:13px;color:#666"><strong>防治：</strong>{{ p.prevention }}</p>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPests } from '../../../api/knowledge'

const list = ref([]), keyword = ref(''), category = ref('')

async function load() { try { const r = await getPests({ keyword: keyword.value || undefined, category: category.value || undefined }); list.value = r.data } catch(e){} }
onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div><h2>农技文章</h2><p>浏览和发布农业技术文章</p></div>
        <el-button type="primary" @click="$router.push('/knowledge/articles/create')"><el-icon><Edit /></el-icon>发布文章</el-button>
      </div>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索文章标题" clearable style="width:240px" />
      <el-select v-model="categoryId" placeholder="分类" clearable style="width:160px">
        <el-option v-for="c in categories" :key="c.id" :value="c.id" :label="c.name" />
      </el-select>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
    </div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="a in list" :key="a.id" style="margin-bottom:16px">
        <el-card shadow="hover" @click="$router.push('/knowledge/articles/'+a.id)" style="cursor:pointer">
          <h3 style="margin-bottom:8px;font-size:16px">{{ a.title }}</h3>
          <p style="color:#999;font-size:13px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ a.summary }}</p>
          <div style="display:flex;gap:12px;margin-top:12px;font-size:12px;color:#aaa">
            <span><el-icon><View /></el-icon> {{ a.viewCount || 0 }}</span>
            <span><el-icon><Star /></el-icon> {{ a.likeCount || 0 }}</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ a.commentCount || 0 }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    <el-empty v-if="!list.length" description="暂无文章" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, getCategories } from '../../../api/knowledge'

const list = ref([]), total = ref(0), pageNum = ref(1), pageSize = ref(12)
const keyword = ref(''), categoryId = ref(null), categories = ref([])

async function load() {
  try { const r = await getArticles({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined, categoryId: categoryId.value }); list.value = r.data.records; total.value = r.data.total } catch(e){}
}
onMounted(async () => { await load(); try { const r = await getCategories(); categories.value = r.data } catch(e){} })
</script>

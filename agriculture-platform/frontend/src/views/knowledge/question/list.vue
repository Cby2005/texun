<template>
  <div class="page">
    <div class="page-header"><h2>问答社区</h2><el-button type="primary" @click="$router.push('/knowledge/questions/create')">我要提问</el-button></div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索问题" clearable style="width: 250px" @keyup.enter="load" />
      <el-select v-model="cropType" placeholder="作物类型" clearable style="width: 150px" @change="load">
        <el-option label="水稻" value="水稻" /><el-option label="小麦" value="小麦" />
        <el-option label="玉米" value="玉米" /><el-option label="番茄" value="番茄" />
        <el-option label="黄瓜" value="黄瓜" /><el-option label="辣椒" value="辣椒" />
      </el-select>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon> 搜索</el-button>
    </div>

    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="title" label="问题" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <router-link :to="'/knowledge/questions/'+row.id" class="link">{{ row.title }}</router-link>
          </template>
        </el-table-column>
        <el-table-column prop="cropType" label="作物" width="100" />
        <el-table-column prop="authorName" label="提问者" width="120">
          <template #default="{ row }"><span>{{ row.authorName || row.username || '匿名' }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.status==='RESOLVED'||row.status===1" type="success" size="small">已解决</el-tag>
            <el-tag v-else-if="row.status==='PENDING'" type="warning" size="small">待回答</el-tag>
            <el-tag v-else type="info" size="small">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="answerCount" label="回答" width="80" />
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getQuestions } from '../../../api/knowledge'

const list = ref([]), total = ref(0), loading = ref(false), pageNum = ref(1), pageSize = ref(10)
const keyword = ref(''), cropType = ref('')

function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '-' }

async function load() {
  loading.value = true
  try {
    const r = await getQuestions({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined, cropType: cropType.value || undefined })
    list.value = r.data.records || []; total.value = r.data.total || 0
  } finally { loading.value = false }
}
onMounted(load)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.link { color: #409eff; text-decoration: none; font-weight: 500; }
.link:hover { text-decoration: underline; }
</style>

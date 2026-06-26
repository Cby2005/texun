<template>
  <div class="page">
    <div class="page-header"><h2>个性化推荐</h2></div>
    <el-card>
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <el-button type="primary" @click="loadRecommend">刷新推荐</el-button>
        <span style="color:#999;font-size:13px">基于您的浏览、点赞、收藏行为智能推荐</span>
      </div>
      <el-row :gutter="16">
        <el-col :span="8" v-for="item in articles" :key="item.articleId" style="margin-bottom:16px">
          <el-card shadow="hover" style="height:280px;cursor:pointer" @click="$router.push('/knowledge/articles/' + item.articleId)">
            <div style="font-weight:bold;font-size:15px;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;height:44px">
              {{ item.title }}
            </div>
            <div style="color:#999;font-size:12px;margin:8px 0">{{ item.source || '--' }}</div>
            <div style="font-size:13px;line-height:1.6;display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden;height:62px;color:#666">
              {{ item.summary || '暂无摘要' }}
            </div>
            <div style="margin-top:8px">
              <el-tag v-if="item.trustedLevel" size="small" :type="item.trustedLevel==='official'?'danger':item.trustedLevel==='expert'?'warning':'info'">
                {{ {official:'官方',expert:'专家',normal:'普通'}[item.trustedLevel] }}
              </el-tag>
              <el-tag v-for="t in (item.tags||[]).slice(0,3)" :key="t" size="small" style="margin-left:4px" type="success">{{ t }}</el-tag>
            </div>
            <div style="display:flex;justify-content:space-between;align-items:center;margin-top:8px;color:#999;font-size:12px">
              <span>推荐分: {{ item.score }}</span>
              <el-tag size="small">{{ item.strategy === 'cold_start' ? '冷启动' : '标签匹配' }}</el-tag>
            </div>
            <div style="margin-top:4px;font-size:11px;color:#bbb">{{ item.reason }}</div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!articles.length" description="暂无推荐内容" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRecommendArticles, refreshProfile } from '../../api/recommend'

const articles = ref([])

async function loadRecommend() {
  try {
    await refreshProfile()
    const r = await getRecommendArticles({ limit: 12 })
    articles.value = r.data || []
  } catch (e) {}
}

onMounted(loadRecommend)
</script>

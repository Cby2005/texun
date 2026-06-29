<template>
  <div class="profile-analysis-page">
    <div class="page-header">
      <h1 class="page-title">用户画像分析</h1>
      <p class="page-subtitle">查看用户兴趣标签、行为统计与内容偏好</p>
    </div>

    <!-- 用户查询 -->
    <div class="search-bar">
      <el-input
        v-model="searchUserId"
        placeholder="输入用户ID查询画像"
        clearable
        class="search-input"
        @keyup.enter="queryProfile"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
        <template #append>
          <el-button type="primary" @click="queryProfile">查询</el-button>
        </template>
      </el-input>
    </div>

    <div v-if="profile" v-loading="loading" class="profile-result">
      <!-- 基本信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-tag :type="profile.coldStart ? 'warning' : 'success'">
              {{ profile.coldStart ? '冷启动用户' : '活跃用户' }}
            </el-tag>
          </div>
        </template>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="用户ID">{{ profile.userId }}</el-descriptions-item>
          <el-descriptions-item label="作物偏好">{{ profile.cropPreference || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="画像状态">{{ profile.coldStart ? '待积累' : '已构建' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 兴趣标签 Top 10 -->
      <el-card class="tag-card" shadow="never">
        <template #header><span>主要兴趣标签（TOP 10）</span></template>
        <div v-if="tagList.length" class="tag-list">
          <div v-for="(tag, idx) in tagList" :key="idx" class="tag-bar-item">
            <span class="tag-label">{{ tag.name }}</span>
            <div class="tag-bar-wrap">
              <div class="tag-bar" :style="{ width: tag.percent + '%', background: tagColors[idx % tagColors.length] }" />
            </div>
            <span class="tag-weight">{{ tag.weight.toFixed(1) }}</span>
          </div>
        </div>
        <el-empty v-else description="暂无兴趣标签，用户尚无行为数据" :image-size="80" />
      </el-card>

      <!-- 关注方向分析 -->
      <el-row :gutter="16" class="direction-row">
        <el-col :span="12">
          <el-card shadow="never">
            <template #header><span>标签类型分布</span></template>
            <div v-if="Object.keys(tagTypeStats).length" class="direction-grid">
              <div v-for="(count, type) in tagTypeStats" :key="type" class="direction-item">
                <span class="direction-name">{{ typeLabel(type) }}</span>
                <span class="direction-value">{{ count }}</span>
              </div>
            </div>
            <el-empty v-else description="暂无数据" :image-size="60" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <template #header><span>行为统计</span></template>
            <div v-if="Object.keys(behaviorStats).length" class="direction-grid">
              <div v-for="(count, type) in behaviorStats" :key="type" class="direction-item">
                <span class="direction-name">{{ behaviorLabel(type) }}</span>
                <span class="direction-value">{{ count }}</span>
              </div>
            </div>
            <el-empty v-else description="暂无行为数据" :image-size="60" />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-if="!profile && !loading && searched" description="未找到该用户画像" :image-size="120" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { getUserProfile } from '@/api/agri/content'

const searchUserId = ref('')
const profile = ref(null)
const loading = ref(false)
const searched = ref(false)
const behaviorStats = ref({})

const tagColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']

const tagList = computed(() => {
  if (!profile.value?.interestTags) return []
  const entries = Object.entries(profile.value.interestTags)
  const maxWeight = Math.max(...entries.map(e => e[1]), 1)
  return entries.slice(0, 10).map(([name, weight]) => ({
    name, weight, percent: (weight / maxWeight) * 100
  }))
})

const tagTypeStats = computed(() => {
  if (!profile.value?.interestTags) return {}
  const stats = {}
  Object.keys(profile.value.interestTags).forEach(key => {
    const match = key.match(/\[([A-Z]+)\]$/)
    const type = match ? match[1] : 'OTHER'
    stats[type] = (stats[type] || 0) + 1
  })
  return stats
})

function typeLabel(type) {
  const map = { TAG: '内容标签', CATEGORY: '分类偏好', KEYWORD: '搜索词', OTHER: '其他' }
  return map[type] || type
}

function behaviorLabel(type) {
  const map = { view: '浏览', click: '点击', like: '点赞', collect: '收藏', comment: '评论', search: '搜索', play_finish: '播放完成' }
  return map[type] || type
}

async function queryProfile() {
  const uid = searchUserId.value.trim()
  if (!uid) return
  loading.value = true
  searched.value = true
  behaviorStats.value = {}
  try {
    const r = await getUserProfile(uid)
    profile.value = r.data || r
  } catch {
    profile.value = null
  }
  loading.value = false
}
</script>

<style scoped>
.profile-analysis-page { max-width: 900px; margin: 0 auto; padding: 8px 16px 24px; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 22px; font-weight: 700; color: #2d6b3f; margin: 0 0 4px; }
.page-subtitle { font-size: 13px; color: #8ba99e; margin: 0; }

.search-bar { margin-bottom: 20px; }
.search-input { max-width: 400px; }

.profile-result { display: flex; flex-direction: column; gap: 16px; }

.card-header { display: flex; justify-content: space-between; align-items: center; }

/* 标签条 */
.tag-list { display: flex; flex-direction: column; gap: 10px; }
.tag-bar-item { display: flex; align-items: center; gap: 10px; }
.tag-label { width: 120px; font-size: 13px; color: #333; text-align: right; flex-shrink: 0; }
.tag-bar-wrap { flex: 1; height: 18px; background: #f0f0f0; border-radius: 9px; overflow: hidden; }
.tag-bar { height: 100%; border-radius: 9px; transition: width .5s; }
.tag-weight { width: 50px; font-size: 12px; color: #888; text-align: right; }

.direction-row { }
.direction-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.direction-item { display: flex; justify-content: space-between; padding: 8px 12px; background: #f8faf8; border-radius: 8px; }
.direction-name { font-size: 13px; color: #555; }
.direction-value { font-size: 16px; font-weight: 600; color: #3c8c4e; }
</style>

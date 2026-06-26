<template>
  <div class="page">
    <el-button @click="$router.back()" style="margin-bottom:16px"><el-icon><ArrowLeft /></el-icon>返回</el-button>

    <div v-loading="loading" class="detail-layout">
      <!-- 视频播放器 -->
      <div class="player-section">
        <div class="video-wrapper">
          <video ref="videoPlayer" :src="video.videoUrl" controls class="video-player" @play="onPlay" @error="onVideoError">
            <source v-if="video.videoUrl" :src="video.videoUrl" />
          </video>
          <div v-if="videoError" class="video-error">
            <el-icon :size="48"><WarningFilled /></el-icon>
            <p>视频无法加载，请检查视频地址或重试</p>
            <p v-if="video.videoUrl" style="font-size:12px;color:#999">{{ video.videoUrl }}</p>
          </div>
        </div>

        <div class="video-actions">
          <h2>{{ video.title }}</h2>
          <div class="action-btns">
            <el-button :type="video.liked ? 'primary' : 'default'" @click="toggleLike">
              {{ video.liked ? '已点赞' : '点赞' }} {{ video.likeCount > 0 ? video.likeCount : '' }}
            </el-button>
            <el-button :type="video.favorited ? 'warning' : 'default'" @click="toggleFavorite">
              {{ video.favorited ? '已收藏' : '收藏' }} {{ video.favoriteCount > 0 ? video.favoriteCount : '' }}
            </el-button>
            <span class="view-stat"><el-icon><View /></el-icon> {{ video.viewCount || 0 }} 次播放</span>
          </div>
        </div>

        <div class="video-desc">
          <div class="desc-header">
            <span class="desc-label">视频简介</span>
            <span v-if="video.cropType" class="tag">{{ video.cropType }}</span>
            <el-tag v-if="video.tags && video.tags.length" v-for="t in video.tags" :key="t" size="small" effect="plain" class="tag">{{ t }}</el-tag>
          </div>
          <p class="desc-text">{{ video.description || '暂无简介' }}</p>
        </div>
      </div>

      <!-- 相关视频 -->
      <div class="related-section" v-if="video.relatedVideos && video.relatedVideos.length">
        <h3>相关推荐</h3>
        <div v-for="r in video.relatedVideos" :key="r.id" class="related-item" @click="$router.replace('/knowledge/videos/'+r.id)">
          <div class="related-cover">
            <img v-if="r.coverUrl" :src="r.coverUrl" />
            <div v-else class="cover-ph"><el-icon :size="24"><VideoCamera /></el-icon></div>
          </div>
          <div class="related-info">
            <div class="related-title">{{ r.title }}</div>
            <div class="related-meta">
              <span v-if="r.cropType">{{ r.cropType }}</span>
              <span><el-icon><View /></el-icon> {{ r.viewCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVideo, recordVideoView, likeVideo, unlikeVideo, favoriteVideo, cancelFavoriteVideo } from '../../../api/knowledge'

const route = useRoute(), router = useRouter()
const video = ref({}), loading = ref(false), videoPlayer = ref(null), videoError = ref(false)
let viewRecorded = false

async function fetchDetail() {
  loading.value = true; videoError.value = false
  try {
    const r = await getVideo(route.params.id)
    video.value = r.data || {}
    viewRecorded = false
  } finally { loading.value = false }
}

function onPlay() {
  if (!viewRecorded) {
    viewRecorded = true
    recordVideoView(route.params.id).catch(() => {})
    video.value.viewCount = (video.value.viewCount || 0) + 1
  }
}

function onVideoError() { videoError.value = true }

async function toggleLike() {
  try {
    if (video.value.liked) {
      await unlikeVideo(video.value.id); video.value.liked = false
      video.value.likeCount = Math.max((video.value.likeCount || 1) - 1, 0)
    } else {
      await likeVideo(video.value.id); video.value.liked = true
      video.value.likeCount = (video.value.likeCount || 0) + 1
    }
  } catch(e) { ElMessage.error('操作失败') }
}

async function toggleFavorite() {
  try {
    if (video.value.favorited) {
      await cancelFavoriteVideo(video.value.id); video.value.favorited = false
      video.value.favoriteCount = Math.max((video.value.favoriteCount || 1) - 1, 0)
      ElMessage.success('已取消收藏')
    } else {
      await favoriteVideo(video.value.id); video.value.favorited = true
      video.value.favoriteCount = (video.value.favoriteCount || 0) + 1
      ElMessage.success('收藏成功')
    }
  } catch(e) { ElMessage.error('操作失败') }
}

onMounted(fetchDetail)
watch(() => route.params.id, (n, o) => { if (n !== o) fetchDetail() })
</script>

<style scoped>
.detail-layout { display: flex; gap: 24px; }
.player-section { flex: 1; min-width: 0; }
.video-wrapper { width: 100%; background: #000; border-radius: 8px; overflow: hidden; position: relative; }
.video-player { width: 100%; display: block; max-height: 500px; }
.video-error { padding: 60px 20px; text-align: center; color: #f56c6c; background: #fef0f0; }
.video-actions { margin-top: 16px; }
.video-actions h2 { margin: 0 0 12px; font-size: 20px; }
.action-btns { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.view-stat { color: #909399; font-size: 13px; display: flex; align-items: center; gap: 4px; }
.video-desc { margin-top: 16px; padding: 16px; background: #f5f7fa; border-radius: 8px; }
.desc-header { display: flex; gap: 8px; align-items: center; margin-bottom: 8px; }
.desc-label { font-weight: 600; color: #303133; }
.tag { margin-right: 6px; }
.desc-text { color: #606266; line-height: 1.8; margin: 0; }
.related-section { width: 280px; flex-shrink: 0; }
.related-section h3 { margin: 0 0 12px; font-size: 16px; }
.related-item { display: flex; gap: 10px; margin-bottom: 12px; cursor: pointer; border-radius: 8px; padding: 8px; }
.related-item:hover { background: #f5f7fa; }
.related-cover { width: 120px; height: 68px; border-radius: 4px; overflow: hidden; background: #f0f0f0; display: flex; align-items: center; justify-content: center; }
.related-cover img { width: 100%; height: 100%; object-fit: cover; }
.related-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.related-title { font-size: 13px; font-weight: 500; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.related-meta { font-size: 12px; color: #909399; display: flex; gap: 8px; align-items: center; }
</style>

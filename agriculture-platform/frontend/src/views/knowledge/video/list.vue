<template>
  <div class="agri-video-page">
    <!-- 顶部分类导航 -->
    <div class="category-nav">
      <div class="nav-specials">
        <span class="special-item hot" :class="{ active: activeCategory === 'hot' }" @click="switchCategory('hot')">
          <el-icon><TrendCharts /></el-icon> 热门
        </span>
        <span class="special-item dynamic" :class="{ active: activeCategory === 'dynamic' }" @click="switchCategory('dynamic')">
          <el-icon><VideoCamera /></el-icon> 动态
        </span>
      </div>
      <div class="nav-categories" ref="catScrollRef">
        <span
          v-for="cat in categories"
          :key="cat.key"
          class="cat-btn"
          :class="{ active: activeCategory === cat.key }"
          @click="switchCategory(cat.key)"
        >{{ cat.label }}</span>
      </div>
      <div class="nav-actions">
        <el-button text type="primary" @click="openCreate">
          <el-icon><Upload /></el-icon> 发布视频
        </el-button>
      </div>
    </div>

    <!-- ========== 主内容区：CSS Grid 左右布局 ========== -->
    <div class="main-content">
      <!-- ========== 左栏：推荐内容 ========== -->
      <div class="left-column">

        <!-- 1. 大推荐轮播图 -->
        <div class="panel banner-panel">
          <div class="banner-carousel" @mouseenter="stopAutoSlide" @mouseleave="startAutoSlide">
            <div class="banner-track" :style="{ transform: `translateX(-${bannerIndex * 100}%)` }">
              <div
                v-for="(item, i) in bannerVideos"
                :key="i"
                class="banner-slide"
                @click="openVideo(item)"
              >
                <div class="banner-img-wrap">
                  <img :src="item.cover" :alt="item.title" />
                  <div class="banner-overlay"></div>
                </div>
                <div class="banner-info">
                  <span class="banner-category">{{ item.category }}</span>
                  <h3 class="banner-title">{{ item.title }}</h3>
                </div>
              </div>
            </div>
            <button class="banner-arrow left" @click.stop="prevBanner"><el-icon><ArrowLeft /></el-icon></button>
            <button class="banner-arrow right" @click.stop="nextBanner"><el-icon><ArrowRight /></el-icon></button>
            <div class="banner-dots">
              <span v-for="(_, i) in bannerVideos" :key="i" class="dot" :class="{ active: i === bannerIndex }" @click.stop="bannerIndex = i"></span>
            </div>
          </div>
        </div>

        <!-- 2. 热门农技榜 -->
        <div class="panel hot-rank-panel">
          <div class="panel-header">
            <h3 class="panel-title"><el-icon><TrendCharts /></el-icon> 热门农技榜</h3>
            <span class="panel-more" @click="switchCategory('hot')">更多 &gt;</span>
          </div>
          <div class="hot-rank-list">
            <div
              v-for="(item, idx) in hotRankList"
              :key="idx"
              class="hot-rank-item"
              @click="openVideo(item)"
            >
              <span class="rank-num" :class="idx < 3 ? 'top3' : ''">{{ idx + 1 }}</span>
              <div class="rank-info">
                <p class="rank-title">{{ item.title }}</p>
                <div class="rank-meta">
                  <span class="rank-views"><el-icon><View /></el-icon>{{ item.views }}</span>
                  <span v-if="item.tag" class="mini-tag" :class="item.tag === '推荐' ? 'green' : 'orange'">{{ item.tag }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 3. 草莓专题合集 -->
        <div class="panel topic-panel">
          <div class="panel-header">
            <h3 class="panel-title"><el-icon><Collection /></el-icon> 草莓种植专题</h3>
          </div>
          <div class="topic-grid">
            <div
              v-for="t in topics"
              :key="t.key"
              class="topic-card"
              :style="{ background: t.bg }"
              @click="switchCategory(t.key)"
            >
              <el-icon :size="24"><component :is="t.icon" /></el-icon>
              <span class="topic-name">{{ t.name }}</span>
              <span class="topic-count">{{ t.count }}集</span>
            </div>
          </div>
        </div>

        <!-- 4. 专家推荐课程 -->
        <div class="panel expert-panel">
          <div class="panel-header">
            <h3 class="panel-title"><el-icon><Medal /></el-icon> 专家推荐</h3>
          </div>
          <div class="expert-card" @click="switchCategory('expert')">
            <div class="expert-avatar">
              <el-avatar :size="48" src="https://picsum.photos/seed/expert/96/96" />
            </div>
            <div class="expert-info">
              <p class="expert-name">张教授</p>
              <p class="expert-org">河南农技推广中心</p>
              <p class="expert-course">推荐课程：温室草莓高产栽培关键技术</p>
            </div>
            <el-icon class="expert-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- ========== 右栏：视频内容流 ========== -->
      <div class="right-column">
        <div class="video-grid-header">
          <h3 class="grid-title">{{ activeCategory === 'recommend' ? '全部视频' : categoryLabel }}</h3>
          <span class="grid-count">共 {{ displayVideos.length }} 个视频</span>
        </div>

        <div class="video-grid-wrapper">
          <el-empty v-if="displayVideos.length === 0" description="暂无视频，换个分类试试" :image-size="80" />

          <div
            v-for="item in displayVideos"
            :key="item.id"
            class="video-card"
            @click="openVideo(item)"
          >
            <div class="card-cover">
              <img :src="item.cover" :alt="item.title" />
              <div class="cover-mask">
                <div class="play-btn-circle">
                  <el-icon :size="22"><VideoPlay /></el-icon>
                </div>
              </div>
              <div class="cover-stats">
                <span class="stat-views"><el-icon><View /></el-icon>{{ item.views }}</span>
                <span class="stat-comments"><el-icon><ChatDotRound /></el-icon>{{ item.comments }}</span>
              </div>
              <span class="cover-duration">{{ item.duration }}</span>
            </div>
            <div class="card-body">
              <h4 class="card-title" :title="item.title">{{ item.title }}</h4>
              <div class="card-meta">
                <span class="meta-author">{{ item.author }}</span>
                <span class="meta-time">{{ item.publishTime }}</span>
              </div>
              <span v-if="item.tag" class="card-tag" :class="tagClass(item.tag)">{{ item.tag }}</span>
            </div>
          </div>
        </div>

        <!-- 换一换按钮 -->
        <div class="refresh-sidebar" @click="shuffleVideos">
          <div class="refresh-btn">
            <el-icon :size="18"><Refresh /></el-icon>
            <span>换一换</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右下角浮动按钮 -->
    <div class="float-play-btn" @click="onFloatPlay">
      <el-icon :size="22"><VideoPause /></el-icon>
      <span>最近观看</span>
    </div>

    <!-- 视频播放弹窗 -->
    <el-dialog v-model="playVisible" :title="currentVideo?.title || '视频播放'" width="800px" destroy-on-close :close-on-click-modal="false">
      <div class="video-player-placeholder">
        <div class="player-screen">
          <el-icon :size="56"><VideoPlay /></el-icon>
          <p>视频播放器区域</p>
          <p class="player-hint">实际项目中将嵌入真实视频播放器（如 Video.js / DPlayer）</p>
        </div>
      </div>
      <div v-if="currentVideo" class="player-info">
        <p><strong>{{ currentVideo.title }}</strong></p>
        <p class="text-muted">作者：{{ currentVideo.author }} · 发布时间：{{ currentVideo.publishTime }} · 播放量：{{ currentVideo.views }}</p>
      </div>
      <template #footer>
        <el-button @click="playVisible = false">关闭</el-button>
        <el-button type="primary" @click="$router.push('/knowledge/videos/' + currentVideo?.id)">查看详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Sunny, Apple, Warning } from '@element-plus/icons-vue'

const router = useRouter()

// ==================== 分类数据 ====================
const categories = [
  { key: 'recommend',  label: '推荐' },
  { key: 'greenhouse', label: '温室管理' },
  { key: 'strawberry', label: '草莓种植' },
  { key: 'pest',       label: '病虫害防治' },
  { key: 'irrigation', label: '水肥一体化' },
  { key: 'drone',      label: '无人机巡检' },
  { key: 'device',     label: '智能设备' },
  { key: 'traceability', label: '农产品溯源' },
  { key: 'market',     label: '市场行情' },
  { key: 'expert',     label: '专家讲堂' },
  { key: 'policy',     label: '政策解读' },
  { key: 'more',       label: '更多' }
]

// ==================== 模拟数据 ====================
const allVideos = [
  { id: 1,  title: '温室草莓高产栽培关键技术讲解',            cover: 'https://picsum.photos/seed/straw1/400/225', views: '2.3万', comments: 328, duration: '12:36', author: '河南农技推广中心',    publishTime: '06-28', category: 'strawberry',  tag: '推荐' },
  { id: 2,  title: '草莓叶斑病识别与绿色防治方法',             cover: 'https://picsum.photos/seed/straw2/400/225', views: '1.8万', comments: 216, duration: '09:42', author: '植保专家课堂',        publishTime: '06-27', category: 'pest',        tag: '专家' },
  { id: 3,  title: '水肥一体化在草莓种植中的实际应用',         cover: 'https://picsum.photos/seed/irri1/400/225', views: '1.5万', comments: 187, duration: '15:20', author: '智慧农业研究院',      publishTime: '06-26', category: 'irrigation',  tag: '热门' },
  { id: 4,  title: '无人机巡检助力智慧温室管理',               cover: 'https://picsum.photos/seed/drone1/400/225', views: '3.1万', comments: 452, duration: '08:15', author: '农业科技频道',        publishTime: '06-28', category: 'drone',       tag: '推荐' },
  { id: 5,  title: '温室温湿度智能调控系统安装与使用',         cover: 'https://picsum.photos/seed/gh1/400/225',   views: '9.8k',  comments: 124, duration: '11:08', author: '设施农业技术中心',    publishTime: '06-25', category: 'greenhouse',  tag: '' },
  { id: 6,  title: '草莓灰霉病早期预警与综合防控策略',         cover: 'https://picsum.photos/seed/pest1/400/225', views: '2.6万', comments: 389, duration: '14:55', author: '省农科院植保所',      publishTime: '06-24', category: 'pest',        tag: '' },
  { id: 7,  title: '农产品溯源二维码生成与消费者查询演示',      cover: 'https://picsum.photos/seed/trace1/400/225',views: '6.2k',  comments: 73,  duration: '07:30', author: '溯源技术服务平台',      publishTime: '06-23', category: 'traceability', tag: '' },
  { id: 8,  title: '草莓大棚补光灯选型与安装指南',             cover: 'https://picsum.photos/seed/device1/400/225',views: '1.2万', comments: 156, duration: '06:48', author: '光电农业实验室',        publishTime: '06-22', category: 'device',      tag: '' },
  { id: 9,  title: '6月份全国草莓批发市场价格分析',             cover: 'https://picsum.photos/seed/market1/400/225',views: '4.5万', comments: 612, duration: '18:22', author: '农产品市场信息中心',    publishTime: '06-21', category: 'market',      tag: '热门' },
  { id: 10, title: '中国农大教授谈草莓产业未来发展趋势',        cover: 'https://picsum.photos/seed/expert1/400/225',views: '7.8k',  comments: 95,  duration: '22:10', author: '中国农业大学',          publishTime: '06-20', category: 'expert',      tag: '专家' },
  { id: 11, title: '草莓育苗基质配方与穴盘育苗技术',           cover: 'https://picsum.photos/seed/straw3/400/225', views: '1.1万', comments: 142, duration: '10:15', author: '育苗技术推广站',        publishTime: '06-19', category: 'strawberry',  tag: '' },
  { id: 12, title: '智能传感器在温室环境监测中的应用',         cover: 'https://picsum.photos/seed/sensor1/400/225',views: '8.5k',  comments: 108, duration: '13:40', author: '物联网农业实验室',      publishTime: '06-18', category: 'device',      tag: '' },
  { id: 13, title: '2024年农业补贴政策解读——设施农业篇',       cover: 'https://picsum.photos/seed/policy1/400/225',views: '2.2万', comments: 278, duration: '16:05', author: '农业农村部信息中心',    publishTime: '06-17', category: 'policy',      tag: '' },
  { id: 14, title: '草莓白粉病综合防控技术现场教学',           cover: 'https://picsum.photos/seed/pest2/400/225', views: '1.9万', comments: 231, duration: '11:30', author: '绿色防控技术团队',      publishTime: '06-16', category: 'pest',        tag: '' },
  { id: 15, title: '无人机多光谱巡检草莓病害识别实操',         cover: 'https://picsum.photos/seed/drone2/400/225', views: '5.1万', comments: 734, duration: '20:18', author: '精准农业技术中心',      publishTime: '06-15', category: 'drone',       tag: '热门' },
  { id: 16, title: '滴灌系统日常维护与故障排除方法',           cover: 'https://picsum.photos/seed/irri2/400/225', views: '7.2k',  comments: 88,  duration: '08:55', author: '节水灌溉工程公司',      publishTime: '06-14', category: 'irrigation',  tag: '' },
  { id: 17, title: '温室草莓采收后保鲜与冷链运输要点',         cover: 'https://picsum.photos/seed/straw4/400/225', views: '1.3万', comments: 167, duration: '09:12', author: '冷链物流研究中心',      publishTime: '06-13', category: 'strawberry',  tag: '' },
  { id: 18, title: '农产品质量安全追溯体系建设指南',           cover: 'https://picsum.photos/seed/trace2/400/225',views: '1.6万', comments: 204, duration: '17:45', author: '质量标准研究所',        publishTime: '06-12', category: 'traceability', tag: '' },
]

// ==================== 轮播数据 ====================
const bannerVideos = [
  { id: 101, title: '温室草莓高产管理技术，从育苗到采收全流程',   cover: 'https://picsum.photos/seed/banner1/720/400', category: '草莓种植' },
  { id: 102, title: '草莓叶斑病识别与防治方法，绿色防控方案详解',  cover: 'https://picsum.photos/seed/banner2/720/400', category: '病虫害防治' },
  { id: 103, title: '水肥一体化在草莓种植中的应用，节水节肥增效',  cover: 'https://picsum.photos/seed/banner3/720/400', category: '水肥一体化' },
  { id: 104, title: '无人机巡检助力智慧温室管理，精准农业新篇章',  cover: 'https://picsum.photos/seed/banner4/720/400', category: '无人机巡检' },
]

// ==================== 热门排行榜数据 ====================
const hotRankList = [
  { id: 2,  title: '草莓叶斑病识别与绿色防治方法',         views: '1.8万', tag: '专家' },
  { id: 5,  title: '温室湿度过高的调控技巧',               views: '1.4万', tag: '推荐' },
  { id: 3,  title: '水肥一体化在草莓种植中的实际应用',     views: '1.5万', tag: '热门' },
  { id: 15, title: '无人机多光谱巡检草莓病害识别实操',     views: '5.1万', tag: '热门' },
  { id: 17, title: '草莓采收后的冷链运输要点',             views: '1.3万', tag: '' },
]

// ==================== 草莓专题合集 ====================
const topics = [
  { key: 'greenhouse', name: '育苗管理',   icon: 'Sunny',   count: 12, bg: '#e8f5e9' },
  { key: 'strawberry', name: '开花坐果',   icon: 'Apple',   count: 8,  bg: '#fff8e1' },
  { key: 'pest',       name: '病虫害防治', icon: 'Warning', count: 15, bg: '#fce4ec' },
]

// ==================== 状态 ====================
const activeCategory = ref('recommend')
const bannerIndex = ref(0)
const shuffleSeed = ref(0)
const playVisible = ref(false)
const currentVideo = ref(null)
let autoSlideTimer = null
const SLIDE_INTERVAL = 4000

// ==================== 计算属性 ====================
const categoryLabel = computed(() => {
  const c = categories.find(c => c.key === activeCategory.value)
  return c ? c.label : '全部视频'
})

const displayVideos = computed(() => {
  let filtered
  if (activeCategory.value === 'recommend') {
    filtered = allVideos
  } else if (activeCategory.value === 'hot') {
    filtered = allVideos.filter(v => v.tag === '热门' || v.tag === '推荐')
  } else if (activeCategory.value === 'dynamic') {
    filtered = [...allVideos].sort(() => 0.5 - Math.random())
  } else if (activeCategory.value === 'more') {
    return []
  } else {
    filtered = allVideos.filter(v => v.category === activeCategory.value)
  }
  if (shuffleSeed.value > 0) {
    const arr = [...filtered]
    for (let i = arr.length - 1; i > 0; i--) {
      const j = (shuffleSeed.value * (i + 1)) % arr.length
      ;[arr[i], arr[j]] = [arr[j], arr[i]]
    }
    return arr
  }
  return filtered
})

// ==================== 方法 ====================
function switchCategory(key) {
  activeCategory.value = key
  shuffleSeed.value = 0
}

function prevBanner() {
  bannerIndex.value = (bannerIndex.value - 1 + bannerVideos.length) % bannerVideos.length
  resetAutoSlide()
}

function nextBanner() {
  bannerIndex.value = (bannerIndex.value + 1) % bannerVideos.length
  resetAutoSlide()
}

function startAutoSlide() {
  stopAutoSlide()
  autoSlideTimer = setInterval(() => { nextBanner() }, SLIDE_INTERVAL)
}

function stopAutoSlide() {
  if (autoSlideTimer) { clearInterval(autoSlideTimer); autoSlideTimer = null }
}

function resetAutoSlide() {
  stopAutoSlide()
  startAutoSlide()
}

function shuffleVideos() {
  shuffleSeed.value = Date.now() % 100 + 1
}

function openVideo(item) {
  currentVideo.value = item
  playVisible.value = true
}

function openCreate() {
  router.push('/knowledge/videos/create')
}

function onFloatPlay() {
  ElMessage.info('暂无最近观看记录')
}

function tagClass(tag) {
  return { '推荐': 'tag-rec', '热门': 'tag-hot', '专家': 'tag-expert' }[tag] || ''
}

onMounted(() => { startAutoSlide() })
onUnmounted(() => { stopAutoSlide() })
</script>

<style scoped>
/* ========== 页面容器 ========== */
.agri-video-page {
  max-width: 1500px;
  margin: -20px auto 0;
  padding: 16px 20px 32px;
  min-height: calc(100vh - 60px);
  background: #f4f7f4;
}

/* ========== 顶部分类导航 ========== */
.category-nav {
  display: flex; align-items: center; gap: 12px;
  margin-bottom: 18px; padding: 10px 16px;
  background: #fff; border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  flex-wrap: wrap;
}
.nav-specials { display: flex; gap: 8px; flex-shrink: 0; }
.special-item {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 7px 14px; border-radius: 20px; font-size: 13px; font-weight: 600;
  cursor: pointer; transition: all 0.25s; border: 1px solid transparent;
}
.special-item.hot { background: #fff3e0; color: #e65100; }
.special-item.dynamic { background: #e8f5e9; color: #2e7d32; }
.special-item:hover, .special-item.active { transform: translateY(-1px); box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.special-item.hot.active { border-color: #e65100; }
.special-item.dynamic.active { border-color: #2e7d32; }

.nav-categories {
  display: flex; gap: 6px; flex: 1; overflow-x: auto;
  scrollbar-width: none; -ms-overflow-style: none;
}
.nav-categories::-webkit-scrollbar { display: none; }

.cat-btn {
  display: inline-block; padding: 7px 16px; border-radius: 20px;
  font-size: 13px; white-space: nowrap; cursor: pointer;
  background: #f5f7f5; color: #555; border: 1px solid transparent;
  transition: all 0.25s; user-select: none;
}
.cat-btn:hover { background: #e8f5e9; color: #2e7d32; transform: translateY(-1px); }
.cat-btn.active { background: #2e7d32; color: #fff; font-weight: 600; box-shadow: 0 2px 8px rgba(46,125,50,0.3); }
.nav-actions { flex-shrink: 0; margin-left: auto; }

/* ========== 主内容区：CSS Grid 左右布局 ========== */
.main-content {
  display: grid;
  grid-template-columns: 42% 1fr;
  gap: 20px;
  align-items: start;
}

/* ========== 左侧推荐栏 ========== */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ========== 通用面板 ========== */
.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.05);
  overflow: hidden;
}
.panel-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 16px 10px;
}
.panel-title {
  margin: 0; font-size: 15px; font-weight: 700; color: #303133;
  display: flex; align-items: center; gap: 6px;
}
.panel-title .el-icon { color: #2e7d32; }
.panel-more {
  font-size: 12px; color: #999; cursor: pointer; user-select: none;
}
.panel-more:hover { color: #2e7d32; }

/* ========== 1. 轮播图 ========== */
.banner-panel { padding: 0; }
.banner-carousel {
  position: relative; width: 100%; aspect-ratio: 16 / 10;
  border-radius: 12px; overflow: hidden; cursor: pointer;
}
.banner-track { display: flex; height: 100%; transition: transform 0.5s cubic-bezier(0.4,0,0.2,1); }
.banner-slide { min-width: 100%; height: 100%; position: relative; }
.banner-img-wrap { width: 100%; height: 100%; overflow: hidden; }
.banner-img-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s; }
.banner-carousel:hover .banner-img-wrap img { transform: scale(1.05); }
.banner-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.65) 0%, transparent 50%); pointer-events: none; }
.banner-info { position: absolute; left: 20px; bottom: 20px; right: 60px; color: #fff; }
.banner-category { display: inline-block; padding: 3px 10px; border-radius: 4px; font-size: 12px; background: rgba(255,255,255,0.25); backdrop-filter: blur(4px); margin-bottom: 8px; }
.banner-title { margin: 0; font-size: 20px; font-weight: 700; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.banner-arrow {
  position: absolute; top: 50%; transform: translateY(-50%);
  width: 36px; height: 36px; border: none; border-radius: 50%;
  background: rgba(255,255,255,0.85); color: #333; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.3s; z-index: 2;
}
.banner-carousel:hover .banner-arrow { opacity: 1; }
.banner-arrow.left { left: 10px; } .banner-arrow.right { right: 10px; }
.banner-arrow:hover { background: #fff; }

.banner-dots { position: absolute; bottom: 14px; left: 50%; transform: translateX(-50%); display: flex; gap: 8px; z-index: 2; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(255,255,255,0.5); cursor: pointer; transition: all 0.3s; }
.dot.active { background: #fff; width: 24px; border-radius: 4px; }

/* ========== 2. 热门农技榜 ========== */
.hot-rank-list { padding: 0 16px 14px; }
.hot-rank-item {
  display: flex; align-items: center; gap: 12px; padding: 10px 0;
  border-bottom: 1px solid #f5f5f5; cursor: pointer; transition: all 0.2s;
}
.hot-rank-item:last-child { border-bottom: none; }
.hot-rank-item:hover { background: #f9fdf9; margin: 0 -8px; padding-left: 8px; padding-right: 8px; border-radius: 6px; }

.rank-num {
  width: 22px; height: 22px; border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700; color: #999; background: #f0f0f0;
  flex-shrink: 0;
}
.rank-num.top3 { color: #fff; }
.rank-num.top3:nth-child(1) { background: #e65100; } /* 它作为 flex item，无法用 nth-child，用内联更好 */
/* 实际 rank-num 不在容器内用 nth，而是根据 idx 绑定 class */
.hot-rank-item:first-child .rank-num { background: #e65100; color: #fff; }
.hot-rank-item:nth-child(2) .rank-num { background: #f57c00; color: #fff; }
.hot-rank-item:nth-child(3) .rank-num { background: #f9a825; color: #fff; }

.rank-info { flex: 1; min-width: 0; }
.rank-title { margin: 0 0 4px; font-size: 13px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rank-meta { display: flex; align-items: center; gap: 8px; font-size: 11px; color: #999; }
.rank-views { display: flex; align-items: center; gap: 2px; }
.mini-tag { padding: 1px 5px; border-radius: 3px; font-size: 10px; color: #fff; }
.mini-tag.green { background: #2e7d32; }
.mini-tag.orange { background: #e65100; }

/* ========== 3. 草莓专题合集 ========== */
.topic-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; padding: 0 16px 14px; }
.topic-card {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 14px 8px; border-radius: 10px; cursor: pointer;
  transition: all 0.25s;
}
.topic-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.topic-card .el-icon { color: #2e7d32; }
.topic-name { font-size: 12px; font-weight: 600; color: #444; }
.topic-count { font-size: 11px; color: #999; }

/* ========== 4. 专家推荐 ========== */
.expert-card {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px; cursor: pointer; transition: background 0.2s;
}
.expert-card:hover { background: #f9fdf9; }
.expert-avatar { flex-shrink: 0; }
.expert-name { margin: 0; font-size: 14px; font-weight: 700; color: #333; }
.expert-org { margin: 2px 0; font-size: 12px; color: #888; }
.expert-course { margin: 6px 0 0; font-size: 12px; color: #2e7d32; }
.expert-arrow { color: #ccc; font-size: 18px; flex-shrink: 0; }

/* ========== 右侧视频区 ========== */
.right-column { min-width: 0; position: relative; }
.video-grid-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.grid-title { margin: 0; font-size: 17px; color: #303133; font-weight: 700; }
.grid-count { font-size: 13px; color: #999; }

.video-grid-wrapper {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  padding-right: 52px;
}

/* ========== 视频卡片 ========== */
.video-card {
  background: #fff; border-radius: 10px; overflow: hidden; cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s; box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.video-card:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(0,0,0,0.1); }
.card-cover { position: relative; aspect-ratio: 16 / 9; overflow: hidden; background: #e0e5e0; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s; }
.video-card:hover .card-cover img { transform: scale(1.06); }
.cover-mask {
  position: absolute; inset: 0; background: rgba(0,0,0,0.08);
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.3s;
}
.video-card:hover .cover-mask { opacity: 1; }
.play-btn-circle {
  width: 44px; height: 44px; border-radius: 50%;
  background: rgba(255,255,255,0.9); color: #2e7d32;
  display: flex; align-items: center; justify-content: center;
}
.cover-stats { position: absolute; left: 6px; bottom: 6px; display: flex; gap: 10px; font-size: 12px; color: #fff; text-shadow: 0 1px 2px rgba(0,0,0,0.5); }
.stat-views, .stat-comments { display: flex; align-items: center; gap: 3px; }
.cover-duration { position: absolute; right: 6px; bottom: 6px; padding: 2px 7px; border-radius: 4px; background: rgba(0,0,0,0.7); color: #fff; font-size: 11px; font-weight: 500; }
.card-body { padding: 10px 12px 12px; position: relative; }
.card-title { margin: 0 0 8px; font-size: 14px; font-weight: 600; color: #222; line-height: 1.45; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; min-height: 40px; }
.card-meta { display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #999; }
.card-tag { position: absolute; top: -14px; left: 12px; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; color: #fff; }
.tag-rec { background: #2e7d32; } .tag-hot { background: #e65100; } .tag-expert { background: #1565c0; }

/* ========== 换一换按钮 ========== */
.refresh-sidebar { position: absolute; right: 20px; top: 4px; }
.refresh-btn {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 12px 8px; border-radius: 10px; background: #fff; color: #666;
  font-size: 12px; cursor: pointer; box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border: 1px solid #eee; transition: all 0.25s; user-select: none;
}
.refresh-btn:hover { color: #2e7d32; border-color: #2e7d32; box-shadow: 0 4px 14px rgba(46,125,50,0.15); transform: translateY(-1px); }

/* ========== 浮动按钮 ========== */
.float-play-btn {
  position: fixed; right: 28px; bottom: 36px;
  display: flex; align-items: center; gap: 6px;
  padding: 10px 18px; border-radius: 24px; background: #fff;
  box-shadow: 0 4px 16px rgba(0,0,0,0.12); cursor: pointer;
  font-size: 13px; color: #555; transition: all 0.25s; z-index: 100;
}
.float-play-btn:hover { color: #2e7d32; box-shadow: 0 6px 24px rgba(46,125,50,0.2); transform: translateY(-2px); }

/* ========== 视频播放弹窗 ========== */
.video-player-placeholder {
  width: 100%; aspect-ratio: 16 / 9; border-radius: 8px; background: #1a1a2e;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  color: #ccc; gap: 8px;
}
.player-screen .el-icon { color: #4caf50; }
.player-hint { font-size: 12px; color: #888; }
.player-info { margin-top: 12px; padding: 10px 0; }
.player-info strong { font-size: 15px; color: #303133; }
.text-muted { color: #999; font-size: 13px; margin: 4px 0 0; }

/* ========== 响应式 ========== */
@media (max-width: 1300px) {
  .video-grid-wrapper { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  .left-column { flex-direction: row; flex-wrap: wrap; }
  .banner-panel { width: 100%; }
  .hot-rank-panel, .topic-panel, .expert-panel { flex: 1; min-width: 200px; }
  .banner-carousel { aspect-ratio: 16 / 7; }
  .video-grid-wrapper { padding-right: 0; }
  .refresh-sidebar { position: fixed; right: 20px; top: 50%; transform: translateY(-50%); }
}
@media (max-width: 640px) {
  .left-column { flex-direction: column; }
  .video-grid-wrapper { grid-template-columns: 1fr; }
  .banner-title { font-size: 16px; }
  .topic-grid { grid-template-columns: repeat(3, 1fr); }
  .category-nav { padding: 8px; gap: 6px; }
  .cat-btn { padding: 5px 12px; font-size: 12px; }
}
</style>

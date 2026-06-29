<template>
  <div class="agri-content-page">
    <!-- 页头 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">农技内容中心</h1>
        <p class="page-subtitle">汇聚草莓种植文章、农技视频与病虫害防治知识，服务温室草莓生产管理</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" size="large" round @click="openPublishDialog">
          <el-icon><Edit /></el-icon> 发布内容
        </el-button>
        <el-input
          v-model="keyword"
          placeholder="搜索草莓种植、病虫害防治、温室管理、水肥技术"
          clearable
          size="large"
          class="header-search"
          @keyup.enter="doSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </div>

    <!-- 分类 Tab -->
    <div class="category-tabs">
      <span
        v-for="tab in categoryTabs"
        :key="tab.value"
        :class="['cat-item', { active: activeCategory === tab.value }]"
        @click="activeCategory = tab.value; currentPage = 1; loadContent()"
      >{{ tab.label }}</span>
    </div>

    <!-- 内容类型筛选 -->
    <div class="type-filter">
      <el-radio-group v-model="contentType" size="default" @change="currentPage = 1; loadContent()">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="ARTICLE">文章</el-radio-button>
        <el-radio-button value="VIDEO">视频</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 主体布局 -->
    <div class="page-body">
      <!-- 左侧栏 -->
      <aside class="sidebar">
        <!-- 农技精选 -->
        <div class="side-card">
          <h3 class="side-title"><el-icon><StarFilled /></el-icon> 农技精选</h3>
          <div class="rank-list" v-if="featuredList.length">
            <div v-for="(item, idx) in featuredList" :key="item.id" class="rank-item" @click="viewDetail(item)">
              <span :class="['rank-num', { top3: idx < 3 }]">{{ idx + 1 }}</span>
              <div class="rank-info">
                <p class="rank-title">{{ item.title }}</p>
                <span class="rank-meta">{{ item.author || item.source }} · {{ formatTime(item.publishTime) }}</span>
              </div>
              <el-tag size="small" :type="item.contentType === 'VIDEO' ? 'danger' : 'success'" effect="plain">
                {{ item.contentType === 'VIDEO' ? '视频' : '文章' }}
              </el-tag>
            </div>
          </div>
          <el-empty v-else description="暂无精选内容" :image-size="60" />
        </div>

        <!-- 热门农技 -->
        <div class="side-card">
          <h3 class="side-title"><el-icon><TrendCharts /></el-icon> 热门农技</h3>
          <div class="rank-list" v-if="hotList.length">
            <div v-for="(item, idx) in hotList" :key="item.id" class="rank-item" @click="viewDetail(item)">
              <span :class="['rank-num', { top3: idx < 3 }]">{{ idx + 1 }}</span>
              <div class="rank-info">
                <p class="rank-title">{{ item.title }}</p>
                <span class="rank-meta">{{ formatCount(item.viewCount) }} 浏览 · {{ formatTime(item.publishTime) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无热门内容" :image-size="60" />
        </div>

        <!-- 草莓专题合集 -->
        <div class="side-card">
          <h3 class="side-title"><el-icon><Collection /></el-icon> 草莓专题</h3>
          <div class="topic-grid">
            <div v-for="t in topics" :key="t.name" class="topic-item" @click="activeCategory = t.category; currentPage = 1; loadContent()">
              <div class="topic-icon" :style="{ background: t.color }"><el-icon><component :is="t.icon" /></el-icon></div>
              <span>{{ t.name }}</span>
            </div>
          </div>
        </div>

        <!-- 专家推荐 -->
        <div class="side-card">
          <h3 class="side-title"><el-icon><UserFilled /></el-icon> 专家团队</h3>
          <div v-if="experts.length" class="expert-list">
            <div v-for="e in experts" :key="e.name" class="expert-item">
              <el-avatar :size="36" :src="e.avatar" />
              <div class="expert-info">
                <p class="expert-name">{{ e.name }}</p>
                <p class="expert-title">{{ e.org }}</p>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="content-main">
        <div class="section-header">
          <h2 class="section-title">{{ sectionTitle }}</h2>
        </div>
        <div class="content-grid" v-loading="listLoading">
          <div
            v-for="item in dataList"
            :key="item.id"
            class="content-card"
            @click="viewDetail(item)"
          >
            <!-- 封面 -->
            <div class="card-cover">
              <img :src="item.coverUrl || defaultCover" :alt="item.title" />
              <el-tag class="content-tag" size="small" :type="item.contentType === 'VIDEO' ? 'danger' : 'success'">
                <el-icon v-if="item.contentType === 'VIDEO'"><VideoPlay /></el-icon>
                <el-icon v-else><Document /></el-icon>
                {{ item.contentType === 'VIDEO' ? '视频' : '文章' }}
              </el-tag>
              <div v-if="item.contentType === 'VIDEO'" class="play-btn">
                <el-icon :size="36"><VideoPlay /></el-icon>
              </div>
              <span v-if="item.contentType === 'VIDEO' && item.videoDuration" class="duration-tag">{{ item.videoDuration }}</span>
            </div>
            <!-- 信息 -->
            <div class="card-body">
              <h4 class="card-title" :title="item.title">{{ item.title }}</h4>
              <div class="card-meta">
                <span class="meta-author">{{ item.author || item.source || '未知来源' }}</span>
                <span class="meta-time">{{ formatTime(item.publishTime) }}</span>
              </div>
              <div class="card-stats">
                <span class="stat-item"><el-icon><View /></el-icon> {{ formatCount(item.viewCount) }}</span>
                <span class="stat-item"><el-icon><Star /></el-icon> {{ formatCount(item.collectCount || 0) }}</span>
              </div>
              <div class="card-tags">
                <el-tag size="small" type="info" effect="plain">{{ item.category || '未分类' }}</el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!listLoading && !dataList.length" description="暂无相关内容" :image-size="100" />

        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination-wrap">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            background
            @current-change="loadContent"
          />
        </div>
      </main>
    </div>

    <!-- ============ 内容发布弹窗 ============ -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '发布内容' : '编辑内容'"
      width="680px"
      top="4vh"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div class="publish-dialog">
        <div v-if="dialogMode === 'add'" class="type-selector">
          <span class="type-label">内容类型：</span>
          <el-radio-group v-model="form.contentType" size="large" @change="onTypeChange">
            <el-radio-button value="ARTICLE">文章</el-radio-button>
            <el-radio-button value="VIDEO">视频</el-radio-button>
          </el-radio-group>
        </div>
        <el-form :model="form" label-width="80px" label-position="top" class="publish-form">
          <el-row :gutter="16">
            <el-col :span="16"><el-form-item label="标题" required><el-input v-model="form.title" placeholder="请输入标题" /></el-form-item></el-col>
            <el-col :span="8">
              <el-form-item label="分类" required>
                <el-select v-model="form.category" placeholder="请选择分类">
                  <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="标签">
                <el-input v-model="form.tags" placeholder="逗号分隔，如 草莓,白粉病,防治" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="资源来源">
                <el-radio-group v-model="form.mediaSource" size="small">
                  <el-radio value="local">本地上传</el-radio>
                  <el-radio value="external">外链URL</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 封面上传 -->
          <el-form-item label="封面图">
            <template v-if="form.mediaSource === 'local'">
              <el-upload
                class="cover-uploader"
                :show-file-list="false"
                :before-upload="beforeCoverUpload"
                :http-request="handleCoverUpload"
                accept="image/jpeg,image/png,image/webp"
              >
                <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
                <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
                <div v-if="coverUploading" class="upload-loading"><el-icon class="is-loading"><Loading /></el-icon> 上传中...</div>
              </el-upload>
            </template>
            <el-input v-else v-model="form.coverUrl" placeholder="封面图外链URL" />
          </el-form-item>
          <template v-if="form.contentType === 'ARTICLE'">
            <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要" /></el-form-item>
            <el-form-item label="正文内容" required><el-input v-model="form.content" type="textarea" :rows="6" placeholder="文章正文，支持HTML" /></el-form-item>
          </template>
          <template v-if="form.contentType === 'VIDEO'">
            <el-form-item label="视频文件">
              <template v-if="form.mediaSource === 'local'">
                <el-upload
                  class="video-uploader"
                  :show-file-list="false"
                  :before-upload="beforeVideoUpload"
                  :http-request="handleVideoUpload"
                  accept="video/mp4,video/webm,video/quicktime"
                >
                  <div v-if="!form.videoUrl" class="video-upload-box">
                    <el-icon :size="32"><VideoPlay /></el-icon>
                    <span>点击上传视频</span>
                    <span class="upload-hint">支持 mp4/webm/mov, 最大100MB</span>
                  </div>
                  <div v-else class="video-uploaded">
                    <el-icon :size="24"><VideoPlay /></el-icon>
                    <span>视频已上传</span>
                  </div>
                  <div v-if="videoUploading" class="upload-loading">
                    <el-progress :percentage="videoUploadPercent" />
                  </div>
                </el-upload>
                <p v-if="form.videoUrl" class="video-preview-link">
                  <video :src="form.videoUrl" controls preload="metadata" style="width:100%;max-height:180px;border-radius:6px;margin-top:8px"></video>
                </p>
              </template>
              <el-input v-else v-model="form.videoUrl" placeholder="视频外链URL" />
            </el-form-item>
            <el-form-item label="视频时长"><el-input v-model="form.videoDuration" placeholder="如 12:36" /></el-form-item>
            <el-form-item label="视频简介"><el-input v-model="form.summary" type="textarea" :rows="2" placeholder="选填" /></el-form-item>
          </template>
          <el-row :gutter="16">
            <el-col :span="12"><el-form-item label="作者"><el-input v-model="form.author" placeholder="作者名称" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="来源机构"><el-input v-model="form.source" placeholder="选填" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="发布状态">
                <el-select v-model="form.publishStatus">
                  <el-option label="立即发布" value="PUBLISHED" />
                  <el-option label="存为草稿" value="DRAFT" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doPublish" :loading="publishLoading">{{ dialogMode === 'add' ? '发布' : '保存' }}</el-button>
      </template>
    </el-dialog>

    <!-- ============ 内容详情弹窗 ============ -->
    <el-dialog
      v-model="detailVisible"
      :title="detailItem?.title"
      width="860px"
      top="4vh"
      :close-on-click-modal="false"
      @opened="onDetailOpened"
    >
      <div v-if="detailItem" class="detail-content">
        <div class="detail-meta">
          <el-tag :type="detailItem.contentType === 'VIDEO' ? 'danger' : 'success'" size="small">{{ detailItem.contentType === 'VIDEO' ? '视频' : '文章' }}</el-tag>
          <el-tag type="info" size="small" effect="plain">{{ detailItem.category }}</el-tag>
          <span class="meta-text">{{ detailItem.author || '未知作者' }}</span>
          <span class="meta-text">{{ formatTime(detailItem.publishTime) }}</span>
          <span class="meta-text">{{ formatCount(detailItem.viewCount) }} 浏览</span>
          <span class="meta-text">{{ formatCount(detailItem.collectCount || 0) }} 收藏</span>
        </div>
        <img v-if="detailItem.coverUrl" :src="detailItem.coverUrl" class="detail-cover" />
        <div v-if="detailItem.contentType === 'VIDEO' && detailItem.videoUrl" class="detail-video">
          <video :src="detailItem.videoUrl" controls preload="none" style="width:100%;border-radius:8px" @ended="onVideoEnded">
            您的浏览器不支持视频播放
          </video>
        </div>
        <div v-if="detailItem.contentType === 'ARTICLE' || detailItem.summary" class="detail-body">
          <p v-if="detailItem.summary" class="detail-summary">{{ detailItem.summary }}</p>
          <div v-if="detailItem.content" class="detail-content-html" v-html="detailItem.content" />
        </div>
        <!-- 操作按钮 -->
        <div class="detail-actions">
          <el-button size="small" round @click="handleLike(detailItem)">
            <el-icon><Star /></el-icon> 点赞 {{ formatCount(detailItem.likeCount) }}
          </el-button>
          <el-button size="small" round @click="handleCollect(detailItem)">
            <el-icon><FolderOpened /></el-icon> 收藏 {{ formatCount(detailItem.collectCount) }}
          </el-button>
          <el-button size="small" round plain @click="detailVisible = false">关闭</el-button>
        </div>

        <!-- 相关技术 -->
        <div class="similar-section" v-if="similarList.length">
          <h3 class="similar-title">相关技术</h3>
          <div class="similar-grid">
            <div v-for="item in similarList" :key="item.id" class="similar-item" @click="viewRelatedDetail(item)">
              <img :src="item.coverUrl || defaultCover" class="similar-cover" />
              <div class="similar-info">
                <p class="similar-name">{{ item.title }}</p>
                <span class="similar-tag">{{ item.category }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store'
import {
  listContent, addContent, updateContent, deleteContent,
  getRecommendContent, getSimilarContent, reportBehavior, getHotContent
} from '@/api/agri/content'
import { uploadImage, uploadVideo } from '@/api/common/upload'

const route = useRoute()
const userStore = useUserStore()

// ==================== 常量 ====================
const categories = ['草莓种植', '病虫害防治', '水肥一体化', '温室管理', '无人机巡检', '智能设备', '农产品溯源', '市场行情', '专家讲堂', '政策解读']

const categoryTabs = [
  { label: '全部', value: '' },
  { label: '文章', value: '' },
  { label: '视频', value: '' },
  { label: '病虫害防治', value: '病虫害防治' },
  { label: '种植管理', value: '草莓种植' },
  { label: '政策资讯', value: '政策解读' },
]

const topics = [
  { name: '高产栽培', category: '草莓种植', icon: 'Sunny', color: '#67c23a' },
  { name: '病害防治', category: '病虫害防治', icon: 'Warning', color: '#e6a23c' },
  { name: '水肥管理', category: '水肥一体化', icon: 'Drizzling', color: '#409eff' },
  { name: '温室巡检', category: '无人机巡检', icon: 'Promotion', color: '#909399' },
]

const experts = [
  { name: '李国华', org: '中国农科院草莓研究所', avatar: '' },
  { name: '赵明远', org: '河南农业大学园艺学院', avatar: '' },
  { name: '陈晓梅', org: '草莓育种创新中心', avatar: '' },
]

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjIyNSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZThmNWU5Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5MGE0YWUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiI+5Yac5oqA5YaF5a65PC90ZXh0Pjwvc3ZnPg=='

// ==================== 列表数据 ====================
const dataList = ref([])
const listLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const activeCategory = ref('')
const contentType = ref('')
const keyword = ref('')

// 侧边栏
const featuredList = ref([])
const hotList = ref([])

// 发布弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const publishLoading = ref(false)
const form = reactive(getEmptyForm())

// 详情弹窗
const detailVisible = ref(false)
const detailItem = ref(null)
const similarList = ref([])

// 当前 section 标题
const sectionTitle = computed(() => {
  if (activeCategory.value) return activeCategory.value
  if (contentType.value === 'ARTICLE') return '全部文章'
  if (contentType.value === 'VIDEO') return '全部视频'
  return '最新发布'
})

// ==================== 表单工具 ====================
function getEmptyForm() {
  return {
    contentType: 'ARTICLE',
    title: '', category: '', tags: '', coverUrl: '', mediaSource: 'local', summary: '', content: '',
    videoUrl: '', videoDuration: '', author: '', source: '',
    recommendFlag: 0, hotFlag: 0, publishStatus: 'PUBLISHED'
  }
}
function onTypeChange() {
  Object.assign(form, { videoUrl: '', videoDuration: '', content: '' })
  coverUploading.value = false; videoUploading.value = false
}

// ==================== 文件上传 ====================
const coverUploading = ref(false)
const videoUploading = ref(false)
const videoUploadPercent = ref(0)

function beforeCoverUpload(file) {
  const validTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!validTypes.includes(file.type)) { ElMessage.error('封面图只支持 jpg/png/webp 格式'); return false }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('封面图不能超过 5MB'); return false }
  return true
}
async function handleCoverUpload({ file }) {
  coverUploading.value = true
  try {
    const res = await uploadImage(file, form.contentType === 'VIDEO' ? 'video_cover' : 'article_cover')
    form.coverUrl = res.data.fileUrl
    ElMessage.success('封面上传成功')
  } catch { /* handled by interceptor */ }
  coverUploading.value = false
}

function beforeVideoUpload(file) {
  if (file.size > 100 * 1024 * 1024) { ElMessage.error('视频不能超过 100MB'); return false }
  return true
}
async function handleVideoUpload({ file }) {
  videoUploading.value = true; videoUploadPercent.value = 0
  try {
    const res = await uploadVideo(file, 'video_file')
    form.videoUrl = res.data.fileUrl
    ElMessage.success('视频上传成功')
    videoUploadPercent.value = 100
  } catch { /* handled by interceptor */ }
  videoUploading.value = false
}

// ==================== 用户 ID ====================
function getUserId() {
  return userStore.userId || null
}

// ==================== 加载数据 ====================
async function loadContent() {
  listLoading.value = true
  try {
    const r = await listContent({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      category: activeCategory.value || undefined,
      contentType: contentType.value || undefined,
      keyword: keyword.value || undefined
    })
    const d = r.data || r
    dataList.value = d.records || d.data || []
    total.value = d.total || 0
  } catch { dataList.value = []; total.value = 0 }
  listLoading.value = false
}

async function loadFeatured() {
  try {
    const uid = getUserId()
    const r = await getRecommendContent({ userId: uid, pageNum: 1, pageSize: 6 })
    const d = r.data || r
    featuredList.value = d.records || d.data || []
  } catch { featuredList.value = [] }
}

async function loadHot() {
  try {
    const r = await getHotContent({ pageNum: 1, pageSize: 8 })
    const d = r.data || r
    hotList.value = d.records || d.data || []
  } catch { hotList.value = [] }
}

function doSearch() {
  currentPage.value = 1
  loadContent()
  // 上报搜索行为
  const uid = getUserId()
  if (uid && keyword.value) {
    reportBehavior({ userId: uid, behaviorType: 'search', keyword: keyword.value }).catch(() => {})
  }
}

// ==================== 发布 ====================
function openPublishDialog() {
  dialogMode.value = 'add'
  Object.assign(form, getEmptyForm())
  dialogVisible.value = true
}

async function doPublish() {
  if (!form.title) return ElMessage.warning('请输入标题')
  if (!form.category) return ElMessage.warning('请选择分类')
  if (form.contentType === 'ARTICLE' && !form.content) return ElMessage.warning('请输入文章正文')
  if (form.contentType === 'VIDEO' && !form.videoUrl) return ElMessage.warning('请上传视频文件或输入视频地址')
  publishLoading.value = true
  try {
    const data = { ...form }
    if (data.contentType === 'VIDEO' && !data.summary) data.summary = data.title
    if (dialogMode.value === 'add') await addContent(data)
    else await updateContent(data)
    ElMessage.success(dialogMode.value === 'add' ? '发布成功' : '保存成功')
    dialogVisible.value = false
    loadContent(); loadFeatured(); loadHot()
  } catch { ElMessage.error('操作失败') }
  publishLoading.value = false
}

// ==================== 详情 & 行为跟踪 ====================
async function viewDetail(item) {
  detailItem.value = { ...item }
  detailVisible.value = true
  similarList.value = []
  // 上报点击行为
  trackBehavior('click', item.id)
}

async function onDetailOpened() {
  if (detailItem.value) {
    // 上报浏览行为
    trackBehavior('view', detailItem.value.id)
    // 加载相关技术
    loadSimilar(detailItem.value.id)
  }
}

async function viewRelatedDetail(item) {
  detailItem.value = { ...item }
  similarList.value = []
  await loadSimilar(item.id)
  trackBehavior('click', item.id)
  trackBehavior('view', item.id)
}

async function loadSimilar(contentId) {
  try {
    const r = await getSimilarContent({ contentId, pageSize: 6 })
    similarList.value = r.data || []
  } catch { similarList.value = [] }
}

function handleLike(item) {
  ElMessage.success('已点赞')
  trackBehavior('like', item.id)
  item.likeCount = (item.likeCount || 0) + 1
}

function handleCollect(item) {
  ElMessage.success('已收藏')
  trackBehavior('collect', item.id)
  item.collectCount = (item.collectCount || 0) + 1
}

function onVideoEnded() {
  if (detailItem.value) {
    trackBehavior('play_finish', detailItem.value.id, { duration: 0 })
  }
}

function trackBehavior(behaviorType, contentId, extra = {}) {
  const uid = getUserId()
  if (!uid) return
  reportBehavior({
    userId: uid,
    contentId: contentId || null,
    behaviorType,
    keyword: null,
    duration: extra.duration || 0
  }).catch(() => {})
}

// ==================== 删除 ====================
async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除该内容吗？', '确认删除', { type: 'warning' })
    await deleteContent(detailItem.value.id)
    ElMessage.success('已删除')
    detailVisible.value = false
    loadContent(); loadFeatured(); loadHot()
  } catch { /* cancelled */ }
}

// ==================== 格式化 ====================
function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}
function formatCount(n) {
  if (!n && n !== 0) return '0'
  n = Number(n)
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

// ==================== 初始化 ====================
onMounted(() => {
  const qt = route.query?.type
  if (qt === 'ARTICLE' || qt === 'VIDEO') contentType.value = qt
  loadContent()
  loadFeatured()
  loadHot()
})
</script>

<style scoped>
.agri-content-page { max-width: 1400px; margin: 0 auto; padding: 8px 16px 24px; }

/* 页头 */
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 22px; font-weight: 700; color: #2d6b3f; margin: 0 0 4px; }
.page-subtitle { font-size: 13px; color: #8ba99e; margin: 0; }
.header-actions { display: flex; align-items: center; gap: 10px; }
.header-search { width: 340px; }

/* 分类 Tab */
.category-tabs { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
.cat-item { padding: 6px 18px; border-radius: 20px; background: #f0f5f1; color: #5a7a66; font-size: 13px; cursor: pointer; transition: .2s; white-space: nowrap; }
.cat-item:hover, .cat-item.active { background: #3c8c4e; color: #fff; font-weight: 600; }

/* 类型筛选 */
.type-filter { margin-bottom: 16px; }

/* 主体布局 */
.page-body { display: flex; gap: 20px; align-items: flex-start; }
.sidebar { width: 280px; flex-shrink: 0; display: flex; flex-direction: column; gap: 16px; }
.content-main { flex: 1; min-width: 0; }

/* 侧边卡片 */
.side-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 1px 6px rgba(0,0,0,.06); }
.side-title { display: flex; align-items: center; gap: 6px; font-size: 15px; font-weight: 600; color: #2d6b3f; margin: 0 0 12px; padding-bottom: 10px; border-bottom: 1px solid #eef3ef; }
.side-title .el-icon { color: #3c8c4e; }

/* 排行列表 */
.rank-list { display: flex; flex-direction: column; gap: 10px; }
.rank-item { display: flex; align-items: center; gap: 8px; padding: 6px 0; cursor: pointer; border-bottom: 1px solid #f5f7f5; }
.rank-item:last-child { border-bottom: none; }
.rank-num { width: 22px; height: 22px; border-radius: 50%; background: #e8f5e9; color: #5a7a66; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; flex-shrink: 0; }
.rank-num.top3 { background: #3c8c4e; color: #fff; }
.rank-info { flex: 1; min-width: 0; }
.rank-title { font-size: 13px; color: #333; margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rank-meta { font-size: 11px; color: #999; }
.rank-views { font-size: 11px; color: #999; white-space: nowrap; }

/* 专题 */
.topic-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.topic-item { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 10px 6px; border-radius: 10px; background: #f8faf8; cursor: pointer; transition: .2s; }
.topic-item:hover { background: #e8f5e9; }
.topic-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #fff; }
.topic-item span { font-size: 12px; color: #5a7a66; font-weight: 500; }

/* 专家 */
.expert-list { display: flex; flex-direction: column; gap: 10px; }
.expert-item { display: flex; align-items: center; gap: 10px; }
.expert-info { flex: 1; }
.expert-name { font-size: 13px; font-weight: 600; color: #333; margin: 0; }
.expert-title { font-size: 11px; color: #999; margin: 2px 0 0; }

/* 内容区 */
.section-header { margin-bottom: 14px; }
.section-title { font-size: 17px; font-weight: 600; color: #2d6b3f; margin: 0; padding-left: 10px; border-left: 3px solid #3c8c4e; }

/* 内容网格 */
.content-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.content-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; box-shadow: 0 1px 6px rgba(0,0,0,.06); transition: transform .2s, box-shadow .2s; }
.content-card:hover { transform: translateY(-3px); box-shadow: 0 4px 16px rgba(0,0,0,.1); }

/* 封面 */
.card-cover { position: relative; height: 160px; overflow: hidden; background: #e8f5e9; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; }
.content-tag { position: absolute; top: 8px; left: 8px; }
.play-btn { position: absolute; top: 50%; left: 50%; transform: translate(-50%,-50%); color: rgba(255,255,255,.9); background: rgba(0,0,0,.4); border-radius: 50%; width: 52px; height: 52px; display: flex; align-items: center; justify-content: center; }
.duration-tag { position: absolute; bottom: 8px; right: 8px; background: rgba(0,0,0,.65); color: #fff; font-size: 11px; padding: 2px 6px; border-radius: 4px; }

/* 卡片信息 */
.card-body { padding: 12px; }
.card-title { font-size: 14px; font-weight: 600; color: #333; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; justify-content: space-between; font-size: 12px; color: #999; margin-bottom: 6px; }
.meta-author { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 140px; }
.card-stats { display: flex; gap: 12px; font-size: 12px; color: #999; margin-bottom: 8px; }
.stat-item { display: flex; align-items: center; gap: 3px; }
.card-tags { display: flex; gap: 4px; flex-wrap: wrap; }

/* 分页 */
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }

/* ============ 弹窗样式 ============ */
.publish-dialog { }
.type-selector { margin-bottom: 16px; display: flex; align-items: center; gap: 12px; }
.type-label { font-size: 14px; font-weight: 500; color: #333; }
.publish-form { }

/* 详情 */
.detail-content { }
.detail-meta { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; margin-bottom: 14px; }
.detail-meta .meta-text { font-size: 13px; color: #888; }
.detail-cover { width: 100%; border-radius: 8px; margin-bottom: 14px; max-height: 320px; object-fit: cover; }
.detail-video { margin-bottom: 14px; }
.detail-body { margin-bottom: 14px; }
.detail-summary { font-size: 14px; color: #555; line-height: 1.7; margin-bottom: 10px; padding: 10px; background: #f8faf8; border-radius: 8px; }
.detail-content-html { font-size: 14px; color: #333; line-height: 1.8; }
.detail-actions { display: flex; gap: 10px; margin-top: 16px; padding-top: 14px; border-top: 1px solid #eef3ef; }

/* 相关技术 */
.similar-section { margin-top: 20px; padding-top: 16px; border-top: 1px solid #eef3ef; }
.similar-title { font-size: 15px; font-weight: 600; color: #2d6b3f; margin: 0 0 12px; }
.similar-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.similar-item { cursor: pointer; border-radius: 8px; overflow: hidden; background: #f8faf8; transition: .2s; }
.similar-item:hover { background: #e8f5e9; }
.similar-cover { width: 100%; height: 100px; object-fit: cover; }
.similar-info { padding: 6px 8px; }
.similar-name { font-size: 12px; color: #333; margin: 0 0 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.similar-tag { font-size: 11px; color: #8ba99e; }

@media (max-width: 1200px) { .content-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 900px) {
  .page-body { flex-direction: column; }
  .sidebar { width: 100%; flex-direction: row; flex-wrap: wrap; }
  .side-card { flex: 1; min-width: 180px; }
  .content-grid { grid-template-columns: 1fr; }
}
</style>

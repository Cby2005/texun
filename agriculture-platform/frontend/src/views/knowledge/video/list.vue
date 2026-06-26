<template>
  <div class="page video-list-page">
    <div class="page-header">
      <div>
        <h2>农技视频</h2>
        <p>沉淀种植、病虫害、设施管理等实操视频资料</p>
      </div>
      <el-button type="primary" @click="$router.push('/knowledge/videos/create')">
        <el-icon><Upload /></el-icon>
        发布视频
      </el-button>
    </div>

    <el-card class="search-panel" shadow="never">
      <div class="search-bar">
        <el-input
          v-model="keyword"
          class="search-input"
          placeholder="搜索视频标题"
          clearable
          @keyup.enter="load"
        />
        <el-select v-model="cropType" class="crop-select" placeholder="作物类型" clearable @change="load">
          <el-option v-for="item in cropOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-button type="primary" @click="load">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <section v-if="recommendList.length > 0 && !hasSearch" class="section">
      <div class="section-header">
        <h3>推荐视频</h3>
      </div>
      <div class="video-grid">
        <video-card
          v-for="item in recommendList"
          :key="'recommend-' + item.id"
          :item="item"
          @open="openVideo"
        />
      </div>
    </section>

    <section class="section">
      <div class="section-header">
        <h3>{{ hasSearch ? '搜索结果' : '全部视频' }}</h3>
        <span>{{ total }} 条</span>
      </div>

      <div v-loading="loading" class="video-grid">
        <el-empty v-if="list.length === 0 && !loading" class="empty-state" description="暂无视频">
          <el-button type="primary" @click="$router.push('/knowledge/videos/create')">发布第一个视频</el-button>
        </el-empty>
        <video-card
          v-for="item in list"
          :key="item.id"
          :item="item"
          show-delete
          @open="openVideo"
          @delete="handleDelete"
        />
      </div>

      <el-pagination
        v-if="total > pageSize"
        class="pagination"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="pageNum"
        @current-change="load"
      />
    </section>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElButton, ElIcon, ElMessage, ElMessageBox } from 'element-plus'
import { Delete, VideoCamera, VideoPlay, View } from '@element-plus/icons-vue'
import { deleteVideo, getRecommendedVideos, getVideos } from '../../../api/knowledge'

const cropOptions = ['水稻', '小麦', '玉米', '大豆', '番茄', '黄瓜', '果树']
const router = useRouter()
const list = ref([])
const recommendList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const loading = ref(false)
const keyword = ref('')
const cropType = ref('')
const hasSearch = computed(() => Boolean(keyword.value || cropType.value))

const VideoCard = defineComponent({
  name: 'VideoCard',
  props: {
    item: { type: Object, required: true },
    showDelete: { type: Boolean, default: false }
  },
  emits: ['open', 'delete'],
  setup(props, { emit }) {
    return () => h('article', {
      class: 'video-card',
      tabindex: 0,
      role: 'button',
      onClick: () => emit('open', props.item),
      onKeydown: (event) => {
        if (event.key === 'Enter') emit('open', props.item)
      }
    }, [
      h('div', { class: 'video-cover' }, [
        props.item.coverUrl
          ? h('img', { src: props.item.coverUrl, alt: props.item.title })
          : h('div', { class: 'cover-placeholder' }, [h(ElIcon, { size: 40 }, () => h(VideoCamera))]),
        h('div', { class: 'play-icon' }, [h(ElIcon, { size: 28 }, () => h(VideoPlay))]),
        props.showDelete
          ? h(ElButton, {
              class: 'delete-btn',
              type: 'danger',
              size: 'small',
              circle: true,
              'aria-label': `删除${props.item.title}`,
              onClick: (event) => {
                event.stopPropagation()
                emit('delete', props.item)
              }
            }, () => h(ElIcon, null, () => h(Delete)))
          : null
      ]),
      h('div', { class: 'video-info' }, [
        h('h4', null, props.item.title),
        h('div', { class: 'video-meta' }, [
          props.item.cropType ? h('span', { class: 'crop-tag' }, props.item.cropType) : null,
          h('span', { class: 'view-count' }, [h(ElIcon, null, () => h(View)), ` ${props.item.viewCount || 0}`])
        ])
      ])
    ])
  }
})

async function load() {
  loading.value = true
  try {
    const response = await getVideos({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      cropType: cropType.value || undefined
    })
    list.value = response.data.records || []
    total.value = response.data.total || 0
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  keyword.value = ''
  cropType.value = ''
  pageNum.value = 1
  load()
}

function openVideo(item) {
  router.push('/knowledge/videos/' + item.id)
}

async function handleDelete(item) {
  try {
    await ElMessageBox.confirm(`确定要删除《${item.title}》吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }

  try {
    await deleteVideo(item.id)
    ElMessage.success('已删除')
    load()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(async () => {
  await load()
  try {
    const response = await getRecommendedVideos()
    recommendList.value = response.data || []
  } catch {
    recommendList.value = []
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
}

.search-panel {
  margin-bottom: 18px;
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 280px;
}

.crop-select {
  width: 160px;
}

.section {
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.section-header span {
  color: #909399;
  font-size: 13px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  min-height: 120px;
}

.video-card {
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-bg-white);
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, transform 0.2s;
}

.video-card:hover,
.video-card:focus {
  border-color: var(--color-primary-light);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  outline: none;
  transform: translateY(-1px);
}

.video-cover {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 8px;
  background: #f0f2f5;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.play-icon {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  color: #fff;
  background: rgba(0, 0, 0, 0.52);
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
}

.video-info h4 {
  margin: 10px 0 8px;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-meta,
.view-count {
  display: flex;
  gap: 10px;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.view-count {
  gap: 4px;
}

.crop-tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 1px 8px;
  border-radius: 4px;
}

.empty-state {
  grid-column: 1 / -1;
}

.pagination {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-input,
  .crop-select {
    width: 100%;
  }

  .search-bar .el-button {
    flex: 1;
  }
}
</style>

<template>
  <div class="article-detail-page">
    <div class="page-toolbar">
      <el-button @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <el-skeleton v-if="loading" :rows="10" animated />

    <el-empty v-else-if="!article" description="文章不存在或已下架" />

    <div v-else class="article-layout">
      <main class="article-main">
        <article class="article-card">
          <div class="article-kicker">
            <el-tag v-if="article.trustedLevel" :type="trustedTagType" effect="plain">
              {{ trustedLabel }}
            </el-tag>
            <span v-if="article.source">来源：{{ article.source }}</span>
            <span v-if="article.publishedAt || article.createTime">{{ formatDate(article.publishedAt || article.createTime) }}</span>
          </div>

          <h1>{{ article.title }}</h1>

          <p v-if="article.summary" class="article-summary">
            {{ article.summary }}
          </p>

          <div class="article-meta" aria-label="文章数据">
            <span><el-icon><View /></el-icon>{{ article.viewCount || 0 }} 浏览</span>
            <span><el-icon><CaretTop /></el-icon>{{ interaction.likeCount }} 点赞</span>
            <span><el-icon><Star /></el-icon>{{ interaction.favoriteCount }} 收藏</span>
            <span><el-icon><ChatDotRound /></el-icon>{{ interaction.commentCount }} 评论</span>
          </div>

          <el-divider />

          <div class="article-content">
            <template v-for="(block, index) in contentBlocks" :key="index">
              <h2 v-if="block.type === 'heading'">{{ block.text }}</h2>
              <ul v-else-if="block.type === 'list'">
                <li v-for="(item, itemIndex) in block.items" :key="itemIndex">{{ item }}</li>
              </ul>
              <p v-else>{{ block.text }}</p>
            </template>
          </div>

          <div v-if="article.safetyTip" class="safety-tip">
            <el-icon><Warning /></el-icon>
            <div>
              <strong>安全提示</strong>
              <p>{{ article.safetyTip }}</p>
            </div>
          </div>
        </article>

        <section class="comments-card" aria-labelledby="comments-title">
          <div class="section-title-row">
            <h2 id="comments-title">评论</h2>
            <span>{{ comments.length }} 条</span>
          </div>

          <div class="comment-editor">
            <el-input
              v-model="commentText"
              type="textarea"
              :rows="4"
              maxlength="1000"
              show-word-limit
              placeholder="写下你的看法、经验或补充问题"
            />
            <div class="comment-actions">
              <span v-if="!interactionReady">互动接口未启用，请重启后端服务</span>
              <span v-else-if="!isLoggedIn">登录后可发表评论</span>
              <el-button type="primary" :loading="commentSubmitting" :disabled="!interactionReady || !commentText.trim()" @click="submitComment">
                发布评论
              </el-button>
            </div>
          </div>

          <el-empty v-if="!commentsLoading && comments.length === 0" description="还没有评论" />
          <div v-else v-loading="commentsLoading" class="comment-list">
            <div v-for="item in comments" :key="item.id" class="comment-item">
              <el-avatar :size="36" :src="item.avatar">
                {{ (item.nickname || '用户').slice(0, 1) }}
              </el-avatar>
              <div class="comment-body">
                <div class="comment-head">
                  <strong>{{ item.nickname }}</strong>
                  <span>{{ formatDate(item.createTime) }}</span>
                </div>
                <p>{{ item.content }}</p>
              </div>
            </div>
          </div>
        </section>
      </main>

      <aside class="article-side">
        <div class="action-panel">
          <el-button :type="interaction.liked ? 'primary' : 'default'" :disabled="!interactionReady" @click="toggleLike">
            <el-icon><CaretTop /></el-icon>
            {{ interaction.liked ? '已点赞' : '点赞' }}
          </el-button>
          <el-button :type="interaction.favorited ? 'warning' : 'default'" :disabled="!interactionReady" @click="toggleFavorite">
            <el-icon><Star /></el-icon>
            {{ interaction.favorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button @click="scrollToComments">
            <el-icon><ChatDotRound /></el-icon>
            评论
          </el-button>
        </div>

        <div class="info-panel">
          <h2>文章信息</h2>
          <dl>
            <template v-if="article.cropType">
              <dt>作物类型</dt>
              <dd>{{ article.cropType }}</dd>
            </template>
            <template v-if="article.region">
              <dt>适用地区</dt>
              <dd>{{ article.region }}</dd>
            </template>
            <template v-if="article.season">
              <dt>适用季节</dt>
              <dd>{{ article.season }}</dd>
            </template>
            <template v-if="article.difficulty">
              <dt>难度</dt>
              <dd>{{ article.difficulty }}</dd>
            </template>
            <template v-if="article.verifiedBy">
              <dt>审核来源</dt>
              <dd>{{ article.verifiedBy }}</dd>
            </template>
          </dl>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  cancelFavoriteArticle,
  createArticleComment,
  favoriteArticle,
  getArticle,
  getArticleComments,
  getArticleInteraction,
  likeArticle,
  unlikeArticle
} from '../../../api/knowledge'

const route = useRoute()
const router = useRouter()
const articleId = route.params.id

const article = ref(null)
const comments = ref([])
const commentText = ref('')
const loading = ref(false)
const commentsLoading = ref(false)
const commentSubmitting = ref(false)
const interactionReady = ref(true)
const interaction = reactive({
  liked: false,
  favorited: false,
  likeCount: 0,
  favoriteCount: 0,
  commentCount: 0
})

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const trustedLabel = computed(() => {
  const map = { official: '官方', expert: '专家', normal: '普通' }
  return map[article.value?.trustedLevel] || article.value?.trustedLevel
})

const trustedTagType = computed(() => {
  const map = { official: 'danger', expert: 'warning', normal: 'info' }
  return map[article.value?.trustedLevel] || 'info'
})

const contentBlocks = computed(() => formatArticleContent(article.value?.content || ''))

onMounted(async () => {
  await loadArticle()
  await Promise.allSettled([loadInteraction(), loadComments()])
})

async function loadArticle() {
  loading.value = true
  try {
    const res = await getArticle(articleId)
    article.value = res.data
    interaction.likeCount = res.data?.likeCount || 0
    interaction.favoriteCount = res.data?.favoriteCount || 0
    interaction.commentCount = res.data?.commentCount || 0
  } finally {
    loading.value = false
  }
}

async function loadInteraction() {
  try {
    const res = await getArticleInteraction(articleId)
    interactionReady.value = true
    Object.assign(interaction, {
      liked: !!res.data?.liked,
      favorited: !!res.data?.favorited,
      likeCount: res.data?.likeCount || interaction.likeCount,
      favoriteCount: res.data?.favoriteCount || interaction.favoriteCount,
      commentCount: res.data?.commentCount || interaction.commentCount
    })
  } catch (e) {
    interactionReady.value = false
    // Old backend processes may not have article interaction endpoints until restarted.
  }
}

async function loadComments() {
  commentsLoading.value = true
  try {
    const res = await getArticleComments(articleId)
    comments.value = res.data || []
  } catch (e) {
    comments.value = []
  } finally {
    commentsLoading.value = false
  }
}

async function toggleLike() {
  if (!ensureLogin()) return
  if (interaction.liked) {
    await unlikeArticle(articleId)
    interaction.liked = false
    interaction.likeCount = Math.max(interaction.likeCount - 1, 0)
  } else {
    await likeArticle(articleId)
    interaction.liked = true
    interaction.likeCount += 1
  }
}

async function toggleFavorite() {
  if (!ensureLogin()) return
  if (interaction.favorited) {
    await cancelFavoriteArticle(articleId)
    interaction.favorited = false
    interaction.favoriteCount = Math.max(interaction.favoriteCount - 1, 0)
    ElMessage.success('已取消收藏')
  } else {
    await favoriteArticle(articleId)
    interaction.favorited = true
    interaction.favoriteCount += 1
    ElMessage.success('收藏成功')
  }
}

async function submitComment() {
  if (!ensureLogin()) return
  const content = commentText.value.trim()
  if (!content) return
  commentSubmitting.value = true
  try {
    await createArticleComment(articleId, { content })
    commentText.value = ''
    interaction.commentCount += 1
    await loadComments()
    ElMessage.success('评论已发布')
  } finally {
    commentSubmitting.value = false
  }
}

function ensureLogin() {
  if (isLoggedIn.value) return true
  ElMessage.warning('请先登录')
  return false
}

function scrollToComments() {
  document.getElementById('comments-title')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function formatArticleContent(content) {
  const lines = content
    .replace(/\r\n/g, '\n')
    .split('\n')
    .map(line => line.trim())
    .filter(Boolean)

  const blocks = []
  let listItems = []

  const flushList = () => {
    if (listItems.length) {
      blocks.push({ type: 'list', items: listItems })
      listItems = []
    }
  }

  for (const line of lines) {
    const listMatch = line.match(/^([0-9]+[.、]|[-*•])\s*(.+)$/)
    if (listMatch) {
      listItems.push(listMatch[2])
      continue
    }
    flushList()
    if (isHeadingLine(line)) {
      blocks.push({ type: 'heading', text: line.replace(/^#+\s*/, '').replace(/[：:]$/, '') })
    } else {
      blocks.push({ type: 'paragraph', text: line })
    }
  }
  flushList()
  return blocks.length ? blocks : [{ type: 'paragraph', text: '暂无正文内容' }]
}

function isHeadingLine(line) {
  if (/^#{1,3}\s+/.test(line)) return true
  if (line.length > 28) return false
  return /^(一、|二、|三、|四、|五、|六、|七、|八、|九、|十、|[0-9]+[.、])/.test(line) || /[：:]$/.test(line)
}

function formatDate(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.article-detail-page {
  padding: 20px;
}

.page-toolbar {
  margin-bottom: 16px;
}

.article-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 260px;
  gap: 20px;
  align-items: start;
}

.article-main {
  display: grid;
  gap: 16px;
  min-width: 0;
}

.article-card,
.comments-card,
.action-panel,
.info-panel {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.article-card,
.comments-card {
  padding: 28px;
}

.article-kicker {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  color: #7a8599;
  font-size: 13px;
}

.article-card h1 {
  margin: 14px 0 12px;
  color: #1f2d3d;
  font-size: 30px;
  line-height: 1.35;
  font-weight: 700;
}

.article-summary {
  margin: 0 0 18px;
  padding: 14px 16px;
  color: #4e5969;
  line-height: 1.8;
  background: #f7f9f8;
  border-left: 4px solid #67c23a;
  border-radius: 6px;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  color: #7a8599;
  font-size: 13px;
}

.article-meta span,
.action-panel .el-button {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.article-content {
  color: #2c3e50;
  font-size: 16px;
  line-height: 1.95;
}

.article-content h2 {
  margin: 28px 0 10px;
  color: #1f2d3d;
  font-size: 20px;
  line-height: 1.45;
}

.article-content p {
  margin: 0 0 16px;
}

.article-content ul {
  margin: 0 0 18px;
  padding-left: 22px;
}

.article-content li {
  margin-bottom: 8px;
}

.safety-tip {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding: 14px 16px;
  color: #8a5a00;
  background: #fff7e8;
  border: 1px solid #f3d19e;
  border-radius: 8px;
}

.safety-tip p {
  margin: 6px 0 0;
  line-height: 1.7;
}

.article-side {
  position: sticky;
  top: 84px;
  display: grid;
  gap: 14px;
}

.action-panel,
.info-panel {
  padding: 16px;
}

.action-panel {
  display: grid;
  gap: 10px;
}

.action-panel .el-button {
  width: 100%;
  justify-content: center;
  margin-left: 0;
}

.info-panel h2,
.section-title-row h2 {
  margin: 0;
  color: #1f2d3d;
  font-size: 18px;
}

.info-panel dl {
  margin: 14px 0 0;
}

.info-panel dt {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}

.info-panel dd {
  margin: 4px 0 0;
  color: #303133;
  line-height: 1.6;
}

.section-title-row,
.comment-actions,
.comment-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-title-row span,
.comment-actions span,
.comment-head span {
  color: #909399;
  font-size: 13px;
}

.comment-editor {
  margin-top: 16px;
}

.comment-actions {
  margin-top: 10px;
}

.comment-list {
  display: grid;
  gap: 16px;
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-head strong {
  color: #303133;
  font-size: 14px;
}

.comment-body p {
  margin: 8px 0 0;
  color: #4e5969;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 900px) {
  .article-layout {
    grid-template-columns: 1fr;
  }

  .article-side {
    position: static;
    order: -1;
  }

  .action-panel {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .article-detail-page {
    padding: 12px;
  }

  .article-card,
  .comments-card {
    padding: 18px;
  }

  .article-card h1 {
    font-size: 24px;
  }

  .action-panel {
    grid-template-columns: 1fr;
  }

  .section-title-row,
  .comment-actions,
  .comment-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>

<template>
  <div class="page rag-page">
    <div class="page-header">
      <div>
        <h2>知识检索 RAG</h2>
        <p>从已发布农技文章中检索相关片段，生成可追溯的参考答案</p>
      </div>
      <el-button :loading="indexing" @click="handleIndexAll">
        <el-icon><Refresh /></el-icon>
        重建索引
      </el-button>
    </div>

    <el-card class="query-panel" shadow="never">
      <div class="query-row">
        <el-input
          v-model="query"
          class="query-input"
          placeholder="输入问题，例如：番茄晚疫病怎么防治？水稻分蘖期如何施肥？"
          clearable
          @keyup.enter="handleAsk"
        />
        <el-select v-model="topK" class="topk-select" aria-label="召回片段数量">
          <el-option :value="5" label="召回 5 条" />
          <el-option :value="8" label="召回 8 条" />
          <el-option :value="12" label="召回 12 条" />
        </el-select>
        <el-button type="primary" :loading="loading" @click="handleAsk">
          <el-icon><Search /></el-icon>
          提问
        </el-button>
      </div>
      <div class="quick-questions">
        <el-button v-for="item in examples" :key="item" plain size="small" @click="useExample(item)">
          {{ item }}
        </el-button>
      </div>
    </el-card>

    <el-empty v-if="!searched && !loading" description="输入问题后开始检索知识库" />

    <el-card v-if="answerData" class="answer-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-title">
          <el-icon><ChatLineRound /></el-icon>
          RAG 回答
          <span v-if="answerData.spentMs" class="spent-time">{{ answerData.spentMs }}ms</span>
        </div>
      </template>
      <div class="answer-text">{{ answerData.answer }}</div>
    </el-card>

    <el-row v-if="searched" :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><Link /></el-icon>
              引用来源
            </div>
          </template>
          <el-empty v-if="sources.length === 0" description="没有可引用的来源" />
          <div v-else class="source-list">
            <div v-for="(item, index) in sources" :key="item.chunkId || index" class="source-item">
              <div class="source-index">{{ index + 1 }}</div>
              <div class="source-main">
                <router-link v-if="item.articleId" :to="'/knowledge/articles/' + item.articleId" class="source-title">
                  {{ item.title || '未命名文档' }}
                </router-link>
                <div v-else class="source-title">{{ item.title || '未命名文档' }}</div>
                <div class="source-meta">
                  <span>{{ item.source || '平台知识库' }}</span>
                  <span>片段 {{ item.chunkIndex }}</span>
                  <span>得分 {{ item.score }}</span>
                </div>
                <p>{{ item.content }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><Document /></el-icon>
              检索结果
            </div>
          </template>
          <el-empty v-if="results.length === 0" description="未找到匹配文档，请先重建索引或换用更具体关键词" />
          <div v-else class="result-list">
            <div v-for="item in results" :key="item.documentId" class="result-item">
              <div class="result-header">
                <div>
                  <router-link v-if="item.articleId" :to="'/knowledge/articles/' + item.articleId" class="result-title">
                    {{ item.title || '未命名文档' }}
                  </router-link>
                  <div v-else class="result-title">{{ item.title || '未命名文档' }}</div>
                  <div class="source-meta">
                    <span>{{ item.source || '平台知识库' }}</span>
                    <span>综合得分 {{ formatScore(item.score) }}</span>
                  </div>
                </div>
              </div>
              <div v-if="item.hitKeywords && item.hitKeywords.length" class="keyword-row">
                <el-tag v-for="kw in item.hitKeywords" :key="kw" size="small" effect="plain">{{ kw }}</el-tag>
              </div>
              <div class="chunk-list">
                <div v-for="chunk in item.chunks" :key="chunk.id || chunk.index" class="chunk-item">
                  {{ chunk.content }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { askKnowledge, indexAllArticles } from '../../api/rag'

const examples = ['番茄晚疫病怎么防治？', '水稻分蘖期如何施肥？', '黄瓜霜霉病有哪些症状？']
const query = ref('')
const topK = ref(8)
const loading = ref(false)
const indexing = ref(false)
const searched = ref(false)
const answerData = ref(null)

const sources = computed(() => answerData.value?.sources || [])
const results = computed(() => answerData.value?.results || [])

async function handleAsk() {
  const text = query.value.trim()
  if (!text) {
    ElMessage.warning('请输入检索问题')
    return
  }

  loading.value = true
  try {
    const response = await askKnowledge(text, topK.value)
    answerData.value = response.data
    searched.value = true
  } catch {
    ElMessage.error('RAG 检索失败')
  } finally {
    loading.value = false
  }
}

async function handleIndexAll() {
  indexing.value = true
  try {
    await indexAllArticles()
    ElMessage.success('索引重建完成')
  } catch {
    ElMessage.error('索引重建失败')
  } finally {
    indexing.value = false
  }
}

function useExample(text) {
  query.value = text
  handleAsk()
}

function formatScore(score) {
  const value = Number(score)
  return Number.isFinite(value) ? value.toFixed(2) : '--'
}
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
}

.query-panel {
  margin-bottom: 18px;
}

.query-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.query-input {
  flex: 1;
}

.topk-select {
  width: 132px;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.answer-card {
  margin-bottom: 16px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.spent-time {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}

.answer-text {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #303133;
}

.source-list,
.result-list {
  display: grid;
  gap: 12px;
}

.source-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-bg-white);
}

.source-index {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 12px;
}

.source-main {
  min-width: 0;
}

.source-title,
.result-title {
  color: var(--color-primary);
  font-weight: 600;
}

.source-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 6px 0;
  color: #909399;
  font-size: 12px;
}

.source-item p {
  margin: 0;
  color: #606266;
  line-height: 1.7;
}

.result-item {
  padding: 14px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.keyword-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 8px 0;
}

.chunk-list {
  display: grid;
  gap: 8px;
}

.chunk-item {
  padding: 10px;
  border-radius: 6px;
  background: #f5f7fa;
  color: #606266;
  line-height: 1.7;
  font-size: 13px;
}

@media (max-width: 768px) {
  .page-header,
  .query-row {
    align-items: stretch;
    flex-direction: column;
  }

  .topk-select {
    width: 100%;
  }
}
</style>

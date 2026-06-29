<template>
  <el-card shadow="hover" class="market-card">
    <template #header>
      <div class="card-header">
        <span class="card-title">草莓市场行情</span>
        <span class="card-subtitle" v-if="summary">更新于 {{ summary.updateTime }}</span>
      </div>
    </template>

    <!-- 顶部统计 -->
    <div class="top-stats">
      <div class="stat-item primary">
        <span class="stat-label">今日均价</span>
        <span class="stat-value">{{ summary?.avgPrice }}<small> {{ summary?.unit }}</small></span>
      </div>
      <div class="stat-item">
        <span class="stat-label">最高价</span>
        <span class="stat-value small">{{ summary?.maxPrice }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">最低价</span>
        <span class="stat-value small">{{ summary?.minPrice }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">较昨日</span>
        <span class="stat-value small" :class="changeClass">
          {{ changeText }}
        </span>
      </div>
    </div>

    <!-- 价格状态 + 销售建议 -->
    <div class="status-row">
      <el-tag :type="statusTagType" size="small" effect="dark">{{ statusLabel }}</el-tag>
      <span class="suggestion-text">{{ summary?.suggestion }}</span>
    </div>

    <!-- 近7天趋势图 -->
    <div class="chart-box">
      <div ref="trendChartRef" class="chart"></div>
    </div>

    <!-- 市场对比图 -->
    <div class="chart-box" v-if="compareData.length">
      <div ref="compareChartRef" class="chart chart-sm"></div>
    </div>

    <!-- 价格预警 -->
    <div v-if="alerts.length" class="alert-section">
      <el-alert
        v-for="(a, i) in alerts"
        :key="i"
        :type="a.alertLevel === 'IMPORTANT' ? 'warning' : 'info'"
        :title="a.alertContent"
        :closable="false"
        show-icon
        class="alert-item"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  getMarketPriceSummary, getMarketPriceTrend, getMarketPriceCompare, getMarketPriceAlerts,
  fallbackSummary, fallbackTrend, fallbackCompare, fallbackAlerts
} from '../../../api/market/price'

const summary = ref(null)
const trendData = ref([])
const compareData = ref([])
const alerts = ref([])

const trendChartRef = ref(null)
const compareChartRef = ref(null)
let trendChart = null
let compareChart = null

// 涨跌显示
const changeClass = computed(() => {
  const r = summary.value?.changeRate || 0
  if (r > 0) return 'up'
  if (r < 0) return 'down'
  return ''
})
const changeText = computed(() => {
  const r = summary.value?.changeRate
  if (r == null) return '-'
  return (r > 0 ? '+' : '') + r + '%'
})

// 状态标签
const statusTagType = computed(() => {
  const s = summary.value?.priceStatus
  if (s === 'HIGH') return 'warning'
  if (s === 'LOW') return 'danger'
  return 'success'
})
const statusLabel = computed(() => {
  const s = summary.value?.priceStatus
  if (s === 'HIGH') return '价格偏高'
  if (s === 'LOW') return '价格偏低'
  return '价格平稳'
})

async function loadData() {
  // summary
  try {
    const r = await getMarketPriceSummary()
    summary.value = r.data
  } catch {
    summary.value = { ...fallbackSummary }
  }
  // trend
  try {
    const r = await getMarketPriceTrend(7)
    trendData.value = r.data || []
  } catch {
    trendData.value = [...fallbackTrend]
  }
  // compare
  try {
    const r = await getMarketPriceCompare()
    compareData.value = r.data || []
  } catch {
    compareData.value = [...fallbackCompare]
  }
  // alerts
  try {
    const r = await getMarketPriceAlerts()
    alerts.value = r.data || []
  } catch {
    alerts.value = [...fallbackAlerts]
  }
  await nextTick()
  renderTrendChart()
  renderCompareChart()
}

function renderTrendChart() {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendChartRef.value)

  const dates = trendData.value.map(d => d.date)
  const values = trendData.value.map(d => d.avgPrice)

  trendChart.setOption({
    grid: { top: 20, right: 16, bottom: 28, left: 44 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 10, color: '#888' } },
    yAxis: { type: 'value', name: '元/公斤', nameTextStyle: { fontSize: 10, color: '#888' }, axisLabel: { fontSize: 10, color: '#888' } },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      lineStyle: { color: '#43a047', width: 2 },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(67,160,71,0.25)' },
        { offset: 1, color: 'rgba(67,160,71,0.02)' }
      ])},
      itemStyle: { color: '#2e7d32' },
      symbol: 'circle',
      symbolSize: 6
    }]
  })
}

function renderCompareChart() {
  if (!compareChartRef.value) return
  if (compareChart) compareChart.dispose()
  compareChart = echarts.init(compareChartRef.value)

  compareChart.setOption({
    grid: { top: 10, right: 16, bottom: 28, left: 44 },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: {
      type: 'category',
      data: compareData.value.map(d => d.marketName),
      axisLabel: { fontSize: 9, color: '#888', rotate: 15 }
    },
    yAxis: { type: 'value', name: '元/公斤', nameTextStyle: { fontSize: 10, color: '#888' }, axisLabel: { fontSize: 10, color: '#888' } },
    series: [{
      type: 'bar',
      data: compareData.value.map(d => d.avgPrice),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#66bb6a' },
          { offset: 1, color: '#2e7d32' }
        ])
      },
      barMaxWidth: 36
    }]
  })
}

// 定时刷新 (5分钟)
let refreshTimer = null
onMounted(() => {
  loadData()
  refreshTimer = setInterval(loadData, 5 * 60 * 1000)
})
onUnmounted(() => {
  if (trendChart) trendChart.dispose()
  if (compareChart) compareChart.dispose()
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>

<style scoped>
.market-card {
  border: 1px solid #c8e6c9;
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-weight: 700;
  color: #2d8c2d;
  font-size: 15px;
}
.card-subtitle {
  font-size: 11px;
  color: #999;
}
.top-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}
.stat-item {
  flex: 1;
  padding: 10px 8px;
  border-radius: 8px;
  background: #f6fdf6;
  text-align: center;
}
.stat-item.primary {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
}
.stat-label {
  display: block;
  font-size: 11px;
  color: #888;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2e7d32;
}
.stat-value.small {
  font-size: 16px;
  color: #333;
}
.stat-value small {
  font-size: 13px;
  font-weight: 400;
  color: #666;
}
.stat-value.up { color: #ef5350; }
.stat-value.down { color: #43a047; }

.status-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.suggestion-text {
  flex: 1;
  font-size: 12px;
  color: #555;
  line-height: 1.5;
}

.chart-box {
  margin: 6px 0;
}
.chart {
  width: 100%;
  height: 200px;
}
.chart-sm {
  height: 170px;
}

.alert-section {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.alert-item {
  border-radius: 6px;
}
</style>

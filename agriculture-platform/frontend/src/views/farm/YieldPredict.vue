<template>
  <div class="yield-page">
    <header class="page-header">
      <div>
        <h2>草莓产量预测</h2>
        <p>基于全国省级草莓历史产量的近期趋势预测</p>
      </div>
      <el-tag type="success" effect="plain">数据截至 {{ lastActualYear }} 年</el-tag>
    </header>

    <section class="control-band">
      <div class="field">
        <span>预测地区</span>
        <el-select v-model="selectedRegion" filterable @change="refresh">
          <el-option v-for="region in regionOptions" :key="region" :label="region" :value="region" />
        </el-select>
      </div>
      <div class="field">
        <span>预测年份</span>
        <el-segmented v-model="selectedYear" :options="forecastYears" @change="refresh" />
      </div>
      <div class="source-note">
        <span>数据来源</span>
        <strong>{{ dataset.source }}</strong>
      </div>
    </section>

    <el-row :gutter="14" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="stat-card">
          <span>预测总产量</span>
          <strong class="primary">{{ formatNumber(summary.predicted) }}</strong>
          <small>万吨</small>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="stat-card">
          <span>较上年变化</span>
          <strong :class="summary.changeRate >= 0 ? 'positive' : 'negative'">
            {{ summary.changeRate >= 0 ? '+' : '' }}{{ summary.changeRate.toFixed(1) }}%
          </strong>
          <small>{{ selectedYear - 1 }} → {{ selectedYear }}</small>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="stat-card">
          <span>近五年平均</span>
          <strong>{{ formatNumber(summary.average) }}</strong>
          <small>万吨</small>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="never" class="stat-card">
          <span>趋势拟合度</span>
          <strong class="warning">{{ summary.rSquared.toFixed(1) }}%</strong>
          <small>最近五年线性趋势</small>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="chart-panel">
      <template #header>
        <div class="panel-header">
          <span>{{ selectedRegion }}草莓产量趋势</span>
          <div class="legend">
            <span><i class="actual-dot"></i>历史实际</span>
            <span><i class="forecast-dot"></i>趋势预测</span>
          </div>
        </div>
      </template>
      <div ref="yieldChart" class="yield-chart"></div>
    </el-card>

    <el-row :gutter="14" class="detail-row">
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="ranking-panel">
          <template #header>
            <div class="panel-header">
              <span>{{ selectedYear }} 年省级草莓产量预测</span>
              <el-tag size="small" type="info">前 10 名</el-tag>
            </div>
          </template>
          <el-table :data="ranking" size="small" stripe height="350">
            <el-table-column type="index" label="排名" width="64" align="center" />
            <el-table-column prop="name" label="地区" min-width="140" />
            <el-table-column label="预测产量（万吨）" min-width="160" align="right">
              <template #default="{ row }">{{ formatNumber(row.predicted) }}</template>
            </el-table-column>
            <el-table-column label="较上年" min-width="110" align="right">
              <template #default="{ row }">
                <span :class="row.changeRate >= 0 ? 'positive-text' : 'negative-text'">
                  {{ row.changeRate >= 0 ? '+' : '' }}{{ row.changeRate.toFixed(1) }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="insight-panel">
          <template #header><span>预测判断</span></template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="历史区间">{{ firstActualYear }}–{{ lastActualYear }} 年</el-descriptions-item>
            <el-descriptions-item label="建模窗口">最近 5 个有效年份</el-descriptions-item>
            <el-descriptions-item label="年均趋势">
              {{ summary.slope >= 0 ? '+' : '' }}{{ summary.slope.toFixed(2) }} 万吨/年
            </el-descriptions-item>
          </el-descriptions>
          <el-timeline class="insight-list">
            <el-timeline-item v-for="item in insights" :key="item" color="#2f7d32">
              {{ item }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import dataset from '../../data/strawberryYield.json'

const forecastYears = [2023, 2024, 2025, 2026]
const firstActualYear = dataset.years[0]
const lastActualYear = dataset.years.at(-1)
const regionOptions = ['全国', ...dataset.regions.map(item => item.name)]
const selectedRegion = ref('河南省')
const selectedYear = ref(2026)
const yieldChart = ref(null)
let chart
let resizeHandler

function valuesForRegion(regionName) {
  if (regionName !== '全国') {
    return dataset.regions.find(item => item.name === regionName)?.values.map(value => value ?? 0) || []
  }
  return dataset.years.map((_, index) => dataset.regions.reduce((sum, region) => sum + (region.values[index] ?? 0), 0))
}

function linearModel(values) {
  const points = dataset.years
    .map((year, index) => ({ year, value: values[index] }))
    .filter(point => Number.isFinite(point.value))
    .slice(-5)
  const count = points.length
  const meanYear = points.reduce((sum, point) => sum + point.year, 0) / count
  const meanValue = points.reduce((sum, point) => sum + point.value, 0) / count
  const denominator = points.reduce((sum, point) => sum + (point.year - meanYear) ** 2, 0)
  const slope = denominator === 0 ? 0 : points.reduce((sum, point) => sum + (point.year - meanYear) * (point.value - meanValue), 0) / denominator
  const intercept = meanValue - slope * meanYear
  const totalVariance = points.reduce((sum, point) => sum + (point.value - meanValue) ** 2, 0)
  const residualVariance = points.reduce((sum, point) => sum + (point.value - (slope * point.year + intercept)) ** 2, 0)
  const rSquared = totalVariance === 0 ? 1 : Math.max(0, 1 - residualVariance / totalVariance)
  return {
    slope,
    rSquared,
    predict: year => Math.max(0, slope * year + intercept)
  }
}

function predictionFor(regionName, year) {
  const values = valuesForRegion(regionName)
  if (year <= lastActualYear) return values[dataset.years.indexOf(year)] || 0
  return linearModel(values).predict(year)
}

const currentValues = computed(() => valuesForRegion(selectedRegion.value))
const currentModel = computed(() => linearModel(currentValues.value))
const summary = computed(() => {
  const predicted = predictionFor(selectedRegion.value, selectedYear.value)
  const previous = predictionFor(selectedRegion.value, selectedYear.value - 1)
  const recent = currentValues.value.slice(-5)
  return {
    predicted,
    changeRate: previous ? (predicted - previous) / previous * 100 : 0,
    average: recent.reduce((sum, value) => sum + value, 0) / recent.length,
    rSquared: currentModel.value.rSquared * 100,
    slope: currentModel.value.slope
  }
})

const ranking = computed(() => dataset.regions
  .map(region => {
    const predicted = predictionFor(region.name, selectedYear.value)
    const previous = predictionFor(region.name, selectedYear.value - 1)
    return {
      name: region.name,
      predicted,
      changeRate: previous ? (predicted - previous) / previous * 100 : 0
    }
  })
  .sort((a, b) => b.predicted - a.predicted)
  .slice(0, 10))

const insights = computed(() => {
  const result = []
  if (summary.value.slope > 0.5) result.push('近五年产量呈明显增长趋势，应同步规划采后分级、冷链与销售承接能力。')
  else if (summary.value.slope > 0) result.push('近五年产量温和增长，可在保持品质的前提下稳步扩大生产能力。')
  else result.push('近五年产量趋势偏弱，建议结合种植面积、灾害和市场数据复核下降原因。')
  if (summary.value.rSquared < 60) result.push('历史波动较大，趋势拟合度偏低，本结果适合辅助研判，不宜作为单一决策依据。')
  else result.push('近期趋势相对稳定，线性模型能够解释大部分年度变化。')
  if (selectedYear.value - lastActualYear >= 3) result.push('预测跨度超过两年，不确定性会随时间扩大，获得新年度数据后应及时更新。')
  return result
})

function formatNumber(value) {
  return Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function refresh() {
  nextTick(renderChart)
}

function renderChart() {
  if (!yieldChart.value) return
  if (!chart) {
    chart = echarts.init(yieldChart.value)
    resizeHandler = () => chart.resize()
    window.addEventListener('resize', resizeHandler)
  }
  const years = [...dataset.years, ...forecastYears]
  const actual = [...currentValues.value, ...forecastYears.map(() => null)]
  const forecast = years.map(year => year < lastActualYear ? null : predictionFor(selectedRegion.value, year))
  forecast[dataset.years.indexOf(lastActualYear)] = currentValues.value.at(-1)
  chart.setOption({
    animationDuration: 500,
    tooltip: {
      trigger: 'axis',
      valueFormatter: value => value == null ? '-' : `${formatNumber(value)} 万吨`
    },
    grid: { left: 58, right: 24, top: 32, bottom: 44 },
    xAxis: { type: 'category', data: years, boundaryGap: false, axisLabel: { interval: 1 } },
    yAxis: { type: 'value', name: '万吨', nameTextStyle: { color: '#6b7280' }, splitLine: { lineStyle: { color: '#edf1ed' } } },
    series: [
      {
        name: '历史实际',
        type: 'line',
        data: actual,
        symbolSize: 6,
        lineStyle: { width: 3, color: '#2f7d32' },
        itemStyle: { color: '#2f7d32' },
        areaStyle: { color: 'rgba(47,125,50,0.10)' }
      },
      {
        name: '趋势预测',
        type: 'line',
        data: forecast,
        symbol: 'diamond',
        symbolSize: 8,
        lineStyle: { width: 3, type: 'dashed', color: '#d97706' },
        itemStyle: { color: '#d97706' }
      }
    ]
  }, true)
}

onMounted(async () => {
  await nextTick()
  renderChart()
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeHandler)
  chart?.dispose()
})
</script>

<style scoped>
.yield-page {
  min-height: calc(100vh - 92px);
  padding: 4px;
  color: #263329;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  letter-spacing: 0;
}

.page-header p {
  margin: 5px 0 0;
  color: #718075;
  font-size: 13px;
}

.control-band {
  display: flex;
  align-items: end;
  gap: 24px;
  padding: 14px 16px;
  border: 1px solid #dfe8df;
  border-radius: 6px;
  background: #fff;
}

.field,
.source-note {
  display: grid;
  gap: 6px;
}

.field > span,
.source-note > span {
  color: #718075;
  font-size: 12px;
}

.field .el-select {
  width: 190px;
}

.source-note {
  margin-left: auto;
  text-align: right;
}

.source-note strong {
  font-size: 13px;
}

.stats-row {
  margin-top: 14px;
}

.stat-card {
  height: 120px;
  border-color: #dfe8df;
}

.stat-card :deep(.el-card__body) {
  display: grid;
  align-content: center;
  height: 100%;
  box-sizing: border-box;
}

.stat-card span {
  color: #718075;
  font-size: 12px;
}

.stat-card strong {
  margin-top: 6px;
  font-size: 27px;
  line-height: 1.1;
}

.stat-card small {
  margin-top: 5px;
  color: #8b978d;
}

.primary { color: #256d39; }
.positive { color: #198754; }
.negative { color: #c2413b; }
.warning { color: #b86708; }
.positive-text { color: #198754; }
.negative-text { color: #c2413b; }

.chart-panel,
.ranking-panel,
.insight-panel {
  border-color: #dfe8df;
}

.chart-panel {
  margin-top: 14px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 700;
}

.legend {
  display: flex;
  gap: 14px;
  color: #718075;
  font-size: 12px;
  font-weight: 400;
}

.legend span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.legend i {
  width: 18px;
  height: 3px;
}

.actual-dot { background: #2f7d32; }
.forecast-dot { border-top: 3px dashed #d97706; }

.yield-chart {
  width: 100%;
  height: 370px;
}

.detail-row {
  margin-top: 14px;
}

.insight-list {
  margin-top: 22px;
}

@media (max-width: 1199px) {
  .control-band {
    flex-wrap: wrap;
    align-items: start;
  }

  .source-note {
    width: 100%;
    margin-left: 0;
    text-align: left;
  }

  .stat-card,
  .insight-panel {
    margin-bottom: 14px;
  }
}

@media (max-width: 640px) {
  .page-header {
    align-items: start;
    gap: 12px;
  }

  .control-band {
    display: grid;
    gap: 14px;
  }

  .field .el-select {
    width: 100%;
  }

  .yield-chart {
    height: 320px;
  }
}
</style>

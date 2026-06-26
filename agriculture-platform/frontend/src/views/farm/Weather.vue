<template>
  <div class="page weather-page">
    <div class="page-header">
      <h2>天气预测</h2>
      <p>基于高德 Web 服务 API 的实时天气、预报趋势与农事风险提醒</p>
    </div>

    <el-card class="search-panel" shadow="never">
      <div class="search-row">
        <div class="search-main">
          <el-input
            v-model="cityKeyword"
            class="city-input"
            placeholder="输入城市或区县名称，如 北京、杭州、广州"
            clearable
            @keyup.enter="searchWeather"
          />
          <el-button type="primary" :loading="loading" @click="searchWeather">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button :disabled="loading" @click="resetCity">默认城市</el-button>
        </div>
        <div class="city-tabs" aria-label="常用城市">
          <el-button
            v-for="item in cityOptions"
            :key="item.adcode"
            :type="item.name === selectedCity ? 'success' : 'default'"
            plain
            :disabled="loading"
            @click="selectCity(item)"
          >
            {{ item.name }}
          </el-button>
        </div>
      </div>
      <div class="data-status">
        <el-tag :type="apiError ? 'warning' : 'success'" effect="light">{{ dataSourceLabel }}</el-tag>
        <span>{{ statusText }}</span>
      </div>
    </el-card>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :md="10">
        <el-card class="current-card" shadow="never" v-loading="loading">
          <div class="current-header">
            <div>
              <div class="city-name">{{ selectedCity }}</div>
              <div class="weather-desc">{{ currentWeather.condition }} · {{ currentWeather.wind }}</div>
              <div v-if="currentWeather.reportTime" class="report-time">发布时间：{{ currentWeather.reportTime }}</div>
            </div>
            <el-icon class="weather-icon"><component :is="weatherIcon" /></el-icon>
          </div>
          <div class="temperature">{{ currentWeather.temp }}<span>℃</span></div>
          <div class="metrics-grid">
            <div class="metric-item">
              <span>湿度</span>
              <strong>{{ currentWeather.humidity }}%</strong>
            </div>
            <div class="metric-item">
              <span>降水风险</span>
              <strong>{{ currentWeather.rain }}%</strong>
            </div>
            <div class="metric-item">
              <span>风力</span>
              <strong>{{ currentWeather.windPower }}</strong>
            </div>
            <div class="metric-item">
              <span>数据源</span>
              <strong>{{ apiError ? '本地' : '高德' }}</strong>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="14">
        <el-card class="risk-card" shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><Warning /></el-icon>
              农事风险提醒
            </div>
          </template>
          <div class="risk-list">
            <div v-for="item in riskList" :key="item.title" class="risk-item" :class="item.level">
              <div>
                <strong>{{ item.title }}</strong>
                <p>{{ item.content }}</p>
              </div>
              <el-tag :type="item.level === 'high' ? 'danger' : item.level === 'medium' ? 'warning' : 'success'">
                {{ item.label }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <template #header>
        <div class="card-title">
          <el-icon><TrendCharts /></el-icon>
          未来天气趋势
        </div>
      </template>
      <div ref="tempChart" class="chart-container"></div>
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><Calendar /></el-icon>
              高德天气预报
            </div>
          </template>
          <el-table :data="forecastList" border class="forecast-table">
            <el-table-column prop="date" label="日期" min-width="100" />
            <el-table-column prop="weekText" label="星期" min-width="80" />
            <el-table-column prop="condition" label="天气" min-width="110" />
            <el-table-column label="温度" min-width="120">
              <template #default="{ row }">{{ row.low }}℃ - {{ row.high }}℃</template>
            </el-table-column>
            <el-table-column label="降水风险" min-width="100">
              <template #default="{ row }">{{ row.rain }}%</template>
            </el-table-column>
            <el-table-column prop="wind" label="风向风力" min-width="120" />
            <el-table-column prop="suggestion" label="农事建议" min-width="220" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><DataAnalysis /></el-icon>
              土壤墒情预测
            </div>
          </template>
          <div ref="soilChart" class="chart-container soil-chart"></div>
          <div class="soil-summary">
            <el-tag :type="soilStatus.type">{{ soilStatus.label }}</el-tag>
            <span>{{ soilStatus.text }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <template #header>
        <div class="card-title">
          <el-icon><Sunny /></el-icon>
          农事建议
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="item in adviceList"
          :key="item.date + item.content"
          :timestamp="item.date"
          placement="top"
          :color="item.type === 'warn' ? '#ff9800' : '#4caf50'"
        >
          <p>{{ item.content }}</p>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const AMAP_KEY = import.meta.env.VITE_AMAP_WEATHER_KEY || 'cb2cede86ca293c819cbf9a538d3faa4'
const AMAP_WEATHER_URL = 'https://restapi.amap.com/v3/weather/weatherInfo'
const AMAP_GEOCODE_URL = 'https://restapi.amap.com/v3/geocode/geo'

const cityOptions = [
  { name: '北京', adcode: '110000' },
  { name: '上海', adcode: '310000' },
  { name: '杭州', adcode: '330100' },
  { name: '广州', adcode: '440100' },
  { name: '成都', adcode: '510100' },
  { name: '郑州', adcode: '410100' },
  { name: '武汉', adcode: '420100' },
  { name: '西安', adcode: '610100' }
]

const cityKeyword = ref('北京')
const selectedCity = ref('北京')
const selectedAdcode = ref('110000')
const loading = ref(false)
const apiError = ref('')
const liveWeather = ref(null)
const forecastWeather = ref([])
const tempChart = ref(null)
const soilChart = ref(null)
let tempChartInstance = null
let soilChartInstance = null
let resizeHandler = null

const fallbackWeather = {
  temp: 28,
  humidity: 42,
  rain: 15,
  condition: '晴',
  wind: '西南风 2 级',
  windPower: '2 级',
  reportTime: ''
}

const fallbackForecast = [
  { date: '今天', weekText: '今天', condition: '晴', high: 28, low: 18, rain: 15, wind: '西南风 2 级', suggestion: '适合巡田、追肥和机械作业' },
  { date: '明天', weekText: '明天', condition: '多云', high: 30, low: 20, rain: 20, wind: '西南风 2 级', suggestion: '适合移栽补苗，注意午后遮阴' },
  { date: '后天', weekText: '后天', condition: '小雨', high: 26, low: 16, rain: 55, wind: '东风 3 级', suggestion: '提前检查排水沟，暂停露天喷药' },
  { date: '第 4 天', weekText: '后续', condition: '阴', high: 24, low: 14, rain: 35, wind: '东北风 2 级', suggestion: '可安排病虫害巡查和棚内通风' }
]

const currentWeather = computed(() => liveWeather.value || fallbackWeather)
const forecastList = computed(() => forecastWeather.value.length ? forecastWeather.value : fallbackForecast)
const dataSourceLabel = computed(() => apiError.value ? '本地备用数据' : '高德实时数据')
const statusText = computed(() => {
  if (loading.value) return '正在从高德 API 获取实时天气和预报数据。'
  if (apiError.value) return `高德 API 暂不可用：${apiError.value}。页面已自动切换到本地备用预测。`
  return `已连接高德 API，当前城市编码：${selectedAdcode.value}。`
})

const weatherIcon = computed(() => {
  const condition = currentWeather.value.condition || ''
  if (condition.includes('雨') || condition.includes('雪')) return 'Lightning'
  if (condition.includes('阴')) return 'Cloudy'
  if (condition.includes('云')) return 'PartlyCloudy'
  return 'Sunny'
})

const riskList = computed(() => {
  const rainyDays = forecastList.value.filter(item => item.rain >= 50).length
  const highTempDays = forecastList.value.filter(item => Number(item.high) >= 32).length
  const dryDays = forecastList.value.filter(item => item.rain <= 20).length
  return [
    {
      level: highTempDays >= 2 ? 'medium' : 'low',
      label: highTempDays >= 2 ? '需关注' : '正常',
      title: '高温风险',
      content: highTempDays >= 2 ? '未来几天存在高温过程，建议增加早晚灌溉频次。' : '高温风险较低，可按常规节奏管理。'
    },
    {
      level: rainyDays >= 2 ? 'high' : 'low',
      label: rainyDays >= 2 ? '偏高' : '正常',
      title: '降雨风险',
      content: rainyDays >= 2 ? '降雨日较多，低洼地块需提前清沟排水。' : '降雨风险可控，适合安排户外农事作业。'
    },
    {
      level: dryDays >= 3 ? 'medium' : 'low',
      label: dryDays >= 3 ? '偏干' : '适宜',
      title: '墒情风险',
      content: dryDays >= 3 ? '少雨天数较多，建议关注表层土壤含水量。' : '土壤水分整体适宜，保持常规监测即可。'
    }
  ]
})

const adviceList = computed(() => forecastList.value.map(item => ({
  date: item.date,
  content: item.suggestion,
  type: item.rain >= 50 || Number(item.high) >= 33 ? 'warn' : 'ok'
})))

const soilStatus = computed(() => {
  const avgRain = Math.round(forecastList.value.reduce((sum, item) => sum + item.rain, 0) / forecastList.value.length)
  if (avgRain >= 50) return { type: 'warning', label: '偏湿', text: '未来降水偏多，建议保持排水通畅，减少大水漫灌。' }
  if (avgRain <= 20) return { type: 'danger', label: '偏干', text: '未来降水偏少，建议启用滴灌或分区补水。' }
  return { type: 'success', label: '适宜', text: '墒情处于适宜区间，可维持当前灌溉策略。' }
})

async function searchWeather() {
  const keyword = cityKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入城市名称')
    return
  }

  const knownCity = cityOptions.find(item => item.name.includes(keyword) || keyword.includes(item.name))
  if (knownCity) {
    await selectCity(knownCity)
    return
  }

  loading.value = true
  try {
    const location = await resolveAdcode(keyword)
    selectedCity.value = location.name
    selectedAdcode.value = location.adcode
    cityKeyword.value = location.name
    await fetchWeather(location.adcode)
  } catch (error) {
    apiError.value = error.message || '城市解析失败'
    liveWeather.value = null
    forecastWeather.value = []
    ElMessage.warning(apiError.value)
  } finally {
    loading.value = false
  }
}

async function selectCity(city) {
  selectedCity.value = city.name
  selectedAdcode.value = city.adcode
  cityKeyword.value = city.name
  await fetchWeather(city.adcode)
}

function resetCity() {
  selectCity(cityOptions[0])
}

async function resolveAdcode(keyword) {
  const params = new URLSearchParams({
    key: AMAP_KEY,
    address: keyword,
    output: 'JSON'
  })
  const data = await requestAmap(`${AMAP_GEOCODE_URL}?${params.toString()}`)
  const item = data.geocodes?.[0]
  if (!item?.adcode) throw new Error('未找到该城市或区县的 adcode')
  const cityName = typeof item.city === 'string' && item.city ? item.city : item.province || item.district || keyword
  return { name: cityName, adcode: item.adcode }
}

async function fetchWeather(adcode) {
  loading.value = true
  apiError.value = ''
  try {
    const [baseData, allData] = await Promise.all([
      fetchWeatherByType(adcode, 'base'),
      fetchWeatherByType(adcode, 'all')
    ])
    const live = baseData.lives?.[0]
    const forecast = allData.forecasts?.[0]
    if (!live) throw new Error('高德未返回实时天气')
    if (!forecast?.casts?.length) throw new Error('高德未返回预报天气')

    liveWeather.value = normalizeLiveWeather(live)
    forecastWeather.value = forecast.casts.map(normalizeForecastWeather)
    selectedCity.value = live.city || forecast.city || selectedCity.value
    selectedAdcode.value = live.adcode || forecast.adcode || adcode
  } catch (error) {
    apiError.value = error.message || '请求失败'
    liveWeather.value = null
    forecastWeather.value = []
  } finally {
    loading.value = false
    await nextTick()
    renderCharts()
  }
}

function fetchWeatherByType(adcode, extensions) {
  const params = new URLSearchParams({
    key: AMAP_KEY,
    city: adcode,
    extensions,
    output: 'JSON'
  })
  return requestAmap(`${AMAP_WEATHER_URL}?${params.toString()}`)
}

async function requestAmap(url) {
  const response = await fetch(url)
  if (!response.ok) throw new Error(`网络请求失败（${response.status}）`)
  const data = await response.json()
  if (data.status !== '1') throw new Error(data.info || '高德 API 返回失败')
  return data
}

function normalizeLiveWeather(live) {
  return {
    temp: formatNumber(live.temperature),
    humidity: formatNumber(live.humidity),
    rain: inferRainRisk(live.weather),
    condition: live.weather || '--',
    wind: `${live.winddirection || '--'}风 ${live.windpower || '--'} 级`,
    windPower: `${live.windpower || '--'} 级`,
    reportTime: live.reporttime || ''
  }
}

function normalizeForecastWeather(item) {
  const condition = item.dayweather === item.nightweather ? item.dayweather : `${item.dayweather}转${item.nightweather}`
  const wind = item.daywind === item.nightwind
    ? `${item.daywind}风 ${item.daypower} 级`
    : `${item.daywind}转${item.nightwind}风 ${item.daypower}/${item.nightpower} 级`
  return {
    date: item.date,
    weekText: `周${item.week}`,
    condition,
    high: formatNumber(item.daytemp),
    low: formatNumber(item.nighttemp),
    rain: inferRainRisk(condition),
    wind,
    suggestion: buildAdvice(condition, Number(item.daytemp), inferRainRisk(condition))
  }
}

function buildAdvice(condition, highTemp, rainRisk) {
  if (rainRisk >= 60) return '预计有明显降水，提前清沟排水，露天喷药需顺延。'
  if (highTemp >= 33) return '高温偏强，建议早晚灌溉，避免正午移栽和喷药。'
  if (condition.includes('阴') || condition.includes('云')) return '适合巡田、补苗和棚内通风，注意病虫害早期排查。'
  return '适合田间管理、追肥和机械作业，保持常规墒情监测。'
}

function inferRainRisk(condition = '') {
  if (condition.includes('暴雨') || condition.includes('大雨')) return 90
  if (condition.includes('中雨') || condition.includes('雷阵雨')) return 75
  if (condition.includes('小雨') || condition.includes('阵雨') || condition.includes('雨')) return 60
  if (condition.includes('雪')) return 55
  if (condition.includes('阴')) return 35
  if (condition.includes('云')) return 25
  return 10
}

function formatNumber(value) {
  const number = Number(value)
  return Number.isFinite(number) ? Math.round(number) : '--'
}

function renderCharts() {
  renderTempChart()
  renderSoilChart()
}

function renderTempChart() {
  if (!tempChart.value) return
  if (!tempChartInstance) tempChartInstance = echarts.init(tempChart.value)
  tempChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['最高温', '最低温', '降水风险'], bottom: 0 },
    grid: { left: 40, right: 48, top: 32, bottom: 64 },
    xAxis: { type: 'category', data: forecastList.value.map(item => item.date) },
    yAxis: [
      { type: 'value', name: '温度℃' },
      { type: 'value', name: '风险%', min: 0, max: 100 }
    ],
    series: [
      { name: '最高温', type: 'line', data: forecastList.value.map(item => item.high), smooth: true, itemStyle: { color: '#f44336' } },
      { name: '最低温', type: 'line', data: forecastList.value.map(item => item.low), smooth: true, itemStyle: { color: '#2196f3' } },
      { name: '降水风险', type: 'bar', yAxisIndex: 1, data: forecastList.value.map(item => item.rain), itemStyle: { color: '#4caf50', borderRadius: 4 } }
    ]
  })
}

function renderSoilChart() {
  if (!soilChart.value) return
  if (!soilChartInstance) soilChartInstance = echarts.init(soilChart.value)
  const moisture = forecastList.value.map((item, index) => Math.min(88, Math.max(32, 44 + item.rain * 0.36 - index)))
  const soilTemp = forecastList.value.map(item => Math.round((Number(item.high) + Number(item.low)) / 2 - 3))
  soilChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['土壤湿度', '土壤温度'], bottom: 0 },
    grid: { left: 42, right: 42, top: 32, bottom: 64 },
    xAxis: { type: 'category', data: forecastList.value.map(item => item.date) },
    yAxis: [
      { type: 'value', name: '湿度%', min: 0, max: 100 },
      { type: 'value', name: '温度℃' }
    ],
    series: [
      { name: '土壤湿度', type: 'bar', data: moisture, itemStyle: { color: '#4caf50', borderRadius: 4 } },
      { name: '土壤温度', type: 'line', yAxisIndex: 1, data: soilTemp, smooth: true, itemStyle: { color: '#ff9800' } }
    ]
  })
}

watch(forecastList, async () => {
  await nextTick()
  renderCharts()
})

onMounted(async () => {
  await nextTick()
  renderCharts()
  await fetchWeather(selectedAdcode.value)
  resizeHandler = () => {
    tempChartInstance?.resize()
    soilChartInstance?.resize()
  }
  window.addEventListener('resize', resizeHandler)
})

onUnmounted(() => {
  if (resizeHandler) window.removeEventListener('resize', resizeHandler)
  tempChartInstance?.dispose()
  soilChartInstance?.dispose()
})
</script>

<style scoped>
.weather-page {
  color: var(--color-text);
}

.search-panel {
  margin-bottom: 16px;
}

.search-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.search-main,
.city-tabs,
.data-status,
.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.city-input {
  width: 320px;
  max-width: 100%;
}

.data-status {
  margin-top: 12px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.summary-row {
  margin-bottom: 16px;
}

.current-card,
.risk-card {
  min-height: 300px;
}

.current-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.city-name {
  font-size: 22px;
  font-weight: 700;
}

.weather-desc,
.report-time {
  color: var(--color-text-secondary);
  margin-top: 6px;
}

.report-time {
  font-size: 12px;
}

.weather-icon {
  font-size: 48px;
  color: var(--color-primary);
}

.temperature {
  margin: 24px 0;
  font-size: 56px;
  line-height: 1;
  font-weight: 700;
  color: var(--color-primary-dark);
}

.temperature span {
  font-size: 24px;
  margin-left: 4px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.metric-item {
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-bg);
}

.metric-item span {
  display: block;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.metric-item strong {
  display: block;
  margin-top: 4px;
  font-size: 18px;
}

.risk-list {
  display: grid;
  gap: 12px;
}

.risk-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 14px;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background: var(--color-bg-white);
}

.risk-item p {
  margin-top: 6px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.risk-item.high {
  border-color: rgba(244, 67, 54, 0.35);
  background: rgba(244, 67, 54, 0.06);
}

.risk-item.medium {
  border-color: rgba(255, 152, 0, 0.35);
  background: rgba(255, 152, 0, 0.08);
}

.risk-item.low {
  border-color: rgba(76, 175, 80, 0.28);
}

.chart-container {
  width: 100%;
  height: 320px;
}

.soil-chart {
  height: 300px;
}

.soil-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.forecast-table {
  width: 100%;
}

@media (max-width: 768px) {
  .search-main {
    width: 100%;
  }

  .search-main .el-button {
    flex: 1;
  }

  .city-input {
    width: 100%;
  }

  .temperature {
    font-size: 44px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>

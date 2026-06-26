<template>
  <div class="page">
    <div class="page-header"><h2>产量预测</h2><p>基于环境数据和历史趋势的农作物产量智能预测</p></div>

    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6">
        <el-select v-model="predictCrop" placeholder="选择作物" @change="calcPredict">
          <el-option label="水稻" value="水稻" /><el-option label="小麦" value="小麦" />
          <el-option label="玉米" value="玉米" /><el-option label="大豆" value="大豆" />
          <el-option label="番茄" value="番茄" />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-select v-model="predictYear" placeholder="选择年份" @change="calcPredict">
          <el-option label="2026年" :value="2026" /><el-option label="2025年" :value="2025" />
          <el-option label="2024年" :value="2024" />
        </el-select>
      </el-col>
      <el-col :span="4"><el-button type="primary" @click="calcPredict"><el-icon><DataAnalysis /></el-icon> 分析</el-button></el-col>
    </el-row>

    <!-- 产量预测卡片 -->
    <el-row :gutter="16" v-if="predicted">
      <el-col :span="6"><el-card class="stat-card"><div class="stat-label">预测亩产量</div><div class="stat-value primary">{{ predictData.yield }} <small>kg/亩</small></div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-label">同比去年</div><div class="stat-value" :class="predictData.changeRate>=0?'success':'danger'">{{ predictData.changeRate>=0?'+':'' }}{{ predictData.changeRate }}%</div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-label">适宜度评分</div><div class="stat-value warning">{{ predictData.score }}/100</div></el-card></el-col>
      <el-col :span="6"><el-card class="stat-card"><div class="stat-label">预测种植面积</div><div class="stat-value">{{ predictData.area }} <small>亩</small></div></el-card></el-col>
    </el-row>

    <!-- 历年产量趋势 -->
    <el-card style="margin-top:16px">
      <template #header>历年产量趋势（{{ predictCrop }}）</template>
      <div ref="yieldChart" class="chart-lg"></div>
    </el-card>

    <!-- 影响因素分析 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card>
          <template #header>影响因素权重</template>
          <div ref="factorChart" class="chart-md"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>产量预测建议</template>
          <el-timeline>
            <el-timeline-item v-for="(t, i) in suggestions" :key="i" :timestamp="'建议'+(i+1)" placement="top" color="#409eff">
              <p>{{ t }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const predictCrop = ref('水稻'), predictYear = ref(2026), predicted = ref(false)
const predictData = ref({ yield: 0, changeRate: 0, score: 0, area: 0 })
const suggestions = ref([])
const yieldChart = ref(null), factorChart = ref(null)
let yCh = null, fCh = null, yResize = null, fResize = null

function calcPredict() {
  const data = {
    '水稻': { yield: 628, changeRate: 3.2, score: 82, area: 12500, suggestions: ['加强水稻分蘖期水肥管理，适时晒田控制无效分蘖','关注稻瘟病预警，建议孕穗期前喷施预防药剂','根据墒情监测合理灌溉，保持浅水层3-5cm','增施硅钾肥提高抗倒伏能力'] },
    '小麦': { yield: 485, changeRate: -1.5, score: 76, area: 9800, suggestions: ['注意春季返青肥追施，促进分蘖成穗','加强条锈病监测与防控','拔节期注意控制群体密度防倒伏','灌浆期保持土壤适宜含水量(60-70%)'] },
    '玉米': { yield: 720, changeRate: 5.8, score: 88, area: 15000, suggestions: ['推荐密植高产栽培模式亩株数4500-5000','大喇叭口期追施穗肥，增产效果显著','加强玉米螟防治，使用性诱剂或赤眼蜂','适时晚收增加灌浆天数提高千粒重'] },
    '大豆': { yield: 210, changeRate: 2.1, score: 74, area: 8200, suggestions: ['推荐根瘤菌拌种提高固氮效率','花期遇干旱及时补水','防治大豆食心虫和蚜虫','鼓粒期喷施磷酸二氢钾增粒重'] },
    '番茄': { yield: 5200, changeRate: 8.5, score: 85, area: 3200, suggestions: ['加强棚内温湿度调控防晚疫病','合理整枝打杈保果实品质','增施有机肥和钙肥防脐腐病','适时采收分批次上市提高商品率'] }
  }
  predictData.value = data[predictCrop.value]
  suggestions.value = data[predictCrop.value].suggestions
  predicted.value = true
  renderCharts()
}

function renderCharts() {
  if (yieldChart.value) {
    if (!yCh) { yCh = echarts.init(yieldChart.value); yResize = () => yCh.resize(); window.addEventListener('resize', yResize) }
    const years = ['2020','2021','2022','2023','2024','2025','2026(预测)']
    const baseYield = predictCrop.value === '水稻' ? [580,592,605,610,608,615,628] :
      predictCrop.value === '小麦' ? [450,462,475,478,490,492,485] :
      predictCrop.value === '玉米' ? [650,665,680,695,702,710,720] :
      predictCrop.value === '大豆' ? [185,190,195,200,198,206,210] : [4500,4620,4750,4820,4900,5000,5200]
    yCh.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: years }, yAxis: { type: 'value', name: 'kg/亩' },
      series: [{
        type: 'bar', data: baseYield.map((v, i) => i === 6 ? { value: v, itemStyle: { color: '#409eff' } } : v),
        itemStyle: { borderRadius: 6, color: '#67c23a' }, label: { show: true, position: 'top' }
      }]
    })
  }
  if (factorChart.value) {
    if (!fCh) { fCh = echarts.init(factorChart.value); fResize = () => fCh.resize(); window.addEventListener('resize', fResize) }
    fCh.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['45%','70%'], center: ['50%','55%'],
        label: { formatter: '{b}\n{d}%' },
        data: [
          { value: 35, name: '气候条件', itemStyle: { color: '#409eff' } },
          { value: 25, name: '土壤肥力', itemStyle: { color: '#67c23a' } },
          { value: 20, name: '品种特性', itemStyle: { color: '#e6a23c' } },
          { value: 12, name: '管理技术', itemStyle: { color: '#f56c6c' } },
          { value: 8, name: '病虫害', itemStyle: { color: '#909399' } }
        ]
      }]
    })
  }
}

onMounted(async () => { calcPredict(); await nextTick(); renderCharts() })
onUnmounted(() => {
  window.removeEventListener('resize', yResize); window.removeEventListener('resize', fResize)
  yCh?.dispose(); fCh?.dispose()
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-value small { font-size: 14px; font-weight: 400; }
.primary { color: #409eff; } .success { color: #67c23a; } .warning { color: #e6a23c; } .danger { color: #f56c6c; }
.chart-lg { width: 100%; height: 350px; }
.chart-md { width: 100%; height: 300px; }
</style>

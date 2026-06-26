<template>
  <div class="page">
    <div class="page-header"><h2>知识图谱</h2><p>农产品知识关联关系可视化</p></div>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6">
        <el-input v-model="cropKeyword" placeholder="输入作物查询" clearable @keyup.enter="searchGraph" />
      </el-col>
      <el-col :span="6">
        <el-select v-model="graphType" placeholder="图谱类型" @change="searchGraph">
          <el-option label="病虫害关联" value="pest" /><el-option label="作物-环境" value="env" />
          <el-option label="施肥方案" value="fertilizer" /><el-option label="栽培技术" value="tech" />
        </el-select>
      </el-col>
      <el-col :span="4"><el-button type="primary" @click="searchGraph"><el-icon><Search /></el-icon> 查询</el-button></el-col>
    </el-row>
    <el-card>
      <div ref="graphDom" class="graph-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'

const graphDom = ref(null), cropKeyword = ref(''), graphType = ref('pest')
let chart = null, resizeHandler = null

function buildGraphData() {
  const nodes = [], links = [], type = graphType.value, kw = cropKeyword.value
  if (type === 'pest') {
    nodes.push(
      { id: '水稻', name: '水稻', category: 0, symbolSize: 50 },
      { id: '小麦', name: '小麦', category: 0, symbolSize: 45 },
      { id: '玉米', name: '玉米', category: 0, symbolSize: 45 },
      { id: '番茄', name: '番茄', category: 0, symbolSize: 40 },
      { id: '黄瓜', name: '黄瓜', category: 0, symbolSize: 40 },
      { id: '稻瘟病', name: '稻瘟病', category: 1, symbolSize: 30 },
      { id: '纹枯病', name: '纹枯病', category: 1, symbolSize: 28 },
      { id: '白粉病', name: '白粉病', category: 1, symbolSize: 28 },
      { id: '蚜虫', name: '蚜虫', category: 2, symbolSize: 28 },
      { id: '玉米螟', name: '玉米螟', category: 2, symbolSize: 28 },
      { id: '红蜘蛛', name: '红蜘蛛', category: 2, symbolSize: 28 },
      { id: '三唑酮', name: '三唑酮', category: 3, symbolSize: 24 },
      { id: '吡虫啉', name: '吡虫啉', category: 3, symbolSize: 24 },
      { id: '阿维菌素', name: '阿维菌素', category: 3, symbolSize: 24 }
    )
    links.push(
      { source: '水稻', target: '稻瘟病' }, { source: '水稻', target: '纹枯病' }, { source: '水稻', target: '蚜虫' },
      { source: '小麦', target: '白粉病' }, { source: '小麦', target: '蚜虫' }, { source: '小麦', target: '红蜘蛛' },
      { source: '玉米', target: '玉米螟' }, { source: '玉米', target: '蚜虫' },
      { source: '番茄', target: '白粉病' }, { source: '番茄', target: '蚜虫' }, { source: '番茄', target: '红蜘蛛' },
      { source: '黄瓜', target: '白粉病' }, { source: '黄瓜', target: '蚜虫' },
      { source: '稻瘟病', target: '三唑酮' }, { source: '白粉病', target: '三唑酮' },
      { source: '蚜虫', target: '吡虫啉' }, { source: '玉米螟', target: '阿维菌素' }, { source: '红蜘蛛', target: '阿维菌素' }
    )
  } else if (type === 'env') {
    nodes.push(
      { id: '水稻', name: '水稻', category: 0, symbolSize: 50 },
      { id: '高温', name: '高温>35°C', category: 1, symbolSize: 28 },
      { id: '干旱', name: '干旱', category: 1, symbolSize: 28 },
      { id: '多雨', name: '多雨', category: 1, symbolSize: 28 },
      { id: '低温', name: '低温<10°C', category: 1, symbolSize: 28 },
      { id: '水稻减产', name: '减产风险', category: 2, symbolSize: 24 },
      { id: '病虫害增加', name: '病虫害增加', category: 2, symbolSize: 24 }
    )
    links.push({ source: '水稻', target: '高温' }, { source: '水稻', target: '干旱' }, { source: '水稻', target: '多雨' }, { source: '水稻', target: '低温' }, { source: '高温', target: '水稻减产' }, { source: '干旱', target: '水稻减产' }, { source: '多雨', target: '病虫害增加' })
  } else if (type === 'fertilizer') {
    nodes.push(
      { id: '水稻', name: '水稻', category: 0, symbolSize: 50 }, { id: '玉米', name: '玉米', category: 0, symbolSize: 45 },
      { id: '氮肥', name: '氮肥', category: 1, symbolSize: 28 }, { id: '磷肥', name: '磷肥', category: 1, symbolSize: 28 },
      { id: '钾肥', name: '钾肥', category: 1, symbolSize: 28 }, { id: '有机肥', name: '有机肥', category: 1, symbolSize: 28 },
      { id: '基肥', name: '基肥期', category: 2, symbolSize: 22 }, { id: '追肥', name: '追肥期', category: 2, symbolSize: 22 }
    )
    links.push({ source: '水稻', target: '氮肥' }, { source: '水稻', target: '磷肥' }, { source: '水稻', target: '钾肥' }, { source: '玉米', target: '氮肥' }, { source: '氮肥', target: '基肥' }, { source: '磷肥', target: '基肥' }, { source: '钾肥', target: '追肥' }, { source: '水稻', target: '有机肥' })
  } else {
    nodes.push(
      { id: '水稻', name: '水稻', category: 0, symbolSize: 50 },
      { id: '育秧', name: '育秧技术', category: 1, symbolSize: 28 }, { id: '插秧', name: '机插秧', category: 1, symbolSize: 28 },
      { id: '水管理', name: '水层管理', category: 1, symbolSize: 28 }, { id: '晒田', name: '适时晒田', category: 1, symbolSize: 28 },
      { id: '植保', name: '绿色防控', category: 2, symbolSize: 24 }
    )
    links.push({ source: '水稻', target: '育秧' }, { source: '水稻', target: '插秧' }, { source: '水稻', target: '水管理' }, { source: '水稻', target: '晒田' }, { source: '水管理', target: '晒田' }, { source: '晒田', target: '植保' })
  }
  if (kw) {
    return { nodes: nodes.filter(n => n.name.includes(kw) || nodes.filter(n2 => n2.name.includes(kw)).some(n2 => links.some(l => (l.source===n.id && l.target===n2.id) || (l.target===n.id && l.source===n2.id)))), links: links.filter(l => nodes.filter(n => n.name.includes(kw)).some(n => l.source===n.id || l.target===n.id)) }
  }
  return { nodes, links }
}

function renderChart() {
  if (!graphDom.value) return
  if (!chart) {
    chart = echarts.init(graphDom.value)
    resizeHandler = () => chart && chart.resize()
    window.addEventListener('resize', resizeHandler)
  }
  const { nodes, links } = buildGraphData()
  chart.setOption({
    tooltip: { formatter: (p) => p.dataType==='node' ? p.name : `${p.data.source} → ${p.data.target}` },
    legend: { data: ['作物', '病虫害', '虫害', '农药', '环境', '风险'], bottom: 0 },
    series: [{
      type: 'graph', layout: 'force', roam: true, draggable: true,
      categories: [
        { name: '作物', itemStyle: { color: '#67c23a' } },
        { name: '病虫害', itemStyle: { color: '#f56c6c' } },
        { name: '虫害', itemStyle: { color: '#e6a23c' } },
        { name: '农药', itemStyle: { color: '#409eff' } },
        { name: '环境', itemStyle: { color: '#909399' } },
        { name: '风险', itemStyle: { color: '#f56c6c' } }
      ],
      data: nodes, links: links,
      force: { repulsion: 300, edgeLength: [100, 250] },
      label: { show: true, fontSize: 13 },
      emphasis: { focus: 'adjacency', lineStyle: { width: 4 } },
      edgeSymbol: ['none', 'arrow'], edgeSymbolSize: 8,
      lineStyle: { opacity: 0.6, curveness: 0.2 }
    }]
  })
}

function searchGraph() { renderChart() }

onMounted(async () => { await nextTick(); renderChart() })
onUnmounted(() => {
  window.removeEventListener('resize', resizeHandler)
  chart?.dispose()
})
</script>

<style scoped>
.graph-container { width: 100%; height: 600px; }
</style>

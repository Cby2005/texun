<template>
  <div class="page">
    <div class="page-header"><h2>溯源记录</h2><p>查看产品的全链路溯源信息</p></div>
    <el-card>
      <el-timeline>
        <el-timeline-item v-for="(item, idx) in records" :key="idx" :timestamp="item.time" placement="top">
          <el-card shadow="hover">
            <h4>{{ item.title }}</h4>
            <p style="color:#666;font-size:13px">{{ item.desc }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="!records.length" description="暂无溯源记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const records = ref([
  { title: '种植记录', desc: '2024-06-01 草莓播种，品种：红颜，面积：2亩，使用有机肥料', time: '2024-06-01' },
  { title: '施肥记录', desc: '2024-06-15 追施有机肥，用量：50kg/亩', time: '2024-06-15' },
  { title: '质检记录', desc: '2024-07-20 农残检测合格，符合绿色食品标准', time: '2024-07-20' },
  { title: '采摘记录', desc: '2024-08-01 成熟采摘，批次 B202406001', time: '2024-08-01' },
  { title: '包装记录', desc: '2024-08-02 分拣包装，规格：500g/盒，30盒/箱', time: '2024-08-02' },
  { title: '入库记录', desc: '2024-08-02 冷链入库，仓库：1号仓库，温度：2℃，湿度：85%', time: '2024-08-02' },
  { title: '发货记录', desc: '2024-08-03 冷链发货，目的地：北京新发地市场', time: '2024-08-03' },
])
</script>

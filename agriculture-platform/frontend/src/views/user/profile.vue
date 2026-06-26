<template>
  <div class="page">
    <div class="page-header"><h2>用户画像</h2></div>
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
            <span style="font-weight:bold">兴趣标签热度</span>
            <el-button size="small" @click="loadProfile">刷新画像</el-button>
          </div>
          <div v-if="profile && !profile.coldStart" style="margin-bottom:16px">
            <div v-for="(val, key) in profile.interestTags" :key="key" style="display:flex;align-items:center;margin-bottom:8px">
              <span style="width:80px;font-size:13px">{{ key }}</span>
              <el-progress :percentage="Math.round(val * 10)" :color="colorFn(val)" style="flex:1" />
            </div>
          </div>
          <el-empty v-else description="暂无行为数据，处于冷启动阶段" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card style="margin-bottom:16px">
          <h4 style="margin:0 0 8px">偏好摘要</h4>
          <div v-if="profile">
            <div style="font-size:13px;margin:4px 0"><b>偏好作物:</b> {{ profile.cropPreference || '--' }}</div>
            <div style="font-size:13px;margin:4px 0"><b>偏好技术:</b> {{ profile.techPreference || '--' }}</div>
            <div style="font-size:13px;margin:4px 0"><b>偏好地区:</b> {{ profile.regionPreference || '--' }}</div>
          </div>
        </el-card>
        <el-card>
          <h4 style="margin:0 0 8px">画像描述</h4>
          <p style="font-size:13px;line-height:1.8;color:#666">{{ profile ? profile.profileText : '加载中...' }}</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserProfile } from '../../api/recommend'

const profile = ref(null)

function colorFn(v) { return v > 8 ? '#67c23a' : v > 5 ? '#409eff' : '#909399' }

async function loadProfile() {
  try { const r = await getUserProfile(); profile.value = r.data } catch (e) {}
}

onMounted(loadProfile)
</script>

<template>
  <div class="my-points">
    <h2>我的积分</h2>
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-statistic title="总积分" :value="account.totalPoints || 0" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="可用积分" :value="account.availablePoints || 0" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="已使用" :value="account.usedPoints || 0" />
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="$router.push('/points/mall')">去积分商城兑换</el-button>
      </el-col>
    </el-row>

    <h3 style="margin-top:24px">积分明细</h3>
    <el-table :data="records" stripe size="small" v-loading="loading">
      <el-table-column prop="pointsChange" label="变动" width="80">
        <template #default="{row}">
          <span :style="{color: row.pointsChange > 0 ? '#67c23a' : '#f56c6c'}">
            {{ row.pointsChange > 0 ? '+' : '' }}{{ row.pointsChange }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="changeType" label="类型" width="80">
        <template #default="{row}">{{ { EARN:'获得', SPEND:'消费', REFUND:'退款', ADJUST:'调整' }[row.changeType] || row.changeType }}</template>
      </el-table-column>
      <el-table-column prop="sourceType" label="来源" width="120">
        <template #default="{row}">{{ { LECTURE_QUIZ_REWARD:'答题奖励', MALL_EXCHANGE:'商城兑换', REFUND:'退款', ADMIN_ADJUST:'管理员调整' }[row.sourceType] || row.sourceType }}</template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column prop="beforePoints" label="变动前" width="80" />
      <el-table-column prop="afterPoints" label="变动后" width="80" />
      <el-table-column label="时间" width="170">
        <template #default="{row}">{{ row.createTime?.substring(0,19) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { myPointsAccount, myPointsRecords } from '@/api/quiz'

const account = reactive({ totalPoints: 0, availablePoints: 0, usedPoints: 0, frozenPoints: 0 })
const records = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const [a, r] = await Promise.all([myPointsAccount(), myPointsRecords()])
    if (a.data) Object.assign(account, a.data)
    records.value = r.data || []
  } catch {}
  loading.value = false
}

onMounted(load)
</script>

<style scoped>
.my-points { padding: 20px; }
.my-points h2 { margin: 0 0 16px; }
.stats-row { margin-bottom: 8px; background: #fff; padding: 16px; border-radius: 8px; border: 1px solid #eee; align-items: center; }
</style>

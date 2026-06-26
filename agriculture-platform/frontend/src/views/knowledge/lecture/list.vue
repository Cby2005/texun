<template>
  <div class="page">
    <div class="page-header">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div><h2>农技讲座</h2></div>
        <el-button type="primary" @click="$router.push('/knowledge/lectures/create')"><el-icon><Edit /></el-icon>发布讲座</el-button>
      </div>
    </div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="l in list" :key="l.id" style="margin-bottom:16px">
        <el-card shadow="hover" @click="$router.push('/knowledge/lectures/'+l.id)" style="cursor:pointer">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">{{ l.title }}</span>
              <el-tag size="small" :type="l.status==='OPEN'?'success':l.status==='FINISHED'?'info':l.status==='CLOSED'?'danger':'warning'">{{ {DRAFT:'草稿',OPEN:'报名中',CLOSED:'截止',FINISHED:'已结束'}[l.status]||l.status }}</el-tag>
            </div>
          </template>
          <p style="font-size:13px;color:#999;margin-bottom:8px">主讲人：{{ l.speakerName || '--' }}</p>
          <p style="font-size:13px;color:#999;">时间：{{ l.startsAt }} ~ {{ l.endsAt }}</p>
          <div style="margin-top:8px;font-size:12px;color:#aaa">报名：{{ l.registerCount || 0 }}/{{ l.capacity || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    <el-empty v-if="!list.length" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLectures } from '../../../api/knowledge'

const list = ref([]), total = ref(0), pageNum = ref(1), pageSize = ref(12)
async function load() { try { const r = await getLectures({ pageNum: pageNum.value, pageSize: pageSize.value }); list.value = r.data.records; total.value = r.data.total } catch(e){} }
onMounted(load)
</script>

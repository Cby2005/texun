<template>
  <div class="page">
    <div class="page-header"><h2>全链路溯源</h2><p>输入批次号查询从种植到销售的全链路溯源信息</p></div>
    <div class="filter-bar">
      <el-input v-model="batchNo" placeholder="请输入批次号" clearable style="width:300px" @keyup.enter="search" />
      <el-button type="primary" @click="search"><el-icon><Search /></el-icon>溯源查询</el-button>
    </div>

    <el-empty v-if="!data" description="请输入批次号进行溯源查询" />

    <div v-if="data" class="trace-result">
      <!-- 种植信息 -->
      <el-card v-if="data.production && data.production.length" class="trace-card">
        <template #header>
          <el-tag type="success" effect="dark" size="large">1. 种植环节</el-tag>
        </template>
        <el-timeline>
          <el-timeline-item v-for="p in data.production" :key="p.id" :timestamp="p.sowTime" placement="top">
            <el-card shadow="hover">
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="批次号">{{ p.batchNo }}</el-descriptions-item>
                <el-descriptions-item label="播种时间">{{ p.sowTime }}</el-descriptions-item>
                <el-descriptions-item label="收获时间">{{ p.harvestTime }}</el-descriptions-item>
                <el-descriptions-item label="施肥记录">{{ p.fertilizerRecord }}</el-descriptions-item>
                <el-descriptions-item label="农药记录">{{ p.pesticideRecord }}</el-descriptions-item>
                <el-descriptions-item label="灌溉记录">{{ p.irrigationRecord }}</el-descriptions-item>
                <el-descriptions-item label="土壤温度">{{ p.soilTemperature }}</el-descriptions-item>
                <el-descriptions-item label="土壤湿度">{{ p.soilHumidity }}</el-descriptions-item>
                <el-descriptions-item label="负责人">{{ p.responsiblePerson }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 加工信息 -->
      <el-card v-if="data.processing && data.processing.length" class="trace-card">
        <template #header>
          <el-tag type="primary" effect="dark" size="large">2. 加工环节</el-tag>
        </template>
        <el-timeline>
          <el-timeline-item v-for="p in data.processing" :key="p.id" :timestamp="p.processingTime" placement="top">
            <el-card shadow="hover">
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="加工企业">{{ p.processingEnterprise }}</el-descriptions-item>
                <el-descriptions-item label="加工方式">{{ p.processingMethod }}</el-descriptions-item>
                <el-descriptions-item label="加工温度">{{ p.processingTemperature }}</el-descriptions-item>
                <el-descriptions-item label="质量结果">{{ p.qualityResult }}</el-descriptions-item>
                <el-descriptions-item label="检验员">{{ p.inspector }}</el-descriptions-item>
                <el-descriptions-item label="链上哈希"><el-text truncated style="max-width:160px" size="small" type="info">{{ p.chainHash || '-' }}</el-text></el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 质检信息 -->
      <el-card v-if="data.quality && data.quality.length" class="trace-card">
        <template #header>
          <el-tag type="warning" effect="dark" size="large">3. 质检环节</el-tag>
        </template>
        <el-table :data="data.quality" stripe size="small">
          <el-table-column prop="qualityDate" label="检测日期" width="150" />
          <el-table-column prop="qualityResult" label="检测结果" />
          <el-table-column prop="inspector" label="检验员" width="100" />
          <el-table-column prop="inspectionItems" label="检测项目" />
          <el-table-column prop="inspectionStandard" label="检测标准" />
          <el-table-column prop="qualified" label="是否合格" width="100">
            <template #default="{row}"><el-tag size="small" :type="row.qualified===1?'success':'danger'">{{ row.qualified===1?'合格':'不合格' }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 仓储信息 -->
      <el-card v-if="data.storage && data.storage.length" class="trace-card">
        <template #header>
          <el-tag type="info" effect="dark" size="large">4. 仓储环节</el-tag>
        </template>
        <el-table :data="data.storage" stripe size="small">
          <el-table-column prop="changeTime" label="时间" width="150" />
          <el-table-column prop="changeType" label="类型" width="100">
            <template #default="{row}"><el-tag size="small" :type="row.changeType==='入库'?'success':'warning'">{{ row.changeType }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="quantity" label="变动前数量" />
          <el-table-column prop="changeQuantity" label="变动数量" />
          <el-table-column prop="afterQuantity" label="变动后数量" />
          <el-table-column prop="operatorName" label="操作人" />
        </el-table>
      </el-card>

      <!-- 物流信息 -->
      <el-card v-if="data.logistics && data.logistics.length" class="trace-card">
        <template #header>
          <el-tag type="primary" effect="dark" size="large">5. 物流环节</el-tag>
        </template>
        <el-timeline>
          <el-timeline-item v-for="l in data.logistics" :key="l.id" :timestamp="l.shipTime" placement="top">
            <el-card shadow="hover">
              <el-descriptions :column="2" border size="small">
                <el-descriptions-item label="物流公司">{{ l.logisticsCompany }}</el-descriptions-item>
                <el-descriptions-item label="运输车辆">{{ l.transportVehicle }}</el-descriptions-item>
                <el-descriptions-item label="司机">{{ l.driverName }}</el-descriptions-item>
                <el-descriptions-item label="电话">{{ l.driverPhone }}</el-descriptions-item>
                <el-descriptions-item label="发货地">{{ l.fromAddress }}</el-descriptions-item>
                <el-descriptions-item label="收货地">{{ l.toAddress }}</el-descriptions-item>
                <el-descriptions-item label="发货时间">{{ l.shipTime }}</el-descriptions-item>
                <el-descriptions-item label="到达时间">{{ l.arrivalTime }}</el-descriptions-item>
                <el-descriptions-item label="状态"><el-tag size="small">{{ l.logisticsStatus }}</el-tag></el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 销售信息 -->
      <el-card v-if="data.sales && data.sales.length" class="trace-card">
        <template #header>
          <el-tag type="danger" effect="dark" size="large">6. 销售环节</el-tag>
        </template>
        <el-table :data="data.sales" stripe size="small">
          <el-table-column prop="seller" label="销售方" />
          <el-table-column prop="salesRegion" label="销售区域" />
          <el-table-column prop="listingTime" label="上架时间" width="150" />
          <el-table-column prop="salesPrice" label="单价" width="100" />
          <el-table-column prop="stockQuantity" label="库存" width="80" />
          <el-table-column prop="salesStatus" label="状态" width="100">
            <template #default="{row}"><el-tag size="small">{{ row.salesStatus }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 区块链存证 -->
      <el-card v-if="data.chainBlocks && data.chainBlocks.length" class="trace-card">
        <template #header>
          <el-tag type="success" effect="dark" size="large">7. 区块链存证</el-tag>
        </template>
        <el-timeline>
          <el-timeline-item v-for="b in data.chainBlocks" :key="b.id" :timestamp="b.createTime" placement="top" :type="b.operationType==='上链'?'success':'info'">
            <el-card shadow="hover" size="small">
              <p><strong>操作类型：</strong>{{ b.operationType }} | <strong>区块高度：</strong>#{{ b.blockHeight }}</p>
              <p><strong>区块哈希：</strong><el-text truncated style="max-width:500px" size="small" type="info">{{ b.blockHash }}</el-text></p>
              <p v-if="b.previousHash"><strong>前一哈希：</strong><el-text truncated style="max-width:500px" size="small" type="info">{{ b.previousHash }}</el-text></p>
              <p v-if="b.dataContent"><strong>存证内容：</strong>{{ b.dataContent }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getChainTrace } from '../../../api/trace'
import { ElMessage } from 'element-plus'

const batchNo = ref('')
const data = ref(null)

async function search() {
  if (!batchNo.value) { ElMessage.warning('请输入批次号'); return }
  try { const r = await getChainTrace(batchNo.value); data.value = r.data } catch (e) { ElMessage.error('查询失败') }
}
</script>

<style scoped>
.trace-result { margin-top: 20px; display: flex; flex-direction: column; gap: 16px; }
.trace-card .card-header { display: flex; align-items: center; justify-content: space-between; }
.trace-card .card-time { color: #909399; font-size: 13px; }
</style>

<template>
  <div class="sim-page">
    <div class="top-bar">
      <h2 class="sim-title">草莓数字农田孪生巡检</h2>
      <div class="top-status">
        <el-tag :type="statusTagType" size="large" effect="dark">{{ statusText }}</el-tag>
      </div>
    </div>
    <div class="sim-body">
      <!-- 左侧控制面板 -->
      <div class="left-panel">
        <el-card shadow="hover" class="ctrl-card">
          <template #header><span class="card-title">控制面板</span></template>
          <div class="btn-stack">
            <el-button type="success" @click="initGrid" :disabled="gridReady" :loading="loading">
              初始化地块
            </el-button>
            <el-button type="primary" @click="showPlantingDialog(0, null)" plain>
              种植草莓
            </el-button>
            <el-button @click="updateGrowthBatch" :disabled="!gridReady" plain>
              更新生长状态
            </el-button>
            <el-divider style="margin:8px 0" />
            <el-select v-model="selectedRouteId" placeholder="选择巡检路线" class="full-w-sel" @change="loadInspectionRoute" clearable>
              <el-option v-for="rt in inspectionRoutes" :key="rt.id" :label="rt.routeCode + ' - ' + rt.routeName" :value="rt.id" />
            </el-select>
            <el-select v-model="selectedDroneId" placeholder="选择执行无人机" class="full-w-sel" @change="onSimDroneChange" clearable>
              <el-option v-for="d in simDrones" :key="d.id" :label="d.droneName + ' (' + d.droneCode + ')'" :value="d.id" />
            </el-select>
            <el-button type="warning" @click="generateRoute" :disabled="!gridReady || routeDrawn">
              生成巡检路径
            </el-button>
            <el-button type="primary" @click="startInspection" :disabled="!routeDrawn || !selectedDroneId || inspecting" :loading="inspecting">
              开始巡检
            </el-button>
            <el-button type="warning" @click="pauseInspection" :disabled="!inspecting || paused" plain>
              暂停巡检
            </el-button>
            <el-button @click="resetAll" :disabled="!gridReady" plain>
              重置巡检
            </el-button>
            <el-button type="danger" @click="triggerAbnormal" :disabled="!gridReady" plain>
              模拟病害异常
            </el-button>
            <el-divider style="margin:8px 0" />
            <el-button type="warning" @click="showHarvestDialog" :disabled="!gridReady" plain>
              派出采摘机器人
            </el-button>
            <el-button type="success" @click="showSaleDialog" :disabled="!gridReady" plain>
              售卖草莓
            </el-button>
            <el-button type="success" @click="showRecordList" plain>
              查看种植记录
            </el-button>
          </div>
        </el-card>
        <el-card shadow="hover" class="ctrl-card" style="margin-top:10px">
          <template #header><span class="card-title">区域概览</span></template>
          <div class="area-list">
            <div v-for="a in areaSummary" :key="a.code" class="area-row" :class="a.alarm ? 'ABNORMAL' : 'NORMAL'">
              <span class="area-dot" :style="{background: a.alarm ? '#f44336' : '#67c23a'}"></span>
              <span class="area-name">{{ a.name }}</span>
              <el-tag :type="a.alarm ? 'danger' : 'success'" size="small">{{ a.alarm ? '异常' : '正常' }}</el-tag>
              <span class="area-meta">{{ a.plotCount }}地块 / {{ a.totalPlants }}株</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 中间地块网格 -->
      <div class="center-panel">
        <div v-if="!gridReady" class="placeholder">
          <p>请点击左侧「初始化地块」加载数字农田</p>
          <p class="hint">基于温室草莓种植记录生成地块网格</p>
        </div>
        <template v-else>
          <div v-for="area in areaNames" :key="area" class="area-section">
            <div class="area-label">{{ area }}</div>
            <div class="plot-grid">
              <div
                v-for="plot in plotsByArea(area)" :key="plot.code"
                class="plot-cell"
                :class="[plot.statusClass, { 'plot-highlight': highlightedPlot === plot.code, 'plot-active': activePlot === plot.code }]"
                @click="onPlotClick(plot)"
              >
                <div class="plot-code">{{ plot.code }}</div>
                <div class="plot-info" v-if="plot.plantingId">
                  <div class="plot-variety">{{ plot.variety || '--' }}</div>
                  <div class="plot-count">{{ plot.plantCount || 0 }}株</div>
                  <div v-if="plot.currentPlanting && plot.currentPlanting.matureCount != null" class="plot-mature">
                    成熟{{ plot.currentPlanting.matureCount }} / 已采{{ plot.currentPlanting.harvestedCount || 0 }}
                  </div>
                  <el-tag size="small" :type="growthStatusTag(plot.growthStatus)" class="plot-status-tag">{{ plot.growthStatus || '空地' }}</el-tag>
                  <el-tag v-if="plot.plotStatus && plot.plotStatus !== '空'" size="small" type="warning" class="plot-status-tag" style="margin-left:2px">{{ plot.plotStatus }}</el-tag>
                </div>
                <div class="plot-empty" v-else>空地</div>
              </div>
            </div>
          </div>
          <!-- 巡检路径指示 -->
          <div v-if="routeDrawn && routePoints.length" class="route-bar">
            巡检路线：<span v-for="(rp, i) in routePoints" :key="i">
              <span :class="{ 'route-current': i === currentPathIndex }">{{ rp.plotCode || rp.pointName }}</span>
              <span v-if="i < routePoints.length - 1"> → </span>
            </span>
          </div>
        </template>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <el-card shadow="hover" class="info-card">
          <template #header><span class="card-title">巡检状态</span></template>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="任务状态"><el-tag :type="statusTagType" size="small">{{ statusText }}</el-tag></el-descriptions-item>
            <el-descriptions-item label="当前地块">{{ currentPlotName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前区域">{{ currentPlotArea || '-' }}</el-descriptions-item>
            <el-descriptions-item label="点序号">{{ inspecting ? (currentPathIndex + 1) + '/' + routePoints.length : '-' }}</el-descriptions-item>
            <el-descriptions-item label="巡检进度"><el-progress :percentage="progress" :color="progressColor" :stroke-width="12" text-inside /></el-descriptions-item>
            <el-descriptions-item label="识别结果">{{ detectionResult || '-' }}</el-descriptions-item>
            <el-descriptions-item label="异常数量"><span :style="{color: anomalyCount > 0 ? '#f56c6c' : ''}">{{ anomalyCount }}</span></el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" class="info-card" style="margin-top:10px" v-if="selectedDrone">
          <template #header><span class="card-title">无人机状态</span></template>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="名称">{{ selectedDrone.droneName }}</el-descriptions-item>
            <el-descriptions-item label="编号">{{ selectedDrone.droneCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前电量">
              <el-progress :percentage="Number(selectedDrone.batteryLevel || 0)" :stroke-width="8" :color="selectedDrone.batteryLevel > 60 ? '#67c23a' : '#e6a23c'" />
            </el-descriptions-item>
            <el-descriptions-item label="摄像头">{{ selectedDrone.cameraStatus || '未启动' }}</el-descriptions-item>
            <el-descriptions-item label="当前坐标">
              ({{ selectedDrone.longitude || 0 }}, {{ selectedDrone.latitude || 0 }}, {{ selectedDrone.altitude || 0 }})
            </el-descriptions-item>
            <el-descriptions-item label="累计巡检">{{ selectedDrone.totalInspectionCount ?? 0 }}次</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" class="info-card" style="margin-top:10px" v-loading="statsLoading">
          <template #header><span class="card-title">种植统计</span></template>
          <el-descriptions :column="1" size="small" border v-if="plantingStats">
            <el-descriptions-item label="种植记录">{{ plantingStats.recordCount }}条</el-descriptions-item>
            <el-descriptions-item label="总株数">{{ plantingStats.totalPlants }}株</el-descriptions-item>
            <el-descriptions-item label="平均成本">{{ plantingStats.averageUnitCost }}元/株</el-descriptions-item>
            <el-descriptions-item label="累计成本">{{ plantingStats.totalCost }}元</el-descriptions-item>
            <el-descriptions-item label="成熟地块">{{ matureCount }}个</el-descriptions-item>
            <el-descriptions-item label="异常地块">{{ anomalyCount }}个</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" class="info-card" style="margin-top:10px" v-if="twinStats">
          <template #header><span class="card-title">采摘销售统计</span></template>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="已采收重量">{{ twinStats.totalHarvestWeight || 0 }}kg</el-descriptions-item>
            <el-descriptions-item label="已售卖批次">{{ twinStats.saleBatchCount || 0 }}批</el-descriptions-item>
            <el-descriptions-item label="累计销售额">{{ twinStats.totalSaleAmount || 0 }}元</el-descriptions-item>
            <el-descriptions-item label="溯源码数量">{{ twinStats.traceCodeCount || 0 }}个</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
    </div>

    <!-- ========== 弹窗 ========== -->
    <!-- 种植草莓 -->
    <el-dialog v-model="plantDialog.visible" title="种植草莓" width="550px" destroy-on-close>
      <el-form ref="plantFormRef" :model="plantDialog.form" :rules="plantRules" label-width="100px">
        <el-form-item label="所属区域" prop="areaName">
          <el-select v-model="plantDialog.form.areaName" style="width:100%" @change="onAreaChange">
            <el-option v-for="a in areaNames" :key="a" :value="a" :label="a" />
          </el-select>
        </el-form-item>
        <el-form-item label="地块编号" prop="plotCode">
          <el-input v-model="plantDialog.form.plotCode" disabled />
        </el-form-item>
        <el-form-item label="草莓品种" prop="variety">
          <el-select v-model="plantDialog.form.variety" style="width:100%" filterable allow-create>
            <el-option label="红颜" value="红颜" /><el-option label="章姬" value="章姬" />
            <el-option label="越秀" value="越秀" /><el-option label="妙香" value="妙香" /><el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="种植数量(株)" prop="plantCount">
          <el-input-number v-model="plantDialog.form.plantCount" :min="1" :precision="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="单株成本(元)" prop="unitCost">
          <el-input-number v-model="plantDialog.form.unitCost" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="种植日期" prop="plantingDate">
          <el-date-picker v-model="plantDialog.form.plantingDate" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="生长状态" prop="growthStatus">
          <el-select v-model="plantDialog.form.growthStatus" style="width:100%">
            <el-option label="育苗期" value="育苗期" /><el-option label="生长期" value="生长期" />
            <el-option label="开花期" value="开花期" /><el-option label="结果期" value="结果期" />
            <el-option label="成熟期" value="成熟期" /><el-option label="异常" value="异常" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人"><el-input v-model="plantDialog.form.managerName" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="plantDialog.form.remark" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="总成本(自动)"><el-input :model-value="plantDialog.form.plantCount * plantDialog.form.unitCost" disabled /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="plantDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handlePlantSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 种植记录 -->
    <el-dialog v-model="recordDialog.visible" title="种植记录" width="950px" destroy-on-close>
      <el-table :data="records" stripe size="small" max-height="450" v-loading="recordsLoading">
        <el-table-column prop="id" label="ID" width="55" />
        <el-table-column prop="areaName" label="区域" width="110" />
        <el-table-column prop="variety" label="品种" width="80" />
        <el-table-column prop="plantCount" label="数量" width="70" />
        <el-table-column label="单株成本" width="85"><template #default="{row}">{{ row.unitCost }}元</template></el-table-column>
        <el-table-column label="总成本" width="85"><template #default="{row}">{{ row.totalCost }}元</template></el-table-column>
        <el-table-column prop="plantingDate" label="日期" width="100" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag size="small" :type="growthStatusTag(row.growthStatus)">{{ row.growthStatus }}</el-tag></template></el-table-column>
        <el-table-column prop="managerName" label="负责人" width="75" />
        <el-table-column prop="remark" label="备注" min-width="100" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{row}"><el-button size="small" @click="showEditRecord(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDeleteRecord(row.id)">删除</el-button></template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 编辑种植记录 -->
    <el-dialog v-model="editRecordDialog.visible" title="编辑种植记录" width="500px" destroy-on-close>
      <el-form ref="editFormRef" :model="editRecordDialog.form" :rules="plantRules" label-width="100px">
        <el-form-item label="草莓品种" prop="variety">
          <el-select v-model="editRecordDialog.form.variety" style="width:100%" filterable allow-create>
            <el-option label="红颜" value="红颜" /><el-option label="章姬" value="章姬" />
            <el-option label="越秀" value="越秀" /><el-option label="妙香" value="妙香" /><el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="种植数量" prop="plantCount"><el-input-number v-model="editRecordDialog.form.plantCount" :min="1" :precision="0" style="width:100%" /></el-form-item>
        <el-form-item label="单株成本" prop="unitCost"><el-input-number v-model="editRecordDialog.form.unitCost" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="生长状态" prop="growthStatus">
          <el-select v-model="editRecordDialog.form.growthStatus" style="width:100%">
            <el-option label="育苗期" value="育苗期" /><el-option label="生长期" value="生长期" />
            <el-option label="开花期" value="开花期" /><el-option label="结果期" value="结果期" />
            <el-option label="成熟期" value="成熟期" /><el-option label="异常" value="异常" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人"><el-input v-model="editRecordDialog.form.managerName" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="editRecordDialog.form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="editRecordDialog.visible = false">取消</el-button><el-button type="primary" @click="handleEditRecordSave">保存</el-button></template>
    </el-dialog>

    <!-- 采摘收获 -->
    <el-dialog v-model="harvestDialog.visible" title="派出采摘机器人" width="550px" destroy-on-close>
      <el-form ref="harvestFormRef" :model="harvestDialog.form" :rules="harvestRules" label-width="110px">
        <el-form-item label="种植记录" prop="plantingRecordId">
          <el-select v-model="harvestDialog.form.plantingRecordId" style="width:100%" @change="onHarvestPlantingChange">
            <el-option v-for="p in maturePlots" :key="p.plantingId" :value="p.plantingId"
              :label="p.code + ' - ' + (p.variety||'--') + ' (' + p.growthStatus + ')'" />
          </el-select>
        </el-form-item>
        <el-form-item label="成熟/已采">
          <span style="color:#67c23a">{{ harvestDialog.matureInfo || '请先选择地块' }}</span>
        </el-form-item>
        <el-form-item label="采摘机器人" prop="robotId">
          <el-select v-model="harvestDialog.form.robotId" style="width:100%">
            <el-option v-for="r in robots" :key="r.id" :value="r.id" :label="r.robotName + ' (' + r.battery + '%电量)'" />
          </el-select>
        </el-form-item>
        <el-form-item label="采摘数量(株)" prop="harvestCount">
          <el-input-number v-model="harvestDialog.form.harvestCount" :min="1" :max="harvestDialog.maxHarvest" style="width:100%" />
        </el-form-item>
        <el-form-item label="采摘重量(kg)" prop="harvestWeight">
          <el-input-number v-model="harvestDialog.form.harvestWeight" :min="0.1" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="质量等级" prop="qualityGrade">
          <el-select v-model="harvestDialog.form.qualityGrade" style="width:100%">
            <el-option label="特级" value="特级" /><el-option label="一级" value="一级" />
            <el-option label="二级" value="二级" /><el-option label="次果" value="次果" />
          </el-select>
        </el-form-item>
        <el-form-item label="采摘后处理" prop="postHarvestAction">
          <el-select v-model="harvestDialog.form.postHarvestAction" style="width:100%">
            <el-option label="鲜果入库" value="fresh_storage" />
            <el-option label="直接销售" value="direct_sale" />
            <el-option label="进入加工" value="processing" />
          </el-select>
        </el-form-item>
        <el-form-item label="生成溯源码">
          <el-switch v-model="harvestDialog.form.generateTraceCode" />
        </el-form-item>
        <el-form-item label="操作人员"><el-input v-model="harvestDialog.form.operatorName" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="harvestDialog.form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="harvestDialog.visible = false">取消</el-button><el-button type="primary" @click="handleHarvest">确认采摘</el-button></template>
    </el-dialog>

    <!-- 售卖 -->
    <el-dialog v-model="saleDialog.visible" title="售卖草莓" width="550px" destroy-on-close>
      <el-form ref="saleFormRef" :model="saleDialog.form" :rules="saleRules" label-width="110px">
        <el-form-item label="采收记录" prop="harvestRecordId">
          <el-select v-model="saleDialog.form.harvestRecordId" style="width:100%" @change="onSaleHarvestChange">
            <el-option v-for="h in harvestableList" :key="h.id" :value="h.id" :label="h.harvestCode + ' (' + (h.harvestWeight||0) + 'kg)'" />
          </el-select>
        </el-form-item>
        <el-form-item label="草莓品种"><el-input :model-value="saleDialog.form.variety" disabled /></el-form-item>
        <el-form-item label="销售重量(kg)" prop="saleWeight">
          <el-input-number v-model="saleDialog.form.saleWeight" :min="0.1" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="单价(元/kg)" prop="unitPrice">
          <el-input-number v-model="saleDialog.form.unitPrice" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="销售金额(自动)"><el-input :model-value="(saleDialog.form.saleWeight * saleDialog.form.unitPrice).toFixed(2)" disabled /></el-form-item>
        <el-form-item label="客户名称"><el-input v-model="saleDialog.form.customerName" /></el-form-item>
        <el-form-item label="销售渠道">
          <el-select v-model="saleDialog.form.saleChannel" style="width:100%">
            <el-option label="基地直销" value="基地直销" /><el-option label="批发市场" value="批发市场" />
            <el-option label="电商平台" value="电商平台" /><el-option label="社区团购" value="社区团购" />
            <el-option label="超市供应" value="超市供应" />
          </el-select>
        </el-form-item>
        <el-form-item label="生成溯源码"><el-switch v-model="saleDialog.form.generateTraceCode" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="saleDialog.form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="saleDialog.visible = false">取消</el-button><el-button type="primary" @click="handleSale">确认售卖</el-button></template>
    </el-dialog>

    <!-- 异常植株详情 -->
    <el-dialog v-model="abnormalDialog.visible" title="异常植株详情" width="560px" destroy-on-close>
      <div v-if="abnormalDialog.plot" class="abnormal-detail">
        <div class="abnormal-img-wrap">
          <el-image :src="abnormalDialog.imageUrl" fit="contain" style="width:100%;max-height:260px;border-radius:8px;background:#f5f5f5" />
        </div>
        <el-descriptions :column="2" border size="small" style="margin-top:12px">
          <el-descriptions-item label="地块">{{ abnormalDialog.plot.code }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ abnormalDialog.plot.areaName }}</el-descriptions-item>
          <el-descriptions-item label="品种">{{ abnormalDialog.plot.variety || '-' }}</el-descriptions-item>
          <el-descriptions-item label="生长阶段">{{ abnormalDialog.plot.growthStatus }}</el-descriptions-item>
          <el-descriptions-item label="异常原因">{{ abnormalDialog.reason }}</el-descriptions-item>
          <el-descriptions-item label="识别结果">{{ abnormalDialog.detectResult }}</el-descriptions-item>
          <el-descriptions-item label="置信度">{{ abnormalDialog.confidence }}%</el-descriptions-item>
          <el-descriptions-item label="巡检时间">{{ formatNow() }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="abnormalDialog.visible = false">关闭</el-button>
        <el-button type="primary" @click="saveImageAndAsk">保存图片并智能提问</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addStrawberryPlanting, getStrawberryPlantingList, updateStrawberryPlanting,
  deleteStrawberryPlanting, getStrawberryPlantingStatistics, getAreaPlantingStatistics,
  getDigitalTwinPlots, initDigitalTwinPlots, getAvailableRobots,
  createHarvest, createSale, getDigitalTwinStatistics, getDigitalTwinPlotDetail
} from '@/api/drone'
import { getAvailableDevices, getInspectionRouteList, getInspectionRoute, updateDeviceRuntime } from '@/api/drone'

// ==================== 地块常量 ====================
const areaNames = ['A区育苗区', 'B区开花区', 'C区结果区', 'D区异常修复区']
const areaCodeMap = { 'A区育苗区': 'A', 'B区开花区': 'B', 'C区结果区': 'C', 'D区异常修复区': 'D' }
const areaPlots = { A: 4, B: 4, C: 4, D: 4 }

// ==================== 状态 ====================
const loading = ref(false), gridReady = ref(false)
const routeDrawn = ref(false), inspecting = ref(false), paused = ref(false)
const abnormalActive = ref(false), progress = ref(0), battery = ref(100)
const currentPathIndex = ref(-1), anomalyCount = ref(0)
const detectionResult = ref(''), currentPlotName = ref(''), currentPlotArea = ref('')
const routePoints = ref([]), highlightedPlot = ref(''), activePlot = ref('')
let inspectionTimer = null, pauseTimer = null

// ── 路线/无人机选择 ──
const selectedRouteId = ref(null), inspectionRoutes = ref([])
const selectedDroneId = ref(null), simDrones = ref([]), selectedDrone = ref(null)

const plots = ref([])  // { code, areaCode, areaName, rowNo, colNo, plantingId, variety, plantCount, unitCost, growthStatus, statusClass }
const records = ref([]), recordsLoading = ref(false), plantingStats = ref(null), statsLoading = ref(false)
const plantFormRef = ref(null), editFormRef = ref(null)

const plantDialog = reactive({ visible: false, form: { areaCode:'', areaName:'', plotCode:'', variety:'', plantCount:0, unitCost:2.5, plantingDate:'', growthStatus:'育苗期', managerName:'', remark:'' } })
const recordDialog = reactive({ visible: false })
const editRecordDialog = reactive({ visible: false, form: { id:null, variety:'', plantCount:0, unitCost:0, growthStatus:'', managerName:'', remark:'' } })

const plantRules = {
  areaName: [{required:true, message:'请选择区域', trigger:'change'}],
  variety: [{required:true, message:'请选择品种', trigger:'change'}],
  plantCount: [{required:true, message:'请输入数量', trigger:'blur'},{type:'number',min:1,message:'数量必须>0',trigger:'blur'}],
  unitCost: [{required:true, message:'请输入成本', trigger:'blur'},{type:'number',min:0,message:'不能<0',trigger:'blur'}],
  plantingDate: [{required:true, message:'请选择日期', trigger:'change'}],
  growthStatus: [{required:true, message:'请选择状态', trigger:'change'}]
}

// ==================== computed ====================
const statusText = computed(() => {
  if (paused.value) return '巡检已暂停'; if (inspecting.value) return '巡检中...'
  if (routeDrawn.value) return '路径已就绪'; if (gridReady.value) return '地块已就绪'
  return '未初始化'
})
const statusTagType = computed(() => {
  if (paused.value) return 'warning'; if (inspecting.value) return 'warning'
  if (routeDrawn.value) return 'primary'; if (gridReady.value) return 'success'
  return 'info'
})
const progressColor = computed(() => inspecting.value ? '#e6a23c' : '#409eff')
const matureCount = computed(() => plots.value.filter(p => p.growthStatus === '成熟期').length)
const areaSummary = computed(() => areaNames.map(name => {
  const code = areaCodeMap[name]; const areaPlotsArr = plotsByArea(name)
  return { code, name, plotCount: areaPlotsArr.length, totalPlants: areaPlotsArr.reduce((s,p)=>s+(p.plantCount||0),0), alarm: areaPlotsArr.some(p=>p.growthStatus==='异常') }
}))

function plotsByArea(area) { return plots.value.filter(p => p.areaName === area) }
function growthStatusTag(s) { return { '育苗期':'', '生长期':'success', '开花期':'warning', '结果期':'warning', '成熟期':'success', '异常':'danger' }[s] || '' }
function statusClass(g) { return g ? 'plot-' + ({ '育苗期':'seedling','生长期':'growing','开花期':'flower','结果期':'fruiting','成熟期':'mature','异常':'anomaly'}[g]||'growing') : 'plot-empty' }

// ==================== 地块初始化 ====================
async function initGrid() {
  loading.value = true
  try {
    // 先初始化地块到数据库（幂等，已存在的跳过）
    await initDigitalTwinPlots()
    // 加载地块 + 种植记录
    const pr = await getDigitalTwinPlots()
    const plotData = (pr.data || [])
    const plantRes = await getStrawberryPlantingList({ pageNum: 1, pageSize: 100 })
    const allRecords = (plantRes.data?.records || [])
    const newPlots = []
    let seq = 0
    plotData.forEach(pd => {
      // 找到关联的种植记录
      const rec = pd.currentPlanting
      newPlots.push({
        code: pd.plotCode, areaCode: pd.areaCode, areaName: pd.areaName,
        rowNo: pd.rowNo || 0, colNo: pd.colNo || 0, sortOrder: ++seq,
        plotId: pd.id,
        plantingId: rec?.id || null, variety: rec?.variety || '',
        plantCount: rec?.plantCount || 0, unitCost: rec?.unitCost || 0,
        growthStatus: rec?.growthStatus || '', statusClass: statusClass(rec?.growthStatus || ''),
        plotStatus: pd.plotStatus || '空',
        currentPlanting: rec || null  // ponytail: pass full planting for mature/harvested counts
      })
    })
    // fallback: assign orphaned planting records to empty plots by area
    allRecords.forEach(rec => {
      const areaCode = rec.areaCode || areaCodeMap[rec.areaName] || ''
      const emptyPlot = newPlots.find(p => p.areaCode === areaCode && !p.plantingId)
      if (emptyPlot) {
        emptyPlot.plantingId = rec.id; emptyPlot.variety = rec.variety
        emptyPlot.plantCount = rec.plantCount; emptyPlot.unitCost = rec.unitCost
        emptyPlot.growthStatus = rec.growthStatus; emptyPlot.statusClass = statusClass(rec.growthStatus || '')
      }
    })
    plots.value = newPlots
    anomalyCount.value = newPlots.filter(p => p.growthStatus === '异常').length
    gridReady.value = true; loading.value = false
    ElMessage.success('地块网格初始化完成 (' + newPlots.length + '个地块)')
  } catch (e) {
    console.error(e)
    ElMessage.warning('API加载失败')
    loading.value = false
  }
}

// ==================== 种植 ====================
function showPlantingDialog(plantingId, plotCode) {
  plantDialog.form = { areaCode:'', areaName:'', plotCode:plotCode||'', variety:'', plantCount:0, unitCost:2.5, plantingDate:'', growthStatus:'育苗期', managerName:'', remark:'' }
  if (plotCode) {
    const areaCode = plotCode.split('-')[0]
    plantDialog.form.areaCode = areaCode
    plantDialog.form.areaName = areaNames.find(a => areaCodeMap[a] === areaCode) || ''
  }
  plantDialog.visible = true
  nextTick(() => plantFormRef.value?.clearValidate())
}
function onAreaChange(val) { plantDialog.form.areaCode = areaCodeMap[val] || '' }
async function handlePlantSave() {
  try { await plantFormRef.value.validate() } catch { return }
  try {
    await addStrawberryPlanting({ ...plantDialog.form, totalCost: plantDialog.form.plantCount * plantDialog.form.unitCost })
    plantDialog.visible = false; ElMessage.success('种植成功')
    loadPlantingStats(); initGrid()
  } catch (e) { /* interceptor handles */ }
}

// ==================== 种植记录管理 ====================
async function showRecordList() { recordDialog.visible = true; await loadRecords() }
async function loadRecords() {
  recordsLoading.value = true
  try { const r = await getStrawberryPlantingList({ pageNum:1, pageSize:100 }); records.value = r.data?.records || [] }
  catch(e){} finally { recordsLoading.value = false }
}
function showEditRecord(row) {
  editRecordDialog.form = { id:row.id, variety:row.variety, plantCount:row.plantCount, unitCost:row.unitCost, growthStatus:row.growthStatus, managerName:row.managerName||'', remark:row.remark||'' }
  editRecordDialog.visible = true; nextTick(() => editFormRef.value?.clearValidate())
}
async function handleEditRecordSave() {
  try { await editFormRef.value.validate() } catch { return }
  try { await updateStrawberryPlanting(editRecordDialog.form.id, editRecordDialog.form); editRecordDialog.visible = false; ElMessage.success('已更新'); loadRecords(); loadPlantingStats(); initGrid() }
  catch(e){}
}
async function handleDeleteRecord(id) {
  await ElMessageBox.confirm('确认删除?', '提示', {type:'warning'})
  await deleteStrawberryPlanting(id); ElMessage.success('已删除'); loadRecords(); loadPlantingStats(); initGrid()
}
async function loadPlantingStats() {
  statsLoading.value = true
  try { const r = await getStrawberryPlantingStatistics(); if (r.data) plantingStats.value = r.data }
  catch(e){} finally { statsLoading.value = false }
}

// ==================== 巡检路径 ====================
function generateRoute() {
  // ponytail: 按异常>成熟>结果>生长>其他优先级排序, 空地跳过
  const priority = { '异常':0, '成熟期':1, '结果期':2, '开花期':3, '生长期':4, '育苗期':5 }
  const candidates = [...plots.value].filter(p => p.plantingId).sort((a,b) => {
    const pa = priority[a.growthStatus] ?? 9, pb = priority[b.growthStatus] ?? 9
    if (pa !== pb) return pa - pb
    return (a.sortOrder||0) - (b.sortOrder||0)
  })
  if (!candidates.length) { ElMessage.warning('没有可巡检地块（请先种植草莓）'); return }
  routePoints.value = candidates.map((p, i) => ({ 
    plotCode: p.code, areaName: p.areaName, growthStatus: p.growthStatus, plantingId: p.plantingId, sequenceNo: i+1,
    coordinateX: p.coordinateX || (p.rowNo * 2.5 - 1.25), coordinateY: p.coordinateY || 2.0, coordinateZ: p.coordinateZ || (p.colNo * 2.5 - 1.25)
  }))
  routeDrawn.value = true; currentPathIndex.value = -1; progress.value = 0
  ElMessage.success('巡检路线生成 (' + routePoints.value.length + '个点)')
}

// ── 加载路线列表 ──
async function loadRoutes() {
  try {
    const r = await getInspectionRouteList({ pageNum: 1, pageSize: 100 })
    inspectionRoutes.value = r.data?.records || []
  } catch { inspectionRoutes.value = [] }
}
// ── 选择已有路线 ──
async function loadInspectionRoute() {
  if (!selectedRouteId.value) return
  try {
    const r = await getInspectionRoute(selectedRouteId.value)
    const pts = r.data?.points || []
    routePoints.value = pts.map(p => ({ 
      plotCode: p.plotCode || p.pointName, areaName: p.areaName, 
      growthStatus: p.pointType, plantingId: p.plotId, sequenceNo: p.sequenceNo,
      coordinateX: p.coordinateX || (p.rowNo * 2.5 - 1.25), coordinateY: p.coordinateY || 2.0, coordinateZ: p.coordinateZ || (p.colNo * 2.5 - 1.25)
    }))
    routeDrawn.value = true; currentPathIndex.value = -1; progress.value = 0
    ElMessage.success('已加载路线: ' + r.data?.route?.routeName + ' (' + pts.length + '个点)')
  } catch { ElMessage.error('加载路线失败') }
}
// ── 加载可用无人机 ──
async function loadSimDrones() {
  try {
    const r = await getAvailableDevices({})
    simDrones.value = r.data || []
  } catch { simDrones.value = [] }
}
function onSimDroneChange() {
  selectedDrone.value = simDrones.value.find(d => d.id === selectedDroneId.value) || null
}

function onPlotClick(plot) {
  if (!plot.plantingId) { showPlantingDialog(0, plot.code); return }
  highlightedPlot.value = plot.code
  if (plot.growthStatus === '异常') {
    // 显示异常详情弹窗
    abnormalDialog.plot = plot
    abnormalDialog.reason = '病害感染，叶片出现异常斑纹'
    abnormalDialog.detectResult = '白粉病疑似'
    abnormalDialog.confidence = 85
    // ponytail: 使用 placeholder 图片表示病害截图
    abnormalDialog.imageUrl = 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" width="400" height="260"><rect width="100%" height="100%" fill="#fdf2f2"/><text x="50%" y="40%" font-size="16" fill="#e57373" text-anchor="middle" font-family="sans-serif">异常植株图片</text><text x="50%" y="55%" font-size="12" fill="#ef9a9a" text-anchor="middle" font-family="sans-serif">叶片出现异常斑纹</text><text x="50%" y="72%" font-size="11" fill="#bbb" text-anchor="middle" font-family="sans-serif">点击"保存图片并智能提问"发起诊断</text></svg>')
    abnormalDialog.visible = true
  }
  detectionResult.value = plot.growthStatus === '异常' ? '发现异常！' : plot.growthStatus === '成熟期' ? '草莓已成熟，可采摘' : plot.growthStatus === '结果期' ? '结果期正常' : '生长正常'
  setTimeout(() => highlightedPlot.value = '', 2000)
}

// ==================== 异常植株弹窗 ====================
import { useRouter } from 'vue-router'
import { saveAbnormalImage } from '@/api/drone'

const router = useRouter()
const abnormalDialog = reactive({ visible: false, plot: null, reason: '', detectResult: '', confidence: 0, imageUrl: '' })

async function saveImageAndAsk() {
  const p = abnormalDialog.plot
  if (!p) return
  try {
    // 保存异常图片到数据库
    await saveAbnormalImage({
      imageUrl: abnormalDialog.imageUrl,
      abnormalReason: abnormalDialog.reason,
      detectResult: abnormalDialog.detectResult,
      confidence: abnormalDialog.confidence,
      growthStage: p.growthStatus,
      areaCode: p.areaCode || '',
      plotCode: p.code,
      plotId: p.plotId,
      plantingRecordId: p.plantingId
    })
  } catch { /* ignore save errors */ }
  abnormalDialog.visible = false
  // 跳转智能问答页面
  router.push({
    path: '/smart-submit',
    query: {
      imageUrl: abnormalDialog.imageUrl,
      abnormalReason: abnormalDialog.reason,
      detectResult: abnormalDialog.detectResult,
      confidence: String(abnormalDialog.confidence),
      plotId: String(p.plotId || ''),
      plantingRecordId: String(p.plantingId || ''),
      growthStage: p.growthStatus || ''
    }
  })
}

function formatNow() {
  const d = new Date()
  const pad = n => String(n).padStart(2,'0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

// ==================== 巡检执行 ====================
function startInspection() {
  if (!routePoints.value.length) return
  if (!selectedDrone.value) { ElMessage.warning('请选择执行无人机'); return }
  inspecting.value = true; paused.value = false; currentPathIndex.value = 0
  progress.value = 0; detectionResult.value = ''; battery.value = Number(selectedDrone.value.batteryLevel || 100)
  // 更新无人机状态为巡检中
  updateDeviceRuntime(selectedDroneId.value, { deviceStatus: 'INSPECTING', cameraStatus: '工作中' }).catch(() => {})
  stepInspection()
  inspectionTimer = setInterval(stepInspection, 1500)
}

function stepInspection() {
  if (paused.value) return
  const i = currentPathIndex.value
  if (i >= routePoints.value.length) { finishInspection(); return }
  const rp = routePoints.value[i]
  currentPathIndex.value = i; progress.value = Math.round((i + 1) / routePoints.value.length * 100)
  activePlot.value = rp.plotCode; currentPlotName.value = rp.plotCode; currentPlotArea.value = rp.areaName
  // 电量随巡检进度递减
  battery.value = Math.max(0, Math.round((1 - (i + 1) / routePoints.value.length) * Number(selectedDrone.value?.batteryLevel || 100)))
  // 更新坐标到当前路线点位置
  const cx = rp.coordinateX || 0, cy = rp.coordinateY || 0, cz = rp.coordinateZ || 0
  updateDeviceRuntime(selectedDroneId.value, { 
    batteryLevel: battery.value, longitude: cx, latitude: cy, altitude: cz, 
    deviceStatus: 'INSPECTING', cameraStatus: '工作中' 
  }).catch(() => {})
  if (selectedDrone.value) { selectedDrone.value.batteryLevel = battery.value; selectedDrone.value.longitude = cx; selectedDrone.value.latitude = cy; selectedDrone.value.altitude = cz }
  if (rp.growthStatus === '异常') detectionResult.value = '发现异常！'
  else if (rp.growthStatus === '成熟期') detectionResult.value = '草莓已成熟，可采摘'
  else detectionResult.value = '生长正常'
  if (i < routePoints.value.length) currentPathIndex.value = i + 1
}

function finishInspection() {
  clearInterval(inspectionTimer); inspecting.value = false
  progress.value = 100; activePlot.value = ''; detectionResult.value = '巡检完成'
  // 更新无人机状态为空闲、归位、累计巡检+1
  if (selectedDroneId.value) {
    const newCount = (selectedDrone.value?.totalInspectionCount || 0) + 1
     updateDeviceRuntime(selectedDroneId.value, { 
       deviceStatus: 'IDLE', cameraStatus: '未启动',
       batteryLevel: battery.value, longitude: 0, latitude: 0, altitude: 0,
       totalInspectionCount: newCount
     }).catch(() => {})
    if (selectedDrone.value) { 
      selectedDrone.value.deviceStatus = 'IDLE'; selectedDrone.value.cameraStatus = '未启动'
      selectedDrone.value.totalInspectionCount = newCount
      selectedDrone.value.lastInspectionTime = formatNow()
      selectedDrone.value.longitude = 0; selectedDrone.value.latitude = 0; selectedDrone.value.altitude = 0
    }
  }
  ElMessage.success('巡检结束 (' + routePoints.value.length + '个点)')
}

function pauseInspection() {
  paused.value = !paused.value; if (!paused.value) stepInspection()
  ElMessage.info(paused.value ? '已暂停' : '已继续')
}

function resetAll() {
  clearInterval(inspectionTimer); clearTimeout(pauseTimer)
  inspecting.value = false; paused.value = false; routeDrawn.value = false; abnormalActive.value = false
  progress.value = 0; currentPathIndex.value = -1; routePoints.value = []
  activePlot.value = ''; currentPlotName.value = ''; currentPlotArea.value = ''; detectionResult.value = ''
  if (selectedDroneId.value) {
    updateDeviceRuntime(selectedDroneId.value, { deviceStatus: 'IDLE', cameraStatus: '未启动', batteryLevel: selectedDrone.value?.batteryLevel }).catch(() => {})
  }
  ElMessage.success('已重置')
}

function triggerAbnormal() {
  // ponytail: 随机选一个正常地块标异常
  const normal = plots.value.filter(p => p.plantingId && p.growthStatus !== '异常')
  if (!normal.length) return ElMessage.warning('没有可标记的异常地块')
  const target = normal[Math.floor(Math.random() * normal.length)]
  target.growthStatus = '异常'; target.statusClass = statusClass('异常')
  updateStrawberryPlanting(target.plantingId, { growthStatus: '异常' }).catch(()=>{})
  abnormalActive.value = true; anomalyCount.value++; detectionResult.value = '发现病害异常：' + target.code
  ElMessage.error('异常模拟：' + target.code)
  setTimeout(() => abnormalActive.value = false, 5000)
}

function updateGrowthBatch() {
  // ponytail: 所有非空地前进一个生长阶段
  const order = ['育苗期','生长期','开花期','结果期','成熟期']
  let changed = 0
  plots.value.forEach(p => {
    if (!p.plantingId || p.growthStatus === '成熟期' || p.growthStatus === '异常') return
    const idx = order.indexOf(p.growthStatus)
    if (idx >= 0 && idx < order.length - 1) {
      const newStatus = order[idx + 1]
      p.growthStatus = newStatus; p.statusClass = statusClass(newStatus); changed++
      // ponytail: 进入成熟期时设置 matureCount = plantCount
      const updateData = { growthStatus: newStatus }
      if (newStatus === '成熟期') updateData.matureCount = p.plantCount
      updateStrawberryPlanting(p.plantingId, updateData).catch(()=>{})
    }
  })
  if (changed) { anomalyCount.value = plots.value.filter(p=>p.growthStatus==='异常').length; ElMessage.success('已更新' + changed + '个地块状态') }
  else ElMessage.info('没有可更新的地块')
  loadPlantingStats()
}

onMounted(() => { loadPlantingStats(); loadTwinStats(); loadRoutes(); loadSimDrones() })

// ==================== 采摘/售卖 ====================
const robots = ref([])
const harvestableList = ref([])
const twinStats = ref(null)
const harvestFormRef = ref(null), saleFormRef = ref(null)

const harvestDialog = reactive({ visible: false, matureInfo: '', maxHarvest: 999, form: { plantingRecordId: null, robotId: null, harvestCount: 10, harvestWeight: 10, qualityGrade: '一级', postHarvestAction: 'fresh_storage', generateTraceCode: false, operatorName: '', remark: '' } })
const saleDialog = reactive({ visible: false, form: { harvestRecordId: null, variety: '', saleWeight: 0, unitPrice: 30, customerName: '', saleChannel: '基地直销', generateTraceCode: false, remark: '' } })

const harvestRules = {
  plantingRecordId: [{required:true, message:'请选择种植记录', trigger:'change'}],
  robotId: [{required:true, message:'请选择机器人', trigger:'change'}],
  harvestCount: [{required:true, message:'请输入采摘数量', trigger:'blur'},{type:'number',min:1,message:'数量必须>0',trigger:'blur'}],
  harvestWeight: [{required:true, message:'请输入采摘重量', trigger:'blur'}]
}
const saleRules = {
  harvestRecordId: [{required:true, message:'请选择采收记录', trigger:'change'}],
  saleWeight: [{required:true, message:'请输入销售重量', trigger:'blur'}],
  unitPrice: [{required:true, message:'请输入单价', trigger:'blur'}]
}

const maturePlots = computed(() => plots.value.filter(p => p.growthStatus === '成熟期' && p.plantingId))  // ponytail: filter saved records with plantingId set; not growing/sold ones

async function showHarvestDialog() {
  const rres = await getAvailableRobots()
  robots.value = rres.data || []
  if (!maturePlots.value.length) { ElMessage.warning('没有可采摘的成熟地块'); return }
  harvestDialog.form = { plantingRecordId: null, robotId: robots.value[0]?.id || null, harvestCount: 10, harvestWeight: 10, qualityGrade: '一级', postHarvestAction: 'fresh_storage', generateTraceCode: false, operatorName: '', remark: '' }
  harvestDialog.matureInfo = ''
  harvestDialog.maxHarvest = 999
  harvestDialog.visible = true
  nextTick(() => harvestFormRef.value?.clearValidate())
}
function onHarvestPlantingChange(id) {
  const p = maturePlots.value.find(pp => pp.plantingId === id)
  if (p) {
    const mature = p.currentPlanting?.matureCount ?? p.plantCount
    const harvested = p.currentPlanting?.harvestedCount ?? 0
    harvestDialog.matureInfo = '成熟 ' + mature + '株 / 已采 ' + harvested + '株'
    harvestDialog.maxHarvest = mature || 0
    if (harvestDialog.form.harvestCount > mature) harvestDialog.form.harvestCount = mature
  }
}
async function handleHarvest() {
  try { await harvestFormRef.value.validate() } catch { return }
  try {
    const res = await createHarvest(harvestDialog.form)
    harvestDialog.visible = false
    const d = res.data
    let msg = '采摘完成！剩余成熟 ' + (d.remainingMatureCount || 0) + ' 株'
    if (d.batchNo) msg += '，批次 ' + d.batchNo + ' 已生成'
    if (d.traceCode) msg += '，溯源码 ' + d.traceCode
    if (d.postHarvestAction === 'processing') msg += '，已进入加工流程'
    ElMessage.success(msg)
    initGrid(); loadPlantingStats(); loadTwinStats()
  } catch (e) { /* interceptor handles */ }
}

async function showSaleDialog() {
  // 加载已有采收记录
  const stats = await getDigitalTwinStatistics()
  if (stats.data) twinStats.value = stats.data
  harvestableList.value = []  // ponytail: load from plots detail or simplified
  ElMessage.warning('请先从地块详情查看采收记录，然后填入销售表单')
  saleDialog.form = { harvestRecordId: null, variety: '', saleWeight: 0, unitPrice: 30, customerName: '', saleChannel: '基地直销', generateTraceCode: false, remark: '' }
  saleDialog.visible = true
  nextTick(() => saleFormRef.value?.clearValidate())
  // ponytail: load plot detail for mature/sold plots
  const candidates = plots.value.filter(p => p.plantingId)
  for (const p of candidates.slice(0, 3)) {
    try {
      const d = await getDigitalTwinPlotDetail(p.plotId)
      if (d.data?.harvests) {
        d.data.harvests.filter(h => h.harvestStatus !== '已售出').forEach(h => {
          harvestableList.value.push({ id: h.id, harvestCode: h.harvestCode, harvestWeight: h.harvestWeight, plantingId: h.plantingRecordId })
        })
      }
    } catch {}
  }
}
function onSaleHarvestChange(id) {
  const h = harvestableList.value.find(x => x.id === id)
  if (h) {
    const p = plots.value.find(pp => pp.plantingId === h.plantingId)
    saleDialog.form.variety = p?.variety || ''
    saleDialog.form.saleWeight = h.harvestWeight || 0
  }
}
async function handleSale() {
  try { await saleFormRef.value.validate() } catch { return }
  try {
    await createSale(saleDialog.form)
    saleDialog.visible = false; ElMessage.success('售卖成功')
    initGrid(); loadPlantingStats(); loadTwinStats()
  } catch (e) { /* interceptor handles */ }
}

async function loadTwinStats() {
  try { const r = await getDigitalTwinStatistics(); if (r.data) twinStats.value = r.data }
  catch {}
}
</script>

<style scoped>
.sim-page { position:relative; margin:-16px; height:calc(100vh - 32px); min-height:600px; display:flex; flex-direction:column; background:#e8f5e9; gap:8px; padding:4px; }
.top-bar { display:flex; justify-content:space-between; align-items:center; padding:8px 16px; background:#fff; border-radius:8px; border:1px solid #c8e6c9; flex-shrink:0; }
.sim-title { margin:0; font-size:18px; color:#2d8c2d; font-weight:700; }
.sim-body { flex:1; display:flex; gap:10px; overflow:hidden; min-height:0; }

.left-panel { width:190px; flex-shrink:0; display:flex; flex-direction:column; overflow-y:auto; }
.ctrl-card { border:1px solid #c8e6c9; }
.card-title { font-weight:bold; color:#2d8c2d; }
.btn-stack { display:flex; flex-direction:column; gap:6px; }
.btn-stack .el-button { justify-content:flex-start; }
.full-w-sel { width: 100%; margin-bottom: 2px; }
.area-list { display:flex; flex-direction:column; gap:6px; }
.area-row { display:flex; align-items:center; gap:8px; padding:6px 8px; border-radius:6px; font-size:13px; }
.area-row.NORMAL { background:#f0fdf0; }
.area-row.ABNORMAL { background:#fff0f0; }
.area-dot { width:10px; height:10px; border-radius:50%; }
.area-meta { font-size:11px; color:#888; margin-left:auto; }

.center-panel { flex:1; min-width:0; overflow-y:auto; padding:8px; background:#fff; border-radius:8px; border:1px solid #c8e6c9; }
.placeholder { display:flex; flex-direction:column; align-items:center; justify-content:center; height:100%; color:#9e9e9e; }
.placeholder p { margin:4px 0; font-size:16px; }
.placeholder .hint { font-size:12px; }
.area-section { margin-bottom:16px; }
.area-label { font-weight:700; color:#2d8c2d; margin-bottom:6px; font-size:14px; padding-left:4px; }
.plot-grid { display:grid; grid-template-columns:repeat(4, 1fr); gap:8px; }
.plot-cell { border-radius:8px; padding:10px 8px; text-align:center; cursor:pointer; transition:all .2s; min-height:70px; display:flex; flex-direction:column; justify-content:center; align-items:center; }
.plot-cell:hover { transform:scale(1.04); box-shadow:0 2px 12px rgba(0,0,0,.12); }
.plot-code { font-weight:700; font-size:12px; color:#555; }
.plot-info { margin-top:4px; }
.plot-variety { font-size:12px; font-weight:600; }
.plot-count { font-size:11px; color:#666; }
.plot-mature { font-size:10px; color:#67c23a; margin-top:1px; }
.plot-status-tag { margin-top:2px; font-size:11px; }
.plot-empty { color:#9e9e9e; font-size:13px; }
.plot-empty { background:#f5f5f5; }
.plot-seedling { background:#e8f5e9; }
.plot-growing { background:#c8e6c9; }
.plot-flower { background:#fce4ec; }
.plot-fruiting { background:#ffccbc; }
.plot-mature { background:#ef5350; color:#fff; }
.plot-mature .plot-code { color:#fff; }
.plot-mature .plot-count { color:#eee; }
.plot-anomaly { background:#b71c1c; color:#fff; animation:pulse .8s infinite alternate; }
.plot-anomaly .plot-code, .plot-anomaly .plot-count { color:#fff; }
.plot-highlight { outline:3px solid #ffc107; z-index:2; }
.plot-active { outline:3px solid #2196f3; z-index:2; }
@keyframes pulse { from{opacity:.8} to{opacity:1} }

.route-bar { margin-top:12px; padding:8px 12px; background:#f5f5f5; border-radius:6px; font-size:13px; line-height:1.8; }
.route-current { background:#2196f3; color:#fff; padding:1px 6px; border-radius:3px; }

.right-panel { width:220px; flex-shrink:0; display:flex; flex-direction:column; overflow-y:auto; gap:8px; }
.info-card { border:1px solid #c8e6c9; }
:deep(.el-card__header) { padding:10px 14px; background:#f6fdf6; border-bottom:1px solid #c8e6c9; }
:deep(.el-card__body) { padding:10px 14px; }
</style>

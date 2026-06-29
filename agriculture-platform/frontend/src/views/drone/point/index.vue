<template>
  <div class="page">
    <div class="page-header"><h2>温室巡检点管理</h2><p>维护真实巡检点，数据供数字孪生巡检使用</p></div>
    <div class="filter-bar">
      <el-select v-model="searchGreenhouseId" placeholder="所属温室" clearable style="width:160px">
        <el-option v-for="g in greenhouses" :key="g.id" :value="g.id" :label="g.greenhouseName || g.landName || g.name || ('温室-' + g.id)" />
      </el-select>
      <el-select v-model="searchArea" placeholder="区域" clearable style="width:140px">
        <el-option v-for="a in areaOptions" :key="a" :value="a" :label="a" />
      </el-select>
      <el-select v-model="searchType" placeholder="类型" clearable style="width:140px">
        <el-option v-for="t in pointTypeOptions" :key="t" :value="t" :label="t" />
      </el-select>
      <el-select v-model="searchEnabled" placeholder="状态" clearable style="width:100px">
        <el-option label="启用" :value="1" /><el-option label="停用" :value="0" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openAdd">新增巡检点</el-button>
    </div>
    <el-card>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="pointCode" label="编码" width="100" />
        <el-table-column prop="pointName" label="名称" min-width="120" />
        <el-table-column label="温室" width="80"><template #default="{row}">{{ row.greenhouseId || '-' }}</template></el-table-column>
        <el-table-column prop="areaName" label="区域" width="110" />
        <el-table-column prop="rowNo" label="种植行" width="70" />
        <el-table-column prop="pointPosition" label="位置" width="80" />
        <el-table-column label="坐标(X,Y,Z)" width="160">
          <template #default="{row}">({{ row.sceneX || 0 }}, {{ row.sceneY || 3.5 }}, {{ row.sceneZ || 0 }})</template>
        </el-table-column>
        <el-table-column prop="pointType" label="类型" width="110">
          <template #default="{row}"><el-tag size="small" :type="typeTag(row.pointType)">{{ row.pointType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险" width="70">
          <template #default="{row}"><el-tag size="small" :type="riskTag(row.riskLevel)">{{ row.riskLevel || '普通' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="启用" width="65">
          <template #default="{row}"><el-tag size="small" :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="60" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total>0" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="p=>{pageNum=p;load()}" />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="620px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="巡检点名称" prop="pointName"><el-input v-model="form.pointName" placeholder="如 A区-1行-环境采集" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="巡检点编码"><el-input v-model="form.pointCode" placeholder="如 PT-A-001" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属温室" prop="greenhouseId">
              <el-select v-model="form.greenhouseId" style="width:100%" placeholder="请选择">
                <el-option v-for="g in greenhouses" :key="g.id" :value="g.id" :label="g.greenhouseName || g.landName || g.name || ('温室-' + g.id)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaName">
              <el-select v-model="form.areaName" style="width:100%" placeholder="请选择" @change="onAreaRowPosChange">
                <el-option v-for="a in areaOptions" :key="a" :value="a" :label="a" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="种植行" prop="rowNo">
              <el-input-number v-model="form.rowNo" :min="1" :max="8" style="width:100%" @change="onAreaRowPosChange" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位位置" prop="pointPosition">
              <el-select v-model="form.pointPosition" style="width:100%" @change="onAreaRowPosChange">
                <el-option v-for="p in posOptions" :key="p" :value="p" :label="p" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序号">
              <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="数字孪生坐标 X">
              <el-input-number v-model="form.sceneX" :precision="2" :disabled="coordAuto" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数字孪生坐标 Z">
              <el-input-number v-model="form.sceneZ" :precision="2" :disabled="coordAuto" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="巡检点类型" prop="pointType">
              <el-select v-model="form.pointType" style="width:100%">
                <el-option v-for="t in pointTypeOptions" :key="t" :value="t" :label="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-select v-model="form.riskLevel" style="width:100%">
                <el-option label="普通" value="普通" /><el-option label="重点" value="重点" /><el-option label="高风险" value="高风险" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="是否启用">
              <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="高度 Y">
              <el-input-number v-model="form.sceneY" :precision="1" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-checkbox v-model="coordAuto" style="margin-top:8px" @change="onAreaRowPosChange">自动生成坐标</el-checkbox>
          </el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPointList, addPoint, updatePoint, deletePoint, generatePointCoordinate } from '@/api/drone'
import { getLands } from '@/api/farm'

const areaOptions = ['A区育苗区', 'B区开花区', 'C区结果区', 'D区异常修复区']
const pointTypeOptions = ['环境采集点', '病害识别点', '生长监测点', '设备检查点', '异常复查点']
const posOptions = ['行首', '行中', '行尾', '设备旁', '异常复查点']

const records = ref([]); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10)
const loading = ref(false); const searchGreenhouseId = ref(null); const searchArea = ref(''); const searchType = ref(''); const searchEnabled = ref(null)
const dialogVisible = ref(false); const dialogTitle = ref('新增巡检点'); const isEdit = ref(false); const formRef = ref(null)
const greenhouses = ref([]); const coordAuto = ref(true)

const defaultForm = () => ({ pointCode:'', pointName:'', greenhouseId:null, areaName:'', rowNo:1, pointPosition:'行中', sortOrder:0, sceneX:0, sceneY:3.5, sceneZ:0, pointType:'环境采集点', riskLevel:'普通', enabled:1, remark:'' })
const form = ref(defaultForm())

const rules = {
  pointName: [{required:true, message:'请输入名称', trigger:'blur'}],
  greenhouseId: [{required:true, message:'请选择温室', trigger:'change'}],
  areaName: [{required:true, message:'请选择区域', trigger:'change'}],
  rowNo: [{required:true, message:'请输入行号', trigger:'blur'}],
  pointPosition: [{required:true, message:'请选择位置', trigger:'change'}],
  pointType: [{required:true, message:'请选择类型', trigger:'change'}],
  riskLevel: [{required:true, message:'请选择风险等级', trigger:'change'}]
}

function typeTag(t) { const m={'环境采集点':'success','病害识别点':'danger','生长监测点':'','设备检查点':'warning','异常复查点':'danger'}; return m[t]||'info' }
function riskTag(r) { const m={'普通':'','重点':'warning','高风险':'danger'}; return m[r]||'' }

async function loadGreenhouses() { try { const r = await getLands({pageNum:1,pageSize:100}); greenhouses.value = r.data?.records || [] } catch(e){} }

async function load() {
  loading.value = true
  try {
    const r = await getPointList({ pageNum: pageNum.value, pageSize: pageSize.value,
      greenhouseId: searchGreenhouseId.value || undefined, areaName: searchArea.value || undefined,
      pointType: searchType.value || undefined })
    let list = r.data.records
    if (searchEnabled.value !== null && searchEnabled.value !== '') list = list.filter(p => p.enabled === searchEnabled.value)
    records.value = list; total.value = r.data.total
  } catch (e) { /* ignore */ } finally { loading.value = false }
}

async function onAreaRowPosChange() {
  if (!coordAuto.value || !form.value.areaName) return
  try {
    const r = await generatePointCoordinate({ areaName: form.value.areaName, rowNo: form.value.rowNo || 1, pointPosition: form.value.pointPosition || '行中' })
    if (r.data) { form.value.sceneX = r.data.sceneX; form.value.sceneY = r.data.sceneY; form.value.sceneZ = r.data.sceneZ }
  } catch (e) { /* ignore */ }
}

function openAdd() {
  isEdit.value = false; dialogTitle.value = '新增巡检点'; form.value = defaultForm(); coordAuto.value = true
  dialogVisible.value = true; nextTick(() => formRef.value?.clearValidate())
}
function openEdit(row) {
  isEdit.value = true; dialogTitle.value = '编辑巡检点'; form.value = { ...row }; coordAuto.value = false
  dialogVisible.value = true; nextTick(() => formRef.value?.clearValidate())
}
async function submit() {
  try { await formRef.value.validate() } catch { return }
  try {
    if (isEdit.value) { await updatePoint(form.value.id, form.value); ElMessage.success('更新成功') }
    else { await addPoint(form.value); ElMessage.success('新增成功') }
    dialogVisible.value = false; load()
  } catch (e) { /* interceptor handles */ }
}
async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该巡检点？', '提示', { type: 'warning' })
  await deletePoint(id); ElMessage.success('删除成功'); load()
}
onMounted(() => { load(); loadGreenhouses() })
</script>
<style scoped>
.page{padding:16px}.page-header h2{margin:0 0 4px}.page-header p{color:#909399;font-size:13px;margin:0}
.filter-bar{display:flex;gap:10px;margin-bottom:16px;flex-wrap:wrap}
</style>

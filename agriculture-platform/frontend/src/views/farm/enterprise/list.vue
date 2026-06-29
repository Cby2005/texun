<template>
  <div class="page">
    <div class="page-header">
      <h2>草莓基地管理</h2>
      <p>管理草莓种植基地基础档案、生产规模与功能配置</p>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索基地名称 / 所属企业 / 负责人" clearable style="width:280px" @keyup.enter="load" />
      <el-select v-model="baseType" placeholder="基地类型" clearable style="width:140px">
        <el-option label="温室基地" value="温室基地" />
        <el-option label="示范基地" value="示范基地" />
        <el-option label="合作社基地" value="合作社基地" />
        <el-option label="科研基地" value="科研基地" />
      </el-select>
      <el-select v-model="status" placeholder="状态" clearable style="width:120px">
        <el-option label="正常" :value="0" />
        <el-option label="停用" :value="1" />
        <el-option label="建设中" :value="2" />
      </el-select>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增基地</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="baseName" label="基地名称" min-width="140" />
        <el-table-column prop="enterpriseName" label="所属企业" min-width="120" />
        <el-table-column label="基地类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="success">{{ row.baseType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="strawberryVariety" label="草莓品种" width="100" />
        <el-table-column prop="managerName" label="负责人" width="90" />
        <el-table-column prop="managerPhone" label="联系电话" width="120" />
        <el-table-column prop="region" label="所在地区" min-width="100" />
        <el-table-column label="总面积" width="80">
          <template #default="{ row }">{{ row.totalArea || 0 }}亩</template>
        </el-table-column>
        <el-table-column label="温室数" width="70">
          <template #default="{ row }">{{ row.greenhouseCount || 0 }}座</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dlg.visible" :title="dlg.isEdit ? '编辑草莓基地' : '新增草莓基地'" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="dlg.form" :rules="rules" label-width="130px">
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="基地名称" prop="baseName">
              <el-input v-model="dlg.form.baseName" placeholder="请输入基地名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基地编码">
              <el-input v-model="dlg.form.baseCode" placeholder="自动生成或手动输入" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属企业/合作社">
              <el-input v-model="dlg.form.enterpriseName" placeholder="请输入所属企业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基地类型">
              <el-select v-model="dlg.form.baseType" style="width:100%" placeholder="请选择基地类型">
                <el-option label="温室基地" value="温室基地" />
                <el-option label="示范基地" value="示范基地" />
                <el-option label="合作社基地" value="合作社基地" />
                <el-option label="科研基地" value="科研基地" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="主栽作物">
              <el-input v-model="dlg.form.mainCrop" placeholder="默认为草莓" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="草莓品种">
              <el-select v-model="dlg.form.strawberryVariety" style="width:100%" placeholder="请选择草莓品种" allow-create filterable>
                <el-option label="红颜" value="红颜" />
                <el-option label="章姬" value="章姬" />
                <el-option label="越秀" value="越秀" />
                <el-option label="妙香" value="妙香" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="基地负责人" prop="managerName">
              <el-input v-model="dlg.form.managerName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="managerPhone">
              <el-input v-model="dlg.form.managerPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">位置信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所在地区" prop="region">
              <el-input v-model="dlg.form.region" placeholder="如：河南省许昌市建安区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细地址" prop="detailAddress">
              <el-input v-model="dlg.form.detailAddress" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
        </el-row>
        </el-row>

        <el-divider content-position="left">生产规模</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="总面积(亩)" prop="totalArea">
              <el-input-number v-model="dlg.form.totalArea" :min="0" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="温室数量(座)" prop="greenhouseCount">
              <el-input-number v-model="dlg.form.greenhouseCount" :min="0" :step="1" :precision="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="种植面积(亩)" prop="plantingArea">
              <el-input-number v-model="dlg.form.plantingArea" :min="0" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="种植模式">
              <el-select v-model="dlg.form.plantingMode" style="width:100%" placeholder="请选择">
                <el-option label="土壤栽培" value="土壤栽培" />
                <el-option label="基质栽培" value="基质栽培" />
                <el-option label="高架栽培" value="高架栽培" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="灌溉方式">
              <el-select v-model="dlg.form.irrigationMode" style="width:100%" placeholder="请选择">
                <el-option label="滴灌" value="滴灌" />
                <el-option label="喷灌" value="喷灌" />
                <el-option label="水肥一体化" value="水肥一体化" />
                <el-option label="人工灌溉" value="人工灌溉" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计年产量(kg)" prop="expectedYield">
              <el-input-number v-model="dlg.form.expectedYield" :min="0" :precision="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">功能配置</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="环境监测">
              <el-switch v-model="dlg.form.enableMonitor" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="病虫害识别">
              <el-switch v-model="dlg.form.enableDiseaseDetect" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="溯源功能">
              <el-switch v-model="dlg.form.enableTrace" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dlg.form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
            <el-radio :value="2">建设中</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getEnterprises, addEnterprise, updateEnterprise, deleteEnterprise } from '../../../api/farm'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const baseType = ref('')
const status = ref(null)

const formRef = ref(null)

const defaultForm = () => ({
  baseName: '',
  baseCode: '',
  enterpriseName: '',
  baseType: '',
  mainCrop: '草莓',
  strawberryVariety: '',
  managerName: '',
  managerPhone: '',
  region: '',
  detailAddress: '',
  totalArea: 0,
  greenhouseCount: 0,
  plantingArea: 0,
  plantingMode: '',
  irrigationMode: '',
  expectedYield: 0,
  enableMonitor: 0,
  enableDiseaseDetect: 0,
  enableTrace: 0,
  status: 0
})

const dlg = reactive({
  visible: false,
  isEdit: false,
  form: defaultForm()
})

const validatePlantingArea = (_rule, value, callback) => {
  const total = Number(dlg.form.totalArea) || 0
  if (Number(value) > total) {
    callback(new Error('种植面积不能大于总面积'))
  } else {
    callback()
  }
}

const rules = {
  baseName: [{ required: true, message: '请输入基地名称', trigger: 'blur' }],
  managerName: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  managerPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [{ required: true, message: '请输入所在地区', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  totalArea: [{ type: 'number', min: 0, message: '面积不能小于0', trigger: 'blur' }],
  greenhouseCount: [{ type: 'number', min: 0, message: '数量不能小于0', trigger: 'blur' }],
  plantingArea: [
    { type: 'number', min: 0, message: '面积不能小于0', trigger: 'blur' },
    { validator: validatePlantingArea, trigger: 'blur' }
  ],
  expectedYield: [{ type: 'number', min: 0, message: '产量不能小于0', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function statusTag(s) {
  return s === 0 ? 'success' : s === 2 ? 'warning' : 'danger'
}

function statusLabel(s) {
  return s === 0 ? '正常' : s === 2 ? '建设中' : '停用'
}

async function load() {
  loading.value = true
  try {
    const r = await getEnterprises({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      baseType: baseType.value || undefined,
      status: status.value != null ? status.value : undefined
    })
    list.value = r.data.records
    total.value = r.data.total
  } catch (e) { /* ignore */ } finally {
    loading.value = false
  }
}

function showAdd() {
  dlg.isEdit = false
  dlg.form = defaultForm()
  dlg.visible = true
  nextTick(() => formRef.value?.clearValidate())
}

function showEdit(row) {
  dlg.isEdit = true
  dlg.form = { ...row }
  dlg.visible = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSave() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    if (dlg.isEdit) {
      await updateEnterprise(dlg.form.id, dlg.form)
    } else {
      await addEnterprise(dlg.form)
    }
    dlg.visible = false
    ElMessage.success('保存成功')
    load()
  } catch (e) {
    // error handled by interceptor
  }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该草莓基地吗？', '提示', { type: 'warning' })
  try {
    await deleteEnterprise(id)
    ElMessage.success('已删除')
    load()
  } catch (e) { /* ignore */ }
}

onMounted(load)
</script>

<style scoped>
.el-divider { margin: 12px 0 16px; }
:deep(.el-divider__text) { font-weight: 600; color: #606266; }
</style>

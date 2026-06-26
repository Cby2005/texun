<template>
  <div class="page">
    <div class="page-header"><h2>批次管理</h2><p>管理溯源产品批次信息</p></div>
    <div class="filter-bar">
      <el-input v-model="productCode" placeholder="产品编码" clearable style="width:180px" @keyup.enter="load" />
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增批次</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="productCode" label="产品编码" width="140" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="productionDate" label="生产日期" width="120" />
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag size="small" :type="row.status===0?'warning':row.status===1?'success':'info'">{{ ['生产中','已完成','已失效'][row.status] }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="160" fixed="right"><template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑批次':'新增批次'" width="500px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="批次号"><el-input v-model="dlg.form.batchNo" /></el-form-item>
        <el-form-item label="产品编码"><el-input v-model="dlg.form.productCode" /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="dlg.form.productName" /></el-form-item>
        <el-form-item label="数量"><el-input v-model="dlg.form.quantity" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="dlg.form.unit" /></el-form-item>
        <el-form-item label="生产日期"><el-date-picker v-model="dlg.form.productionDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="dlg.form.status"><el-option :value="0" label="生产中"/><el-option :value="1" label="已完成"/><el-option :value="2" label="已失效"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBatches, addBatch, updateBatch, deleteBatch } from '../../../api/trace'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10), productCode = ref('')
const dlg = reactive({ visible: false, isEdit: false, form: { batchNo:'',productCode:'',productName:'',quantity:'',unit:'',productionDate:'',status:0 } })

async function load() {
  loading.value = true
  try { const r = await getBatches({ pageNum: pageNum.value, pageSize: pageSize.value, productCode: productCode.value || undefined }); list.value = r.data.records; total.value = r.data.total } catch(e){} finally{loading.value=false}
}
function showAdd() { dlg.isEdit = false; dlg.form = { batchNo:'',productCode:'',productName:'',quantity:'',unit:'',productionDate:'',status:0 }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateBatch(dlg.form.id, dlg.form); else await addBatch(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteBatch(id); ElMessage.success('已删除'); load() } catch(e){} }

onMounted(load)
</script>

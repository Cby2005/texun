<template>
  <div class="page">
    <div class="page-header"><h2>仓储记录管理</h2><p>管理农产品仓储出入库记录</p></div>
    <div class="filter-bar">
      <el-input v-model="batchNo" placeholder="批次号" clearable style="width:200px" @keyup.enter="load" />
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增记录</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="quantity" label="变动前数量" width="110" />
        <el-table-column prop="changeType" label="变动类型" width="100">
          <template #default="{row}"><el-tag size="small" :type="row.changeType==='入库'?'success':'warning'">{{ row.changeType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="changeQuantity" label="变动数量" width="100" />
        <el-table-column prop="afterQuantity" label="变动后数量" width="110" />
        <el-table-column prop="changeTime" label="变动时间" width="150" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑仓储记录':'新增仓储记录'" width="500px">
      <el-form :model="dlg.form" label-width="100px">
        <el-form-item label="批次号"><el-input v-model="dlg.form.batchNo" /></el-form-item>
        <el-form-item label="变动前数量"><el-input-number v-model="dlg.form.quantity" style="width:100%" /></el-form-item>
        <el-form-item label="变动类型"><el-select v-model="dlg.form.changeType" style="width:100%"><el-option label="入库" value="入库"/><el-option label="出库" value="出库"/></el-select></el-form-item>
        <el-form-item label="变动数量"><el-input-number v-model="dlg.form.changeQuantity" style="width:100%" /></el-form-item>
        <el-form-item label="变动后数量"><el-input-number v-model="dlg.form.afterQuantity" style="width:100%" /></el-form-item>
        <el-form-item label="变动时间"><el-date-picker v-model="dlg.form.changeTime" type="datetime" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="dlg.form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRecords, addRecord, updateRecord, deleteRecord } from '../../../api/trace'
import { ElMessage, ElMessageBox } from 'element-plus'

const type = 'storage'
const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10), batchNo = ref('')
const dlg = reactive({ visible: false, isEdit: false, form: { batchNo:'', quantity:0, changeType:'入库', changeQuantity:0, afterQuantity:0, changeTime:'', remark:'' } })

async function load() {
  loading.value = true
  try { const r = await getRecords(type, { pageNum: pageNum.value, pageSize: pageSize.value, batchNo: batchNo.value || undefined }); list.value = r.data.records; total.value = r.data.total } catch(e){} finally{loading.value=false}
}
function showAdd() { dlg.isEdit = false; dlg.form = { batchNo:'', quantity:0, changeType:'入库', changeQuantity:0, afterQuantity:0, changeTime:'', remark:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }
async function handleSave() {
  try { if (dlg.isEdit) await updateRecord(type, dlg.form.id, dlg.form); else await addRecord(type, dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteRecord(type, id); ElMessage.success('已删除'); load() } catch(e){} }
onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-header"><h2>种植记录管理</h2><p>管理农产品种植过程记录</p></div>
    <div class="filter-bar">
      <el-input v-model="batchNo" placeholder="批次号" clearable style="width:200px" @keyup.enter="load" />
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增记录</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="sowTime" label="播种时间" width="150" />
        <el-table-column prop="fertilizerRecord" label="施肥记录" />
        <el-table-column prop="pesticideRecord" label="农药记录" />
        <el-table-column prop="irrigationRecord" label="灌溉记录" />
        <el-table-column prop="soilTemperature" label="土壤温度" width="100" />
        <el-table-column prop="soilHumidity" label="土壤湿度" width="100" />
        <el-table-column prop="harvestTime" label="收获时间" width="150" />
        <el-table-column prop="responsiblePerson" label="负责人" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑种植记录':'新增种植记录'" width="600px">
      <el-form :model="dlg.form" label-width="100px">
        <el-form-item label="批次号"><el-input v-model="dlg.form.batchNo" /></el-form-item>
        <el-form-item label="播种时间"><el-date-picker v-model="dlg.form.sowTime" type="datetime" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="施肥记录"><el-input v-model="dlg.form.fertilizerRecord" /></el-form-item>
        <el-form-item label="农药记录"><el-input v-model="dlg.form.pesticideRecord" /></el-form-item>
        <el-form-item label="灌溉记录"><el-input v-model="dlg.form.irrigationRecord" /></el-form-item>
        <el-form-item label="土壤温度"><el-input v-model="dlg.form.soilTemperature" placeholder="例：25°C" /></el-form-item>
        <el-form-item label="土壤湿度"><el-input v-model="dlg.form.soilHumidity" placeholder="例：65%" /></el-form-item>
        <el-form-item label="收获时间"><el-date-picker v-model="dlg.form.harvestTime" type="datetime" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model="dlg.form.responsiblePerson" /></el-form-item>
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

const type = 'production'
const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10), batchNo = ref('')
const dlg = reactive({ visible: false, isEdit: false, form: { batchNo:'', sowTime:'', fertilizerRecord:'', pesticideRecord:'', irrigationRecord:'', soilTemperature:'', soilHumidity:'', harvestTime:'', responsiblePerson:'', remark:'' } })

async function load() {
  loading.value = true
  try { const r = await getRecords(type, { pageNum: pageNum.value, pageSize: pageSize.value, batchNo: batchNo.value || undefined }); list.value = r.data.records; total.value = r.data.total } catch(e){} finally{loading.value=false}
}
function showAdd() { dlg.isEdit = false; dlg.form = { batchNo:'', sowTime:'', fertilizerRecord:'', pesticideRecord:'', irrigationRecord:'', soilTemperature:'', soilHumidity:'', harvestTime:'', responsiblePerson:'', remark:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }
async function handleSave() {
  try { if (dlg.isEdit) await updateRecord(type, dlg.form.id, dlg.form); else await addRecord(type, dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteRecord(type, id); ElMessage.success('已删除'); load() } catch(e){} }
onMounted(load)
</script>

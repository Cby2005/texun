<template>
  <div class="page">
    <div class="page-header"><h2>物流记录管理</h2><p>管理农产品运输物流记录</p></div>
    <div class="filter-bar">
      <el-input v-model="batchNo" placeholder="批次号" clearable style="width:200px" @keyup.enter="load" />
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增记录</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="logisticsCompany" label="物流公司" />
        <el-table-column prop="transportVehicle" label="运输车辆" width="120" />
        <el-table-column prop="driverName" label="司机" width="100" />
        <el-table-column prop="driverPhone" label="联系电话" width="120" />
        <el-table-column prop="fromAddress" label="发货地" />
        <el-table-column prop="toAddress" label="收货地" />
        <el-table-column prop="shipTime" label="发货时间" width="150" />
        <el-table-column prop="arrivalTime" label="到达时间" width="150" />
        <el-table-column prop="logisticsStatus" label="物流状态" width="100">
          <template #default="{row}"><el-tag size="small" :type="row.logisticsStatus==='已签收'?'success':row.logisticsStatus==='运输中'?'warning':'info'">{{ row.logisticsStatus }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑物流记录':'新增物流记录'" width="600px">
      <el-form :model="dlg.form" label-width="100px">
        <el-form-item label="批次号"><el-input v-model="dlg.form.batchNo" /></el-form-item>
        <el-form-item label="物流公司"><el-input v-model="dlg.form.logisticsCompany" /></el-form-item>
        <el-form-item label="运输车辆"><el-input v-model="dlg.form.transportVehicle" /></el-form-item>
        <el-form-item label="司机姓名"><el-input v-model="dlg.form.driverName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="dlg.form.driverPhone" /></el-form-item>
        <el-form-item label="发货地址"><el-input v-model="dlg.form.fromAddress" /></el-form-item>
        <el-form-item label="收货地址"><el-input v-model="dlg.form.toAddress" /></el-form-item>
        <el-form-item label="发货时间"><el-date-picker v-model="dlg.form.shipTime" type="datetime" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="到达时间"><el-date-picker v-model="dlg.form.arrivalTime" type="datetime" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="运输温度"><el-input v-model="dlg.form.transportTemperature" placeholder="例：8°C" /></el-form-item>
        <el-form-item label="运输湿度"><el-input v-model="dlg.form.transportHumidity" placeholder="例：60%" /></el-form-item>
        <el-form-item label="物流状态"><el-select v-model="dlg.form.logisticsStatus" style="width:100%"><el-option label="待发货" value="待发货"/><el-option label="运输中" value="运输中"/><el-option label="已签收" value="已签收"/></el-select></el-form-item>
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

const type = 'logistics'
const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10), batchNo = ref('')
const dlg = reactive({ visible: false, isEdit: false, form: { batchNo:'', logisticsCompany:'', transportVehicle:'', driverName:'', driverPhone:'', fromAddress:'', toAddress:'', shipTime:'', arrivalTime:'', transportTemperature:'', transportHumidity:'', logisticsStatus:'待发货', remark:'' } })

async function load() {
  loading.value = true
  try { const r = await getRecords(type, { pageNum: pageNum.value, pageSize: pageSize.value, batchNo: batchNo.value || undefined }); list.value = r.data.records; total.value = r.data.total } catch(e){} finally{loading.value=false}
}
function showAdd() { dlg.isEdit = false; dlg.form = { batchNo:'', logisticsCompany:'', transportVehicle:'', driverName:'', driverPhone:'', fromAddress:'', toAddress:'', shipTime:'', arrivalTime:'', transportTemperature:'', transportHumidity:'', logisticsStatus:'待发货', remark:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }
async function handleSave() {
  try { if (dlg.isEdit) await updateRecord(type, dlg.form.id, dlg.form); else await addRecord(type, dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteRecord(type, id); ElMessage.success('已删除'); load() } catch(e){} }
onMounted(load)
</script>

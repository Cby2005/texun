<template>
  <div class="page">
    <div class="page-header"><h2>设备管理</h2><p>管理农场传感器和控制器设备</p></div>
    <div class="filter-bar">
      <el-select v-model="deviceType" placeholder="设备类型" clearable style="width:150px" @change="load">
        <el-option value="IRRIGATION" label="灌溉"/><el-option value="LIGHT" label="补光"/><el-option value="FAN" label="通风"/>
        <el-option value="HEATER" label="加热"/><el-option value="SENSOR" label="传感器"/>
      </el-select>
      <el-select v-model="state" placeholder="状态" clearable style="width:150px" @change="load">
        <el-option value="RUNNING" label="运行中"/><el-option value="STANDBY" label="待机"/><el-option value="MAINTENANCE" label="维护中"/><el-option value="FAULT" label="故障"/>
      </el-select>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增设备</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="deviceCode" label="设备编码" width="130" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="deviceType" label="类型" width="100"><template #default="{row}"><el-tag size="small">{{ {IRRIGATION:'灌溉',LIGHT:'补光',FAN:'通风',HEATER:'加热',SENSOR:'传感器'}[row.deviceType]||row.deviceType }}</el-tag></template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="row.state==='RUNNING'?'success':row.state==='FAULT'?'danger':row.state==='MAINTENANCE'?'warning':'info'" size="small">{{ {RUNNING:'运行中',STANDBY:'待机',MAINTENANCE:'维护',FAULT:'故障'}[row.state]||row.state }}</el-tag></template></el-table-column>
        <el-table-column prop="area" label="区域" width="120" />
        <el-table-column label="在线" width="70"><template #default="{row}"><el-tag :type="row.online?'success':'info'" size="small">{{row.online?'在线':'离线'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑设备':'新增设备'" width="500px">
      <el-form :model="dlg.form" label-width="100px">
        <el-form-item label="设备编码"><el-input v-model="dlg.form.deviceCode" /></el-form-item>
        <el-form-item label="设备名称"><el-input v-model="dlg.form.deviceName" /></el-form-item>
        <el-form-item label="设备类型"><el-select v-model="dlg.form.deviceType"><el-option value="IRRIGATION" label="灌溉"/><el-option value="LIGHT" label="补光"/><el-option value="FAN" label="通风"/><el-option value="HEATER" label="加热"/><el-option value="SENSOR" label="传感器"/></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="dlg.form.state"><el-option value="STANDBY" label="待机"/><el-option value="RUNNING" label="运行中"/><el-option value="MAINTENANCE" label="维护中"/><el-option value="FAULT" label="故障"/></el-select></el-form-item>
        <el-form-item label="区域"><el-input v-model="dlg.form.area" /></el-form-item>
        <el-form-item label="在线"><el-switch v-model="dlg.form.online" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getDevices, addDevice, updateDevice } from '../../../api/farm'
import { ElMessage } from 'element-plus'

const list = ref([]), deviceType = ref(null), state = ref(null)
const dlg = reactive({ visible: false, isEdit: false, form: { deviceCode:'',deviceName:'',deviceType:'SENSOR',state:'STANDBY',area:'',online:1 } })

async function load() { try { const r = await getDevices({ deviceType: deviceType.value, state: state.value }); list.value = r.data.records } catch(e){} }
function showAdd() { dlg.isEdit = false; dlg.form = { deviceCode:'',deviceName:'',deviceType:'SENSOR',state:'STANDBY',area:'',online:1 }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateDevice(dlg.form.id, dlg.form); else await addDevice(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { try { ElMessage.warning('暂未开放删除') } catch(e){} }

onMounted(load)
</script>

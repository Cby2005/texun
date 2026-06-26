<template>
  <div class="page">
    <div class="page-header"><h2>用户管理</h2><p>管理平台所有用户账号</p></div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称/手机号" clearable style="width:240px" @clear="load" @keyup.enter="load" />
      <el-select v-model="userType" placeholder="用户类型" clearable style="width:160px" @change="load">
        <el-option :value="0" label="超级管理员" /><el-option :value="1" label="农场管理员" />
        <el-option :value="2" label="溯源企业" /><el-option :value="3" label="农技专家" />
        <el-option :value="4" label="普通农户" /><el-option :value="5" label="审核员" />
      </el-select>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon>搜索</el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增用户</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column label="用户类型" width="110">
          <template #default="{row}"><el-tag size="small" :type="typeTag(row.userType)">{{ typeLabel(row.userType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag size="small" :type="row.status===0?'success':'danger'">{{ row.status===0?'正常':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑用户':'新增用户'" width="500px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="dlg.form.username" :disabled="dlg.isEdit" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="dlg.form.password" type="password" :placeholder="dlg.isEdit?'留空不修改':''" show-password /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="dlg.form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="dlg.form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="dlg.form.email" /></el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="dlg.form.userType">
            <el-option :value="0" label="超级管理员" /><el-option :value="1" label="农场管理员" />
            <el-option :value="2" label="溯源企业" /><el-option :value="3" label="农技专家" />
            <el-option :value="4" label="普通农户" /><el-option :value="5" label="审核员" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dlg.form.status"><el-radio :value="0">正常</el-radio><el-radio :value="1">禁用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUsers, addUser, updateUser, deleteUser } from '../../../api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10)
const keyword = ref(''), userType = ref(null)

const dlg = reactive({ visible: false, isEdit: false, form: { username:'',password:'',nickname:'',phone:'',email:'',userType:4,status:0 } })

const typeLabel = v => ['超级管理员','农场管理员','溯源企业','农技专家','普通农户','审核员'][v] || ''
const typeTag = v => ['danger','warning','primary','success','','info'][v] || ''

async function load() {
  loading.value = true
  try {
    const r = await getUsers({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined, userType: userType.value })
    list.value = r.data.records; total.value = r.data.total
  } catch(e){} finally{loading.value=false}
}

function showAdd() { dlg.isEdit = false; dlg.form = { username:'',password:'',nickname:'',phone:'',email:'',userType:4,status:0 }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row, password:'' }; dlg.visible = true }

async function handleSave() {
  try {
    if (dlg.isEdit) { await updateUser(dlg.form.id, dlg.form) } else { await addUser(dlg.form) }
    dlg.visible = false; ElMessage.success('保存成功'); load()
  } catch(e){}
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除此用户？', '提示', { type: 'warning' })
  try { await deleteUser(id); ElMessage.success('已删除'); load() } catch(e){}
}

onMounted(load)
</script>

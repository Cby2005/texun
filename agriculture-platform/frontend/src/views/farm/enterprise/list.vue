<template>
  <div class="page">
    <div class="page-header"><h2>农场管理</h2><p>管理农场/企业基本信息</p></div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索农场名称" clearable style="width:240px" @keyup.enter="load" />
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="农场名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag size="small" :type="row.status===0?'success':'danger'">{{ row.status===0?'正常':'停用' }}</el-tag></template>
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

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑农场':'新增农场'" width="550px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="农场名称"><el-input v-model="dlg.form.name" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="dlg.form.address" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="dlg.form.contactName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="dlg.form.contactPhone" /></el-form-item>
        <el-form-item label="主营"><el-input v-model="dlg.form.mainBusiness" /></el-form-item>
        <el-form-item label="规模"><el-input v-model="dlg.form.scale" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dlg.form.status"><el-radio :value="0">正常</el-radio><el-radio :value="1">停用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getEnterprises, addEnterprise, updateEnterprise, deleteEnterprise } from '../../../api/farm'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10), keyword = ref('')
const dlg = reactive({ visible: false, isEdit: false, form: { name:'',address:'',contactName:'',contactPhone:'',mainBusiness:'',scale:'',status:0 } })

async function load() {
  loading.value = true
  try {
    const r = await getEnterprises({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined })
    list.value = r.data.records; total.value = r.data.total
  } catch(e){} finally{loading.value = false}
}

function showAdd() { dlg.isEdit = false; dlg.form = { name:'',address:'',contactName:'',contactPhone:'',mainBusiness:'',scale:'',status:0 }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateEnterprise(dlg.form.id, dlg.form); else await addEnterprise(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteEnterprise(id); ElMessage.success('已删除'); load() } catch(e){} }

onMounted(load)
</script>

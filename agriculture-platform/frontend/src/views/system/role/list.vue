<template>
  <div class="page">
    <div class="page-header"><h2>角色管理</h2><p>管理系统角色及权限</p></div>
    <div class="filter-bar"><el-button type="primary" @click="showAdd"><el-icon><Plus /></el-icon>新增角色</el-button></div>
    <el-card>
      <el-table :data="list" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色名称" width="140" />
        <el-table-column prop="code" label="角色编码" width="140" />
        <el-table-column prop="roleKey" label="权限标识" width="140" />
        <el-table-column prop="roleSort" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag size="small" :type="row.status===0?'success':'danger'">{{ row.status===0?'正常':'停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑角色':'新增角色'" width="500px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="角色名称"><el-input v-model="dlg.form.name" /></el-form-item>
        <el-form-item label="角色编码"><el-input v-model="dlg.form.code" /></el-form-item>
        <el-form-item label="权限标识"><el-input v-model="dlg.form.roleKey" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="dlg.form.roleSort" /></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="dlg.form.status"><el-radio :value="0">正常</el-radio><el-radio :value="1">停用</el-radio></el-radio-group></el-form-item>
        <el-form-item label="描述"><el-input v-model="dlg.form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRoles, addRole, updateRole, deleteRole } from '../../../api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const dlg = reactive({ visible: false, isEdit: false, form: { name:'',code:'',roleKey:'',roleSort:0,status:0,description:'' } })

async function load() { try { const r = await getRoles(); list.value = r.data } catch(e){} }
function showAdd() { dlg.isEdit = false; dlg.form = { name:'',code:'',roleKey:'',roleSort:0,status:0,description:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateRole(dlg.form.id, dlg.form); else await addRole(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？','提示',{type:'warning'})
  try { await deleteRole(id); ElMessage.success('已删除'); load() } catch(e){}
}

onMounted(load)
</script>

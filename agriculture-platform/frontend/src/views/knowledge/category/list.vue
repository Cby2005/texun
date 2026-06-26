<template>
  <div class="page">
    <div class="page-header"><h2>技术分类</h2><p>管理农业技术文章分类</p></div>
    <div class="filter-bar"><el-button type="primary" @click="showAdd"><el-icon><Plus /></el-icon>新增分类</el-button></div>
    <el-card>
      <el-table :data="list" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="code" label="编码" width="140" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="160"><template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑分类':'新增分类'" width="450px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="dlg.form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="dlg.form.code" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="dlg.form.sortOrder" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="dlg.form.description" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCategories, addCategory, updateCategory, deleteCategory } from '../../../api/knowledge'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const dlg = reactive({ visible: false, isEdit: false, form: { name:'',code:'',sortOrder:0,description:'' } })

async function load() { try { const r = await getCategories(); list.value = r.data } catch(e){} }
function showAdd() { dlg.isEdit = false; dlg.form = { name:'',code:'',sortOrder:0,description:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateCategory(dlg.form.id, dlg.form); else await addCategory(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteCategory(id); ElMessage.success('已删除'); load() } catch(e){} }

onMounted(load)
</script>

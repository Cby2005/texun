<template>
  <div class="page">
    <div class="page-header"><h2>菜单管理</h2><p>管理系统菜单及权限</p></div>
    <div class="filter-bar"><el-button type="primary" @click="showAdd">新增菜单</el-button></div>
    <el-card>
      <el-table :data="list" row-key="menuId" stripe default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" />
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column label="菜单类型" width="80">
          <template #default="{row}"><el-tag size="small">{{ row.menuType==='M'?'目录':row.menuType==='C'?'菜单':'按钮' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="perms" label="权限标识" width="180" />
        <el-table-column prop="orderNum" label="排序" width="60" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.menuId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑菜单':'新增菜单'" width="500px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="菜单名称"><el-input v-model="dlg.form.menuName" /></el-form-item>
        <el-form-item label="父菜单">
          <el-tree-select v-model="dlg.form.parentId" :data="treeOpts" :props="{label:'menuName',value:'menuId',children:'children'}" check-strictly clearable style="width:100%" />
        </el-form-item>
        <el-form-item label="菜单类型"><el-select v-model="dlg.form.menuType"><el-option value="M" label="目录"/><el-option value="C" label="菜单"/><el-option value="F" label="按钮"/></el-select></el-form-item>
        <el-form-item label="路由路径" v-if="dlg.form.menuType!=='F'"><el-input v-model="dlg.form.path" /></el-form-item>
        <el-form-item label="权限标识" v-if="dlg.form.menuType!=='M'"><el-input v-model="dlg.form.perms" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="dlg.form.orderNum" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="dlg.form.icon" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMenus, getMenuTree, addMenu, updateMenu, deleteMenu } from '../../../api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), treeOpts = ref([])
const dlg = reactive({ visible: false, isEdit: false, form: { menuName:'',parentId:0,menuType:'C',path:'',perms:'',orderNum:0,icon:'' } })

async function load() {
  try {
    const [r1, r2] = await Promise.all([getMenuTree(), getMenus()])
    list.value = r1.data; treeOpts.value = [{menuId:0, menuName:'根目录', children: r1.data}]
  } catch(e){}
}

function showAdd() { dlg.isEdit = false; dlg.form = { menuName:'',parentId:0,menuType:'C',path:'',perms:'',orderNum:0,icon:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateMenu(dlg.form.menuId, dlg.form); else await addMenu(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？','提示',{type:'warning'})
  try { await deleteMenu(id); ElMessage.success('已删除'); load() } catch(e){}
}

onMounted(load)
</script>

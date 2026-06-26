<template>
  <div class="page">
    <div class="page-header"><h2>地块管理</h2><p>管理大棚/鱼塘/大田/仓库等生产地块</p></div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索地块名" clearable style="width:200px" />
      <el-select v-model="type" placeholder="地块类型" clearable style="width:140px"><el-option :value="0" label="大棚"/><el-option :value="1" label="鱼塘"/><el-option :value="2" label="大田"/><el-option :value="3" label="仓库"/></el-select>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="displayName" label="名称" />
        <el-table-column label="类型" width="80"><template #default="{row}"><el-tag size="small">{{ ['大棚','鱼塘','大田','仓库'][row.type] }}</el-tag></template></el-table-column>
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="location" label="位置" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag size="small" :type="row.status===0?'success':'danger'">{{ row.status===0?'使用中':'停用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="160" fixed="right"><template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑地块':'新增地块'" width="550px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="所属农场">
          <el-select v-model="dlg.form.enterpriseId" style="width:100%">
            <el-option v-for="e in enterprises" :key="e.id" :value="e.id" :label="e.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="编号"><el-input-number v-model="dlg.form.number" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="dlg.form.type"><el-option :value="0" label="大棚"/><el-option :value="1" label="鱼塘"/><el-option :value="2" label="大田"/><el-option :value="3" label="仓库"/></el-select></el-form-item>
        <el-form-item label="名称"><el-input v-model="dlg.form.displayName" /></el-form-item>
        <el-form-item label="面积(㎡)"><el-input v-model="dlg.form.area" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="dlg.form.location" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLands, addLand, updateLand, deleteLand, getAllEnterprises } from '../../../api/farm'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10)
const keyword = ref(''), type = ref(null), enterprises = ref([])
const dlg = reactive({ visible: false, isEdit: false, form: { enterpriseId:null,number:1,type:0,displayName:'',area:'',location:'' } })

async function load() {
  loading.value = true
  try {
    const r = await getLands({ pageNum: pageNum.value, pageSize: pageSize.value, enterpriseId: undefined, type: type.value })
    list.value = r.data.records; total.value = r.data.total
  } catch(e){} finally { loading.value = false }
}

async function showAdd() { dlg.isEdit = false; dlg.form = { enterpriseId: null, number: 1, type: 0, displayName:'', area:'', location:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateLand(dlg.form.id, dlg.form); else await addLand(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteLand(id); ElMessage.success('已删除'); load() } catch(e){} }

onMounted(async () => { await load(); try { const r = await getAllEnterprises(); enterprises.value = r.data } catch(e){} })
</script>

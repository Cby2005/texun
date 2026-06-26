<template>
  <div class="page">
    <div class="page-header"><h2>产品管理</h2><p>管理溯源产品信息</p></div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索产品名/编码" clearable style="width:240px" @keyup.enter="load" />
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button>
      <el-button type="success" @click="showAdd"><el-icon><Plus /></el-icon>新增产品</el-button>
    </div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="productCode" label="产品编码" width="140" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column prop="specification" label="规格" width="100" />
        <el-table-column prop="origin" label="产地" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button></template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px" background layout="total,prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />
    </el-card>

    <el-dialog v-model="dlg.visible" :title="dlg.isEdit?'编辑产品':'新增产品'" width="550px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="产品编码"><el-input v-model="dlg.form.productCode" /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="dlg.form.productName" /></el-form-item>
        <el-form-item label="类别"><el-input v-model="dlg.form.category" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="dlg.form.specification" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="dlg.form.unit" /></el-form-item>
        <el-form-item label="产地"><el-input v-model="dlg.form.origin" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="dlg.form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg.visible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProducts, addProduct, updateProduct, deleteProduct } from '../../../api/trace'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), total = ref(0), pageNum = ref(1), pageSize = ref(10), keyword = ref('')
const dlg = reactive({ visible: false, isEdit: false, form: { productCode:'',productName:'',category:'',specification:'',unit:'',origin:'',description:'' } })

async function load() {
  loading.value = true
  try { const r = await getProducts({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined }); list.value = r.data.records; total.value = r.data.total } catch(e){} finally{loading.value=false}
}
function showAdd() { dlg.isEdit = false; dlg.form = { productCode:'',productName:'',category:'',specification:'',unit:'',origin:'',description:'' }; dlg.visible = true }
function showEdit(row) { dlg.isEdit = true; dlg.form = { ...row }; dlg.visible = true }

async function handleSave() {
  try { if (dlg.isEdit) await updateProduct(dlg.form.id, dlg.form); else await addProduct(dlg.form); dlg.visible=false; ElMessage.success('保存成功'); load() } catch(e){}
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); try { await deleteProduct(id); ElMessage.success('已删除'); load() } catch(e){} }

onMounted(load)
</script>

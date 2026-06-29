<template>
  <div class="mall-admin">
    <h2>积分商城管理</h2>

    <el-tabs v-model="tab">
      <el-tab-pane label="商品管理" name="goods">
        <div style="margin-bottom:12px">
          <el-button type="primary" @click="showGoodsDialog()">+ 新增商品</el-button>
        </div>
        <el-table :data="allGoods" border stripe size="small" v-loading="loading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="goodsName" label="商品名称" />
          <el-table-column prop="goodsCategory" label="分类" width="100" />
          <el-table-column prop="requiredPoints" label="所需积分" width="100" />
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{row}"><el-tag :type="row.status==='ON_SALE'?'success':row.status==='SOLD_OUT'?'danger':'info'">{{ row.status==='ON_SALE'?'在售':row.status==='SOLD_OUT'?'售罄':'下架' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="240">
            <template #default="{row}">
              <el-button size="small" @click="showGoodsDialog(row)">编辑</el-button>
              <el-button size="small" :type="row.status==='ON_SALE'?'warning':'success'" @click="toggleGoods(row)">
                {{ row.status==='ON_SALE' ? '下架' : '上架' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="兑换订单" name="orders">
        <el-table :data="allOrders" border stripe size="small">
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="goodsName" label="商品" />
          <el-table-column prop="totalPoints" label="积分" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{row}">{{ { PENDING:'待处理', PROCESSING:'处理中', COMPLETED:'已完成', CANCELLED:'已取消' }[row.orderStatus] }}</template>
          </el-table-column>
          <el-table-column prop="exchangeTime" label="时间" width="170" />
          <el-table-column label="操作" width="160">
            <template #default="{row}">
              <el-button v-if="row.orderStatus==='PENDING'" size="small" type="success" @click="updateOrderStatus(row.id,'COMPLETED')">完成</el-button>
              <el-button v-if="row.orderStatus==='PENDING'" size="small" type="danger" @click="updateOrderStatus(row.id,'CANCELLED')">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 商品编辑弹窗 -->
    <el-dialog v-model="goodsDialog.visible" :title="goodsDialog.isEdit ? '编辑商品' : '新增商品'" width="500px" destroy-on-close>
      <el-form :model="goodsDialog.form" label-width="90px">
        <el-form-item label="商品名称"><el-input v-model="goodsDialog.form.goodsName" /></el-form-item>
        <el-form-item label="分类"><el-select v-model="goodsDialog.form.goodsCategory">
          <el-option label="资料包" value="资料包" /><el-option label="课程" value="课程" />
          <el-option label="优惠券" value="优惠券" /><el-option label="工具用品" value="工具用品" /><el-option label="其他" value="其他" />
        </el-select></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="goodsDialog.form.goodsImage" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="goodsDialog.form.goodsDesc" type="textarea" /></el-form-item>
        <el-form-item label="所需积分"><el-input-number v-model="goodsDialog.form.requiredPoints" :min="1" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="goodsDialog.form.stock" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="goodsDialog.visible=false">取消</el-button>
        <el-button type="primary" @click="handleSaveGoods">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { allMallGoods, createMallGoods, updateMallGoods, updateGoodsStatus, allExchangeOrders, updateOrderStatus as setOrderStatus } from '@/api/quiz'

const tab = ref('goods')
const allGoods = ref([])
const allOrders = ref([])
const loading = ref(false)

const goodsDialog = reactive({ visible: false, isEdit: false, form: {} })

async function loadGoods() {
  loading.value = true
  try { const r = await allMallGoods(); allGoods.value = r.data || [] } catch {}
  loading.value = false
}
async function loadOrders() {
  try { const r = await allExchangeOrders(); allOrders.value = r.data || [] } catch {}
}

function showGoodsDialog(row) {
  goodsDialog.isEdit = !!row
  goodsDialog.form = row ? { ...row } : { goodsName: '', goodsCategory: '资料包', goodsImage: '', goodsDesc: '', requiredPoints: 100, stock: 100 }
  goodsDialog.visible = true
}
async function handleSaveGoods() {
  try {
    if (goodsDialog.isEdit) { await updateMallGoods(goodsDialog.form.id, goodsDialog.form); ElMessage.success('已更新') }
    else { await createMallGoods(goodsDialog.form); ElMessage.success('已创建') }
    goodsDialog.visible = false; loadGoods()
  } catch (e) { ElMessage.error(e.message) }
}
async function toggleGoods(row) {
  const newStatus = row.status === 'ON_SALE' ? 'OFF_SALE' : 'ON_SALE'
  try { await updateGoodsStatus(row.id, newStatus); ElMessage.success('状态已更新'); loadGoods() } catch {}
}
async function updateOrderStatus(id, status) {
  try { await setOrderStatus(id, status); ElMessage.success('状态已更新'); loadOrders() } catch {}
}

onMounted(() => { loadGoods(); loadOrders() })
</script>

<style scoped>
.mall-admin { padding: 20px; }
.mall-admin h2 { margin: 0 0 16px; }
</style>

<template>
  <div class="points-mall">
    <div class="mall-header">
      <div>
        <h2>积分商城</h2>
        <span class="mall-sub">使用答题积分兑换实用好物</span>
      </div>
      <div class="mall-actions">
        <span class="points-badge">可用积分：<strong>{{ availablePoints }}</strong></span>
        <el-button @click="$router.push('/points/my')">积分明细</el-button>
        <el-button type="primary" @click="$router.push('/points/my')">我的兑换记录</el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col v-for="g in goods" :key="g.id" :span="6">
        <el-card shadow="hover" class="goods-card">
          <img :src="g.goodsImage || 'data:image/svg+xml,'+encodeURIComponent('<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%22200%22 height=%22120%22><rect width=%22200%22 height=%22120%22 fill=%22%23f0f2f5%22/><text x=%2250%%22 y=%2250%%22 text-anchor=%22middle%22 fill=%22%23999%22>'+g.goodsName+'</text></svg>')" class="goods-img" />
          <div class="goods-info">
            <h4>{{ g.goodsName }}</h4>
            <p class="goods-desc">{{ g.goodsDesc?.substring(0, 50) || '' }}</p>
            <div class="goods-footer">
              <span class="goods-points">{{ g.requiredPoints }} 积分</span>
              <el-tag size="small" :type="g.stock <= 0 ? 'danger' : 'success'">库存: {{ g.stock }}</el-tag>
            </div>
          </div>
          <el-button type="warning" :disabled="availablePoints < g.requiredPoints || g.stock <= 0"
            @click="showExchangeDialog(g)" plain style="width:100%;margin-top:8px">
            {{ availablePoints < g.requiredPoints ? '积分不足' : g.stock <= 0 ? '已售罄' : '立即兑换' }}
          </el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!goods.length && !loading" description="暂无商品" />

    <!-- 兑换弹窗 -->
    <el-dialog v-model="exDialog.visible" title="确认兑换" width="400px">
      <div v-if="exDialog.goods">
        <p>商品：<strong>{{ exDialog.goods.goodsName }}</strong></p>
        <p>所需积分：<strong>{{ exDialog.goods.requiredPoints }}</strong></p>
        <el-form label-width="80px">
          <el-form-item label="兑换数量"><el-input-number v-model="exDialog.quantity" :min="1" :max="exDialog.goods.stock" /></el-form-item>
          <el-form-item label="收货人"><el-input v-model="exDialog.name" placeholder="姓名" /></el-form-item>
          <el-form-item label="电话"><el-input v-model="exDialog.phone" placeholder="手机号" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="exDialog.address" placeholder="收货地址" type="textarea" /></el-form-item>
        </el-form>
        <p class="total-points">总计：{{ exDialog.goods.requiredPoints * exDialog.quantity }} 积分</p>
      </div>
      <template #footer>
        <el-button @click="exDialog.visible=false">取消</el-button>
        <el-button type="primary" @click="handleExchange" :disabled="availablePoints < exDialog.goods.requiredPoints * exDialog.quantity">确认兑换</el-button>
      </template>
    </el-dialog>

    <!-- 我的兑换记录 -->
    <el-dialog v-model="orderDialog.visible" title="我的兑换记录" width="700px">
      <el-table :data="orders" size="small" max-height="400">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="goodsName" label="商品" />
        <el-table-column prop="totalPoints" label="积分" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{row}">{{ { PENDING:'待处理', PROCESSING:'处理中', COMPLETED:'已完成', CANCELLED:'已取消' }[row.orderStatus] || row.orderStatus }}</template>
        </el-table-column>
        <el-table-column prop="exchangeTime" label="兑换时间" width="170" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listMallGoods, exchangeGoods, myExchangeOrders, myPointsAccount } from '@/api/quiz'

const goods = ref([])
const orders = ref([])
const loading = ref(false)
const availablePoints = ref(0)

const exDialog = reactive({ visible: false, goods: null, quantity: 1, name: '', phone: '', address: '' })
const orderDialog = reactive({ visible: false })

async function loadGoods() {
  loading.value = true
  try { const r = await listMallGoods(); goods.value = r.data || [] } catch {}
  loading.value = false
}
async function loadPoints() {
  try { const r = await myPointsAccount(); availablePoints.value = r.data?.availablePoints || 0 } catch {}
}
async function loadOrders() {
  try { const r = await myExchangeOrders(); orders.value = r.data || [] } catch {}
}

function showExchangeDialog(g) {
  exDialog.goods = g; exDialog.quantity = 1; exDialog.name = ''; exDialog.phone = ''; exDialog.address = ''
  exDialog.visible = true
}
async function handleExchange() {
  try {
    await exchangeGoods(exDialog.goods.id, { quantity: exDialog.quantity, receiverName: exDialog.name, receiverPhone: exDialog.phone, receiverAddress: exDialog.address })
    ElMessage.success('兑换成功'); exDialog.visible = false; loadGoods(); loadPoints(); loadOrders()
  } catch (e) { ElMessage.error(e.message) }
}

onMounted(() => { loadGoods(); loadPoints(); loadOrders() })
</script>

<style scoped>
.points-mall { padding: 20px; }
.mall-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.mall-header h2 { margin: 0; }
.mall-sub { color: #888; font-size: 13px; }
.mall-actions { display: flex; gap: 12px; align-items: center; }
.points-badge { padding: 6px 14px; background: #fef0d0; border-radius: 20px; color: #e6a23c; font-size: 14px; }
.goods-card { margin-bottom: 16px; }
.goods-img { width: 100%; height: 120px; object-fit: cover; border-radius: 6px; background: #f5f5f5; }
.goods-info h4 { margin: 8px 0 4px; font-size: 14px; }
.goods-desc { font-size: 12px; color: #888; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.goods-footer { display: flex; justify-content: space-between; align-items: center; margin: 8px 0; }
.goods-points { color: #e6a23c; font-weight: 700; font-size: 16px; }
.total-points { color: #e6a23c; font-weight: 700; font-size: 16px; }
</style>

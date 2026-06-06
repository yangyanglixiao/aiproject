<template>
  <div class="my-orders" v-loading="loading">
    <h2>我的订单</h2>
    <el-tabs v-model="activeStatus">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待支付" name="PENDING" />
      <el-tab-pane label="已支付" name="PAID" />
      <el-tab-pane label="制作中" name="PREPARING" />
      <el-tab-pane label="配送中" name="DELIVERING" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="已取消" name="CANCELLED" />
    </el-tabs>
    <div class="order-list">
      <el-card v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号: {{ order.orderNo }}</span>
          <el-tag :type="getStatusType(order.status)">{{ statusText(order.status) }}</el-tag>
          <el-tag :type="order.payStatus === 'PAID' ? 'success' : 'danger'">{{ order.payStatus === 'PAID' ? '已支付' : '未支付' }}</el-tag>
        </div>
        <div class="order-body">
          <p>金额: ¥{{ order.totalAmount }}</p>
          <p>创建时间: {{ formatTime(order.createTime) }}</p>
        </div>
        <div class="order-footer" v-if="order.payStatus === 'UNPAID'">
          <el-button-group>
            <el-button type="primary" @click="$router.push(`/order/pay/${order.id}`)">去支付</el-button>
            <el-button type="danger" @click="handleCancel(order.id)">取消订单</el-button>
          </el-button-group>
        </div>
      </el-card>
    </div>
    <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getOrderList, cancelOrder } from '../api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const activeStatus = ref('')
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 20 }
    if (activeStatus.value) params.status = activeStatus.value
    const res = await getOrderList(params)
    orders.value = res.data.records || []
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

watch(activeStatus, load)

const statusText = (status) => {
  const map = { PENDING: '待支付', PAID: '已支付', PREPARING: '制作中', DELIVERING: '配送中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { PENDING: '', PAID: 'success', PREPARING: 'warning', DELIVERING: 'warning', COMPLETED: 'success', CANCELLED: 'info' }
  return map[status] || ''
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : '-'

const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定取消此订单？', '提示')
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

onMounted(load)
</script>

<style scoped>
.my-orders {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.my-orders h2 { margin-bottom: 20px; }
.order-card {
  margin-bottom: 12px;
}
.order-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}
.order-body p { margin-bottom: 4px; font-size: 14px; color: #666; }
.order-footer { margin-top: 12px; }
</style>

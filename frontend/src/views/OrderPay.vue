<template>
  <div class="order-pay" v-loading="loading">
    <el-page-header @back="$router.back()" title="返回" />
    <h2>订单支付</h2>
    <div class="order-info">
      <p>订单号: {{ orderNo }}</p>
      <p class="amount">支付金额: ¥{{ amount }}</p>
    </div>
    <el-button type="success" size="large" :loading="paying" @click="handlePay">模拟支付</el-button>
    <el-dialog v-model="successDialog" title="支付成功" width="400px">
      <p>订单 {{ orderNo }} 支付成功</p>
      <template #footer>
        <el-button type="primary" @click="$router.push('/my-orders')">查看订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mockPay } from '../api/pay'
import { getOrderDetail } from '../api/order'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const orderId = route.params.orderId
const orderNo = ref('')
const amount = ref(0)
const loading = ref(true)
const paying = ref(false)
const successDialog = ref(false)

const load = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(orderId)
    orderNo.value = res.data.orderNo
    amount.value = res.data.totalAmount
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  paying.value = true
  try {
    await mockPay(orderId)
    successDialog.value = true
  } catch (e) {
    ElMessage.error('支付失败')
  } finally {
    paying.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.order-pay {
  max-width: 500px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
}
.order-pay h2 { margin-bottom: 24px; }
.order-info {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
}
.order-info p { margin-bottom: 8px; }
.amount { font-size: 24px; color: #e65100; font-weight: bold; }
</style>

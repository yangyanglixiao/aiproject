<template>
  <div class="order-confirm" v-loading="loading">
    <el-page-header @back="$router.push('/cart')" title="返回购物车" />
    <h2>确认订单</h2>
    <div class="order-section" v-for="group in merchantGroups" :key="group.merchantId">
      <h3>{{ group.merchantName }}</h3>
      <div class="order-item" v-for="item in group.items" :key="item.productId">
        <div>{{ getProductInfo(item) }}</div>
        <div class="item-price">¥{{ item.price }} × {{ item.quantity }} = ¥{{ (item.price * item.quantity).toFixed(2) }}</div>
      </div>
    </div>
    <div class="total">合计: ¥{{ totalAmount.toFixed(2) }}</div>
    <div style="text-align: right">
      <el-button type="primary" size="large" :loading="submitting" @click="handleCreate">提交订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList } from '../api/cart'
import { getMerchantDetail, getProducts } from '../api/product'
import { createOrder } from '../api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartItems = ref([])
const merchantInfo = ref({})
const productsMap = ref({})
const loading = ref(true)
const submitting = ref(false)

const load = async () => {
  loading.value = true
  try {
    const cartRes = await getCartList()
    cartItems.value = cartRes.data || []

    const merchantIds = [...new Set(cartItems.value.map(i => i.merchantId))]
    const results = await Promise.all([
      ...merchantIds.map(id => getMerchantDetail(id).catch(() => null)),
      ...cartItems.value.map(i => getProducts(i.merchantId).catch(() => [])),
    ])
    results.forEach((res, idx) => {
      if (idx < merchantIds.length && res?.data) merchantInfo.value[res.data.id] = res.data.name
    })
    results.slice(merchantIds.length).flat().forEach(p => {
      productsMap.value[p.id] = p
    })
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const getProductInfo = (item) => {
  const p = productsMap.value[item.productId]
  return p ? `${p.name} ×${item.quantity}` : `商品 ${item.productId} ×${item.quantity}`
}

const merchantGroups = computed(() => {
  const groups = {}
  cartItems.value.forEach(item => {
    const mid = item.merchantId
    if (!groups[mid]) groups[mid] = { merchantId: mid, merchantName: merchantInfo.value[mid] || '未知商家', items: [] }
    groups[mid].items.push(item)
  })
  return Object.values(groups)
})

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    const p = productsMap.value[item.productId]
    return sum + (p ? p.price * item.quantity : 0)
  }, 0)
})

const handleCreate = async () => {
  submitting.value = true
  try {
    const res = await createOrder({})
    router.push({ path: `/order/pay/${res.data.id}`, query: { back: 'my-orders' } })
  } catch (e) {
    ElMessage.error('下单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.order-confirm {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.order-section {
  background: #fff;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 8px;
}
.order-section h3 { margin-bottom: 12px; }
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
.item-price { color: #e65100; font-weight: bold; }
.total {
  text-align: right;
  font-size: 20px;
  font-weight: bold;
  color: #e65100;
  margin: 20px 0;
}
</style>

<template>
  <div class="cart-page" v-loading="loading">
    <h2>我的购物车</h2>
    <el-empty v-if="!loading && cartItems.length === 0" description="购物车为空" />
    <div class="cart-list" v-else>
      <div class="cart-item" v-for="item in cartItems" :key="item.id">
        <div class="item-info">
          <div class="item-name">{{ getProductInfo(item) }}</div>
          <div class="item-merchant">商家: {{ merchantInfo[item.merchantId] || '未知' }}</div>
        </div>
        <div class="item-actions">
          <el-button-group>
            <el-button size="small" :icon="Minus" @click="handleSub(item)" />
            <span class="qty">{{ item.quantity }}</span>
            <el-button size="small" :icon="Plus" @click="handlePlus(item)" />
          </el-button-group>
          <el-button type="danger" text @click="handleRemove(item.id)">删除</el-button>
        </div>
      </div>
    </div>
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="merchant-totals">
        <div v-for="group in merchantGroups" :key="group.merchantId" class="merchant-total">
          <span>{{ group.name }}</span>
          <span>小计: ¥{{ group.total.toFixed(2) }}</span>
        </div>
      </div>
      <el-button type="primary" size="large" @click="$router.push('/order/confirm')">提交订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCartItem, removeCartItem } from '../api/cart'
import { getMerchantDetail, getProducts } from '../api/product'
import { Plus, Minus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartItems = ref([])
const merchantInfo = ref({})
const productsMap = ref({})
const loading = ref(true)

const load = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = res.data || []

    const merchantIds = [...new Set(cartItems.value.map(i => i.merchantId))]
    const results = await Promise.all([
      ...merchantIds.map(id => getMerchantDetail(id).catch(() => null)),
      ...cartItems.value.map(i => getProducts(i.merchantId).catch(() => [])),
    ])
    results.forEach((res, idx) => {
      if (idx < merchantIds.length && res?.data) {
        merchantInfo.value[res.data.id] = res.data.name
      }
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
  return p ? `${p.name} (×${item.quantity})` : `商品 ${item.productId} (×${item.quantity})`
}

const handleSub = async (item) => {
  if (item.quantity <= 1) {
    await handleRemove(item.id)
  } else {
    await updateCartItem(item.id, { quantity: item.quantity - 1 })
    load()
  }
}

const handlePlus = async (item) => {
  await updateCartItem(item.id, { quantity: item.quantity + 1 })
  load()
}

const handleRemove = async (id) => {
  await removeCartItem(id)
  ElMessage.success('已删除')
  load()
}

const merchantGroups = computed(() => {
  const groups = {}
  cartItems.value.forEach(item => {
    const mid = item.merchantId
    if (!groups[mid]) groups[mid] = { merchantId: mid, name: merchantInfo.value[mid] || '未知商家', total: 0 }
    const p = productsMap.value[item.productId]
    if (p) groups[mid].total += p.price * item.quantity
  })
  return Object.values(groups)
})

onMounted(load)
</script>

<style scoped>
.cart-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.cart-page h2 {
  margin-bottom: 20px;
}
.cart-item {
  background: #fff;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.item-name { font-weight: bold; margin-bottom: 4px; }
.item-merchant { font-size: 12px; color: #999; }
.item-actions { display: flex; align-items: center; gap: 16px; }
.qty { font-weight: bold; min-width: 30px; text-align: center; }
.cart-footer {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.merchant-total {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
</style>

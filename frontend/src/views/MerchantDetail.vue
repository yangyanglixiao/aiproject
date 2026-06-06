<template>
  <div class="merchant-detail" v-loading="loading">
    <el-page-header @back="$router.back()" :title="'返回'" class="back-header" />
    <div class="merchant-info" v-if="merchant">
      <h2>{{ merchant.name }}</h2>
      <p>📍 {{ merchant.address || '暂无地址' }}</p>
      <p>📞 {{ merchant.phone || '暂无电话' }}</p>
      <p>{{ merchant.description }}</p>
    </div>
    <el-tabs v-model="activeCategory" v-if="categories.length > 0">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane
        v-for="cat in categories"
        :key="cat.id"
        :label="cat.name"
        :name="String(cat.id)"
      />
    </el-tabs>
    <div class="product-grid">
      <ProductItem
        v-for="p in filteredProducts"
        :key="p.id"
        :product="p"
        @add="handleAdd"
      />
    </div>
    <el-dialog v-model="cartDialog" title="加购成功" width="300px">
      <p>商品 {{ lastAdded }} 已加入购物车</p>
      <template #footer>
        <el-button @click="$router.push('/cart')">查看购物车</el-button>
        <el-button type="primary" @click="cartDialog = false">继续购物</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMerchantDetail, getCategories, getProducts } from '../api/product'
import { addToCart } from '../api/cart'
import ProductItem from '../components/ProductItem.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const merchant = ref(null)
const categories = ref([])
const products = ref([])
const activeCategory = ref('')
const loading = ref(true)
const cartDialog = ref(false)
const lastAdded = ref('')

const filteredProducts = computed(() => {
  if (!activeCategory.value) return products.value
  return products.value.filter(p => p.categoryId === Number(activeCategory.value))
})

const load = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const [mRes, cRes, pRes] = await Promise.all([
      getMerchantDetail(id),
      getCategories(id),
      getProducts(id),
    ])
    merchant.value = mRes.data
    categories.value = cRes.data
    products.value = pRes.data
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const handleAdd = async (product) => {
  try {
    lastAdded.value = product.name
    await addToCart({ merchantId: Number(route.params.id), productId: product.id, quantity: 1 })
    cartDialog.value = true
  } catch (e) {
    ElMessage.error('加购失败')
  }
}

onMounted(load)
</script>

<style scoped>
.merchant-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.back-header {
  margin-bottom: 16px;
}
.merchant-info {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}
.merchant-info h2 {
  margin-bottom: 8px;
}
.merchant-info p {
  color: #666;
  font-size: 14px;
  margin-bottom: 4px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}
</style>

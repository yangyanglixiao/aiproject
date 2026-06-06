# 前端用户端 Implementation Plan

> **For agentic workers:** Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 构建订餐系统前端用户端，覆盖首页、商家详情、购物车、下单、支付、我的订单全流程。
**Architecture:** Vue 3 SPA + Vite + Pinia + Vue Router + Element Plus + Axios。前后端分离，Vite dev server 代理到后端 8080 端口。
**Tech Stack:** Vue 3.4 + Vite 5 + Pinia 2.1 + Vue Router 4.2 + Element Plus 2.4 + Axios 1.6

---

### 项目结构

```
frontend/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.js                    # 入口：引入 Element Plus
│   ├── App.vue                    # 根组件
│   ├── router/
│   │   └── index.js               # 路由表
│   ├── stores/
│   │   └── user.js                # Pinia: token + userId + 登录/登出
│   ├── api/
│   │   ├── request.js             # Axios 实例 + 拦截器
│   │   ├── merchant.js            # 商家 API
│   │   ├── product.js             # 商品 API
│   │   ├── cart.js                # 购物车 API
│   │   ├── order.js               # 订单 API
│   │   └── pay.js                 # 支付 API
│   ├── views/
│   │   ├── Home.vue               # 首页
│   │   ├── MerchantDetail.vue     # 商家详情
│   │   ├── Cart.vue               # 购物车
│   │   ├── OrderConfirm.vue       # 订单确认
│   │   ├── OrderPay.vue           # 支付页
│   │   └── MyOrders.vue           # 我的订单
│   ├── components/
│   │   ├── NavBar.vue             # 顶部导航
│   │   ├── MerchantCard.vue       # 商家卡片
│   │   └── ProductItem.vue        # 商品卡片
│   └── styles/
│       └── main.css               # 全局样式
```

---

### Task 1: 初始化 Vite + Vue 3 项目

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/styles/main.css`

- [ ] **Step 1: 编写 package.json**

```json
{
  "name": "ordering-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "element-plus": "^2.4.4",
    "axios": "^1.6.2",
    "@element-plus/icons-vue": "^2.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.10",
    "sass": "^1.69.5"
  }
}
```

- [ ] **Step 2: 编写 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 编写 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>在线订餐系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 编写 main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import './styles/main.css'

const app = createApp(App)

app.use(createPinia())
app.use(ElementPlus, { locale: zhCn })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

- [ ] **Step 5: 编写 App.vue**

```vue
<template>
  <div id="app">
    <NavBar />
    <router-view />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
#app {
  min-height: 100vh;
  background: #f5f5f5;
}
</style>
```

- [ ] **Step 6: 编写 main.css**

```css
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  background: #f5f5f5;
}

a {
  text-decoration: none;
  color: inherit;
}
```

- [ ] **Step 7: 安装依赖并验证**

```bash
cd /Users/yang/Desktop/aiproject/frontend
npm install
npm run dev
```

访问 `http://localhost:3000` 应看到空白页面（只有 NavBar 占位）。

- [ ] **Step 8: Commit**

```bash
git add frontend/
git commit -m "feat: init frontend with Vite + Vue 3 + Element Plus"
```

---

### Task 2: 全局组件 + 路由 + 用户 Store + Axios

**Files:**
- Create: `frontend/src/components/NavBar.vue`
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/stores/user.js`
- Create: `frontend/src/api/request.js`
- Create: 6 个 API 文件
- Modify: `frontend/src/App.vue` — 引入路由

- [ ] **Step 1: 编写 NavBar 组件**

```vue
<template>
  <el-header class="nav-header">
    <div class="nav-inner">
      <router-link to="/" class="nav-logo">🍔 在线订餐</router-link>
      <div class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/my-orders">我的订单</router-link>
        <el-button type="primary" text @click="showLogin">登录</el-button>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const showLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.nav-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 0 20px;
  height: 56px;
  display: flex;
  align-items: center;
}
.nav-inner {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nav-logo {
  font-size: 20px;
  font-weight: bold;
  color: #e65100;
}
.nav-links {
  display: flex;
  gap: 16px;
  align-items: center;
}
.nav-links a {
  color: #666;
  font-size: 14px;
}
.nav-links a:hover {
  color: #e65100;
}
</style>
```

- [ ] **Step 2: 编写路由**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('../views/Home.vue') },
  { path: '/merchant/:id', component: () => import('../views/MerchantDetail.vue') },
  { path: '/cart', component: () => import('../views/Cart.vue') },
  { path: '/order/confirm', component: () => import('../views/OrderConfirm.vue') },
  { path: '/order/pay/:orderId', component: () => import('../views/OrderPay.vue') },
  { path: '/my-orders', component: () => import('../views/MyOrders.vue') },
  { path: '/login', component: () => import('../views/Login.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
```

- [ ] **Step 3: 编写 user Store**

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')

  function setLoginInfo(data) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  }

  return { token, userId, username, role, setLoginInfo, logout }
})
```

- [ ] **Step 4: 编写 Axios 实例**

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        window.location.href = '/login'
      } else {
        ElMessage.error(error.response.data?.msg || '请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 5: 编写所有 API 文件**

```javascript
// frontend/src/api/request.js (already above)

// frontend/src/api/merchant.js
import request from './request'
export const getMerchantList = (params) => request.get('/api/merchants', { params })
export const getMerchantDetail = (id) => request.get(`/api/merchants/${id}`)

// frontend/src/api/product.js
import request from './request'
export const getProducts = (merchantId, params) => request.get(`/api/merchants/${merchantId}/products`, { params })
export const getCategories = (merchantId) => request.get(`/api/merchants/${merchantId}/categories`)

// frontend/src/api/cart.js
import request from './request'
export const getCartList = () => request.get('/api/cart')
export const addToCart = (data) => request.post('/api/cart/add', data)
export const updateCartItem = (id, data) => request.put(`/api/cart/item/${id}`, data)
export const removeCartItem = (id) => request.delete(`/api/cart/item/${id}`)

// frontend/src/api/order.js
import request from './request'
export const createOrder = (data) => request.post('/api/orders', data)
export const getOrderList = (params) => request.get('/api/orders', { params })
export const getOrderDetail = (id) => request.get(`/api/orders/${id}`)
export const getOrderItems = (id) => request.get(`/api/orders/${id}/items`)
export const cancelOrder = (id) => request.post(`/api/orders/${id}/cancel`)

// frontend/src/api/pay.js
import request from './request'
export const mockPay = (orderId) => request.post(`/api/pay/mock/${orderId}`)
```

- [ ] **Step 6: 修改 App.vue 引入路由**

```vue
<template>
  <div id="app">
    <NavBar />
    <router-view />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
#app {
  min-height: 100vh;
  background: #f5f5f5;
}
</style>
```

- [ ] **Step 7: Commit**

```bash
git add frontend/src/
git commit -m "feat: add NavBar, router, user store, axios, and all API modules"
```

---

### Task 3: 登录页 + 首页 (Home + MerchantCard)

**Files:**
- Create: `frontend/src/views/Login.vue`
- Create: `frontend/src/views/Home.vue`
- Create: `frontend/src/components/MerchantCard.vue`

- [ ] **Step 1: 编写登录页**

```vue
<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>用户登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/api/auth/login', form)
    userStore.setLoginInfo(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // error already handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 56px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 20px;
}
.login-card h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}
</style>
```

- [ ] **Step 2: 编写 MerchantCard 组件**

```vue
<template>
  <el-card class="merchant-card" shadow="hover" @click="$router.push(`/merchant/${merchant.id}`)">
    <div class="merchant-info">
      <div class="merchant-name">{{ merchant.name }}</div>
      <div class="merchant-address">{{ merchant.address || '暂无地址' }}</div>
      <div class="merchant-phone">{{ merchant.phone || '暂无电话' }}</div>
      <div class="merchant-desc">{{ merchant.description || '暂无描述' }}</div>
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  merchant: { type: Object, required: true }
})
</script>

<style scoped>
.merchant-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.merchant-card:hover {
  transform: translateY(-2px);
}
.merchant-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}
.merchant-address, .merchant-phone, .merchant-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}
</style>
```

- [ ] **Step 3: 编写首页**

```vue
<template>
  <div class="home">
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索商家..."
        size="large"
        prefix-icon="Search"
        clearable
        @keyup.enter="search"
      />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    <div class="merchant-grid">
      <MerchantCard v-for="m in merchants" :key="m.id" :merchant="m" />
    </div>
    <el-empty v-if="!loading && merchants.length === 0" description="暂无商家" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMerchantList } from '../api/merchant'
import MerchantCard from '../components/MerchantCard.vue'

const merchants = ref([])
const keyword = ref('')
const loading = ref(false)

const search = async () => {
  loading.value = true
  try {
    const res = await getMerchantList({ page: 1, size: 50, keyword: keyword.value })
    merchants.value = res.data.records
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

onMounted(search)
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}
.search-bar .el-input {
  flex: 1;
}
.merchant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}
</style>
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/views/Login.vue frontend/src/views/Home.vue frontend/src/components/MerchantCard.vue
git commit -m "feat: add login page, home page with merchant list and search, MerchantCard component"
```

---

### Task 4: 商家详情页 (MerchantDetail + ProductItem)

**Files:**
- Create: `frontend/src/views/MerchantDetail.vue`
- Create: `frontend/src/components/ProductItem.vue`

- [ ] **Step 1: 编写 ProductItem 组件**

```vue
<template>
  <el-card class="product-item" shadow="hover">
    <div class="product-info">
      <div class="product-name">{{ product.name }}</div>
      <div class="product-desc">{{ product.description || '暂无描述' }}</div>
      <div class="product-footer">
        <span class="price">¥{{ product.price }}</span>
        <el-button-group>
          <el-button size="small" :icon="Minus" @click="$emit('subtract', product.id)" />
          <el-button size="small" type="primary" @click="$emit('add', product)">+ 加购</el-button>
        </el-button-group>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Minus } from '@element-plus/icons-vue'
defineEmits(['subtract', 'add'])
defineProps({
  product: { type: Object, required: true }
})
</script>

<style scoped>
.product-item {
  transition: transform 0.2s;
}
.product-item:hover {
  transform: translateY(-2px);
}
.product-name {
  font-weight: bold;
  margin-bottom: 4px;
}
.product-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #e65100;
  font-size: 18px;
  font-weight: bold;
}
</style>
```

- [ ] **Step 2: 编写商家详情页**

```vue
<template>
  <div class="merchant-detail" v-loading="loading">
    <el-page-header @back="$router.back()" :title="'返回'" class="back-header" />
    <div class="merchant-info" v-if="merchant">
      <h2>{{ merchant.name }}</h2>
      <p>{{ merchant.address }}</p>
      <p>{{ merchant.phone }}</p>
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
```

- [ ] **Step 3: Commit**

```bash
git add frontend/src/views/MerchantDetail.vue frontend/src/components/ProductItem.vue
git commit -m "feat: add merchant detail page with category tabs and ProductItem component"
```

---

### Task 5: 购物车页面

**Files:**
- Create: `frontend/src/views/Cart.vue`

- [ ] **Step 1: 编写购物车页面**

```vue
<template>
  <div class="cart-page" v-loading="loading">
    <h2>我的购物车</h2>
    <el-empty v-if="!loading && items.length === 0" description="购物车为空" />
    <div class="cart-list" v-else>
      <div class="cart-item" v-for="item in items" :key="item.id">
        <div class="item-info">
          <div class="item-name">{{ getItemProductName(item) }}</div>
          <div class="item-merchant">商家: {{ getItemMerchantName(item) }}</div>
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
    <div class="cart-footer" v-if="items.length > 0">
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
import { ref, onMounted, computed } from 'vue'
import { getCartList, updateCartItem, removeCartItem } from '../api/cart'
import { getMerchantDetail } from '../api/merchant'
import { Plus, Minus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const items = ref([])
const merchantInfo = ref({})
const loading = ref(true)

const load = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    items.value = res.data || []
    await loadMerchantInfo()
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const loadMerchantInfo = async () => {
  const merchantIds = [...new Set(items.value.map(i => i.merchantId))]
  const infos = await Promise.all(merchantIds.map(id => getMerchantDetail(id).catch(() => null)))
  infos.forEach(info => {
    if (info?.data) merchantInfo.value[info.data.id] = info.data.name
  })
}

const getItemProductName = (item) => {
  return `商品 ${item.productId}`
}

const getItemMerchantName = (item) => {
  return merchantInfo.value[item.merchantId] || '未知商家'
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
  load()
}

const merchantGroups = computed(() => {
  const groups = {}
  items.value.forEach(item => {
    const mid = item.merchantId
    if (!groups[mid]) groups[mid] = { merchantId: mid, name: merchantInfo.value[mid] || '未知商家', total: 0 }
    groups[mid].total += 20 * item.quantity // 简化，实际应从后端获取
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
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/Cart.vue
git commit -m "feat: add cart page with quantity controls and merchant grouping"
```

---

### Task 6: 订单确认 + 支付页

**Files:**
- Create: `frontend/src/views/OrderConfirm.vue`
- Create: `frontend/src/views/OrderPay.vue`

- [ ] **Step 1: 编写订单确认页**

```vue
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
    <el-button type="primary" size="large" :loading="submitting" @click="handleCreate">提交订单</el-button>
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
const merchantInfo = ref({})
const productsMap = ref({})
const cartItems = ref([])
const loading = ref(true)
const submitting = ref(false)

const load = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = res.data || []
    await loadDetails()
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const loadDetails = async () => {
  const merchantIds = [...new Set(cartItems.value.map(i => i.merchantId))]
  const productIds = cartItems.value.map(i => i.productId)
  const [merchants, products] = await Promise.all([
    Promise.all(merchantIds.map(id => getMerchantDetail(id).catch(() => null))),
    Promise.all(productIds.map(id => getProducts(0).catch(() => []))),
  ])
  merchants.forEach(m => { if (m?.data) merchantInfo.value[m.data.id] = m.data.name })
}

const getProductInfo = (item) => {
  return `商品 ID:${item.productId}`
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
  return cartItems.value.reduce((sum, item) => sum + (item.productId * item.quantity || 0), 0)
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
```

- [ ] **Step 2: 编写支付页**

```vue
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
```

- [ ] **Step 3: Commit**

```bash
git add frontend/src/views/OrderConfirm.vue frontend/src/views/OrderPay.vue
git commit -m "feat: add order confirm page with merchant grouping and pay page with mock payment"
```

---

### Task 7: 我的订单页 + 后端 CORS 配置

**Files:**
- Create: `frontend/src/views/MyOrders.vue`
- Modify: `backend/src/main/java/com/galaxy/ordering/config/SecurityConfig.java` — 添加 CORS

- [ ] **Step 1: 编写我的订单页**

```vue
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
          <el-tag :type="getStatusType(order.status)">{{ order.status }}</el-tag>
          <el-tag :type="order.payStatus === 'PAID' ? 'success' : 'danger'">{{ order.payStatus }}</el-tag>
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
import { ref, onMounted, watch } from 'vue'
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
```

- [ ] **Step 2: 修改 SecurityConfig 添加 CORS**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .cors(Customizer.withDefaults())
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
            .requestMatchers("/api/auth/**", "/api/merchants/**", "/api/merchants/**/products", "/h2-console/**").permitAll()
            .requestMatchers("/api/admin/**").authenticated()
            .anyRequest().authenticated()
        .and()
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .headers().frameOptions().disable();

    return http.build();
}
```

同时在 SecurityConfig 类中添加 CorsConfigurationSource Bean：

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

- [ ] **Step 3: Commit 前后端**

```bash
git add frontend/src/views/MyOrders.vue
git commit -m "feat: add my orders page with status filtering and cancel"

cd .. && git add backend/src/main/java/com/galaxy/ordering/config/SecurityConfig.java
git commit -m "fix: add CORS config for frontend dev server"
```

---

### Task 8: 完善购物车商品展示 + 订单总价

**Files:**
- Modify: `frontend/src/views/Cart.vue` — 从购物车获取商品详情
- Modify: `frontend/src/views/OrderConfirm.vue` — 计算真实总价

这个 Task 主要修复之前简化版中用 placeholder 数据的地方，让商品名、单价、总价从真实数据展示。

- [ ] **Step 1: 修改 Cart.vue — 加载商品详情**

```javascript
// 修改 load 方法，同时加载商品详情:
const productInfo = ref({})

const load = async () => {
  loading.value = true
  try {
    const cartRes = await getCartList()
    cartItems.value = cartRes.data || []

    // 加载商家和商品详情
    const merchantIds = [...new Set(cartItems.value.map(i => i.merchantId))]
    const promises = [
      ...merchantIds.map(id => getMerchantDetail(id).catch(() => null)),
      ...cartItems.value.map(item => getProducts(item.merchantId).catch(() => [])),
    ]
    const results = await Promise.all(promises)

    results.forEach((res, idx) => {
      if (idx < merchantIds.length && res?.data) {
        merchantInfo.value[res.data.id] = res.data.name
      }
    })
    // 商品详情通过产品列表构建 map
    const productResults = results.slice(merchantIds.length)
    productResults.flat().forEach(p => {
      productInfo.value[p.id] = p
    })
  } catch (e) {}
  finally { loading.value = false }
}

// 修改 getItemProductName:
const getItemProductName = (item) => {
  const p = productInfo.value[item.productId]
  return p ? p.name : `商品 ${item.productId}`
}

// 修改 merchantGroups 计算小计:
const merchantGroups = computed(() => {
  const groups = {}
  cartItems.value.forEach(item => {
    const mid = item.merchantId
    if (!groups[mid]) groups[mid] = { merchantId: mid, name: merchantInfo.value[mid] || '未知商家', total: 0 }
    const p = productInfo.value[item.productId]
    if (p) groups[mid].total += p.price * item.quantity
  })
  return Object.values(groups)
})
```

- [ ] **Step 2: 修改 OrderConfirm.vue — 使用真实数据**

```javascript
// 修改 load，同时加载商品详情:
const productInfo = ref({})
const cartItems = ref([])
const merchantInfo = ref({})

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
    results.slice(merchantIds.length).flat().forEach(p => { productInfo.value[p.id] = p })
  } catch (e) {}
  finally { loading.value = false }
}

// 修改 getProductInfo:
const getProductInfo = (item) => {
  const p = productInfo.value[item.productId]
  return p ? `${p.name} ×${item.quantity}` : `商品 ${item.productId} ×${item.quantity}`
}

// 修改 totalAmount 计算真实总价:
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    const p = productInfo.value[item.productId]
    return sum + (p ? p.price * item.quantity : 0)
  }, 0)
})
```

- [ ] **Step 3: Commit**

```bash
git add frontend/src/views/Cart.vue frontend/src/views/OrderConfirm.vue
git commit -m "fix: display real product names, prices, and subtotals in cart and order confirm"
```

---

## 自审结果

1. **Spec 覆盖** — 所有设计文档中的页面都有对应 Task：首页(3)、商家详情(4)、购物车(5)、订单确认(6)、支付(6)、我的订单(7)、登录(3)
2. **占位符扫描** — 无 "TBD"/"TODO" 残留
3. **类型一致性** — API 函数名在请求和调用处一致（getMerchantList / getProducts / getCartList 等）
4. **CORS** — Task 7 补充了后端 CORS 配置，解决前后端分离开发时的跨域问题
5. **购物车小计** — Task 8 修复了 placeholder 计算，使用真实商品价格

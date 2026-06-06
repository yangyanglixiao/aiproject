import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('../views/Home.vue') },
  { path: '/merchant/:id', component: () => import('../views/MerchantDetail.vue') },
  { path: '/cart', component: () => import('../views/Cart.vue') },
  { path: '/order/confirm', component: () => import('../views/OrderConfirm.vue') },
  { path: '/order/pay/:orderId', component: () => import('../views/OrderPay.vue') },
  { path: '/my-orders', component: () => import('../views/MyOrders.vue') },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    children: [
      { path: '', component: () => import('../views/admin/Dashboard.vue'), index: true },
      { path: 'merchants', component: () => import('../views/admin/MerchantManage.vue') },
      { path: 'products', component: () => import('../views/admin/ProductManage.vue') },
      { path: 'orders', component: () => import('../views/admin/OrderManage.vue') },
      { path: 'categories', component: () => import('../views/admin/CategoryManage.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router

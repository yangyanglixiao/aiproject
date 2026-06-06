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

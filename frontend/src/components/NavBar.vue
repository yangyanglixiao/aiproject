<template>
  <el-header class="nav-header">
    <div class="nav-inner">
      <router-link to="/" class="nav-logo">🍔 在线订餐</router-link>
      <div class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/my-orders">我的订单</router-link>
        <router-link to="/cart">购物车</router-link>
        <el-button type="primary" text @click="$router.push('/login')">登录</el-button>
      </div>
    </div>
  </el-header>
</template>

<script setup>
</script>

<style scoped>
.nav-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
  height: 56px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
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

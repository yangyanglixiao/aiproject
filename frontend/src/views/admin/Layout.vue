<template>
  <el-container class="admin-layout">
    <el-aside width="200px">
      <div class="admin-logo">🍔 管理后台</div>
      <el-menu :default-active="activeMenu" router class="admin-menu">
        <el-menu-item index="/admin">
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/merchants">
          <span>商家管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item @click="handleLogout">
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="admin-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-aside {
  background: #2c3e50;
  color: #fff;
}
.admin-logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  background: #1a2530;
}
.admin-menu {
  border-right: none;
  background: #2c3e50;
}
.admin-menu .el-menu-item {
  color: #fff;
}
.admin-menu .el-menu-item:hover,
.admin-menu .el-menu-item.is-active {
  background: #34495e !important;
  color: #409eff;
}
.admin-main {
  background: #f0f2f5;
  min-height: calc(100vh - 56px);
  padding: 20px;
}
</style>

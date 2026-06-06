<template>
  <div class="admin-dashboard">
    <h2>数据概览</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.merchantCount || 0 }}</div>
            <div class="stat-label">商家总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.productCount || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.orderCount || 0 }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.pendingReview || 0 }}</div>
            <div class="stat-label">待审核商家</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMerchantList } from '../../api/merchant'
import request from '../../api/request'

const stats = ref({})

const load = async () => {
  try {
    const [merchants, products, orders] = await Promise.all([
      getMerchantList({ page: 1, size: 1 }),
      request.get('/api/admin/products'),
      request.get('/api/admin/orders', { params: { page: 1, size: 1 } }),
    ])
    stats.value.merchantCount = merchants.data?.total || 0
    stats.value.productCount = products.data?.length || 0
    stats.value.orderCount = orders.data?.total || 0

    const pending = await getMerchantList({ page: 1, size: 50 })
    stats.value.pendingReview = (pending.data?.records || []).filter(m => m.status === 'REVIEWING').length
  } catch (e) {
    // silent
  }
}

onMounted(load)
</script>

<style scoped>
.admin-dashboard h2 { margin-bottom: 16px; }
.stat-card {
  text-align: center;
  padding: 20px 0;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 8px;
}
</style>

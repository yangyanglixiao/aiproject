<template>
  <div class="admin-orders">
    <h2>订单管理</h2>
    <div class="toolbar">
      <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 160px" @change="load">
        <el-option label="待支付" value="PENDING" />
        <el-option label="已支付" value="PAID" />
        <el-option label="制作中" value="PREPARING" />
        <el-option label="配送中" value="DELIVERING" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
    </div>
    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payStatus" label="支付" width="80">
        <template #default="{ row }">
          <el-tag :type="row.payStatus === 'PAID' ? 'success' : 'danger'">
            {{ row.payStatus === 'PAID' ? '已付' : '未付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payMethod" label="支付方式" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-select v-model="row.tempStatus" size="small" style="width: 120px" @change="handleStatusChange(row)">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="制作中" value="PREPARING" />
            <el-option label="配送中" value="DELIVERING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 16px; text-align: right"
      v-model:current-page="currentPage"
      :page-size="size"
      layout="prev, pager, next"
      :total="total"
      @current-change="load"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'

const orders = ref([])
const loading = ref(false)
const statusFilter = ref('')
const currentPage = ref(1)
const size = ref(10)
const total = ref(0)

const load = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: size.value }
    if (statusFilter.value) params.status = statusFilter.value
    const res = await request.get('/api/admin/orders', { params })
    orders.value = (res.data.records || []).map(o => ({ ...o, tempStatus: o.status }))
    total.value = res.data.total || res.data?.length || 0
  } catch (e) {}
  finally { loading.value = false }
}

const statusText = (s) => {
  const m = { PENDING: '待支付', PAID: '已支付', PREPARING: '制作中', DELIVERING: '配送中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return m[s] || s
}

const statusTag = (s) => {
  const m = { PENDING: '', PAID: 'success', PREPARING: 'warning', DELIVERING: 'warning', COMPLETED: 'success', CANCELLED: 'info' }
  return m[s] || ''
}

const formatTime = (t) => t ? new Date(t).toLocaleString() : '-'

const handleStatusChange = async (row) => {
  try {
    await request.put(`/api/admin/orders/${row.id}/status`, { status: row.tempStatus })
    row.status = row.tempStatus
    ElMessage.success('状态更新成功')
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

onMounted(load)
</script>

<style scoped>
.admin-orders h2 { margin-bottom: 16px; }
.toolbar { margin-bottom: 16px; }
</style>

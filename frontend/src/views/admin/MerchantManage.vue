<template>
  <div class="admin-merchants">
    <h2>商家管理</h2>
    <el-table :data="merchants" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商家名称" width="150" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'REVIEWING'"
            size="small"
            type="success"
            @click="handleAudit(row.id, 'APPROVED')"
          >通过</el-button>
          <el-button
            v-if="row.status === 'REVIEWING'"
            size="small"
            type="danger"
            @click="handleAudit(row.id, 'REJECTED')"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 16px; text-align: right"
      v-model:current-page="page"
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
import { ElMessage, ElMessageBox } from 'element-plus'

const merchants = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/merchants', {
      params: { page: page.value, size: size.value }
    })
    merchants.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {}
  finally { loading.value = false }
}

const statusLabel = (status) => {
  const map = { REVIEWING: '审核中', APPROVED: '已通过', REJECTED: '已拒绝' }
  return map[status] || status
}

const handleAudit = async (id, status) => {
  const msg = status === 'APPROVED' ? '通过该商家？' : '拒绝该商家？'
  try {
    await ElMessageBox.confirm(msg, '提示')
    await request.put(`/api/admin/merchants/${id}/audit`, { status })
    ElMessage.success('操作成功')
    load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(load)
</script>

<style scoped>
.admin-merchants h2 { margin-bottom: 16px; }
</style>

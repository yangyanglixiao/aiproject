<template>
  <div class="admin-products">
    <h2>商品管理</h2>
    <div class="toolbar">
      <el-select v-model="selectedMerchant" placeholder="选择商家" clearable style="width: 160px" @change="load">
        <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-button type="primary" @click="showCreateDialog">新增商品</el-button>
    </div>
    <el-table :data="products" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="商品名" width="150" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ON_SHELF' ? 'success' : 'info'">
            {{ row.status === 'ON_SHELF' ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" @click="handleToggle(row)">{{ row.status === 'ON_SHELF' ? '下架' : '上架' }}</el-button>
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

    <el-dialog v-model="editDialog" :title="isCreate ? '新增商品' : '编辑商品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="商家">
          <el-select v-model="form.merchantId" placeholder="选择商家" style="width: 100%">
            <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%" @change="loadCategories">
            <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
          <el-select v-if="form.merchantId" v-model="form.categoryId" placeholder="选择分类" style="width: 100%; margin-left: 8px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'
import { getMerchantList } from '../../api/merchant'
import { ElMessage, ElMessageBox } from 'element-plus'

const products = ref([])
const merchants = ref([])
const categories = ref([])
const loading = ref(false)
const selectedMerchant = ref(null)
const currentPage = ref(1)
const size = ref(10)
const total = ref(0)
const editDialog = ref(false)
const isCreate = ref(false)
const submitting = ref(false)
const form = ref({})

const load = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: size.value }
    const res = await request.get('/api/admin/products', {
      params: selectedMerchant.value ? { merchantId: selectedMerchant.value, ...params } : params
    })
    products.value = res.data.records || res.data || []
    total.value = res.data.total || res.data?.length || 0
  } catch (e) {}
  finally { loading.value = false }
}

const loadMerchants = async () => {
  try {
    const res = await getMerchantList({ page: 1, size: 50 })
    merchants.value = res.data?.records || []
  } catch (e) {}
}

const loadCategories = async () => {
  if (!form.value.merchantId) return
  try {
    const res = await request.get(`/api/merchants/${form.value.merchantId}/categories`)
    categories.value = res.data || []
  } catch (e) {}
}

const showCreateDialog = () => {
  isCreate.value = true
  form.value = { merchantId: selectedMerchant.value || undefined }
  editDialog.value = true
}

const handleEdit = (row) => {
  isCreate.value = false
  form.value = { ...row }
  editDialog.value = true
}

const handleToggle = async (row) => {
  const newStatus = row.status === 'ON_SHELF' ? 'OFF_SHELF' : 'ON_SHELF'
  try {
    await request.put(`/api/admin/products/${row.id}`, { ...row, status: newStatus })
    ElMessage.success('操作成功')
    load()
  } catch (e) {}
}

const handleSave = async () => {
  submitting.value = true
  try {
    if (isCreate.value) {
      await request.post('/api/admin/products', form.value)
    } else {
      await request.put(`/api/admin/products/${form.value.id}`, form.value)
    }
    ElMessage.success('保存成功')
    editDialog.value = false
    load()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => { loadMerchants(); load() })
</script>

<style scoped>
.admin-products h2 { margin-bottom: 16px; }
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
</style>

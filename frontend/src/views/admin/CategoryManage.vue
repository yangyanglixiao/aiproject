<template>
  <div class="admin-categories">
    <h2>分类管理</h2>
    <div class="toolbar">
      <el-select v-model="selectedMerchant" placeholder="选择商家" style="width: 200px" @change="load">
        <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-button type="primary" @click="showCreate">新增分类</el-button>
    </div>
    <el-table :data="categories" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="150" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增分类" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'
import { getMerchantList } from '../../api/merchant'
import { adminCreateCategory, adminDeleteCategory } from '../../api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const merchants = ref([])
const loading = ref(false)
const selectedMerchant = ref(null)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = ref({ merchantId: 0, name: '', sort: 0 })

const load = async () => {
  if (!selectedMerchant.value) { categories.value = []; return }
  loading.value = true
  try {
    const res = await request.get(`/api/merchants/${selectedMerchant.value}/categories`)
    categories.value = res.data || []
  } catch (e) {}
  finally { loading.value = false }
}

const loadMerchants = async () => {
  try {
    const res = await getMerchantList({ page: 1, size: 50 })
    merchants.value = res.data?.records || []
  } catch (e) {}
}

const showCreate = () => {
  form.value = { merchantId: selectedMerchant.value, name: '', sort: 0 }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该分类？', '提示')
    await adminDeleteCategory(id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleSave = async () => {
  submitting.value = true
  try {
    await adminCreateCategory(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => { loadMerchants() })
</script>

<style scoped>
.admin-categories h2 { margin-bottom: 16px; }
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
</style>

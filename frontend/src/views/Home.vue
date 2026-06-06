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

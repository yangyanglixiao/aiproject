<template>
  <el-card class="merchant-card" shadow="hover" @click="$router.push(`/merchant/${merchant.id}`)">
    <div class="merchant-info">
      <div class="merchant-name">{{ merchant.name }}</div>
      <div class="merchant-address">📍 {{ merchant.address || '暂无地址' }}</div>
      <div class="merchant-phone">📞 {{ merchant.phone || '暂无电话' }}</div>
      <div class="merchant-desc">{{ merchant.description || '暂无描述' }}</div>
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  merchant: { type: Object, required: true }
})
</script>

<style scoped>
.merchant-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.merchant-card:hover {
  transform: translateY(-2px);
}
.merchant-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}
.merchant-address, .merchant-phone, .merchant-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}
</style>

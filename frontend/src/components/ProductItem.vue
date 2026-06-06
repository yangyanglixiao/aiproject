<template>
  <el-card class="product-item" shadow="hover">
    <div class="product-info">
      <div class="product-name">{{ product.name }}</div>
      <div class="product-desc">{{ product.description || '暂无描述' }}</div>
      <div class="product-footer">
        <span class="price">¥{{ product.price }}</span>
        <el-button-group>
          <el-button size="small" :icon="Minus" disabled />
          <el-button size="small" type="primary" @click="$emit('add', product)">+ 加购</el-button>
        </el-button-group>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Minus } from '@element-plus/icons-vue'
defineEmits(['add'])
defineProps({
  product: { type: Object, required: true }
})
</script>

<style scoped>
.product-item {
  transition: transform 0.2s;
}
.product-item:hover {
  transform: translateY(-2px);
}
.product-name {
  font-weight: bold;
  margin-bottom: 4px;
}
.product-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #e65100;
  font-size: 18px;
  font-weight: bold;
}
</style>

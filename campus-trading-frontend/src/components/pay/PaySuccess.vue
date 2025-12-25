<template>
  <div class="success-page">
    <div class="icon-box">
      <el-icon class="success-icon"><Select /></el-icon>
    </div>
    <h2>支付成功</h2>
    <p class="sub-text">感谢您的购买，卖家将尽快联系您</p>

    <div class="info-card">
      <div class="row">
        <span>商品</span>
        <span class="val">{{ route.query.title }}</span>
      </div>
      <div class="row">
        <span>支付金额</span>
        <span class="val price">¥{{ route.query.price }}</span>
      </div>
    </div>

    <div class="btn-group">
      <el-button type="primary" round class="main-btn" @click="$router.push('/home')">返回首页</el-button>
      <el-button round class="sub-btn" @click="$router.push('/my-orders')">查看订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { Select } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.success-page {
  display: flex; flex-direction: column; align-items: center; padding-top: 60px;
  background: white; min-height: 100vh;
}
.icon-box {
  width: 80px; height: 80px; background: #67C23A; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(103, 194, 58, 0.3);
}
.success-icon { font-size: 40px; color: white; }
h2 { font-size: 24px; color: #333; margin: 0 0 10px 0; }
.sub-text { color: #999; font-size: 14px; margin-bottom: 40px; }

.info-card { width: 80%; background: #f9f9f9; padding: 20px; border-radius: 12px; margin-bottom: 40px; }
.row { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; color: #666; }
.row:last-child { margin-bottom: 0; }
.val { color: #333; font-weight: bold; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 150px; }
.price { color: #f56c6c; }

.btn-group { display: flex; flex-direction: column; gap: 15px; width: 80%; }
.main-btn { height: 44px; font-size: 16px; width: 100%; }
.sub-btn { height: 44px; font-size: 16px; width: 100%; margin-left: 0 !important; }
</style>
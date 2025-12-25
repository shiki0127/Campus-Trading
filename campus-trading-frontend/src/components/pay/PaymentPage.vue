<template>
  <div class="pay-page">
    <el-page-header @back="$router.back()" content="确认交易" class="page-header"></el-page-header>

    <!-- 商品卡片 -->
    <div class="order-card" v-if="product">
      <img :src="product.imageUrl || 'https://via.placeholder.com/100'" class="prod-img">
      <div class="prod-info">
        <div class="title">{{ product.title }}</div>
        <div class="tags">
          <el-tag size="small">{{ product.campus }}</el-tag>
          <el-tag size="small" type="warning" class="ml-2">担保交易</el-tag>
        </div>
        <div class="price">¥{{ product.price }}</div>
      </div>
    </div>

    <!-- 支付方式 -->
    <div class="method-card">
      <div class="card-title">支付方式</div>
      <el-radio-group v-model="payMethod" class="w-full">
        <div class="method-item">
          <div class="label">
            <el-icon color="#09bb07" :size="24"><ChatDotRound /></el-icon> <!-- 暂用图标代替微信 -->
            <span class="text">微信支付</span>
          </div>
          <el-radio label="wechat" size="large"><span class="hidden"></span></el-radio>
        </div>
        <div class="divider"></div>
        <div class="method-item">
          <div class="label">
            <el-icon color="#1677ff" :size="24"><Platform /></el-icon> <!-- 暂用图标代替支付宝 -->
            <span class="text">支付宝</span>
          </div>
          <el-radio label="alipay" size="large"><span class="hidden"></span></el-radio>
        </div>
      </el-radio-group>
    </div>

    <!-- 底部栏 -->
    <div class="bottom-bar">
      <div class="total">
        合计: <span class="num">¥{{ product ? product.price : '0.00' }}</span>
      </div>
      <el-button type="primary" round class="pay-btn" @click="handlePay" :loading="loading">
        立即支付
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ChatDotRound, Platform } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const productId = route.query.productId
const product = ref(null)
const payMethod = ref('wechat')
const loading = ref(false)

// 1. 获取商品详情
const loadProduct = async () => {
  try {
    const res = await request.get(`/product/${productId}`)
    product.value = res
  } catch (e) {
    console.error(e)
  }
}

// 2. 提交订单并支付
const handlePay = async () => {
  if (!userStore.userInfo) return router.push('/login')

  loading.value = true
  try {
    // A. 创建订单
    const orderData = {
      productId: product.value.id,
      buyerId: userStore.userInfo.id
    }
    const orderRes = await request.post('/order/create', orderData)
    const orderId = orderRes.id

    // B. 模拟支付 (延迟一下更有感觉)
    setTimeout(async () => {
      await request.post(`/order/pay/${orderId}?payMethod=${payMethod.value}`)
      ElMessage.success('支付成功')
      router.replace({
        path: '/pay-success',
        query: {
          title: product.value.title,
          price: product.value.price
        }
      })
    }, 1000)

  } catch (e) {
    loading.value = false
    console.error(e)
  }
}

onMounted(() => {
  if(!productId) {
    ElMessage.error('参数错误')
    router.back()
    return
  }
  loadProduct()
})
</script>

<style scoped>
.pay-page { background: #f5f7fa; min-height: 100vh; padding-bottom: 70px; }
.page-header { background: white; padding: 15px; }

.order-card { background: white; margin: 15px; padding: 15px; border-radius: 12px; display: flex; }
.prod-img { width: 90px; height: 90px; border-radius: 8px; object-fit: cover; background: #eee; }
.prod-info { flex: 1; margin-left: 12px; display: flex; flex-direction: column; justify-content: space-between; }
.title { font-weight: bold; font-size: 15px; line-height: 1.4; }
.price { color: #f56c6c; font-weight: bold; font-size: 18px; text-align: right; }
.ml-2 { margin-left: 5px; }

.method-card { background: white; margin: 15px; padding: 0 15px; border-radius: 12px; }
.card-title { padding: 15px 0; border-bottom: 1px solid #eee; font-weight: bold; font-size: 14px; }
.method-item { display: flex; justify-content: space-between; align-items: center; padding: 15px 0; }
.label { display: flex; align-items: center; }
.label .text { margin-left: 10px; font-size: 15px; }
.divider { height: 1px; background: #f9f9f9; }
.w-full { width: 100%; }

.bottom-bar {
  position: fixed; bottom: 0; left: 0; width: 100%; height: 60px; background: white;
  border-top: 1px solid #eee; display: flex; justify-content: space-between; align-items: center;
  padding: 0 20px;
}
.total { font-size: 14px; }
.num { color: #f56c6c; font-size: 20px; font-weight: bold; margin-left: 5px; }
.pay-btn { width: 120px; height: 40px; font-size: 16px; }
</style>
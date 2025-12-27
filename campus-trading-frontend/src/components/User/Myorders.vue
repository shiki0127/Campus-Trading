<template>
  <div class="order-page">
    <el-page-header @back="$router.back()" content="我的购买" class="page-header"></el-page-header>

    <div class="list-container">
      <div v-if="orders.length === 0" class="empty-state">
        <el-icon :size="60"><Goods /></el-icon>
        <p>还没有买过东西哦</p>
      </div>

      <div v-for="item in orders" :key="item.id" class="order-item">
        <div class="item-header">
          <span class="time">{{ formatDate(item.createTime) }}</span>
          <span class="status">{{ getStatusText(item.status) }}</span>
        </div>
        <div class="item-body">
          <img :src="item.productImage || 'https://via.placeholder.com/80'" class="prod-img">
          <div class="info">
            <div class="title">{{ item.productTitle }}</div>
            <div class="price">¥{{ item.price }}</div>
            <div class="seller-name">卖家: {{ item.sellerName || '未知' }}</div>
          </div>
        </div>
        <div class="item-footer">
          <el-button size="small" round @click="contactSeller(item)">联系卖家</el-button>

          <el-button
              size="small"
              round
              type="primary"
              plain
              v-if="item.status === 2"
              @click="$router.push(`/product/${item.productId}`)"
          >
            再次购买
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Goods } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus' // 引入 ElMessage

const userStore = useUserStore()
const router = useRouter()
const orders = ref([])

onMounted(async () => {
  if(userStore.userInfo) {
    try {
      const res = await request.get(`/order/my/${userStore.userInfo.id}`)
      orders.value = res
    } catch (e) {
      console.error(e)
    }
  }
})

// 核心跳转逻辑
const contactSeller = (item) => {
  // 逻辑修复：防止联系自己
  if (item.sellerId === userStore.userInfo.id) {
    ElMessage.warning('不能联系自己')
    return
  }

  router.push({
    path: `/chat/${item.sellerId}`,
    query: {
      name: item.sellerName || '卖家',
      productId: item.productId,
      productTitle: item.productTitle,
      productPrice: item.price,
      productImage: item.productImage,
      role: 'buyer',
      orderId: item.id
    }
  })
}

const formatDate = (dateStr) => {
  if(!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth()+1}-${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2,'0')}`
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '已完成' }
  return map[status] || '处理中'
}
</script>

<style scoped>
.order-page { background: #f5f7fa; min-height: 100vh; }
.page-header { background: white; padding: 15px; border-bottom: 1px solid #eee; }
.list-container { padding: 15px; }
.empty-state { text-align: center; color: #ccc; margin-top: 80px; }

.order-item { background: white; border-radius: 12px; padding: 15px; margin-bottom: 15px; }
.item-header { display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 12px; color: #999; border-bottom: 1px solid #f9f9f9; padding-bottom: 8px; }
.status { color: #67C23A; }

.item-body { display: flex; margin-bottom: 12px; }
.prod-img { width: 70px; height: 70px; border-radius: 6px; object-fit: cover; background: #eee; }
.info { flex: 1; margin-left: 10px; display: flex; flex-direction: column; justify-content: space-between; }
.title { font-size: 14px; font-weight: bold; color: #333; }
.price { font-weight: bold; color: #333; }
.seller-name { font-size: 11px; color: #999; }

.item-footer { display: flex; justify-content: flex-end; }
</style>
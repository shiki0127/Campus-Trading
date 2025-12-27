<template>
  <div class="order-page">
    <el-page-header @back="$router.back()" content="我的卖出" class="page-header"></el-page-header>

    <div class="list-container">
      <div v-if="orders.length === 0" class="empty-state">
        <el-icon :size="60"><SoldOut /></el-icon>
        <p>还没有卖出过宝贝，继续加油！</p>
      </div>

      <div v-for="item in orders" :key="item.id" class="order-item">
        <div class="item-header">
          <span class="time">订单号: {{ item.orderNo.substring(0, 8) }}</span>
          <span class="status" :class="getStatusColor(item.status)">
            {{ getStatusText(item.status) }}
          </span>
        </div>
        <div class="item-body" @click="goToDetail(item.productId)">
          <img :src="item.productImage || 'https://via.placeholder.com/80'" class="prod-img">
          <div class="info">
            <div class="title">{{ item.productTitle }}</div>
            <div class="price">成交价: ¥{{ item.price }}</div>
            <div class="buyer">买家: {{ item.buyerName || '未知用户' }}</div>
          </div>
        </div>
        <div class="item-footer">
          <!-- 恢复关联：联系买家时带上商品信息，并标记身份为卖家 -->
          <el-button size="small" round @click="contactBuyer(item)">联系买家</el-button>

          <el-button
              v-if="item.status === 1"
              size="small"
              round
              type="primary"
              @click="handleDeliver(item)"
          >
            立即发货
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { SoldOut } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const orders = ref([])

onMounted(() => {
  if(userStore.userInfo) {
    loadData()
  }
})

const loadData = async () => {
  try {
    const res = await request.get(`/order/sales/${userStore.userInfo.id}`)
    orders.value = res
  } catch (e) {
    console.error(e)
  }
}

const goToDetail = (productId) => {
  router.push(`/product/${productId}`)
}

const handleDeliver = (item) => {
  ElMessageBox.confirm('确认已将商品交付给买家吗?', '发货确认', {
    confirmButtonText: '确认发货',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    await request.post(`/order/deliver/${item.id}`)
    ElMessage.success('发货成功')
    loadData()
  })
}

const contactBuyer = (item) => {
  // 优化：恢复商品上下文，同时增加 role 参数
  router.push({
    path: `/chat/${item.buyerId}`,
    query: {
      name: item.buyerName || '买家',
      // 传递商品信息，恢复“上下文关联”
      productId: item.productId,
      productTitle: item.productTitle,
      productPrice: item.price,
      productImage: item.productImage,
      // 关键：标记当前视角是卖家 (seller)，后续 ChatDetail 可以根据这个字段
      // 将“我想要”按钮改为“发送订单详情”或隐藏，避免尴尬
      role: 'seller',
      orderId: item.id // 还可以带上订单ID
    }
  })
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '已完成' }
  return map[status] || '未知'
}

const getStatusColor = (status) => {
  if(status === 0) return 'text-gray'
  if(status === 1) return 'text-red'
  if(status === 2) return 'text-green'
}
</script>

<style scoped>
.order-page { background: #f5f7fa; min-height: 100vh; }
.page-header { background: white; padding: 15px; border-bottom: 1px solid #eee; }
.list-container { padding: 15px; }
.empty-state { text-align: center; color: #ccc; margin-top: 80px; }

.order-item { background: white; border-radius: 12px; padding: 15px; margin-bottom: 15px; }
.item-header { display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 12px; color: #999; border-bottom: 1px solid #f9f9f9; padding-bottom: 8px; }
.text-green { color: #67C23A; }
.text-red { color: #F56C6C; font-weight: bold; }
.text-gray { color: #909399; }

.item-body { display: flex; margin-bottom: 12px; cursor: pointer; }
.prod-img { width: 70px; height: 70px; border-radius: 6px; object-fit: cover; background: #eee; }
.info { flex: 1; margin-left: 10px; display: flex; flex-direction: column; justify-content: space-between; }
.title { font-size: 14px; font-weight: bold; color: #333; }
.price { color: #f56c6c; font-weight: bold; font-size: 13px; }
.buyer { font-size: 11px; color: #666; }

.item-footer { display: flex; justify-content: flex-end; }
</style>
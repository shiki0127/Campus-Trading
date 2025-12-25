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
          <span class="time">订单号: {{ item.orderNo.substring(0, 8) }}...</span>
          <!-- 状态显示逻辑 -->
          <span class="status" :class="getStatusColor(item.status)">
            {{ getStatusText(item.status) }}
          </span>
        </div>
        <div class="item-body">
          <img :src="item.productImage || 'https://via.placeholder.com/80'" class="prod-img">
          <div class="info">
            <div class="title">{{ item.productTitle }}</div>
            <div class="price">成交价: ¥{{ item.price }}</div>
            <div class="buyer">买家ID: {{ item.buyerId.substring(0,6) }}...</div>
          </div>
        </div>
        <div class="item-footer">
          <el-button size="small" round @click="contactBuyer(item)">联系买家</el-button>
          <!-- 只有已支付(1)状态下才能发货 -->
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

onMounted(async () => {
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

const handleDeliver = (item) => {
  ElMessageBox.confirm('确认已将商品交付给买家吗?', '发货确认', {
    confirmButtonText: '确认发货',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    await request.post(`/order/deliver/${item.id}`)
    ElMessage.success('发货成功')
    loadData() // 刷新列表
  })
}

const contactBuyer = (item) => {
  // 暂时跳到聊天页，名字暂用买家ID代替
  router.push(`/chat/${item.buyerId}?name=买家${item.buyerId.substring(0,4)}`)
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

.item-body { display: flex; margin-bottom: 12px; }
.prod-img { width: 70px; height: 70px; border-radius: 6px; object-fit: cover; background: #eee; }
.info { flex: 1; margin-left: 10px; display: flex; flex-direction: column; justify-content: space-between; }
.title { font-size: 14px; font-weight: bold; color: #333; }
.price { color: #f56c6c; font-weight: bold; font-size: 13px; }
.buyer { font-size: 11px; color: #999; }

.item-footer { display: flex; justify-content: flex-end; }
</style>
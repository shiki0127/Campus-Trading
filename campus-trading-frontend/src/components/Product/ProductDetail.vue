<template>
  <div class="detail-container" v-if="product">
    <div class="top-bar">
      <div class="back-btn" @click="$router.back()"><el-icon><ArrowLeft /></el-icon></div>
    </div>
    <div class="img-container">
      <img :src="product.imageUrl || 'https://via.placeholder.com/400'" class="main-img" />
    </div>
    <div class="info-card">
      <div class="price-row">
        <span class="price">{{ product.price }}</span>
        <span class="wants">12人想要</span>
      </div>
      <h1 class="title">{{ product.title }}</h1>
      <div class="tags">
        <el-tag size="small" effect="dark">{{ product.campus }}校区</el-tag>
        <el-tag size="small" type="info" class="ml-2">{{ product.category }}</el-tag>
        <span class="time-tag">发布于 {{ formatDate(product.createTime) }}</span>
      </div>
    </div>
    <div class="seller-card">
      <el-avatar :size="40" :src="product.sellerAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
      <div class="seller-info">
        <div class="name">{{ product.sellerName }}</div>
        <div class="desc">信用极好 | 交易活跃</div>
      </div>
      <el-button size="small" round v-if="!isMyself">关注</el-button>
    </div>
    <div class="desc-card">
      <h3>商品详情</h3>
      <p class="content">{{ product.description || '卖家很懒，没有留下详细描述，建议直接联系咨询细节~' }}</p>
    </div>

    <!-- 逻辑修复：底部操作栏 -->
    <!-- 如果是自己发布的商品，显示管理提示 -->
    <div class="bottom-action-bar myself" v-if="isMyself">
      <span class="myself-text">这是您发布的商品</span>
    </div>
    <!-- 否则显示购买按钮 -->
    <div class="bottom-action-bar" v-else>
      <div class="action-btn" @click="handleContact">
        <el-icon :size="20"><ChatDotRound /></el-icon>
        <span>联系卖家</span>
      </div>
      <div class="action-btn">
        <el-icon :size="20"><Star /></el-icon>
        <span>想要</span>
      </div>
      <div class="buy-btn" @click="handleBuy">立即购买</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../../api/request'
import { ArrowLeft, ChatDotRound, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref(null)

// 判断是否是自己发布的商品
const isMyself = computed(() => {
  return userStore.userInfo && product.value && userStore.userInfo.id === product.value.sellerId
})

const loadDetail = async () => {
  try {
    const res = await request.get(`/product/${route.params.id}`)
    product.value = res
  } catch (e) {
    console.error(e)
  }
}

const formatDate = (dateStr) => {
  if(!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth()+1}月${date.getDate()}日`
}

const handleContact = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: `/chat/${product.value.sellerId}`,
    query: {
      name: product.value.sellerName,
      productId: product.value.id,
      productTitle: product.value.title,
      productPrice: product.value.price,
      productImage: product.value.imageUrl
    }
  })
}

const handleBuy = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/payment?productId=${product.value.id}`)
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.detail-container { padding-bottom: 80px; background-color: #f8f8f8; min-height: 100vh; }
.top-bar { position: fixed; top: 0; left: 0; width: 100%; z-index: 10; padding: 10px; pointer-events: none; }
.back-btn { width: 36px; height: 36px; background: rgba(0,0,0,0.4); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; cursor: pointer; pointer-events: auto; }
.img-container { width: 100%; height: 350px; background: white; }
.main-img { width: 100%; height: 100%; object-fit: cover; }
.info-card { background: white; padding: 15px; margin-bottom: 10px; }
.price-row { display: flex; align-items: flex-end; margin-bottom: 10px; }
.price { color: #f56c6c; font-size: 24px; font-weight: bold; margin-right: 10px; }
.price::before { content: '¥'; font-size: 16px; }
.wants { color: #999; font-size: 12px; margin-bottom: 4px; }
.title { font-size: 18px; font-weight: bold; margin: 0 0 10px 0; line-height: 1.4; }
.tags { display: flex; align-items: center; }
.ml-2 { margin-left: 8px; }
.time-tag { margin-left: auto; color: #999; font-size: 12px; }
.seller-card { background: white; padding: 12px 15px; margin-bottom: 10px; display: flex; align-items: center; }
.seller-info { flex: 1; margin-left: 10px; }
.seller-info .name { font-weight: bold; font-size: 14px; }
.seller-info .desc { font-size: 11px; color: #999; margin-top: 2px; }
.desc-card { background: white; padding: 15px; min-height: 200px; }
.desc-card h3 { margin: 0 0 10px 0; font-size: 15px; border-left: 3px solid #409EFF; padding-left: 8px; }
.content { font-size: 14px; color: #444; line-height: 1.6; white-space: pre-wrap; }
.bottom-action-bar { position: fixed; bottom: 0; left: 0; width: 100%; height: 60px; background: white; border-top: 1px solid #eee; display: flex; align-items: center; padding: 0 15px; box-shadow: 0 -2px 10px rgba(0,0,0,0.05); }
.action-btn { display: flex; flex-direction: column; align-items: center; justify-content: center; width: 60px; color: #666; font-size: 10px; cursor: pointer; }
.action-btn .el-icon { margin-bottom: 2px; }
.buy-btn { flex: 1; height: 40px; background: #409EFF; color: white; border-radius: 20px; display: flex; align-items: center; justify-content: center; font-weight: bold; margin-left: 10px; cursor: pointer; }
.buy-btn:active { opacity: 0.9; }

/* 只有自己看自己时的底部栏样式 */
.bottom-action-bar.myself {
  justify-content: center;
  background-color: #f5f7fa;
  color: #999;
  font-size: 14px;
}
</style>
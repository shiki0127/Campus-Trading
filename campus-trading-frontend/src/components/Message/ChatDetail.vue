<template>
  <div class="chat-page">
    <!-- 头部 -->
    <div class="chat-header">
      <el-icon class="back-icon" @click="$router.back()"><ArrowLeft /></el-icon>
      <span class="chat-title">{{ chatName }}</span>
      <el-icon class="more-icon"><MoreFilled /></el-icon>
    </div>

    <!-- 商品/订单上下文卡片 -->
    <div class="prod-context" v-if="productContext">
      <img :src="productContext.image" class="pc-img">
      <div class="pc-info">
        <div class="pc-title-tag">
          <!-- 标签逻辑：卖家显示'关联订单'，买家根据是否已买显示'已买到'或'想咨询' -->
          <el-tag size="small" :type="tagType" effect="plain">
            {{ tagText }}
          </el-tag>
        </div>
        <div class="pc-title">{{ productContext.title }}</div>
        <div class="pc-price">¥{{ productContext.price }}</div>
      </div>

      <!-- 动态按钮 -->
      <el-button
          :type="isSeller ? 'success' : 'primary'"
          size="small"
          plain
          @click="sendContextLink"
      >
        {{ buttonText }}
      </el-button>
    </div>

    <!-- 消息内容区 -->
    <div class="msg-container" ref="msgContainer">
      <div v-for="(msg, index) in history" :key="index" class="msg-row" :class="{ 'me': isMe(msg) }">
        <div class="bubble">{{ msg.content }}</div>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="input-area">
      <input v-model="text" @keyup.enter="send" type="text" placeholder="发消息..." />
      <button @click="send" :disabled="!text">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, MoreFilled } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const userStore = useUserStore()

const targetUserId = route.params.id
const chatName = ref(route.query.name || '用户')

// 1. 判断角色和状态
const isSeller = computed(() => route.query.role === 'seller')
const hasBought = computed(() => !!route.query.orderId && !isSeller.value) // 有订单ID且不是卖家，就是已购买的买家

// 2. UI 文案逻辑
const tagType = computed(() => {
  if (isSeller.value) return 'success'
  if (hasBought.value) return 'warning'
  return 'primary'
})

const tagText = computed(() => {
  if (isSeller.value) return '关联订单'
  if (hasBought.value) return '已买到'
  return '想咨询'
})

const buttonText = computed(() => {
  if (isSeller.value) return '核对订单'
  if (hasBought.value) return '咨询订单'
  return '发链接'
})

// 3. 计算商品上下文
const productContext = computed(() => {
  if (route.query.productId) {
    return {
      id: route.query.productId,
      title: route.query.productTitle,
      price: route.query.productPrice,
      image: route.query.productImage || 'https://via.placeholder.com/100',
      orderId: route.query.orderId
    }
  }
  return null
})

const text = ref('')
const msgContainer = ref(null)
const history = ref([])

const isMe = (msg) => msg.fromId === userStore.userInfo?.id

const loadHistory = async () => {
  if (!userStore.userInfo) return
  try {
    const res = await request.get('/chat/history', {
      params: { userId1: userStore.userInfo.id, userId2: targetUserId }
    })
    history.value = res
    scrollToBottom()
  } catch (e) {
    console.error(e)
  }
}

const send = async () => {
  if(!text.value.trim()) return
  const content = text.value
  text.value = ''
  await doSendMessage(content)
}

// 4. 核心逻辑：根据三种状态发送不同的快捷语
const sendContextLink = async () => {
  let linkText = ''

  if (isSeller.value) {
    // 卖家视角
    linkText = `[订单核对] 您好，这是您拍下的宝贝：${productContext.value.title}，请确认收货地址无误。`
  } else if (hasBought.value) {
    // 已购买买家视角 (修复了“还在吗”的尴尬)
    linkText = `[售后咨询] 您好，我买了这件商品：${productContext.value.title}，想咨询一下发货时间。`
  } else {
    // 潜在买家视角
    linkText = `[商品咨询] 我对这个感兴趣：${productContext.value.title} (¥${productContext.value.price})，还在吗？`
  }

  await doSendMessage(linkText)
}

const doSendMessage = async (content) => {
  try {
    const msgData = {
      fromId: userStore.userInfo.id,
      toId: targetUserId,
      content: content
    }
    const newMsg = await request.post('/chat/send', msgData)
    history.value.push(newMsg)
    scrollToBottom()
  } catch (e) {
    console.error(e)
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if(msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.chat-page { display: flex; flex-direction: column; height: 100vh; background-color: #f2f2f2; }
.chat-header { height: 50px; background: white; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between; padding: 0 15px; }
.chat-title { font-weight: bold; font-size: 16px; }

.prod-context {
  background: white; padding: 10px 15px; margin: 10px 15px 0 15px; border-radius: 8px;
  display: flex; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.pc-img { width: 48px; height: 48px; border-radius: 4px; object-fit: cover; background: #eee; }
.pc-info { flex: 1; margin-left: 10px; font-size: 12px; display: flex; flex-direction: column; justify-content: center; }
.pc-title-tag { margin-bottom: 2px; }
.pc-title { font-weight: bold; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 150px; }
.pc-price { color: #f56c6c; margin-top: 2px; font-weight: bold; }

.msg-container { flex: 1; overflow-y: auto; padding: 15px; display: flex; flex-direction: column; gap: 15px; }
.msg-row { display: flex; }
.msg-row.me { justify-content: flex-end; }
.bubble { max-width: 70%; padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.5; background: white; color: #333; border-top-left-radius: 0; word-break: break-all; }
.msg-row.me .bubble { background: #409EFF; color: white; border-top-left-radius: 12px; border-top-right-radius: 0; }

.input-area { height: 50px; background: white; border-top: 1px solid #eee; padding: 8px 15px; display: flex; align-items: center; }
.input-area input { flex: 1; height: 34px; background: #f5f5f5; border: none; border-radius: 17px; padding: 0 15px; outline: none; margin-right: 10px; }
.input-area button { border: none; background: transparent; color: #409EFF; font-weight: bold; cursor: pointer; }
.input-area button:disabled { color: #ccc; }
</style>
<template>
  <div class="chat-page">
    <!-- 头部 -->
    <div class="chat-header">
      <el-icon class="back-icon" @click="$router.back()"><ArrowLeft /></el-icon>
      <span class="chat-title">{{ chatName }}</span>
      <el-icon class="more-icon"><MoreFilled /></el-icon>
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
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, MoreFilled } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const userStore = useUserStore()

// 获取路由参数：对方的 ID 和 名字
const targetUserId = route.params.id
const chatName = ref(route.query.name || '用户')

const text = ref('')
const msgContainer = ref(null)
const history = ref([])

// 判断消息是否是我发的
const isMe = (msg) => {
  return msg.fromId === userStore.userInfo?.id
}

// 加载历史记录
const loadHistory = async () => {
  if (!userStore.userInfo) return
  try {
    const res = await request.get('/chat/history', {
      params: {
        userId1: userStore.userInfo.id,
        userId2: targetUserId
      }
    })
    history.value = res
    scrollToBottom()
  } catch (e) {
    console.error(e)
  }
}

// 发送消息
const send = async () => {
  if(!text.value.trim()) return

  const content = text.value
  text.value = '' // 立即清空输入框，提升体验

  try {
    const msgData = {
      fromId: userStore.userInfo.id,
      toId: targetUserId,
      content: content
    }
    // 调用后端发送接口
    const newMsg = await request.post('/chat/send', msgData)

    // 发送成功后将消息加入列表并滚动到底部
    history.value.push(newMsg)
    scrollToBottom()
  } catch (e) {
    console.error("发送失败", e)
  }
}

// 滚动到底部辅助函数
const scrollToBottom = async () => {
  await nextTick()
  if(msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

onMounted(() => {
  loadHistory()
  // 如果需要实时效果，可以开启下面的轮询 (每3秒刷新一次)
  // setInterval(loadHistory, 3000)
})
</script>

<style scoped>
.chat-page { display: flex; flex-direction: column; height: 100vh; background-color: #f2f2f2; }
.chat-header {
  height: 50px; background: white; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between; padding: 0 15px;
}
.chat-title { font-weight: bold; font-size: 16px; }
.back-icon, .more-icon { font-size: 20px; cursor: pointer; color: #333; }

.msg-container { flex: 1; overflow-y: auto; padding: 15px; display: flex; flex-direction: column; gap: 15px; }
.msg-row { display: flex; }
.msg-row.me { justify-content: flex-end; }
.bubble {
  max-width: 70%; padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.5;
  background: white; color: #333; border-top-left-radius: 0; word-break: break-all;
}
.msg-row.me .bubble {
  background: #409EFF; color: white; border-top-left-radius: 12px; border-top-right-radius: 0;
}

.input-area {
  height: 50px; background: white; border-top: 1px solid #eee; padding: 8px 15px; display: flex; align-items: center;
}
.input-area input {
  flex: 1; height: 34px; background: #f5f5f5; border: none; border-radius: 17px; padding: 0 15px; outline: none; margin-right: 10px;
}
.input-area button {
  border: none; background: transparent; color: #409EFF; font-weight: bold; cursor: pointer;
}
.input-area button:disabled { color: #ccc; }
</style>
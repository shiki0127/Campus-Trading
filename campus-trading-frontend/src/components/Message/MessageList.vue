<template>
  <div class="message-container">
    <div class="msg-header">消息</div>

    <!-- 模拟 INS 风格 Story (装饰用，暂未对接) -->
    <div class="story-bar">
      <div class="story-item">
        <div class="avatar-ring admin">
          <img src="https://via.placeholder.com/50" />
        </div>
        <span>系统通知</span>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="chat-list">
      <div v-if="chatList.length === 0" class="empty-tip">暂无消息记录</div>

      <div v-for="chat in chatList" :key="chat.id" class="chat-item" @click="goChat(chat)">
        <!-- 显示头像，如果没有则显示默认图 -->
        <el-avatar :size="50" :src="chat.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>

        <div class="chat-content">
          <div class="top-row">
            <span class="name">{{ chat.name }}</span>
            <span class="time">{{ formatTime(chat.time) }}</span>
          </div>
          <div class="msg-preview">{{ chat.lastMsg }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const chatList = ref([])

onMounted(async () => {
  if (userStore.userInfo) {
    try {
      // 调用后端接口获取最近聊天列表
      const res = await request.get(`/chat/list/${userStore.userInfo.id}`)
      chatList.value = res
    } catch (e) {
      console.error(e)
    }
  }
})

const goChat = (chat) => {
  // 跳转到详情页，带上对方的ID和名字
  router.push(`/chat/${chat.id}?name=${chat.name}`)
}

// 时间格式化工具
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  // 如果是今天，只显示时间；如果是以前，显示日期
  if (date.toDateString() === now.toDateString()) {
    return `${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
  } else {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
}
</script>

<style scoped>
.message-container { padding-bottom: 70px; background: white; min-height: 100vh; }
.msg-header { padding: 15px; font-weight: bold; font-size: 18px; border-bottom: 1px solid #eee; position: sticky; top: 0; background: white; z-index: 10; }

.story-bar { display: flex; overflow-x: auto; padding: 15px; border-bottom: 1px solid #f5f5f5; }
.story-bar::-webkit-scrollbar { display: none; }
.story-item { display: flex; flex-direction: column; align-items: center; margin-right: 15px; min-width: 60px; }
.avatar-ring { width: 56px; height: 56px; border-radius: 50%; padding: 2px; border: 2px solid #ddd; margin-bottom: 5px; }
.avatar-ring.admin { border-color: #409EFF; }
.avatar-ring img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
.story-item span { font-size: 11px; color: #666; }

.chat-list { padding: 0; }
.chat-item { display: flex; padding: 15px; align-items: center; cursor: pointer; }
.chat-item:active { background-color: #f9f9f9; }
.chat-content { flex: 1; margin-left: 15px; border-bottom: 1px solid #f9f9f9; padding-bottom: 15px; }
.top-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
.name { font-weight: bold; font-size: 15px; color: #333; }
.time { font-size: 11px; color: #bbb; }
.msg-preview { color: #888; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 250px; }
.empty-tip { text-align: center; padding: 30px; color: #999; font-size: 14px; }
</style>
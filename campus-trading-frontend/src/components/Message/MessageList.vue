<template>
  <div class="message-container">
    <div class="msg-header">消息中心</div>

    <!-- 顶部通知栏 -->
    <div class="notice-bar">
      <div class="notice-card system">
        <div class="icon-box">
          <el-icon><BellFilled /></el-icon>
        </div>
        <div class="text-box">
          <div class="n-title">系统通知</div>
          <div class="n-desc">暂无新通知</div>
        </div>
      </div>
      <div class="notice-card activity">
        <div class="icon-box">
          <el-icon><Flag /></el-icon>
        </div>
        <div class="text-box">
          <div class="n-title">交易动态</div>
          <div class="n-desc">订单状态更新</div>
        </div>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="chat-list">
      <div v-if="chatList.length === 0" class="empty-tip">暂无消息记录</div>

      <div v-for="chat in chatList" :key="chat.id" class="chat-item-wrapper">
        <div class="chat-item" @click="goChat(chat)">
          <el-avatar :size="50" :src="chat.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>

          <div class="chat-content">
            <div class="top-row">
              <span class="name">{{ chat.name }}</span>
              <span class="time">{{ formatTime(chat.time) }}</span>
            </div>
            <div class="msg-preview">{{ chat.lastMsg }}</div>
          </div>

          <!-- 删除按钮 (阻止冒泡) -->
          <div class="delete-btn" @click.stop="handleDelete(chat)">
            <el-icon><Delete /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { BellFilled, Flag, Delete } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const chatList = ref([])

const loadChats = async () => {
  if (userStore.userInfo) {
    try {
      const res = await request.get(`/chat/list/${userStore.userInfo.id}`)
      chatList.value = res
    } catch (e) {
      console.error(e)
    }
  }
}

onMounted(() => {
  loadChats()
})

const goChat = (chat) => {
  router.push(`/chat/${chat.id}?name=${chat.name}`)
}

// 修复：真实删除逻辑
const handleDelete = (chat) => {
  ElMessageBox.confirm('确定要删除该对话吗？(这将清空聊天记录)', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 调用后端删除接口，传入对方ID (chat.id) 和当前用户ID
      await request.delete(`/chat/delete/${chat.id}`, {
        params: {
          currentUserId: userStore.userInfo.id
        }
      })

      // 后端删除成功后，前端移除
      chatList.value = chatList.value.filter(item => item.id !== chat.id)
      ElMessage.success('已删除')
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return `${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
  } else {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
}
</script>

<style scoped>
.message-container { padding-bottom: 70px; background: white; min-height: 100vh; }
.msg-header { padding: 15px; font-weight: bold; font-size: 18px; border-bottom: 1px solid #f5f5f5; position: sticky; top: 0; background: white; z-index: 10; }

.notice-bar { padding: 15px; display: flex; gap: 10px; overflow-x: auto; background: #fff; border-bottom: 10px solid #f5f7fa; }
.notice-card {
  flex: 1; min-width: 140px; height: 70px; border-radius: 12px; display: flex; align-items: center; padding: 0 15px;
  color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.05); cursor: pointer;
}
.notice-card.system { background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%); }
.notice-card.activity { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }

.icon-box {
  width: 40px; height: 40px; background: rgba(255,255,255,0.2); border-radius: 50%;
  display: flex; align-items: center; justify-content: center; font-size: 20px;
}
.text-box { margin-left: 10px; }
.n-title { font-weight: bold; font-size: 14px; margin-bottom: 4px; }
.n-desc { font-size: 10px; opacity: 0.9; }

.chat-list { padding: 0; }
.chat-item-wrapper { border-bottom: 1px solid #f9f9f9; }
.chat-item {
  display: flex; padding: 15px; align-items: center; cursor: pointer; position: relative;
  transition: background-color 0.2s;
}
.chat-item:active { background-color: #f5f5f5; }
.chat-content { flex: 1; margin-left: 15px; padding-right: 30px; /* 留出删除按钮空间 */ }
.top-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
.name { font-weight: bold; font-size: 15px; color: #333; }
.time { font-size: 11px; color: #bbb; }
.msg-preview { color: #888; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 220px; }

/* 删除按钮样式 */
.delete-btn {
  padding: 8px;
  color: #ccc;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.delete-btn:hover {
  color: #F56C6C;
  background-color: #fef0f0;
}

.empty-tip { text-align: center; padding: 30px; color: #999; font-size: 14px; }
</style>
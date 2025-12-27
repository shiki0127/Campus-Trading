<template>
  <div class="profile-container">
    <!-- 头部背景与信息 -->
    <div class="header-bg">
      <div class="user-info" v-if="userStore.userInfo">
        <!-- 显示用户头像，若无则显示默认头像 -->
        <el-avatar
            :size="70"
            :src="userStore.userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
            class="avatar-border"
        ></el-avatar>
        <div class="text-info">
          <div class="nickname">{{ userStore.userInfo.nickname }}</div>
          <div class="campus-badge">{{ userStore.userInfo.campus }}校区</div>
        </div>
      </div>
      <!-- 未登录状态 -->
      <div class="user-info" v-else @click="$router.push('/login')">
        <el-avatar :size="70" icon="UserFilled" class="avatar-border"></el-avatar>
        <div class="text-info">
          <div class="nickname">点击登录/注册</div>
          <div class="tips">登录后体验更多功能</div>
        </div>
      </div>
    </div>

    <!-- 功能卡片 (3列布局) -->
    <div class="action-card">
      <div class="card-item" @click="handleGo('/my-publishes')">
        <el-icon class="icon-pub" :size="28"><Shop /></el-icon>
        <span>我发布的</span>
      </div>
      <div class="card-item" @click="handleGo('/my-sales')">
        <el-icon class="icon-sell" :size="28"><Money /></el-icon>
        <span>我卖出的</span>
      </div>
      <div class="card-item" @click="handleGo('/my-orders')">
        <el-icon class="icon-buy" :size="28"><ShoppingBag /></el-icon>
        <span>我买到的</span>
      </div>
    </div>

    <!-- 列表菜单 -->
    <div class="menu-list">
      <!-- 跳转到设置页 -->
      <div class="menu-item" @click="handleGo('/settings')">
        <span>个人设置</span>
        <el-icon><ArrowRight /></el-icon>
      </div>
      <div class="menu-item">
        <span>帮助与反馈</span>
        <el-icon><ArrowRight /></el-icon>
      </div>
      <!-- 退出登录 -->
      <div class="menu-item" v-if="userStore.userInfo" @click="handleLogout">
        <span class="logout-text">退出登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { UserFilled, Shop, ShoppingBag, ArrowRight, Money } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

// 通用跳转逻辑，需登录
const handleGo = (path) => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(path)
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-container { min-height: 100vh; background-color: #f5f7fa; padding-bottom: 70px; }

.header-bg {
  background: linear-gradient(135deg, #409EFF, #79bbff);
  padding: 40px 20px 60px 20px;
  color: white;
}

.user-info { display: flex; align-items: center; cursor: pointer; }
.avatar-border { border: 2px solid rgba(255,255,255,0.8); background: white; color: #ccc; }
.text-info { margin-left: 15px; }
.nickname { font-size: 20px; font-weight: bold; margin-bottom: 5px; }
.campus-badge { background: rgba(255,255,255,0.2); font-size: 12px; padding: 2px 8px; border-radius: 10px; display: inline-block; }
.tips { font-size: 13px; opacity: 0.8; }

.action-card {
  margin: -40px 15px 15px 15px;
  background: white;
  border-radius: 12px;
  padding: 20px 10px;
  display: flex;
  justify-content: space-around;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.card-item { display: flex; flex-direction: column; align-items: center; font-size: 13px; color: #333; cursor: pointer; flex: 1; }
.card-item:active { opacity: 0.7; }
.icon-pub { color: #409EFF; margin-bottom: 8px; }
.icon-buy { color: #67C23A; margin-bottom: 8px; }
.icon-sell { color: #E6A23C; margin-bottom: 8px; }

.menu-list { background: white; margin: 0 15px; border-radius: 12px; overflow: hidden; }
.menu-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center;
  font-size: 15px; color: #333;
  cursor: pointer;
}
.menu-item:last-child { border-bottom: none; }
.menu-item:active { background-color: #f9f9f9; }
.logout-text { color: #F56C6C; width: 100%; text-align: center; font-weight: bold; }
</style>
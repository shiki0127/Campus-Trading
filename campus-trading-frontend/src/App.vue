<template>
  <router-view />

  <!-- 底部导航栏 (只在特定页面显示) -->
  <div v-if="$route.meta.showTab" class="tab-bar">
    <router-link to="/home" class="tab-item" :class="{ active: $route.path === '/home' }">
      <el-icon><HomeFilled /></el-icon>
      <span>首页</span>
    </router-link>
    <router-link to="/publish" class="tab-item" :class="{ active: $route.path === '/publish' }">
      <el-icon><CirclePlusFilled /></el-icon>
      <span>发布</span>
    </router-link>
    <router-link to="/message" class="tab-item" :class="{ active: $route.path === '/message' }">
      <el-icon><Comment /></el-icon>
      <span>消息</span>
    </router-link>
    <router-link to="/profile" class="tab-item" :class="{ active: $route.path === '/profile' }">
      <el-icon><User /></el-icon>
      <span>我的</span>
    </router-link>
  </div>
</template>

<script setup>
// 逻辑在 router-view 中
</script>

<style>
/* 全局样式重置 */
body {
  margin: 0;
  padding: 0;
  background-color: #f3f4f6;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 底部导航栏样式 */
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 1000;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #909399;
  font-size: 12px;
}

.tab-item .el-icon {
  font-size: 24px;
  margin-bottom: 2px;
}

.tab-item.active {
  color: #409EFF;
}
</style>
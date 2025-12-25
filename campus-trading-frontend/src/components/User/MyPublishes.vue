<template>
  <div class="list-page">
    <el-page-header @back="$router.back()" content="我的发布" class="page-header"></el-page-header>

    <div class="list-container">
      <div v-if="products.length === 0" class="empty-state">暂无发布记录</div>

      <div v-for="item in products" :key="item.id" class="list-item">
        <!-- 点击图片或文字跳转详情 -->
        <div class="content-area" @click="$router.push(`/product/${item.id}`)">
          <img :src="item.imageUrl || 'https://via.placeholder.com/100'" class="item-img">
          <div class="item-info">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-status">
              <el-tag size="small" :type="item.status === 0 ? 'success' : 'info'">
                {{ item.status === 0 ? '在售' : '已售出' }}
              </el-tag>
              <span class="price">¥{{ item.price }}</span>
            </div>
          </div>
        </div>

        <!-- 操作栏 -->
        <div class="action-area">
          <el-button
              type="danger"
              icon="Delete"
              circle
              size="small"
              @click.stop="handleDelete(item)"
          ></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const products = ref([])

const loadData = async () => {
  if (userStore.userInfo) {
    try {
      const res = await request.get(`/product/my/${userStore.userInfo.id}`)
      products.value = res
    } catch (e) {
      console.error(e)
    }
  }
}

const handleDelete = (item) => {
  ElMessageBox.confirm('确定要删除这个商品吗?', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.delete(`/product/${item.id}`)
    ElMessage.success('已删除')
    loadData() // 刷新列表
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.list-page { background: #f5f7fa; min-height: 100vh; }
.page-header { background: white; padding: 15px; border-bottom: 1px solid #eee; }
.list-container { padding: 15px; }
.empty-state { text-align: center; color: #999; margin-top: 50px; }

.list-item {
  background: white; padding: 10px; border-radius: 8px; margin-bottom: 10px;
  display: flex; align-items: center; justify-content: space-between;
}
.content-area { display: flex; flex: 1; cursor: pointer; }
.item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; background: #f0f0f0; }
.item-info { flex: 1; margin-left: 10px; display: flex; flex-direction: column; justify-content: space-between; }
.item-title { font-size: 14px; font-weight: bold; line-height: 1.4; }
.item-status { display: flex; justify-content: space-between; align-items: center; margin-right: 10px; }
.price { color: #f56c6c; font-weight: bold; }

.action-area { padding-left: 10px; border-left: 1px solid #f0f0f0; }
</style>
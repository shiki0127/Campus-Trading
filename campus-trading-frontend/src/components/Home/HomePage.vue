<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <!-- 点击Logo重置列表 -->
      <div class="logo" @click="resetList">校园二手</div>
      <div class="search-box">
        <el-input
            v-model="searchKeyword"
            placeholder="搜好物..."
            class="search-input"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearch"
        />
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="carousel-box">
      <el-carousel height="160px" :interval="4000" type="card">
        <el-carousel-item v-for="(img, index) in bannerImages" :key="index">
          <!-- 使用本地图片 -->
          <img class="carousel-img" :src="img" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类 -->
    <div class="category-grid">
      <div v-for="cat in categories" :key="cat.text" class="cat-item" @click="filterByCat(cat.text)">
        <div class="icon-circle" :class="{'active': currentCategory === cat.text}">
          <el-icon :size="20"><component :is="cat.icon" /></el-icon>
        </div>
        <span>{{ cat.text }}</span>
      </div>
    </div>

    <!-- 热门商品 -->
    <div class="product-section">
      <h3 class="section-title">
        {{ currentCategory === '全部' ? '🔥 热门推荐' : `📂 ${currentCategory}` }}
      </h3>
      <div class="product-list">
        <div v-for="prod in productList" :key="prod.id" class="product-card" @click="goDetail(prod.id)">
          <div class="img-box">
            <img :src="prod.imageUrl || 'https://via.placeholder.com/150'" />
            <span class="campus-tag">{{ prod.campus }}</span>
          </div>
          <div class="info">
            <div class="title">{{ prod.title }}</div>
            <div class="price-row">
              <!-- 修复：删除了这里的 ¥ 符号，由 CSS 伪元素生成 -->
              <span class="price">{{ prod.price }}</span>
              <span class="time">{{ formatDate(prod.createTime) }}</span>
            </div>
            <div class="seller">
              <el-avatar :size="16" :src="prod.sellerAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
              <span class="name">{{ prod.sellerName }}</span>
            </div>
          </div>
        </div>
        <!-- 空状态提示 -->
        <div v-if="productList.length === 0" class="empty-tip">
          暂无相关商品
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api/request'
import { Notebook, Monitor, Coffee, Bicycle, Sugar, Goods, Baseball, MoreFilled, Search, Menu } from '@element-plus/icons-vue'

// --- 轮播图配置 ---
// 动态引入 src/assets/images/ 下的图片
const getAssetUrl = (name) => {
  return new URL(`../../assets/images/${name}`, import.meta.url).href
}

const bannerImages = [
  // 请确保你有这些图片，或者暂时换回 https://picsum.photos/400/200?random=1
  getAssetUrl('banner1.jpg'),
  getAssetUrl('banner2.jpg'),
  getAssetUrl('banner3.jpg')
]
// ----------------

const router = useRouter()
const productList = ref([])
const searchKeyword = ref('')
const currentCategory = ref('全部')

const categories = [
  { text: '全部', icon: Menu },
  { text: '书籍', icon: Notebook },
  { text: '数码', icon: Monitor },
  { text: '生活', icon: Coffee },
  { text: '代步', icon: Bicycle },
  { text: '美妆', icon: Sugar },
  { text: '百货', icon: Goods },
  { text: '运动', icon: Baseball },
  { text: '其他', icon: MoreFilled }
]

const loadData = async (params = {}) => {
  try {
    const res = await request.get('/product/list', { params })
    productList.value = res
  } catch (e) {
    console.error(e)
  }
}

// 搜索功能
const handleSearch = () => {
  currentCategory.value = '搜索结果'
  loadData({ keyword: searchKeyword.value })
}

// 分类筛选
const filterByCat = (cat) => {
  searchKeyword.value = ''
  currentCategory.value = cat

  if (cat === '全部') {
    loadData({}) // 加载所有
  } else {
    loadData({ category: cat })
  }
}

// 点击Logo重置
const resetList = () => {
  searchKeyword.value = ''
  currentCategory.value = '全部'
  loadData({})
}

const goDetail = (id) => {
  router.push(`/product/${id}`)
}

const formatDate = (dateStr) => {
  if(!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth()+1}-${date.getDate()}`
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-container { padding-bottom: 70px; background-color: #f3f4f6; min-height: 100vh; }

.nav-bar {
  background-color: #409EFF;
  color: white;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.logo { font-weight: bold; font-size: 18px; margin-right: 12px; white-space: nowrap; cursor: pointer; }
.search-box { flex: 1; }

.nav-bar :deep(.el-input__wrapper) { border-radius: 20px; background-color: rgba(255, 255, 255, 0.9); box-shadow: none; }
.nav-bar :deep(.el-input__inner) { color: #333; }

.carousel-box { padding: 12px; background: transparent; }
.carousel-img { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; }

.category-grid {
  display: grid; grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  padding: 15px; background: white; margin: 0 12px; border-radius: 12px;
}
.cat-item { display: flex; flex-direction: column; align-items: center; font-size: 11px; color: #666; cursor: pointer; }
.icon-circle {
  width: 36px; height: 36px; background: #ecf5ff; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #409EFF; margin-bottom: 5px; transition: all 0.2s;
}
.icon-circle.active { background: #409EFF; color: white; }

.product-section { margin-top: 12px; padding: 0 12px; }
.section-title { font-size: 16px; margin: 10px 4px; font-weight: bold; color: #333; }
.product-list { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }

.product-card { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 2px rgba(0,0,0,0.05); cursor: pointer; }
.img-box { height: 150px; position: relative; background: #eef; }
.img-box img { width: 100%; height: 100%; object-fit: cover; }
.campus-tag {
  position: absolute; bottom: 6px; right: 6px;
  background: rgba(0,0,0,0.6); color: white;
  font-size: 10px; padding: 2px 6px; border-radius: 4px;
}
.info { padding: 10px; }
.title { font-size: 14px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 8px; font-weight: 500; }
.price-row { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 6px; }
.price { color: #f56c6c; font-weight: bold; font-size: 16px; }
.price::before { content: '¥'; font-size: 12px; } /* 这里添加符号 */
.time { font-size: 10px; color: #999; }
.seller { display: flex; align-items: center; font-size: 11px; color: #999; }
.seller .name { margin-left: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 80px;}

.empty-tip { grid-column: span 2; padding: 40px; text-align: center; color: #999; }
</style>
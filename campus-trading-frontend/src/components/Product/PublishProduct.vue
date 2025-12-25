<template>
  <div class="publish-container">
    <div class="nav-header">发布闲置</div>

    <div class="form-content">
      <!-- 图片上传区域 -->
      <div class="upload-area">
        <el-upload
            action="/api/file/upload"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :limit="1"
            :show-file-list="true"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </div>
      <div class="upload-tip" v-if="!form.imageUrl">请上传一张商品主图</div>

      <div class="input-group">
        <textarea v-model="form.title" placeholder="请输入商品标题 品牌型号" class="title-input"></textarea>
        <div class="divider"></div>
        <textarea v-model="form.description" placeholder="描述一下宝贝的品牌型号、新旧程度、入手渠道等..." class="desc-input"></textarea>
      </div>

      <div class="option-list">
        <div class="option-item">
          <div class="label"><el-icon><Money /></el-icon> 价格</div>
          <input type="number" v-model="form.price" placeholder="0.00" class="price-input" />
        </div>
        <div class="option-item">
          <div class="label"><el-icon><Menu /></el-icon> 分类</div>
          <el-select v-model="form.category" placeholder="选择分类" size="small" style="width: 100px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c"></el-option>
          </el-select>
        </div>
        <div class="option-item">
          <div class="label"><el-icon><Location /></el-icon> 校区</div>
          <el-radio-group v-model="form.campus" size="small">
            <el-radio-button label="呈贡">呈贡</el-radio-button>
            <el-radio-button label="东陆">东陆</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <el-button type="primary" round class="submit-btn" @click="handlePublish" :loading="loading">确认发布</el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Plus, Money, Menu, Location } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const categories = ['书籍', '数码', '生活', '代步', '美妆', '百货', '运动', '其他']

const form = reactive({
  title: '',
  description: '',
  price: '',
  category: '',
  campus: '呈贡',
  imageUrl: '' // 默认为空，等待上传成功后填充
})

// 上传成功回调
const handleUploadSuccess = (response) => {
  // response 是后端返回的 Result 对象
  if (response.code === 200) {
    form.imageUrl = response.data // 获取图片URL
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

// 移除图片回调
const handleRemove = () => {
  form.imageUrl = ''
}

const handlePublish = async () => {
  // 1. 登录校验
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 2. 表单校验
  if (!form.title || !form.price || !form.category) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!form.imageUrl) {
    ElMessage.warning('请上传商品图片')
    return
  }

  loading.value = true
  try {
    const postData = {
      ...form,
      sellerId: userStore.userInfo.id,
      sellerName: userStore.userInfo.nickname,
      sellerAvatar: userStore.userInfo.avatar
    }
    await request.post('/product/publish', postData)
    ElMessage.success('发布成功')
    router.push('/home')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.publish-container { background: white; min-height: 100vh; padding-bottom: 70px; }
.nav-header { text-align: center; font-weight: bold; padding: 15px; border-bottom: 1px solid #eee; font-size: 16px; }

.form-content { padding: 20px; }

.upload-area { margin-bottom: 10px; }
.upload-tip { font-size: 12px; color: #f56c6c; margin-bottom: 20px; }

.input-group { border-bottom: 1px solid #eee; margin-bottom: 20px; }
.title-input { width: 100%; border: none; font-size: 18px; font-weight: bold; outline: none; margin-bottom: 10px; resize: none; font-family: inherit; }
.desc-input { width: 100%; border: none; font-size: 14px; height: 100px; outline: none; resize: none; font-family: inherit; }
.divider { height: 1px; background: #f5f5f5; margin: 10px 0; }

.option-list { border-top: 1px solid #f5f5f5; padding-top: 10px; }
.option-item { display: flex; align-items: center; justify-content: space-between; padding: 15px 0; border-bottom: 1px solid #f9f9f9; }
.label { display: flex; align-items: center; color: #333; font-size: 15px; }
.label .el-icon { margin-right: 5px; color: #666; }
.price-input { text-align: right; border: none; outline: none; color: #f56c6c; font-weight: bold; font-size: 16px; }

.submit-btn { width: 100%; margin-top: 30px; height: 44px; font-size: 16px; }
</style>
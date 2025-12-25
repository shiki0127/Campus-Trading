<template>
  <div class="login-container">
    <div class="header">
      <h1>加入我们</h1>
      <p>开启你的校园交易之旅</p>
    </div>

    <div class="form-box">
      <el-input v-model="form.username" placeholder="设置学号/手机号" size="large" :prefix-icon="User" class="mb-4" />
      <el-input v-model="form.nickname" placeholder="设置昵称" size="large" :prefix-icon="Star" class="mb-4" />
      <el-input v-model="form.password" type="password" placeholder="设置密码" size="large" :prefix-icon="Lock" class="mb-4" />

      <!-- 简单的校区选择 -->
      <div class="campus-select mb-6">
        <el-radio-group v-model="form.campus">
          <el-radio-button label="呈贡">呈贡校区</el-radio-button>
          <el-radio-button label="东陆">东陆校区</el-radio-button>
        </el-radio-group>
      </div>

      <el-button type="primary" size="large" class="w-100 btn-round" @click="handleRegister" :loading="loading">
        注 册
      </el-button>

      <div class="footer-links">
        <span>已有账号？</span>
        <span class="link" @click="$router.push('/login')">去登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Star } from '@element-plus/icons-vue'
import request from '../../api/request'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  campus: '呈贡'
})

const handleRegister = async () => {
  if(!form.username || !form.password || !form.nickname) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    await request.post('/user/register', form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 复用 Login 的样式，简单处理 */
.login-container {
  height: 100vh;
  background-color: #fff;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.header { text-align: center; margin-bottom: 40px; }
.header h1 { color: #409EFF; font-size: 28px; margin-bottom: 10px; }
.header p { color: #909399; }
.mb-4 { margin-bottom: 16px; }
.mb-6 { margin-bottom: 24px; }
.w-100 { width: 100%; }
.btn-round { border-radius: 25px; }
.campus-select { display: flex; justify-content: center; }
.footer-links { margin-top: 20px; text-align: center; font-size: 14px; color: #606266; }
.link { color: #409EFF; font-weight: bold; cursor: pointer; margin-left: 5px; }
</style>
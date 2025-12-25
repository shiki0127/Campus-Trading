<template>
  <div class="login-container">
    <div class="header">
      <h1>CampusTrade</h1>
      <p>欢迎回到校园集市</p>
    </div>

    <div class="form-box">
      <el-input
          v-model="form.username"
          placeholder="请输入学号/手机号"
          size="large"
          :prefix-icon="User"
          class="mb-4"
      />

      <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          size="large"
          :prefix-icon="Lock"
          show-password
          class="mb-6"
      />

      <el-button type="primary" size="large" class="w-100 btn-round" @click="handleLogin" :loading="loading">
        登 录
      </el-button>

      <div class="footer-links">
        <span>还没有账号？</span>
        <span class="link" @click="$router.push('/register')">去注册</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../../api/request' // 导入我们封装的 axios
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if(!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    // 调用后端接口 /api/user/login
    const res = await request.post('/user/login', form)
    // 登录成功，保存状态
    userStore.setUser(res)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    // 错误处理已在 request.js 中统一处理，这里不用写
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background-color: #fff;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.header {
  text-align: center;
  margin-bottom: 50px;
}
.header h1 {
  color: #409EFF;
  font-size: 32px;
  margin-bottom: 10px;
}
.header p {
  color: #909399;
}
.mb-4 { margin-bottom: 16px; }
.mb-6 { margin-bottom: 24px; }
.w-100 { width: 100%; }
.btn-round { border-radius: 25px; font-size: 18px; }

.footer-links {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}
.link {
  color: #409EFF;
  font-weight: bold;
  cursor: pointer;
  margin-left: 5px;
}
</style>
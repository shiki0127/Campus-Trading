<template>
  <div class="settings-page">
    <el-page-header @back="$router.back()" content="个人设置" class="page-header"></el-page-header>

    <div class="content">
      <!-- 头像修改 -->
      <div class="avatar-section">
        <el-upload
            class="avatar-uploader"
            action="/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
        >
          <div class="avatar-wrapper">
            <el-avatar :size="80" :src="form.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
            <div class="edit-mask"><el-icon><Camera /></el-icon></div>
          </div>
        </el-upload>
        <div class="tip">点击头像更换</div>
      </div>

      <!-- 基本信息表单 -->
      <div class="form-card">
        <div class="section-title">基本信息</div>
        <el-form label-position="left" label-width="60px">
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="form.username" disabled placeholder="学号不可修改"></el-input>
          </el-form-item>
          <el-form-item label="校区">
            <el-radio-group v-model="form.campus">
              <el-radio label="呈贡">呈贡</el-radio>
              <el-radio label="东陆">东陆</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-button type="primary" class="w-full mt-2" @click="handleUpdateInfo" :loading="loading">保存修改</el-button>
        </el-form>
      </div>

      <!-- 修改密码表单 -->
      <div class="form-card mt-3">
        <div class="section-title">安全设置</div>
        <el-form label-position="left" label-width="70px">
          <el-form-item label="旧密码">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password></el-input>
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPassword" type="password" show-password></el-input>
          </el-form-item>
          <el-button type="danger" plain class="w-full mt-2" @click="handleChangePwd">修改密码</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Camera } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import request from '../../api/request'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  id: '',
  nickname: '',
  username: '',
  avatar: '',
  campus: '',
  gender: '男'
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: ''
})

onMounted(() => {
  if (userStore.userInfo) {
    // 初始化表单
    Object.assign(form, userStore.userInfo)
    // 默认值处理
    if(!form.gender) form.gender = '男'
  }
})

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    form.avatar = res.data
    ElMessage.success('头像上传成功，别忘了点击保存')
  }
}

const handleUpdateInfo = async () => {
  loading.value = true
  try {
    const res = await request.post('/user/update', form)
    // 更新本地 Store
    userStore.setUser(res)
    ElMessage.success('保存成功')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleChangePwd = async () => {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning('请填写密码')
    return
  }
  try {
    await request.post('/user/change-password', {
      userId: form.id,
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.settings-page { background: #f5f7fa; min-height: 100vh; }
.page-header { background: white; padding: 15px; border-bottom: 1px solid #eee; }
.content { padding: 20px; }

.avatar-section { display: flex; flex-direction: column; align-items: center; margin-bottom: 20px; }
.avatar-wrapper { position: relative; width: 80px; height: 80px; border-radius: 50%; overflow: hidden; }
.edit-mask {
  position: absolute; bottom: 0; left: 0; width: 100%; height: 30px; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center; color: white;
}
.tip { font-size: 12px; color: #999; margin-top: 8px; }

.form-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.02); }
.section-title { font-weight: bold; font-size: 15px; margin-bottom: 15px; border-left: 3px solid #409EFF; padding-left: 8px; }
.w-full { width: 100%; }
.mt-2 { margin-top: 10px; }
.mt-3 { margin-top: 20px; }
</style>
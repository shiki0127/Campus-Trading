import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 存储用户信息
    const userInfo = ref(JSON.parse(localStorage.getItem('userInfo')) || null)
    const token = ref(localStorage.getItem('token') || '')

    // 登录成功后设置状态
    function setUser(user) {
        userInfo.value = user
        // 这里的 user.id 可以作为 token 的简单替代（实际项目应使用 JWT token）
        token.value = user.id

        // 持久化存储
        localStorage.setItem('userInfo', JSON.stringify(user))
        localStorage.setItem('token', user.id)
    }

    // 退出登录
    function logout() {
        userInfo.value = null
        token.value = ''
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
    }

    return { userInfo, token, setUser, logout }
})
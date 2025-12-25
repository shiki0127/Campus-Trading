import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service = axios.create({
    baseURL: '/api', // 配合 vite.config.js 的 proxy
    timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        // 在这里可以添加 token，比如:
        // const token = localStorage.getItem('token')
        // if (token) {
        //   config.headers['Authorization'] = token
        // }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        // 如果后端返回的状态码不是 200，则判断为错误
        if (res.code !== 200) {
            ElMessage.error(res.msg || '系统错误')
            return Promise.reject(new Error(res.msg || 'Error'))
        } else {
            return res.data
        }
    },
    error => {
        console.log('err' + error)
        ElMessage.error(error.message)
        return Promise.reject(error)
    }
)

export default service
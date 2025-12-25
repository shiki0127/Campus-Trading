import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('../components/Home/HomePage.vue'), // 懒加载
        meta: { showTab: true }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../components/User/LoginPage.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../components/User/RegisterPage.vue')
    },
    {
        path: '/product/:id',
        name: 'ProductDetail',
        component: () => import('../components/Product/ProductDetail.vue')
    },
    {
        path: '/publish',
        name: 'Publish',
        component: () => import('../components/Product/PublishProduct.vue'),
        meta: { showTab: true }
    },
    {
        path: '/message',
        name: 'Message',
        component: () => import('../components/Message/MessageList.vue'),
        meta: { showTab: true }
    },
    {
        path: '/chat/:id', // 聊天详情
        name: 'ChatDetail',
        component: () => import('../components/Message/ChatDetail.vue')
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('../components/User/UserProfile.vue'),
        meta: { showTab: true }
    },
    {
        path: '/my-publishes',
        name: 'MyPublishes',
        component: () => import('../components/User/MyPublishes.vue')
    },
    {
        path: '/payment',
        name: 'Payment',
        component: () => import('../components/Pay/PaymentPage.vue')
    },
    {
        path: '/pay-success',
        name: 'PaySuccess',
        component: () => import('../components/Pay/PaySuccess.vue')
    },
    {
        path: '/my-sales',
        name: 'MySales',
        component: () => import('../components/User/MySales.vue')
    },
    {
        path: '/my-orders',
        name: 'MyOrders',
        component: () => import('../components/User/MyOrders.vue')
    }
    // ... 其他页面路由后续补充
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
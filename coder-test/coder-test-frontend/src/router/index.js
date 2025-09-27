import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/challenge',
    name: 'Challenge',
    component: () => import('../views/Challenge.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('../views/History.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/result/:id',
    name: 'Result',
    component: () => import('../views/Result.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  console.log('路由守卫 - 目标路径:', to.path)
  console.log('路由守卫 - 当前登录状态:', userStore.isLoggedIn)
  console.log('路由守卫 - 用户数据:', userStore.user)
  
  if (to.meta.requiresAuth) {
    // 如果需要认证但用户未登录，尝试获取用户信息
    if (!userStore.isLoggedIn) {
      console.log('路由守卫 - 尝试获取用户信息')
      await userStore.getCurrentUser()
      
      // 再次检查登录状态
      if (!userStore.isLoggedIn) {
        console.log('路由守卫 - 用户未登录，跳转到登录页')
        next('/login')
        return
      }
    }

    // 检查管理员权限
    if (to.meta.requiresAdmin) {
      if (!userStore.user || userStore.user.userRole !== 'admin') {
        console.log('路由守卫 - 无管理员权限')
        next('/')
        return
      }
    }
  }
  
  console.log('路由守卫 - 允许访问')
  next()
})

export default router

<template>
  <div class="login-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar :show-salary="false" />
    
    <!-- 主要内容 -->
    <div class="login-main">
      <div class="login-box">
        <div class="login-header">
          <div class="header-icon">
            <el-icon><User /></el-icon>
          </div>
          <h2>将士登营</h2>
          <p>欢迎回到程序员技术练兵场，准备征战技术沙场</p>
        </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            <el-icon><KnifeFork /></el-icon>
            登营入伍
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>
          尚未入伍？
          <el-link class="register-link" @click="$router.push('/register')">
            立即入伍
          </el-link>
        </p>
        <el-link class="home-link" @click="$router.push('/')">
          返回营地
        </el-link>
      </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import {User, Lock, KnifeFork} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import GlobalNavbar from '../components/GlobalNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    await userStore.loginUser(loginForm)
    
    // 登录成功后跳转到首页
    router.push('/')
    
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  display: flex;
  flex-direction: column;
}

.login-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(210, 180, 140, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 115, 85, 0.1) 0%, transparent 50%);
}

.login-box {
  background: var(--bg-card);
  border: 3px solid var(--border-light);
  border-radius: 16px;
  padding: 50px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 20px 60px var(--shadow-medium);
  position: relative;
  overflow: hidden;
}

.login-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.header-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px var(--shadow-medium);
}

.header-icon .el-icon {
  font-size: 40px;
  color: var(--accent-gold);
}

.login-header h2 {
  color: var(--text-primary);
  margin-bottom: 12px;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 2px;
}

.login-header p {
  color: var(--text-secondary);
  font-size: 16px;
  line-height: 1.6;
}

.login-form {
  margin-bottom: 30px;
}

.login-form :deep(.el-input) {
  margin-bottom: 20px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid var(--border-light);
  background: var(--bg-secondary);
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--border-medium);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-brown);
  box-shadow: 0 0 0 2px rgba(139, 115, 85, 0.2);
}

.login-form :deep(.el-input__inner) {
  color: var(--text-primary);
  font-size: 16px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  border: 2px solid var(--primary-brown);
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px var(--shadow-heavy);
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

.login-footer p {
  margin-bottom: 15px;
  color: var(--text-secondary);
  font-size: 15px;
}

.register-link, .home-link {
  color: var(--primary-brown) !important;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.register-link::after, .home-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--accent-gold);
  transition: width 0.3s ease;
}

.register-link:hover, .home-link:hover {
  color: var(--accent-gold) !important;
}

.register-link:hover::after, .home-link:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-main {
    padding: 20px 15px;
  }
  
  .login-box {
    padding: 40px 30px;
  }
  
  .header-icon {
    width: 70px;
    height: 70px;
  }
  
  .header-icon .el-icon {
    font-size: 35px;
  }
  
  .login-header h2 {
    font-size: 28px;
  }
  
  .login-header p {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .login-header h2 {
    font-size: 24px;
  }
  
  .login-button {
    height: 45px;
    font-size: 16px;
  }
}
</style>

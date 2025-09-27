<template>
  <div class="register-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar :show-salary="false" />
    
    <!-- 主要内容 -->
    <div class="register-main">
      <div class="register-box">
        <div class="register-header">
          <div class="header-icon">
            <el-icon><UserFilled /></el-icon>
          </div>
          <h2>新兵入伍</h2>
          <p>加入程序员技术练兵场，在技术沙场中磨砺成长</p>
        </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称"
            size="large"
            :prefix-icon="Avatar"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="checkPassword">
          <el-input
            v-model="registerForm.checkPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            <el-icon><Flag /></el-icon>
            立即入伍
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>
          已是将士？
          <el-link class="login-link" @click="$router.push('/login')">
            立即登营
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
import { User, Lock, Avatar, UserFilled, Flag } from '@element-plus/icons-vue'
import GlobalNavbar from '../components/GlobalNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  password: '',
  checkPassword: ''
})

// 确认密码验证函数
const validateCheckPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { max: 20, message: '昵称长度不能超过 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateCheckPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    await userStore.registerUser(registerForm)
    
    // 注册成功后跳转到登录页
    router.push('/login')
    
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  display: flex;
  flex-direction: column;
}

.register-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(210, 180, 140, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 115, 85, 0.1) 0%, transparent 50%);
}

.register-box {
  background: var(--bg-card);
  border: 3px solid var(--border-light);
  border-radius: 16px;
  padding: 50px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px var(--shadow-medium);
  position: relative;
  overflow: hidden;
}

.register-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
}

.register-header {
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

.register-header h2 {
  color: var(--text-primary);
  margin-bottom: 12px;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 2px;
}

.register-header p {
  color: var(--text-secondary);
  font-size: 16px;
  line-height: 1.6;
}

.register-form {
  margin-bottom: 30px;
}

.register-form :deep(.el-input) {
  margin-bottom: 18px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid var(--border-light);
  background: var(--bg-secondary);
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: var(--border-medium);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-brown);
  box-shadow: 0 0 0 2px rgba(139, 115, 85, 0.2);
}

.register-form :deep(.el-input__inner) {
  color: var(--text-primary);
  font-size: 16px;
}

.register-button {
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

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px var(--shadow-heavy);
}

.register-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

.register-footer p {
  margin-bottom: 15px;
  color: var(--text-secondary);
  font-size: 15px;
}

.login-link, .home-link {
  color: var(--primary-brown) !important;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.login-link::after, .home-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--accent-gold);
  transition: width 0.3s ease;
}

.login-link:hover, .home-link:hover {
  color: var(--accent-gold) !important;
}

.login-link:hover::after, .home-link:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-main {
    padding: 20px 15px;
  }
  
  .register-box {
    padding: 40px 30px;
  }
  
  .header-icon {
    width: 70px;
    height: 70px;
  }
  
  .header-icon .el-icon {
    font-size: 35px;
  }
  
  .register-header h2 {
    font-size: 28px;
  }
  
  .register-header p {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .register-box {
    padding: 30px 20px;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
  
  .register-button {
    height: 45px;
    font-size: 16px;
  }
}
</style>

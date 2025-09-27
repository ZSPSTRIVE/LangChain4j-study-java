<template>
  <el-header class="global-navbar">
    <div class="navbar-content">
      <div class="logo" @click="handleLogoClick">
        <el-icon class="logo-icon"><Flag /></el-icon>
        <span class="logo-text">程序员技术练兵场</span>
      </div>
      
      <div class="nav-menu">
        <!-- 未登录状态 -->
        <template v-if="!isLoggedIn">
          <el-button type="primary" @click="$router.push('/login')">
            登录
          </el-button>
          <el-button @click="$router.push('/register')">
            注册
          </el-button>
        </template>
        
        <!-- 已登录状态 -->
        <div v-else class="user-section">
          <div v-if="showSalary" class="salary-info">
            <el-icon class="salary-icon"><Coin /></el-icon>
            <span class="salary-text">¥{{ user?.salary?.toLocaleString() || 0 }}/月</span>
          </div>
          
          <el-dropdown>
            <span class="user-info">
              <el-avatar 
                :src="user?.avatar" 
                :size="32" 
                class="user-avatar"
              >
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">
                {{ user?.nickname || user?.username }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/challenge')">
                  <el-icon><KnifeFork /></el-icon>
                  开始挑战
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/history')">
                  <el-icon><Clock /></el-icon>
                  闯关历史
                </el-dropdown-item>
                <el-dropdown-item 
                  v-if="isAdmin" 
                  @click="$router.push('/admin')"
                  divided
                >
                  <el-icon><Setting /></el-icon>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { 
  Flag,
  ArrowDown, 
  KnifeFork, 
  Clock, 
  SwitchButton, 
  Coin,
  User,
  Setting
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  showSalary: {
    type: Boolean,
    default: true
  }
})

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const user = computed(() => userStore.user)
const isAdmin = computed(() => user.value?.userRole === 'admin')

const handleLogoClick = () => {
  router.push('/')
}

const handleLogout = async () => {
  await userStore.logoutUser()
  router.push('/')
}
</script>

<style scoped>
.global-navbar {
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  color: var(--bg-card);
  height: 70px;
  padding: 0;
  box-shadow: 0 4px 20px var(--shadow-medium);
  position: relative;
  z-index: 100;
}

.global-navbar::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
}

.navbar-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 2px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo:hover {
  transform: translateY(-1px);
}

.logo-icon {
  font-size: 28px;
  margin-right: 12px;
  color: var(--accent-gold);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.3));
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.salary-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(218, 165, 32, 0.2);
  border: 1px solid var(--accent-gold);
  border-radius: 25px;
  backdrop-filter: blur(10px);
  font-size: 14px;
  font-weight: 600;
}

.salary-icon {
  color: var(--accent-gold);
  font-size: 16px;
}

.salary-text {
  color: var(--accent-gold);
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.user-avatar {
  border: 2px solid var(--accent-gold);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.user-name {
  display: flex;
  align-items: center;
  color: var(--bg-card);
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-content {
    padding: 0 20px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .logo-icon {
    font-size: 24px;
  }
  
  .user-section {
    flex-direction: column;
    gap: 10px;
    align-items: flex-end;
  }
  
  .salary-info {
    font-size: 12px;
    padding: 6px 12px;
  }
}

@media (max-width: 480px) {
  .navbar-content {
    flex-direction: column;
    gap: 10px;
    padding: 10px 15px;
  }
  
  .global-navbar {
    height: auto;
    min-height: 70px;
  }
}
</style>

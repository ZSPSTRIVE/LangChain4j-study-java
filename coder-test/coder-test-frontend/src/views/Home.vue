<template>
  <div class="home">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <!-- 主横幅区域 -->
    <div class="hero-section">
      <div class="hero-banner">
        <img :src="bannerImage" alt="技术练兵场" class="banner-image" />
        <div class="hero-overlay">
          <div class="hero-content">
            <h1 class="hero-title">程序员技术练兵场</h1>
            <div class="hero-subtitle">沙场点兵 · 技艺精进</div>
            <p class="hero-desc">
              如古之名将练兵于沙场，今之程序员磨技于此地<br>
              通过AI生成的技术关卡，提升需求分析、方案设计之能<br>
              评估技术水平，助君在编程之路上策马扬鞭
            </p>
            
            <div class="hero-actions" :class="{ 'logged-in': isLoggedIn }">
              <div v-if="isLoggedIn" class="salary-display">
                <el-icon class="salary-icon"><Coin /></el-icon>
                <span class="salary-label">当前身价：</span>
                <span class="salary-value">¥{{ user.salary?.toLocaleString() || 0 }}/月</span>
              </div>
              <el-button type="primary" size="large" class="challenge-btn" @click="handleChallengeClick">
                <el-icon><KnifeFork /></el-icon>
                开始挑战
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 特色功能 -->
      <div class="features-section">
        <div class="section-header">
          <h2 class="section-title">练兵场特色</h2>
          <div class="section-subtitle">如古之兵法，今之技艺</div>
        </div>
        
        <div class="features-grid">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><MagicStick /></el-icon>
            </div>
            <h3>智能生成</h3>
            <p>如孙武练兵，AI生成真实企业场景<br>丰富技术选项，考验决策智慧</p>
            <div class="feature-decoration"></div>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><TrendCharts /></el-icon>
            </div>
            <h3>能力评估</h3>
            <p>根据答题表现动态调整身价<br>准确评估技术水平，知己知彼</p>
            <div class="feature-decoration"></div>
          </el-card>
          
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><Guide /></el-icon>
            </div>
            <h3>策略指导</h3>
            <p>提供投递建议和技术解析<br>助君在职场征途中运筹帷幄</p>
            <div class="feature-decoration"></div>
          </el-card>
        </div>
      </div>

      <!-- 薪资排行榜 -->
      <div class="ranking-section">
        <div class="section-header">
          <h2 class="section-title">薪资排行榜</h2>
          <div class="section-subtitle">群雄逐鹿 · 谁主沉浮</div>
        </div>
        
        <div v-loading="rankingLoading" class="ranking-content">
          <div v-if="rankingList.length === 0 && !rankingLoading" class="no-ranking">
            <el-empty description="暂无排行榜数据" />
          </div>
          
          <div v-else class="ranking-grid">
            <div
              v-for="(column, columnIndex) in rankingColumns"
              :key="columnIndex"
              class="ranking-column"
            >
              <div class="column-header">
                <span class="column-title">第{{ columnIndex * 10 + 1 }}-{{ Math.min((columnIndex + 1) * 10, rankingList.length) }}名</span>
              </div>
              
              <div class="ranking-items">
                <div
                  v-for="user in column"
                  :key="user.id"
                  class="ranking-item"
                  :class="getRankingClass(user.rank)"
                >
                  <div class="ranking-number">{{ user.rank }}</div>
                  <el-avatar 
                    :src="user.avatar" 
                    :size="40"
                    class="ranking-avatar"
                  >
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div class="ranking-info">
                    <div class="ranking-name">{{ user.nickname }}</div>
                    <div class="ranking-salary">¥{{ user.salary?.toLocaleString() || 0 }}/月</div>
                  </div>
                  <div v-if="user.rank <= 3" class="ranking-medal">
                    <el-icon v-if="user.rank === 1" class="medal gold"><Trophy /></el-icon>
                    <el-icon v-else-if="user.rank === 2" class="medal silver"><Trophy /></el-icon>
                    <el-icon v-else-if="user.rank === 3" class="medal bronze"><Trophy /></el-icon>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getSalaryRanking } from '../api/ranking'
import { ElMessage } from 'element-plus'
import { 
  KnifeFork, 
  Coin, 
  MagicStick, 
  TrendCharts, 
  Guide,
  User,
  Trophy
} from '@element-plus/icons-vue'
import bannerImage from '../assets/banner.png'
import GlobalNavbar from '../components/GlobalNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const user = computed(() => userStore.user)

// 排行榜相关数据
const rankingList = ref([])
const rankingLoading = ref(false)

// 计算排行榜分列显示
const rankingColumns = computed(() => {
  const columns = []
  for (let i = 0; i < 3; i++) {
    const start = i * 10
    const end = Math.min(start + 10, rankingList.value.length)
    if (start < rankingList.value.length) {
      columns.push(rankingList.value.slice(start, end))
    }
  }
  return columns
})

// 获取排名样式类
const getRankingClass = (rank) => {
  if (rank === 1) return 'rank-first'
  if (rank === 2) return 'rank-second'
  if (rank === 3) return 'rank-third'
  return ''
}

// 加载排行榜数据
const loadRanking = async () => {
  rankingLoading.value = true
  try {
    const data = await getSalaryRanking(30)
    rankingList.value = data || []
  } catch (error) {
    console.error('获取排行榜失败:', error)
    ElMessage.error('获取排行榜失败')
  } finally {
    rankingLoading.value = false
  }
}

const handleLogout = async () => {
  await userStore.logoutUser()
  router.push('/')
}

const startChallenge = () => {
  router.push('/challenge')
}

const handleChallengeClick = () => {
  if (isLoggedIn.value) {
    // 已登录，直接开始挑战
    startChallenge()
  } else {
    // 未登录，跳转到登录页面
    router.push('/login')
  }
}

// 组件挂载时加载排行榜
onMounted(() => {
  loadRanking()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: var(--bg-primary);
}


/* 主横幅区域 */
.hero-section {
  position: relative;
  height: 600px;
  overflow: hidden;
}

.hero-banner {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.7) sepia(0.3) saturate(1.2);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(60, 36, 21, 0.8) 0%,
    rgba(139, 115, 85, 0.6) 50%,
    rgba(210, 180, 140, 0.4) 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-content {
  text-align: center;
  color: var(--bg-card);
  max-width: 800px;
  padding: 0 30px;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.5);
  letter-spacing: 3px;
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--bg-card) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 24px;
  color: var(--accent-gold);
  letter-spacing: 2px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-desc {
  font-size: 18px;
  line-height: 1.8;
  margin-bottom: 40px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.4);
  opacity: 0.95;
}

.hero-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.hero-actions.logged-in {
  gap: 25px;
}

.salary-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: rgba(218, 165, 32, 0.2);
  border: 2px solid var(--accent-gold);
  border-radius: 50px;
  backdrop-filter: blur(10px);
  font-size: 18px;
  font-weight: 600;
}

.salary-icon {
  color: var(--accent-gold);
  font-size: 22px;
}

.salary-label {
  color: var(--bg-card);
}

.salary-value {
  color: var(--accent-gold);
  font-size: 20px;
  font-weight: 700;
}

.challenge-btn, .primary-btn, .secondary-btn {
  font-size: 18px !important;
  padding: 16px 32px !important;
  border-radius: 50px !important;
  font-weight: 600 !important;
  letter-spacing: 1px !important;
  min-width: 180px;
}

.challenge-btn {
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--accent-copper) 100%) !important;
  border: 3px solid var(--accent-gold) !important;
  color: var(--text-primary) !important;
  box-shadow: 0 6px 20px rgba(218, 165, 32, 0.4) !important;
}

.challenge-btn:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 25px rgba(218, 165, 32, 0.6) !important;
}

/* 主要内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 80px 30px;
}

.features-section {
  margin-bottom: 80px;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--text-primary);
  letter-spacing: 2px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  border-radius: 2px;
}

.section-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
  font-style: italic;
  letter-spacing: 1px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 40px;
  margin-top: 60px;
}

.feature-card {
  text-align: center;
  padding: 40px 30px;
  position: relative;
  overflow: hidden;
  background: var(--bg-card) !important;
  border: 3px solid var(--border-light) !important;
  transition: all 0.4s ease !important;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(218, 165, 32, 0.1), transparent);
  transition: left 0.6s ease;
}

.feature-card:hover::before {
  left: 100%;
}

.feature-card:hover {
  transform: translateY(-8px) !important;
  border-color: var(--accent-gold) !important;
  box-shadow: 0 15px 40px var(--shadow-heavy) !important;
}

.feature-icon {
  margin-bottom: 24px;
  color: var(--primary-brown);
  position: relative;
  z-index: 2;
}

.feature-card h3 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-primary);
  letter-spacing: 1px;
  position: relative;
  z-index: 2;
}

.feature-card p {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 16px;
  position: relative;
  z-index: 2;
}

.feature-decoration {
  position: absolute;
  bottom: 15px;
  right: 15px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  border-radius: 50%;
  opacity: 0.1;
  transform: scale(0);
  transition: all 0.4s ease;
}

.feature-card:hover .feature-decoration {
  transform: scale(1);
  opacity: 0.2;
}

/* 排行榜样式 */
.ranking-section {
  margin-top: 80px;
}

.ranking-content {
  min-height: 300px;
}

.no-ranking {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.ranking-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.ranking-column {
  background: var(--bg-card);
  border: 2px solid var(--border-light);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px var(--shadow-medium);
  transition: all 0.3s ease;
}

.ranking-column:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px var(--shadow-heavy);
  border-color: var(--accent-gold);
}

.column-header {
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  color: var(--bg-card);
  padding: 16px 20px;
  text-align: center;
  position: relative;
}

.column-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
}

.column-title {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
}

.ranking-items {
  padding: 20px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.ranking-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(218, 165, 32, 0.1), transparent);
  transition: left 0.6s ease;
}

.ranking-item:hover::before {
  left: 100%;
}

.ranking-item:hover {
  transform: translateX(4px);
  border-color: var(--accent-gold);
  box-shadow: 0 4px 12px var(--shadow-light);
}

.ranking-item:last-child {
  margin-bottom: 0;
}

.ranking-item.rank-first {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.1) 0%, rgba(255, 193, 7, 0.05) 100%);
  border-color: #FFD700;
}

.ranking-item.rank-second {
  background: linear-gradient(135deg, rgba(192, 192, 192, 0.1) 0%, rgba(169, 169, 169, 0.05) 100%);
  border-color: #C0C0C0;
}

.ranking-item.rank-third {
  background: linear-gradient(135deg, rgba(205, 127, 50, 0.1) 0%, rgba(184, 115, 51, 0.05) 100%);
  border-color: #CD7F32;
}

.ranking-number {
  width: 32px;
  height: 32px;
  background: var(--primary-brown);
  color: var(--bg-card);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
}

.rank-first .ranking-number {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: #333;
}

.rank-second .ranking-number {
  background: linear-gradient(135deg, #C0C0C0 0%, #A9A9A9 100%);
  color: #333;
}

.rank-third .ranking-number {
  background: linear-gradient(135deg, #CD7F32 0%, #B8733B 100%);
  color: #fff;
}

.ranking-avatar {
  margin-right: 12px;
  border: 2px solid var(--border-light);
  flex-shrink: 0;
}

.rank-first .ranking-avatar {
  border-color: #FFD700;
}

.rank-second .ranking-avatar {
  border-color: #C0C0C0;
}

.rank-third .ranking-avatar {
  border-color: #CD7F32;
}

.ranking-info {
  flex: 1;
  min-width: 0;
}

.ranking-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ranking-salary {
  color: var(--accent-gold);
  font-weight: 600;
  font-size: 14px;
}

.ranking-medal {
  margin-left: 8px;
  flex-shrink: 0;
}

.medal {
  font-size: 20px;
}

.medal.gold {
  color: #FFD700;
}

.medal.silver {
  color: #C0C0C0;
}

.medal.bronze {
  color: #CD7F32;
}

/* 响应式设计 */
@media (max-width: 768px) {
  
  .hero-title {
    font-size: 40px;
  }
  
  .hero-desc {
    font-size: 16px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .ranking-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .ranking-column {
    margin-bottom: 20px;
  }
  
  .ranking-item {
    padding: 10px 12px;
  }
  
  .ranking-avatar {
    width: 36px !important;
    height: 36px !important;
  }
  
  .ranking-name {
    font-size: 14px;
  }
  
  .ranking-salary {
    font-size: 13px;
  }
  
  .main-content {
    padding: 60px 20px;
  }
  
  .hero-content {
    padding: 0 20px;
  }
  
  .hero-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .challenge-btn, .primary-btn, .secondary-btn {
    width: 100%;
    max-width: 280px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    height: 500px;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .section-title {
    font-size: 32px;
  }
  
  .feature-card {
    padding: 30px 20px;
  }
}
</style>

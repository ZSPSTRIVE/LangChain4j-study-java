<template>
  <div class="history-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <div class="main-content">
      <div class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1>征战记录</h1>
            <p>回顾沙场征战历程，见证技艺精进之路</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" size="large" @click="$router.push('/challenge')">
              <el-icon><KnifeFork /></el-icon>
              继续征战
            </el-button>
          </div>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><Trophy /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ historyList.length }}</div>
                  <div class="stat-label">总挑战次数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><Star /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ averageScore }}</div>
                  <div class="stat-label">平均分数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><Coin /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ user?.salary?.toLocaleString() || 0 }}</div>
                  <div class="stat-label">当前薪资</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon size="30"><TrendCharts /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number" :class="totalSalaryChangeClass">
                    {{ totalSalaryChange > 0 ? '+' : '' }}{{ totalSalaryChange }}
                  </div>
                  <div class="stat-label">薪资变化</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 历史记录列表 -->
      <div class="history-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>挑战记录</span>
              <el-button type="primary" @click="refreshHistory">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>

          <div v-loading="loading" class="history-list">
            <div v-if="historyList.length === 0" class="empty-state">
              <el-empty description="尚未有征战记录">
                <el-button type="primary" @click="$router.push('/challenge')">
                  <el-icon><KnifeFork /></el-icon>
                  踏上征程
                </el-button>
              </el-empty>
            </div>

            <div v-else class="history-items">
              <div
                v-for="item in historyList"
                :key="item.id"
                class="history-item"
                @click="viewResult(item.id)"
              >
                <div class="item-header">
                  <div class="level-info">
                    <h3 class="level-name">{{ getLevelName(item) }}</h3>
                    <div class="level-meta">
                      <el-tag size="small" :type="getScoreType(item.score)">
                        {{ item.score }}分
                      </el-tag>
                      <span class="challenge-time">
                        {{ formatTime(item.createTime) }}
                      </span>
                    </div>
                  </div>
                  
                  <div class="item-actions">
                    <div class="salary-change" :class="getSalaryChangeClass(item.salaryChange)">
                      薪资{{ item.salaryChange > 0 ? '+' : '' }}{{ item.salaryChange }}
                    </div>
                    <el-icon class="arrow-icon"><ArrowRight /></el-icon>
                  </div>
                </div>
                
                <div class="item-content">
                  <p class="comment">{{ item.comment || '暂无评价' }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUserLevelHistory } from '../api/userLevel'
import { ElMessage } from 'element-plus'
import {
  Trophy,
  KnifeFork,
  Star,
  Coin,
  TrendCharts,
  Refresh,
  ArrowRight
} from '@element-plus/icons-vue'
import GlobalNavbar from '../components/GlobalNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const loading = ref(false)
const historyList = ref([])

// 计算平均分数
const averageScore = computed(() => {
  if (historyList.value.length === 0) return 0
  const total = historyList.value.reduce((sum, item) => sum + (item.score || 0), 0)
  return Math.round(total / historyList.value.length)
})

// 计算总薪资变化
const totalSalaryChange = computed(() => {
  return historyList.value.reduce((sum, item) => sum + (item.salaryChange || 0), 0)
})

// 薪资变化样式类
const totalSalaryChangeClass = computed(() => {
  if (totalSalaryChange.value > 0) return 'positive'
  if (totalSalaryChange.value < 0) return 'negative'
  return 'neutral'
})


// 获取关卡名称（从用户选择的选项中提取或使用默认名称）
const getLevelName = (item) => {
  // 这里可以根据实际需要从 levelId 获取关卡名称
  // 由于后端接口没有直接返回关卡名称，暂时使用默认格式
  return `技术挑战 #${item.id.toString().slice(-6)}`
}

// 获取分数类型
const getScoreType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 获取薪资变化样式类
const getSalaryChangeClass = (change) => {
  if (change > 0) return 'salary-positive'
  if (change < 0) return 'salary-negative'
  return 'salary-neutral'
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看结果详情
const viewResult = (id) => {
  router.push(`/result/${id}`)
}

// 获取历史记录
const fetchHistory = async () => {
  loading.value = true
  try {
    const data = await getUserLevelHistory()
    historyList.value = data || []
  } catch (error) {
    console.error('获取历史记录失败:', error)
    ElMessage.error('获取历史记录失败')
  } finally {
    loading.value = false
  }
}

// 刷新历史记录
const refreshHistory = () => {
  fetchHistory()
}

onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  fetchHistory()
})
</script>

<style scoped>
.history-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(210, 180, 140, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 115, 85, 0.1) 0%, transparent 50%);
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 30px;
}

.page-header {
  margin-bottom: 50px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px 0;
}

.header-text h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 12px;
  color: var(--text-primary);
  letter-spacing: 2px;
}

.header-text p {
  color: var(--text-secondary);
  font-size: 18px;
  line-height: 1.6;
}

.header-actions .el-button {
  font-size: 16px !important;
  font-weight: 600 !important;
  border-radius: 25px !important;
  padding: 14px 28px !important;
  letter-spacing: 1px !important;
  transition: all 0.3s ease !important;
}

.header-actions .el-button:hover {
  transform: translateY(-2px) !important;
}

.stats-section {
  margin-bottom: 40px;
}

.stats-section :deep(.el-card) {
  border: 2px solid var(--border-light) !important;
  border-radius: 12px !important;
  background: var(--bg-card) !important;
  box-shadow: 0 8px 24px var(--shadow-light) !important;
  transition: all 0.3s ease !important;
  height: 120px;
}

.stats-section :deep(.el-card:hover) {
  transform: translateY(-4px) !important;
  box-shadow: 0 12px 32px var(--shadow-medium) !important;
  border-color: var(--border-medium) !important;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 5px;
}

.stat-icon {
  margin-right: 20px;
  color: var(--accent-gold);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 5px;
}

.stat-number.positive {
  color: var(--accent-gold);
}

.stat-number.negative {
  color: var(--secondary-brown);
}

.stat-number.neutral {
  color: var(--text-muted);
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.history-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.history-item {
  border: 2px solid var(--border-light);
  border-radius: 12px;
  padding: 25px;
  background: var(--bg-card);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.history-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(218, 165, 32, 0.1), transparent);
  transition: left 0.6s ease;
}

.history-item:hover::before {
  left: 100%;
}

.history-item:hover {
  border-color: var(--accent-gold);
  box-shadow: 0 8px 25px var(--shadow-medium);
  transform: translateY(-3px);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  position: relative;
  z-index: 2;
}

.level-name {
  color: var(--text-primary);
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.level-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.challenge-time {
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 500;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.salary-change {
  font-weight: 600;
  font-size: 15px;
  padding: 4px 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.8);
}

.salary-positive {
  color: var(--accent-gold);
  background: rgba(218, 165, 32, 0.1);
}

.salary-negative {
  color: var(--secondary-brown);
  background: rgba(160, 82, 45, 0.1);
}

.salary-neutral {
  color: var(--text-muted);
  background: rgba(139, 125, 107, 0.1);
}

.arrow-icon {
  color: var(--accent-gold);
  font-size: 18px;
}

.comment {
  color: var(--text-secondary);
  margin: 0;
  font-size: 15px;
  line-height: 1.6;
  position: relative;
  z-index: 2;
}

.empty-state {
  padding: 80px 20px;
  text-align: center;
}

.empty-state :deep(.el-empty__description) {
  color: var(--text-secondary);
  font-size: 16px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 30px 20px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .header-text h1 {
    font-size: 36px;
  }
  
  .header-text p {
    font-size: 16px;
  }
  
  .stats-section .el-col {
    margin-bottom: 20px;
  }
  
  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .item-actions {
    align-self: flex-end;
  }
  
  .level-name {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 20px 15px;
  }
  
  .header-text h1 {
    font-size: 28px;
  }
  
  .history-item {
    padding: 20px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}
</style>

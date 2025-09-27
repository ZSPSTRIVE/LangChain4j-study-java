<template>
  <div class="result-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <div class="main-content" v-loading="loading">
      <div v-if="resultData" class="result-content">
        <!-- 分数展示 -->
        <div class="score-section">
          <el-card class="score-card">
            <div class="score-display">
              <div class="score-circle" :class="getScoreClass(resultData.score)">
                <div class="score-number">{{ resultData.score }}</div>
                <div class="score-label">分</div>
              </div>
              
              <div class="score-info">
                <h2 class="score-title">挑战完成！</h2>
                <p class="score-comment">{{ resultData.comment }}</p>
                
                <div class="salary-change">
                  <div class="salary-item">
                    <span class="salary-label">薪资变化：</span>
                    <span class="salary-value" :class="getSalaryChangeClass(resultData.salaryChange)">
                      {{ resultData.salaryChange > 0 ? '+' : '' }}{{ resultData.salaryChange }}
                    </span>
                  </div>
                  
                  <div class="salary-item">
                    <span class="salary-label">当前薪资：</span>
                    <span class="current-salary">¥{{ user?.salary?.toLocaleString() || 0 }}/月</span>
                  </div>
                </div>

                <!-- 快速操作按钮 -->
                <div class="quick-actions">
                  <el-button type="primary" @click="$router.push('/challenge')">
                    <el-icon><KnifeFork /></el-icon>
                    继续挑战
                  </el-button>
                  <el-button @click="$router.push('/history')">
                    <el-icon><Clock /></el-icon>
                    历史记录
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 公司建议 -->
        <div class="suggestion-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><OfficeBuilding /></el-icon>
                投递建议
              </div>
            </template>
            
            <div class="suggestion-content">
              <p>{{ resultData.suggest }}</p>
            </div>
          </el-card>
        </div>

        <!-- 评分原因 -->
        <div class="reason-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><Document /></el-icon>
                评分原因
              </div>
            </template>
            
            <div class="reason-content">
              <p>{{ resultData.reason }}</p>
            </div>
          </el-card>
        </div>

        <!-- 正确答案 -->
        <div class="answers-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><Check /></el-icon>
                正确答案
              </div>
            </template>
            
            <div class="answers-content">
              <div class="true-options">
                <h4>标准选项：</h4>
                <div class="options-list">
                  <el-tag
                    v-for="option in trueOptionsList"
                    :key="option"
                    type="success"
                    class="option-tag"
                  >
                    {{ option }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 详细解析 -->
        <div class="analysis-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><Reading /></el-icon>
                详细解析
              </div>
            </template>
            
            <div class="analysis-content">
              <div class="markdown-content">
                <Viewer :value="standardAnswerContent" :plugins="plugins" />
              </div>
            </div>
          </el-card>
        </div>

        <!-- 面试题推荐 -->
        <div v-if="recommendedQuestionsContent" class="interview-questions-section">
          <el-card>
            <template #header>
              <div class="section-title">
                <el-icon><QuestionFilled /></el-icon>
                相关面试题推荐
              </div>
            </template>
            
            <div class="interview-questions-content">
              <div class="markdown-content">
                <Viewer :value="recommendedQuestionsContent" :plugins="plugins" />
              </div>
              
              <div class="questions-footer">
                <el-alert
                  title="💡 提示"
                  type="info"
                  :closable="false"
                  show-icon
                >
                  <template #default>
                    以上面试题来自 <a href="https://www.mianshiya.com" target="_blank" class="mianshiya-link">面试鸭</a>，
                    建议结合本次关卡的知识点进行针对性练习，提升面试通过率！
                  </template>
                </el-alert>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 底部操作 -->
        <div class="bottom-actions">
          <el-button size="large" @click="$router.push('/')" class="home-btn">
            <el-icon><House /></el-icon>
            返回营地
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUserLevelDetail } from '../api/userLevel'
import { ElMessage } from 'element-plus'
import { Viewer } from '@bytemd/vue-next'
import gfm from '@bytemd/plugin-gfm'
import mermaid from '@bytemd/plugin-mermaid'
import {
  KnifeFork,
  Clock,
  OfficeBuilding,
  Document,
  Check,
  Reading,
  House,
  QuestionFilled
} from '@element-plus/icons-vue'
import GlobalNavbar from '../components/GlobalNavbar.vue'

// 导入 ByteMD 样式
import 'bytemd/dist/index.css'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 配置 ByteMD 插件
const plugins = [
  gfm(),
  mermaid()
]

const user = computed(() => userStore.user)
const loading = ref(false)
const resultData = ref(null)

// 解析正确选项列表
const trueOptionsList = computed(() => {
  if (!resultData.value?.trueOptions) return []
  
  try {
    const options = typeof resultData.value.trueOptions === 'string' 
      ? JSON.parse(resultData.value.trueOptions) 
      : resultData.value.trueOptions
    return Array.isArray(options) ? options : []
  } catch (error) {
    console.error('解析正确选项失败:', error)
    return []
  }
})

// 获取标准答案内容
const standardAnswerContent = computed(() => {
  return resultData.value?.standardAnswer || ''
})

// 获取推荐面试题内容
const recommendedQuestionsContent = computed(() => {
  return resultData.value?.recommendedQuestions || ''
})

// 获取分数样式类
const getScoreClass = (score) => {
  if (score >= 80) return 'excellent'
  if (score >= 60) return 'good'
  return 'poor'
}

// 获取薪资变化样式类
const getSalaryChangeClass = (change) => {
  if (change > 0) return 'salary-increase'
  if (change < 0) return 'salary-decrease'
  return 'salary-neutral'
}


// 获取结果详情
const fetchResultDetail = async () => {
  const id = route.params.id
  console.log('结果页面 - 路由参数 ID:', id)
  
  if (!id) {
    ElMessage.error('无效的结果ID')
    router.push('/history')
    return
  }

  loading.value = true
  try {
    console.log('开始获取结果详情:', id)
    const data = await getUserLevelDetail(id)
    console.log('获取到的结果数据:', data)
    resultData.value = data
  } catch (error) {
    console.error('获取结果详情失败:', error)
    ElMessage.error('获取结果详情失败')
    router.push('/history')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  fetchResultDetail()
})
</script>

<style scoped>
.result-container {
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

.result-content {
  display: flex;
  flex-direction: column;
  gap: 35px;
}

.score-section {
  display: flex;
  justify-content: center;
}

.score-card {
  min-width: 700px;
  border: 3px solid var(--border-light) !important;
  border-radius: 16px !important;
  background: var(--bg-card) !important;
  box-shadow: 0 12px 40px var(--shadow-medium) !important;
  position: relative;
  overflow: hidden;
}

.score-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
}

.score-display {
  display: flex;
  align-items: center;
  gap: 50px;
  padding: 35px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  background: conic-gradient(from 0deg, var(--score-color) 0%, var(--score-color) var(--score-percent), #e4e7ed var(--score-percent), #e4e7ed 100%);
}

.score-circle.excellent {
  --score-color: var(--accent-gold);
  --score-percent: 80%;
}

.score-circle.good {
  --score-color: var(--accent-copper);
  --score-percent: 60%;
}

.score-circle.poor {
  --score-color: var(--secondary-brown);
  --score-percent: 40%;
}

.score-circle::before {
  content: '';
  position: absolute;
  width: 90px;
  height: 90px;
  background: white;
  border-radius: 50%;
}

.score-number {
  font-size: 40px;
  font-weight: 700;
  color: var(--text-primary);
  z-index: 1;
}

.score-label {
  font-size: 16px;
  color: var(--text-secondary);
  z-index: 1;
  font-weight: 500;
}

.score-info {
  flex: 1;
}

.score-title {
  color: var(--text-primary);
  margin-bottom: 15px;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 1px;
}

.score-comment {
  color: var(--text-secondary);
  font-size: 18px;
  margin-bottom: 25px;
  line-height: 1.8;
}

.salary-change {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.salary-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.salary-label {
  color: #666;
  font-size: 14px;
}

.salary-value {
  font-weight: bold;
  font-size: 16px;
}

.salary-increase {
  color: #67c23a;
}

.salary-decrease {
  color: #f56c6c;
}

.salary-neutral {
  color: #909399;
}

.current-salary {
  color: var(--accent-gold);
  font-weight: bold;
  font-size: 18px;
}

.quick-actions {
  display: flex;
  gap: 15px;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

.quick-actions .el-button {
  font-weight: 600;
  border-radius: 25px;
  padding: 12px 24px;
  transition: all 0.3s ease;
}

.quick-actions .el-button:hover {
  transform: translateY(-2px);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}

.section-title .el-icon {
  color: var(--accent-gold);
  font-size: 22px;
}

.suggestion-content,
.reason-content {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 16px;
  padding: 5px 0;
}

.answers-content {
  color: #666;
}

.true-options h4 {
  color: #333;
  margin-bottom: 15px;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-tag {
  font-size: 14px;
  padding: 8px 12px;
}

.analysis-content {
  color: #666;
  line-height: 1.8;
}

.markdown-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-secondary);
}

/* ByteMD Viewer 样式覆盖 */
.markdown-content :deep(.bytemd-viewer) {
  background: transparent;
  font-family: inherit;
}

/* Mermaid 图表样式 */
.markdown-content :deep(.mermaid) {
  display: flex;
  justify-content: center;
  margin: 20px 0;
  background: var(--bg-card);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid var(--border-light);
  box-shadow: 0 2px 8px var(--shadow-light);
}

.markdown-content :deep(.mermaid svg) {
  max-width: 100%;
  height: auto;
}

/* 标题样式 */
.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
  color: var(--text-primary);
  font-weight: 600;
  margin: 20px 0 12px 0;
  letter-spacing: 0.5px;
}

.markdown-content :deep(h1) { font-size: 24px; }
.markdown-content :deep(h2) { font-size: 20px; }
.markdown-content :deep(h3) { font-size: 18px; }
.markdown-content :deep(h4) { font-size: 16px; }

/* 段落样式 */
.markdown-content :deep(p) {
  margin: 12px 0;
  line-height: 1.8;
}

/* 强调和斜体 */
.markdown-content :deep(strong) {
  color: var(--text-primary);
  font-weight: 600;
}

.markdown-content :deep(em) {
  font-style: italic;
  color: var(--text-primary);
}

/* 行内代码 */
.markdown-content :deep(code) {
  background: linear-gradient(135deg, var(--secondary-sand) 0%, #F0E68C 100%);
  color: var(--secondary-brown);
  padding: 3px 8px;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid var(--border-light);
}

/* 代码块 */
.markdown-content :deep(pre) {
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #F5F1E8 100%);
  border: 2px solid var(--border-light);
  border-radius: 8px;
  padding: 16px;
  margin: 16px 0;
  overflow-x: auto;
  box-shadow: 0 4px 12px var(--shadow-light);
}

.markdown-content :deep(pre code) {
  background: none;
  border: none;
  padding: 0;
  color: var(--text-primary);
  font-size: 14px;
}

/* 列表样式 */
.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin: 12px 0;
  padding-left: 24px;
}

.markdown-content :deep(li) {
  margin: 6px 0;
  line-height: 1.6;
}

.markdown-content :deep(ul li) {
  list-style-type: none;
  position: relative;
}

.markdown-content :deep(ul li::before) {
  content: '◆';
  color: var(--accent-gold);
  font-size: 12px;
  position: absolute;
  left: -16px;
}

/* 引用块样式 */
.markdown-content :deep(blockquote) {
  border-left: 4px solid var(--accent-gold);
  background: linear-gradient(135deg, var(--bg-card) 0%, var(--secondary-sand) 100%);
  margin: 16px 0;
  padding: 12px 20px;
  border-radius: 0 8px 8px 0;
  color: var(--text-secondary);
  font-style: italic;
  box-shadow: 0 2px 8px var(--shadow-light);
}

/* 链接样式 */
.markdown-content :deep(a) {
  color: var(--accent-copper);
  text-decoration: none;
  border-bottom: 1px dotted var(--accent-copper);
  transition: all 0.3s ease;
}

.markdown-content :deep(a:hover) {
  color: var(--accent-gold);
  border-bottom-color: var(--accent-gold);
}

/* 分割线样式 */
.markdown-content :deep(hr) {
  border: none;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, var(--border-medium) 50%, transparent 100%);
  margin: 24px 0;
}

/* 表格样式 */
.markdown-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
  background: var(--bg-card);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px var(--shadow-light);
}

.markdown-content :deep(th),
.markdown-content :deep(td) {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.markdown-content :deep(th) {
  background: linear-gradient(135deg, var(--primary-sand) 0%, var(--secondary-sand) 100%);
  color: var(--text-primary);
  font-weight: 600;
}

.markdown-content :deep(tr:hover) {
  background: var(--bg-secondary);
}

.bottom-actions {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}

.home-btn {
  font-size: 16px !important;
  font-weight: 600 !important;
  border-radius: 25px !important;
  padding: 14px 28px !important;
  letter-spacing: 1px !important;
  transition: all 0.3s ease !important;
}

.home-btn:hover {
  transform: translateY(-2px) !important;
}

/* 面试题推荐区域样式 */
.interview-questions-section {
  margin-top: 35px;
}

.interview-questions-content {
  padding: 10px 0;
}

.questions-footer {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

.mianshiya-link {
  color: var(--accent-copper);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.mianshiya-link:hover {
  color: var(--accent-gold);
}

@media (max-width: 768px) {
  .main-content {
    padding: 30px 20px;
  }
  
  .score-card {
    min-width: auto;
  }
  
  .score-display {
    flex-direction: column;
    text-align: center;
    gap: 30px;
    padding: 30px 25px;
  }
  
  .score-title {
    font-size: 28px;
  }
  
  .score-comment {
    font-size: 16px;
  }
  
  .quick-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .section-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 20px 15px;
  }
  
  .score-display {
    padding: 25px 20px;
  }
  
  .score-title {
    font-size: 24px;
  }
  
  .quick-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .quick-actions .el-button {
    width: 200px;
  }
}
</style>

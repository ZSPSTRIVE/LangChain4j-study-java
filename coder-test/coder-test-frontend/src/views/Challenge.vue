<template>
  <div class="challenge-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <div class="main-content">
      <!-- 生成关卡区域 -->
      <div v-if="!currentLevel" class="generate-section">
        <el-card class="generate-card">
          <div class="generate-content">
            <h2>准备开始新的挑战</h2>
            <p>选择你的学习方向，系统将根据你当前的薪资水平生成适合的技术关卡</p>
            
            <!-- 学习方向选择 -->
            <div class="direction-selector">
              <h3>选择学习方向</h3>
              <el-select 
                v-model="selectedDirection" 
                placeholder="请选择学习方向"
                size="large"
                style="width: 300px; margin-bottom: 20px;"
              >
                <el-option 
                  v-for="direction in directionOptions" 
                  :key="direction.value" 
                  :label="direction.label" 
                  :value="direction.value"
                >
                  <span style="float: left">{{ direction.label }}</span>
                  <span style="float: right; color: var(--text-muted); font-size: 13px">
                    {{ direction.desc }}
                  </span>
                </el-option>
              </el-select>
            </div>
            
            <el-button
              type="primary"
              size="large"
              :disabled="generating || !selectedDirection"
              @click="generateLevel"
            >
              <el-icon><MagicStick /></el-icon>
              生成关卡
            </el-button>
            
            <!-- 自定义加载区域 -->
            <div v-if="generating" class="custom-loading-area">
              <img 
                :src="loadingIcon" 
                alt="生成中..." 
                class="custom-loading-icon"
                :style="{ left: loadingPosition.x + 'px', top: loadingPosition.y + 'px' }"
              />
              <div class="loading-text">正在生成关卡中...</div>
              <div class="progress-container">
                <el-progress 
                  :percentage="generateProgress" 
                  :stroke-width="6"
                  :show-text="false"
                  color="#DAA520"
                />
                <div class="progress-text">{{ generateProgress }}%</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 精选关卡选择区域 -->
        <el-card class="featured-levels-card">
          <template #header>
            <div class="card-header">
              <h3>或选择精选关卡</h3>
              <el-icon class="star-icon"><Star /></el-icon>
            </div>
          </template>
          
          <div v-loading="featuredLoading" class="featured-content">
            <div v-if="featuredLevels.length === 0 && !featuredLoading" class="no-featured">
              <el-empty description="暂无精选关卡" />
            </div>
            
            <div v-else class="featured-grid">
              <div
                v-for="level in featuredLevels"
                :key="level.id"
                class="featured-item"
                @click="selectFeaturedLevel(level)"
              >
                <div class="featured-header">
                  <h4 class="featured-title">{{ level.levelName }}</h4>
                  <div class="featured-tags">
                    <el-tag 
                      v-if="level.direction" 
                      type="primary" 
                      size="small"
                    >
                      {{ level.direction }}
                    </el-tag>
                    <el-tag 
                      v-if="level.priority >= 9999" 
                      type="danger" 
                      size="small"
                    >
                      置顶
                    </el-tag>
                    <el-tag 
                      v-else-if="level.priority >= 999" 
                      type="warning" 
                      size="small"
                    >
                      精选
                    </el-tag>
                    <el-tag 
                      :type="getDifficultyType(level.difficulty)" 
                      size="small"
                    >
                      {{ level.difficulty }}
                    </el-tag>
                  </div>
                </div>
                
                <div class="featured-desc">
                  {{ truncateText(level.levelDesc, 80) }}
                </div>
                
                <div class="featured-meta">
                  <span class="target-salary">
                    ¥{{ level.targetSalary?.toLocaleString() || 0 }}/月
                  </span>
                  <span class="create-time">
                    {{ formatTime(level.createTime) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <div v-if="featuredTotal > 0" class="featured-pagination">
              <el-pagination
                v-model:current-page="featuredCurrent"
                v-model:page-size="featuredPageSize"
                :total="featuredTotal"
                :page-sizes="[6, 12, 18]"
                layout="prev, pager, next, sizes"
                @current-change="loadFeaturedLevels"
                @size-change="loadFeaturedLevels"
              />
            </div>
          </div>
        </el-card>
      </div>

      <!-- 关卡内容区域 -->
      <div v-if="currentLevel" class="level-section">
        <!-- 关卡信息 -->
        <el-card class="level-info-card">
          <div class="level-header">
            <h2 class="level-name">{{ currentLevel.levelName }}</h2>
            <div class="level-meta">
              <el-tag 
                v-if="currentLevel.direction" 
                type="primary" 
                size="large"
              >
                {{ currentLevel.direction }}
              </el-tag>
              <el-tag :type="getDifficultyType(currentLevel.difficulty)">
                {{ currentLevel.difficulty }}
              </el-tag>
              <span class="target-salary">
                目标薪资：¥{{ currentLevel.targetSalary?.toLocaleString() || 0 }}/月
              </span>
            </div>
          </div>
          
          <div class="level-desc">
            <h3>需求描述：</h3>
            <div class="desc-content" v-html="formatDescription(currentLevel.levelDesc)"></div>
          </div>
        </el-card>

        <!-- 答题区域 -->
        <div class="answer-section">
          <div class="options-area">
            <h3>可选项（拖拽到右侧答题区）</h3>
            <div class="options-grid">
              <div
                v-for="option in availableOptions"
                :key="option.optionName"
                class="option-item"
                :class="{ 'selected': selectedOptions.includes(option.optionName) }"
                draggable="true"
                @dragstart="handleDragStart($event, option)"
                @click="toggleOption(option)"
              >
                {{ option.optionName }}
              </div>
            </div>
          </div>

          <div class="answer-area">
            <h3>你的答案</h3>
            <div
              class="answer-box"
              @drop="handleDrop"
              @dragover="handleDragOver"
              @dragenter="handleDragEnter"
              @dragleave="handleDragLeave"
            >
              <div v-if="selectedOptions.length === 0" class="answer-placeholder">
                将选项拖拽到这里，或者点击左侧选项添加到答案中
              </div>
              
              <div
                v-for="(optionName, index) in selectedOptions"
                :key="index"
                class="selected-option"
              >
                {{ optionName }}
                <el-button
                  type="danger"
                  size="small"
                  :icon="Close"
                  circle
                  @click="removeOption(optionName)"
                />
              </div>
            </div>

            <div class="submit-area">
              <div class="button-row">
                <el-button
                  type="primary"
                  size="large"
                  :disabled="submitting || selectedOptions.length === 0"
                  @click="submitAnswer"
                >
                  <el-icon><Check /></el-icon>
                  提交答案
                </el-button>
                
                <el-button
                  size="large"
                  :disabled="submitting"
                  @click="resetLevel"
                >
                  <el-icon><Refresh /></el-icon>
                  重新生成
                </el-button>
              </div>
              
              <!-- 提交答案加载区域 -->
              <div v-if="submitting" class="custom-loading-area submit-loading">
                <img 
                  :src="loadingIcon" 
                  alt="提交中..." 
                  class="custom-loading-icon"
                  :style="{ left: submitLoadingPosition.x + 'px', top: submitLoadingPosition.y + 'px' }"
                />
                <div class="loading-text">正在提交答案...</div>
                <div class="progress-container">
                  <el-progress 
                    :percentage="submitProgress" 
                    :stroke-width="6"
                    :show-text="false"
                    color="#DAA520"
                  />
                  <div class="progress-text">{{ submitProgress }}%</div>
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
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { generateLevel as generateLevelAPI, getFeaturedLevels } from '../api/level'
import { submitAnswer as submitAnswerAPI } from '../api/userLevel'
import { ElMessage } from 'element-plus'
import {
  MagicStick,
  Check,
  Refresh,
  Close,
  Star
} from '@element-plus/icons-vue'
import GlobalNavbar from '../components/GlobalNavbar.vue'
import loadingIcon from '../assets/loading.png'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const generating = ref(false)
const submitting = ref(false)
const currentLevel = ref(null)
const selectedOptions = ref([])
const draggedOption = ref(null)

// 精选关卡相关数据
const featuredLevels = ref([])
const featuredLoading = ref(false)
const featuredCurrent = ref(1)
const featuredPageSize = ref(6)
const featuredTotal = ref(0)

// 学习方向相关数据
const selectedDirection = ref('')
const directionOptions = [
  { value: '全栈开发', label: '全栈开发', desc: '前后端通吃' },
  { value: '前端开发', label: '前端开发', desc: 'Web界面开发' },
  { value: 'Java后端开发', label: 'Java后端开发', desc: '企业级后端' },
  { value: 'Python后端开发', label: 'Python后端开发', desc: '快速开发' },
  { value: '软件测试', label: '软件测试', desc: '质量保障' },
  { value: 'AI算法', label: 'AI算法', desc: '人工智能' },
  { value: '网络运维', label: '网络运维', desc: '系统运维' },
  { value: '数据分析', label: '数据分析', desc: '数据洞察' },
  { value: '移动端开发', label: '移动端开发', desc: 'App开发' },
  { value: 'DevOps运维', label: 'DevOps运维', desc: '自动化运维' },
  { value: '网络安全', label: '网络安全', desc: '安全防护' },
  { value: '游戏开发', label: '游戏开发', desc: '游戏制作' }
]

// 进度条相关
const generateProgress = ref(0)
const submitProgress = ref(0)
let generateProgressInterval = null
let submitProgressInterval = null

// 加载图标随机移动相关
const loadingPosition = ref({ x: 0, y: 0 })
const submitLoadingPosition = ref({ x: 0, y: 0 })
let loadingInterval = null
let submitLoadingInterval = null

// 随机移动函数
const getRandomPosition = (containerWidth = 300, containerHeight = 180, iconSize = 40) => {
  // 限制图标在容器上半部分活动，避免与文案和进度条重叠
  const maxY = Math.min(containerHeight - 120, 35) // 保留底部给文案和进度条
  return {
    x: Math.random() * (containerWidth - iconSize - 10) + 5, // 左右留5px边距
    y: Math.random() * maxY + 5 // 上方留5px边距
  }
}

// 开始随机移动
const startRandomMovement = (positionRef, intervalRef) => {
  if (intervalRef) {
    clearInterval(intervalRef)
  }
  
  // 根据屏幕大小调整容器尺寸
  const isMobile = window.innerWidth <= 768
  const containerWidth = isMobile ? 250 : 300
  const containerHeight = isMobile ? 160 : 180
  const iconSize = isMobile ? 35 : 40
  
  // 初始位置
  positionRef.value = getRandomPosition(containerWidth, containerHeight, iconSize)
  
  // 每800ms移动一次（稍微慢一点，更优雅）
  return setInterval(() => {
    positionRef.value = getRandomPosition(containerWidth, containerHeight, iconSize)
  }, 800)
}

// 停止随机移动
const stopRandomMovement = (intervalRef) => {
  if (intervalRef) {
    clearInterval(intervalRef)
  }
}

// 进度条模拟逻辑
const simulateProgress = (progressRef, type = 'generate') => {
  progressRef.value = 0
  let currentProgress = 0
  
  return setInterval(() => {
    if (currentProgress < 30) {
      // 前30%：快速增长（模拟初始化）
      currentProgress += Math.random() * 8 + 3
    } else if (currentProgress < 60) {
      // 30%-60%：中等速度（模拟处理中）
      currentProgress += Math.random() * 4 + 2
    } else if (currentProgress < 85) {
      // 60%-85%：较慢速度（模拟深度处理）
      currentProgress += Math.random() * 2 + 1
    } else if (currentProgress < 99) {
      // 85%-99%：很慢速度（模拟最终处理）
      currentProgress += Math.random() * 0.5 + 0.2
    }
    
    // 确保不超过99%，最后1%由实际完成时设置
    progressRef.value = Math.min(Math.floor(currentProgress), 99)
  }, type === 'generate' ? 300 : 200) // 生成关卡稍慢，提交答案稍快
}

const stopProgress = (intervalRef) => {
  if (intervalRef) {
    clearInterval(intervalRef)
  }
}

const completeProgress = (progressRef) => {
  progressRef.value = 100
}

// 计算可用选项（排除已选择的）
const availableOptions = computed(() => {
  if (!currentLevel.value?.options) return []
  
  try {
    const options = typeof currentLevel.value.options === 'string' 
      ? JSON.parse(currentLevel.value.options) 
      : currentLevel.value.options
    return options || []
  } catch (error) {
    console.error('解析选项失败:', error)
    return []
  }
})


const getDifficultyType = (difficulty) => {
  switch (difficulty) {
    case '简单': return 'success'
    case '中等': return 'warning'
    case '困难': return 'danger'
    default: return 'info'
  }
}

const formatDescription = (desc) => {
  if (!desc) return ''
  // 将换行符转换为 <br> 标签，保持原有格式
  return desc.replace(/\n/g, '<br>')
}

// 生成关卡
const generateLevel = async () => {
  console.log('用户信息:', user.value)
  console.log('用户薪资:', user.value?.salary)
  
  if (!user.value) {
    ElMessage.error('用户未登录，请先登录')
    router.push('/login')
    return
  }
  
  // 如果用户没有薪资信息，设置默认薪资为 5000
  const userSalary = user.value.salary || 5000
  console.log('使用的薪资:', userSalary)

  generating.value = true
  // 启动进度条模拟
  generateProgressInterval = simulateProgress(generateProgress, 'generate')
  
  try {
    const requestData = { 
      salary: userSalary,
      direction: selectedDirection.value
    }
    const levelData = await generateLevelAPI(requestData)
    currentLevel.value = levelData
    selectedOptions.value = []
    
    // 完成进度条
    completeProgress(generateProgress)
    
    // 短暂延迟后显示成功消息，让用户看到100%
    setTimeout(() => {
      ElMessage.success('关卡生成成功！')
    }, 200)
  } catch (error) {
    console.error('生成关卡失败:', error)
    ElMessage.error('生成关卡失败，请重试')
  } finally {
    // 清理进度条定时器
    stopProgress(generateProgressInterval)
    generateProgressInterval = null
    
    // 延迟重置状态，让用户看到完成效果
    setTimeout(() => {
      generating.value = false
      generateProgress.value = 0
    }, 500)
  }
}

// 拖拽开始
const handleDragStart = (event, option) => {
  draggedOption.value = option
  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData('text/plain', option.optionName)
}

// 拖拽悬停
const handleDragOver = (event) => {
  event.preventDefault()
  event.dataTransfer.dropEffect = 'move'
}

const handleDragEnter = (event) => {
  event.preventDefault()
  event.target.closest('.answer-box')?.classList.add('drag-over')
}

const handleDragLeave = (event) => {
  event.preventDefault()
  if (!event.target.closest('.answer-box')?.contains(event.relatedTarget)) {
    event.target.closest('.answer-box')?.classList.remove('drag-over')
  }
}

// 拖拽放下
const handleDrop = (event) => {
  event.preventDefault()
  event.target.closest('.answer-box')?.classList.remove('drag-over')
  
  if (draggedOption.value) {
    toggleOption(draggedOption.value)
    draggedOption.value = null
  }
}

// 切换选项（添加或移除）
const toggleOption = (option) => {
  const optionName = option.optionName
  const index = selectedOptions.value.indexOf(optionName)
  
  if (index > -1) {
    selectedOptions.value.splice(index, 1)
  } else {
    selectedOptions.value.push(optionName)
  }
}

// 移除选项
const removeOption = (optionName) => {
  const index = selectedOptions.value.indexOf(optionName)
  if (index > -1) {
    selectedOptions.value.splice(index, 1)
  }
}

// 提交答案
const submitAnswer = async () => {
  if (selectedOptions.value.length === 0) {
    ElMessage.warning('请至少选择一个选项')
    return
  }

  submitting.value = true
  // 启动进度条模拟
  submitProgressInterval = simulateProgress(submitProgress, 'submit')
  
  try {
    const submitData = {
      levelId: currentLevel.value.id,
      userOptions: selectedOptions.value
    }
    
    const result = await submitAnswerAPI(submitData)
    console.log('提交答案结果:', result)
    
    // 更新用户薪资
    if (result.salaryChange) {
      const newSalary = user.value.salary + result.salaryChange
      userStore.updateUserSalary(newSalary)
    }
    
    // 完成进度条
    completeProgress(submitProgress)
    
    // 短暂延迟后跳转，让用户看到100%
    setTimeout(() => {
      console.log('准备跳转到结果页面:', `/result/${result.id}`)
      router.push(`/result/${result.id}`)
    }, 300)
    
  } catch (error) {
    console.error('提交答案失败:', error)
    ElMessage.error('提交答案失败，请重试')
    
    // 清理进度条定时器
    stopProgress(submitProgressInterval)
    submitProgressInterval = null
    
    // 重置状态
    setTimeout(() => {
      submitting.value = false
      submitProgress.value = 0
    }, 300)
  }
}

// 重置关卡
const resetLevel = () => {
  currentLevel.value = null
  selectedOptions.value = []
}

// 加载精选关卡列表
const loadFeaturedLevels = async () => {
  featuredLoading.value = true
  try {
    const response = await getFeaturedLevels(featuredCurrent.value, featuredPageSize.value)
    featuredLevels.value = response.records || []
    featuredTotal.value = response.total || 0
  } catch (error) {
    console.error('获取精选关卡失败:', error)
    ElMessage.error('获取精选关卡失败')
  } finally {
    featuredLoading.value = false
  }
}

// 选择精选关卡
const selectFeaturedLevel = (level) => {
  currentLevel.value = level
  selectedOptions.value = []
  ElMessage.success(`已选择关卡：${level.levelName}`)
}

// 文本截断
const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

// 监听生成状态变化
watch(generating, (newVal) => {
  if (newVal) {
    loadingInterval = startRandomMovement(loadingPosition, loadingInterval)
  } else {
    stopRandomMovement(loadingInterval)
    loadingInterval = null
    // 确保进度条定时器也被清理
    if (generateProgressInterval) {
      stopProgress(generateProgressInterval)
      generateProgressInterval = null
    }
  }
})

// 监听提交状态变化
watch(submitting, (newVal) => {
  if (newVal) {
    submitLoadingInterval = startRandomMovement(submitLoadingPosition, submitLoadingInterval)
  } else {
    stopRandomMovement(submitLoadingInterval)
    submitLoadingInterval = null
    // 确保进度条定时器也被清理
    if (submitProgressInterval) {
      stopProgress(submitProgressInterval)
      submitProgressInterval = null
    }
  }
})

onMounted(() => {
  // 页面加载时检查用户登录状态
  if (!user.value) {
    router.push('/login')
  } else {
    // 加载精选关卡列表
    loadFeaturedLevels()
  }
})

onUnmounted(() => {
  // 组件销毁时清理所有定时器
  stopRandomMovement(loadingInterval)
  stopRandomMovement(submitLoadingInterval)
  stopProgress(generateProgressInterval)
  stopProgress(submitProgressInterval)
})
</script>

<style scoped>
.challenge-container {
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

.generate-section {
  display: flex;
  flex-direction: column;
  gap: 30px;
  align-items: center;
  min-height: 400px;
}

.generate-card {
  max-width: 500px;
  width: 100%;
}

.featured-levels-card {
  width: 100%;
  max-width: 1200px;
  box-shadow: 0 8px 32px var(--shadow-heavy);
  border-radius: 20px;
  border: 1px solid var(--border-light);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header h3 {
  margin: 0;
  color: var(--primary-brown);
  font-size: 20px;
  font-weight: 600;
}

.star-icon {
  font-size: 24px;
  color: var(--accent-gold);
}

.featured-content {
  min-height: 200px;
}

.no-featured {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.featured-item {
  background: var(--bg-card);
  border: 2px solid var(--border-light);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.featured-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.featured-item:hover {
  border-color: var(--accent-gold);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px var(--shadow-medium);
}

.featured-item:hover::before {
  opacity: 1;
}

.featured-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 10px;
}

.featured-title {
  margin: 0;
  color: var(--primary-brown);
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  flex: 1;
}

.featured-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  align-items: flex-start;
}

.featured-desc {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 16px;
  min-height: 42px;
}

.featured-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-muted);
}

.target-salary {
  color: var(--accent-gold);
  font-weight: 600;
}

.create-time {
  color: var(--text-muted);
}

.featured-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

.generate-content {
  text-align: center;
  padding: 40px 20px;
}

.generate-content h2 {
  margin-bottom: 20px;
  color: var(--text-primary);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.generate-content p {
  margin-bottom: 35px;
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 16px;
}

.direction-selector {
  margin-bottom: 30px;
}

.direction-selector h3 {
  margin-bottom: 15px;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
}

.level-section {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.level-info-card {
  width: 100%;
}

.level-header {
  margin-bottom: 20px;
}

.level-name {
  color: var(--text-primary);
  margin-bottom: 12px;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.level-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.target-salary {
  color: var(--text-secondary);
  font-size: 15px;
  font-weight: 600;
}

.level-desc h3 {
  color: var(--text-primary);
  margin-bottom: 15px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.desc-content {
  color: var(--text-secondary);
  line-height: 1.8;
  background: var(--bg-secondary);
  padding: 25px;
  border-radius: 12px;
  border-left: 4px solid var(--accent-gold);
  border: 2px solid var(--border-light);
  font-size: 15px;
}

.answer-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.options-area h3,
.answer-area h3 {
  color: var(--text-primary);
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.option-item {
  background: var(--bg-card);
  border: 2px solid var(--border-light);
  border-radius: 12px;
  padding: 18px 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
  color: var(--text-primary);
  font-weight: 500;
}

.option-item:hover {
  border-color: var(--primary-brown);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px var(--shadow-medium);
}

.option-item.selected {
  background: linear-gradient(135deg, var(--accent-gold) 0%, var(--accent-copper) 100%);
  border-color: var(--accent-gold);
  color: var(--text-primary);
  font-weight: 600;
}

.answer-box {
  min-height: 220px;
  border: 2px dashed var(--border-medium);
  border-radius: 12px;
  padding: 25px;
  background: var(--bg-card);
  transition: all 0.3s ease;
}

.answer-box.drag-over {
  border-color: var(--accent-gold);
  background: var(--secondary-sand);
  border-style: solid;
}

.answer-placeholder {
  text-align: center;
  color: var(--text-muted);
  font-style: italic;
  padding: 70px 20px;
  font-size: 15px;
}

.selected-option {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  color: var(--bg-card);
  padding: 10px 18px;
  border-radius: 25px;
  margin: 6px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 12px var(--shadow-medium);
}

.submit-area {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.submit-area .button-row {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 10px;
}

/* 自定义加载区域 */
.custom-loading-area {
  position: relative;
  width: 300px;
  height: 180px;
  margin: 20px auto;
  border: 2px dashed var(--border-medium);
  border-radius: 12px;
  background: var(--bg-secondary);
  overflow: hidden;
}

.custom-loading-icon {
  position: absolute;
  width: 40px;
  height: 40px;
  transition: all 0.3s ease-in-out;
  z-index: 2;
  /* 限制图标活动范围，避免与文案和进度条重叠 */
  top: 5px;
}

.loading-text {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  z-index: 3;
  background: var(--bg-secondary);
  padding: 2px 8px;
  border-radius: 4px;
}

/* 进度条容器 */
.progress-container {
  position: absolute;
  bottom: 8px;
  left: 20px;
  right: 20px;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-container .el-progress {
  flex: 1;
}

.progress-text {
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: 600;
  min-width: 30px;
  text-align: right;
}

.submit-loading {
  margin-top: 15px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 30px 20px;
  }
  
  .answer-section {
    grid-template-columns: 1fr;
  }
  
  .options-grid {
    grid-template-columns: 1fr;
  }
  
  .submit-area .button-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .custom-loading-area {
    width: 250px;
    height: 80px;
  }
  
  .custom-loading-icon {
    width: 35px;
    height: 35px;
  }
  
  .progress-container {
    left: 15px;
    right: 15px;
    gap: 8px;
  }
  
  .progress-text {
    font-size: 11px;
    min-width: 28px;
  }
  
  .featured-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .featured-item {
    padding: 15px;
  }
  
  .featured-title {
    font-size: 15px;
  }
  
  .featured-desc {
    font-size: 13px;
    min-height: 36px;
  }
}
</style>

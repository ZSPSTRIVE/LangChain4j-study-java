<template>
  <div class="admin-container">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <div class="main-content">
      <el-card class="admin-card">
        <template #header>
          <div class="card-header">
            <h2>关卡管理</h2>
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              添加关卡
            </el-button>
          </div>
        </template>

        <!-- 搜索区域 -->
        <div class="search-section">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-input 
                v-model="queryParams.levelName" 
                placeholder="关卡名称"
                clearable
                @clear="handleSearch"
                @keyup.enter="handleSearch"
              />
            </el-col>
            <el-col :span="6">
              <el-select 
                v-model="queryParams.difficulty" 
                placeholder="难度等级"
                clearable
                @change="handleSearch"
              >
                <el-option label="简单" value="简单" />
                <el-option label="中等" value="中等" />
                <el-option label="困难" value="困难" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select 
                v-model="queryParams.direction" 
                placeholder="学习方向"
                clearable
                @change="handleSearch"
              >
                <el-option label="全栈开发" value="全栈开发" />
                <el-option label="前端开发" value="前端开发" />
                <el-option label="Java后端开发" value="Java后端开发" />
                <el-option label="Python后端开发" value="Python后端开发" />
                <el-option label="软件测试" value="软件测试" />
                <el-option label="AI算法" value="AI算法" />
                <el-option label="网络运维" value="网络运维" />
                <el-option label="数据分析" value="数据分析" />
                <el-option label="移动端开发" value="移动端开发" />
                <el-option label="DevOps运维" value="DevOps运维" />
                <el-option label="网络安全" value="网络安全" />
                <el-option label="游戏开发" value="游戏开发" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select 
                v-model="queryParams.priority" 
                placeholder="优先级"
                clearable
                @change="handleSearch"
              >
                <el-option label="普通" :value="0" />
                <el-option label="推荐" :value="99" />
                <el-option label="精选" :value="999" />
                <el-option label="置顶" :value="9999" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 批量操作区域 -->
        <div class="batch-section" v-if="selectedRows.length > 0">
          <el-alert 
            :title="`已选择 ${selectedRows.length} 项`" 
            type="info" 
            show-icon
            :closable="false"
          />
          <div class="batch-buttons">
            <el-button type="success" @click="batchSetPriority(999)">
              <el-icon><Star /></el-icon>
              设为精选
            </el-button>
            <el-button type="warning" @click="batchSetPriority(99)">
              <el-icon><TopRight /></el-icon>
              设为推荐
            </el-button>
            <el-button @click="batchSetPriority(0)">
              设为普通
            </el-button>
          </div>
        </div>

        <!-- 关卡列表 -->
        <el-table 
          :data="levelList" 
          v-loading="loading"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="levelName" label="关卡名称" min-width="200">
            <template #default="{ row }">
              <div class="level-name">
                {{ row.levelName }}
                <el-tag v-if="row.priority >= 999" type="danger" size="small">精选</el-tag>
                <el-tag v-else-if="row.priority >= 99" type="warning" size="small">推荐</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="direction" label="学习方向" width="120">
            <template #default="{ row }">
              <el-tag type="primary" size="small">
                {{ row.direction || '全栈开发' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="100">
            <template #default="{ row }">
              <el-tag 
                :type="getDifficultyType(row.difficulty)"
                size="small"
              >
                {{ row.difficulty }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="targetSalary" label="目标薪资" width="120">
            <template #default="{ row }">
              ¥{{ row.targetSalary?.toLocaleString() || 0 }}/月
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="{ row }">
              {{ getPriorityText(row.priority) }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="380" fixed="right">
            <template #default="{ row }">
              <el-button 
                v-if="row.priority < 999"
                type="success" 
                size="small" 
                @click="setFeatured(row)"
              >
                <el-icon><Star /></el-icon>
                设为精选
              </el-button>
              <el-button 
                v-else
                type="warning" 
                size="small" 
                @click="cancelFeatured(row)"
              >
                <el-icon><StarFilled /></el-icon>
                取消精选
              </el-button>
              <el-button 
                type="primary" 
                size="small" 
                @click="editLevel(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteLevel(row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="queryParams.current"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
          />
        </div>
      </el-card>
    </div>

    <!-- 添加/编辑关卡对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="showAddDialog"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="levelForm" 
        :rules="levelRules" 
        ref="levelFormRef"
        label-width="120px"
      >
        <el-form-item label="关卡名称" prop="levelName">
          <el-input v-model="levelForm.levelName" placeholder="请输入关卡名称" />
        </el-form-item>
        <el-form-item label="关卡描述" prop="levelDesc">
          <el-input 
            v-model="levelForm.levelDesc" 
            type="textarea" 
            :rows="4"
            placeholder="请输入关卡需求描述"
          />
        </el-form-item>
        <el-form-item label="关卡选项" prop="options">
          <el-input 
            v-model="levelForm.options" 
            type="textarea" 
            :rows="6"
            placeholder="请输入JSON格式的关卡选项"
          />
        </el-form-item>
        <el-form-item label="学习方向" prop="direction">
          <el-select v-model="levelForm.direction" placeholder="请选择学习方向">
            <el-option label="全栈开发" value="全栈开发" />
            <el-option label="前端开发" value="前端开发" />
            <el-option label="Java后端开发" value="Java后端开发" />
            <el-option label="Python后端开发" value="Python后端开发" />
            <el-option label="软件测试" value="软件测试" />
            <el-option label="AI算法" value="AI算法" />
            <el-option label="网络运维" value="网络运维" />
            <el-option label="数据分析" value="数据分析" />
            <el-option label="移动端开发" value="移动端开发" />
            <el-option label="DevOps运维" value="DevOps运维" />
            <el-option label="网络安全" value="网络安全" />
            <el-option label="游戏开发" value="游戏开发" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="levelForm.difficulty" placeholder="请选择难度等级">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标薪资" prop="targetSalary">
          <el-input-number 
            v-model="levelForm.targetSalary" 
            :min="1000" 
            :max="100000"
            :step="1000"
            placeholder="目标薪资"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="levelForm.priority" placeholder="请选择优先级">
            <el-option label="普通" :value="0" />
            <el-option label="推荐" :value="99" />
            <el-option label="精选" :value="999" />
            <el-option label="置顶" :value="9999" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Search, 
  Edit, 
  Delete, 
  Star, 
  StarFilled,
  TopRight 
} from '@element-plus/icons-vue'
import GlobalNavbar from '../components/GlobalNavbar.vue'
import * as adminApi from '../api/admin'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const levelList = ref([])
const total = ref(0)
const selectedRows = ref([])
const showAddDialog = ref(false)
const isEdit = ref(false)
const levelFormRef = ref()

// 查询参数
const queryParams = reactive({
  levelName: '',
  difficulty: '',
  direction: '',
  priority: null,
  current: 1,
  pageSize: 10
})

// 表单数据
const levelForm = reactive({
  id: '',
  levelName: '',
  levelDesc: '',
  options: '',
  difficulty: '',
  targetSalary: 10000,
  direction: '全栈开发',
  priority: 0
})

// 表单验证规则
const levelRules = {
  levelName: [
    { required: true, message: '请输入关卡名称', trigger: 'blur' }
  ],
  levelDesc: [
    { required: true, message: '请输入关卡描述', trigger: 'blur' }
  ],
  options: [
    { required: true, message: '请输入关卡选项', trigger: 'blur' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑关卡' : '添加关卡'
})

// 权限检查
const checkAdminAuth = () => {
  if (!userStore.user || userStore.user.userRole !== 'admin') {
    ElMessage.error('无权限访问，仅限管理员')
    router.push('/')
    return false
  }
  return true
}

// 获取难度标签类型
const getDifficultyType = (difficulty) => {
  switch (difficulty) {
    case '简单': return 'success'
    case '中等': return 'warning'
    case '困难': return 'danger'
    default: return 'info'
  }
}

// 获取优先级文本
const getPriorityText = (priority) => {
  switch (priority) {
    case 0: return '普通'
    case 99: return '推荐'
    case 999: return '精选'
    case 9999: return '置顶'
    default: return '普通'
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 搜索关卡
const handleSearch = async () => {
  if (!checkAdminAuth()) return
  
  loading.value = true
  try {
    const response = await adminApi.listLevels(queryParams)
    levelList.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('查询关卡失败:', error)
    ElMessage.error('查询关卡失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const handleReset = () => {
  queryParams.levelName = ''
  queryParams.difficulty = ''
  queryParams.direction = ''
  queryParams.priority = null
  queryParams.current = 1
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量设置优先级
const batchSetPriority = async (priority) => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的关卡')
    return
  }

  try {
    const ids = selectedRows.value.map(row => row.id).join(',')
    await adminApi.setLevelPriority(ids, priority)
    ElMessage.success('批量设置成功')
    selectedRows.value = []
    handleSearch()
  } catch (error) {
    console.error('批量设置优先级失败:', error)
    ElMessage.error('批量设置失败')
  }
}

// 设置精选
const setFeatured = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定将关卡 "${row.levelName}" 设为精选吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await adminApi.setLevelFeatured(row.id)
    ElMessage.success('设置成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设置精选失败:', error)
      ElMessage.error('设置失败')
    }
  }
}

// 取消精选
const cancelFeatured = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定取消关卡 "${row.levelName}" 的精选状态吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await adminApi.cancelLevelFeatured(row.id)
    ElMessage.success('取消成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消精选失败:', error)
      ElMessage.error('取消失败')
    }
  }
}

// 编辑关卡
const editLevel = (row) => {
  isEdit.value = true
  Object.assign(levelForm, row)
  showAddDialog.value = true
}

// 删除关卡
const deleteLevel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除关卡 "${row.levelName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await adminApi.deleteLevel(row.id)
    ElMessage.success('删除成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除关卡失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!levelFormRef.value) return

  try {
    await levelFormRef.value.validate()
    
    // 验证JSON格式
    try {
      JSON.parse(levelForm.options)
    } catch {
      ElMessage.error('关卡选项必须是有效的JSON格式')
      return
    }

    submitting.value = true

    if (isEdit.value) {
      await adminApi.updateLevel(levelForm)
      ElMessage.success('更新成功')
    } else {
      await adminApi.addLevel(levelForm)
      ElMessage.success('添加成功')
    }

    showAddDialog.value = false
    resetForm()
    handleSearch()

  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  isEdit.value = false
  Object.assign(levelForm, {
    id: '',
    levelName: '',
    levelDesc: '',
    options: '',
    difficulty: '',
    targetSalary: 10000,
    direction: '全栈开发',
    priority: 0
  })
  if (levelFormRef.value) {
    levelFormRef.value.resetFields()
  }
}

// 监听对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 组件挂载时检查权限并加载数据
onMounted(async () => {
  if (checkAdminAuth()) {
    await handleSearch()
  }
})
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
}

.admin-card {
  box-shadow: 0 8px 32px var(--shadow-heavy);
  border-radius: 20px;
  border: 1px solid var(--border-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: var(--primary-brown);
  font-size: 24px;
  font-weight: 700;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-light);
}

.batch-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-buttons {
  display: flex;
  gap: 10px;
}

.level-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-section {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 15px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .batch-section {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .batch-buttons {
    justify-content: center;
  }
}
</style>

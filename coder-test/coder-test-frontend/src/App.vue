<template>
  <div id="app">
    <div class="app-content">
      <router-view />
    </div>
    <GlobalFooter />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'
import GlobalFooter from './components/GlobalFooter.vue'

const userStore = useUserStore()

onMounted(async () => {
  // 应用启动时尝试获取当前用户信息
  await userStore.getCurrentUser()
})
</script>

<style>

:root {
  /* 古朴配色方案 */
  --primary-sand: #D2B48C;           /* 沙黄色主色 */
  --primary-brown: #8B7355;          /* 棕褐色主色 */
  --secondary-sand: #F5DEB3;         /* 浅沙色 */
  --secondary-brown: #A0522D;        /* 深棕色 */
  --accent-gold: #DAA520;            /* 金色强调 */
  --accent-copper: #B87333;          /* 铜色强调 */
  
  /* 中性色 */
  --text-primary: #3C2415;           /* 深棕色文字 */
  --text-secondary: #6B5B47;         /* 中棕色文字 */
  --text-muted: #8B7D6B;             /* 浅棕色文字 */
  --bg-primary: #FAF7F0;             /* 主背景色 */
  --bg-secondary: #F5F1E8;           /* 次背景色 */
  --bg-card: #FEFCF7;                /* 卡片背景 */
  
  /* 边框和阴影 */
  --border-light: #E8DCC6;           /* 浅色边框 */
  --border-medium: #D2B48C;          /* 中色边框 */
  --shadow-light: rgba(139, 115, 85, 0.1);
  --shadow-medium: rgba(139, 115, 85, 0.2);
  --shadow-heavy: rgba(139, 115, 85, 0.3);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Source Han Serif SC', 'Noto Serif CJK SC', 'SimSun', '宋体', 'Times New Roman', serif;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  color: var(--text-primary);
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: 
    radial-gradient(circle at 20% 80%, rgba(210, 180, 140, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 115, 85, 0.1) 0%, transparent 50%);
}

.app-content {
  flex: 1;
}

/* 全局按钮样式 */
.el-button {
  border-radius: 6px !important;
  font-family: inherit !important;
  font-weight: 500 !important;
  letter-spacing: 0.5px !important;
  transition: all 0.3s ease !important;
}

.el-button--primary {
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%) !important;
  border: 2px solid var(--primary-brown) !important;
  box-shadow: 0 4px 12px var(--shadow-medium) !important;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, var(--secondary-brown) 0%, var(--primary-brown) 100%) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px var(--shadow-heavy) !important;
}

.el-button--default {
  background: var(--bg-card) !important;
  border: 2px solid var(--border-medium) !important;
  color: var(--text-primary) !important;
}

.el-button--default:hover {
  background: var(--secondary-sand) !important;
  border-color: var(--primary-brown) !important;
  transform: translateY(-1px) !important;
}

/* 全局卡片样式 */
.el-card {
  border: 2px solid var(--border-light) !important;
  border-radius: 12px !important;
  background: var(--bg-card) !important;
  box-shadow: 0 8px 24px var(--shadow-light) !important;
  transition: all 0.3s ease !important;
}

.el-card:hover {
  transform: translateY(-4px) !important;
  box-shadow: 0 12px 32px var(--shadow-medium) !important;
  border-color: var(--border-medium) !important;
}

/* 全局标题样式 */
h1, h2, h3, h4, h5, h6 {
  color: var(--text-primary);
  font-weight: 600;
  letter-spacing: 1px;
}
</style>

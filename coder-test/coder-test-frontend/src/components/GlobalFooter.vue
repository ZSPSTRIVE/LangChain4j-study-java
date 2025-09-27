<template>
  <div class="footer-container">
    <div class="footer-row">
      <!-- Logo和基本信息 -->
      <div class="footer-section logo-section">
        <div class="footer-logo">
          <el-icon class="logo-icon" :size="48"><Trophy /></el-icon>
          <h5 class="logo-text">程序员技术练兵场</h5>
        </div>
        <div class="footer-links">
          <a href="#" class="footer-protocol-button">《用户协议》</a>
          <a href="#" class="footer-protocol-button">《隐私政策》</a>
        </div>
      </div>

      <!-- 友情链接 -->
      <div class="footer-section">
        <h5 class="footer-title">友情链接</h5>
        <div class="footer-links">
          <a href="https://www.codecopy.cn/" target="_blank" class="footer-protocol-button">代码小抄</a>
          <a href="https://jianqiezhushou.com/" target="_blank" class="footer-protocol-button">剪切助手</a>
          <a href="https://mianshiya.com/" target="_blank" class="footer-protocol-button">面试鸭</a>
        </div>
      </div>

      <!-- 联系我们 -->
      <div class="footer-section">
        <h5 class="footer-title">联系我们</h5>
        <div class="footer-links">
          <a href="#" target="_blank" class="footer-protocol-button">商务合作</a>
          <a href="#" target="_blank" class="footer-protocol-button">BIGbird</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Trophy } from '@element-plus/icons-vue'
import qrcodeImage from '../assets/qrcode.jpg'

// 获取当前年份
const currentYear = computed(() => {
  return new Date().getFullYear()
})
</script>

<style scoped>
.footer-container {
  background: linear-gradient(135deg, var(--primary-brown) 0%, var(--secondary-brown) 100%);
  padding: 60px 0 0;
  border-top: 4px solid var(--accent-gold);
  position: relative;
}

.footer-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-gold) 0%, var(--accent-copper) 50%, var(--accent-gold) 100%);
}

.footer-row {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  padding: 0 26px 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.logo-section {
  padding-left: 0;
}

.footer-logo {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.logo-icon {
  margin-right: 12px;
  margin-top: -10px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--bg-card);
  margin: 0;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
}

.footer-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--accent-gold);
  margin: 0 0 20px 0;
  letter-spacing: 1px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 46px;
}

.logo-section .footer-links {
  padding-left: 46px;
}

.footer-section:not(.logo-section) .footer-links {
  padding-left: 0;
}

.footer-protocol-button {
  color: var(--secondary-sand);
  text-decoration: none;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s ease;
  display: inline-block;
  padding: 6px 0;
  position: relative;
}

.footer-protocol-button::before {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--accent-gold);
  transition: width 0.3s ease;
}

.footer-protocol-button:hover {
  color: var(--accent-gold);
  transform: translateX(4px);
}

.footer-protocol-button:hover::before {
  width: 100%;
}

.about-section {
  align-items: flex-start;
}

.qrcode-section {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.qrcode-image {
  width: 65px;
  height: 65px;
  border-radius: 4px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  object-fit: cover;
}

.qrcode-text {
  font-size: 12px;
  color: #999;
  line-height: 1.3;
  margin-bottom: 6px;
  margin-left: 4px;
  display: block;
}

.copyright-footer {
  text-align: center;
  margin-bottom: 40px;
  color: var(--secondary-sand);
  font-size: 14px;
  padding: 30px 20px 0;
  border-top: 1px solid rgba(210, 180, 140, 0.2);
  position: relative;
}

.copyright-footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 1px;
  background: var(--accent-gold);
}

.copyright-text {
  margin-right: 20px;
  font-weight: 500;
}

.footer-protocol-link,
.footer-about-me {
  color: var(--secondary-sand);
  text-decoration: none;
  margin: 0 12px;
  transition: all 0.3s ease;
  position: relative;
  padding: 2px 0;
}

.footer-protocol-link::before,
.footer-about-me::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--accent-gold);
  transition: width 0.3s ease;
}

.footer-protocol-link:hover,
.footer-about-me:hover {
  color: var(--accent-gold);
}

.footer-protocol-link:hover::before,
.footer-about-me:hover::before {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-row {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 0 20px 30px;
  }
  
  .footer-links {
    padding-left: 0 !important;
  }
  
  .logo-section .footer-links {
    padding-left: 0 !important;
  }
  
  .qrcode-section {
    justify-content: flex-start;
  }
  
  .copyright-footer {
    font-size: 12px;
    line-height: 1.5;
  }
  
  .copyright-text {
    display: block;
    margin: 0 0 10px 0;
  }
}

@media (max-width: 480px) {
  .footer-container {
    padding: 30px 0 0;
  }
  
  .footer-row {
    padding: 0 15px 20px;
  }
  
  .footer-logo {
    justify-content: center;
  }
  
  .footer-section {
    text-align: center;
  }
  
  .footer-links {
    align-items: center;
  }
  
  .qrcode-section {
    justify-content: center;
  }
}
</style>

import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as userApi from '../api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const isLoggedIn = ref(false)

  // 登录
  const loginUser = async (loginData) => {
    try {
      const userData = await userApi.login(loginData)
      user.value = userData
      isLoggedIn.value = true
      ElMessage.success('登录成功')
      return userData
    } catch (error) {
      throw error
    }
  }

  // 注册
  const registerUser = async (registerData) => {
    try {
      await userApi.register(registerData)
      ElMessage.success('注册成功')
    } catch (error) {
      throw error
    }
  }

  // 注销
  const logoutUser = async () => {
    try {
      await userApi.logout()
      user.value = null
      isLoggedIn.value = false
      ElMessage.success('注销成功')
    } catch (error) {
      console.error('注销失败:', error)
    }
  }

  // 获取当前用户信息
  const getCurrentUser = async () => {
    try {
      const userData = await userApi.getCurrentUser()
      console.log('获取到的用户数据:', userData)
      user.value = userData
      isLoggedIn.value = true
      return userData
    } catch (error) {
      console.log('获取用户信息失败:', error)
      user.value = null
      isLoggedIn.value = false
      return null
    }
  }

  // 更新用户薪资
  const updateUserSalary = (newSalary) => {
    if (user.value) {
      user.value.salary = newSalary
    }
  }

  return {
    user,
    isLoggedIn,
    loginUser,
    registerUser,
    logoutUser,
    getCurrentUser,
    updateUserSalary
  }
})
